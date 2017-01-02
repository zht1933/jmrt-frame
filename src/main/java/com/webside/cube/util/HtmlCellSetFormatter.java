package com.webside.cube.util;

import org.apache.commons.lang.StringUtils;
import org.olap4j.*;
import org.olap4j.impl.CoordinateIterator;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.layout.CellSetFormatter;
import org.olap4j.metadata.Member;

import java.io.PrintWriter;
import java.util.*;

public class HtmlCellSetFormatter implements CellSetFormatter {

	private Integer cellSpacing;

	private Integer cellPadding;
	
	private String rowStyleClass = "pv-row";//行样式

	private String evenRowStyleClass = "pv-row-even";

	private String oddRowStyleClass = "pv-row-odd";

	private String columnHeaderStyleClass = "pv-col-hdr";//列头样式

	private String rowHeaderStyleClass = "pv-row-hdr";//行头样式

	private String columnTitleStyleClass = columnHeaderStyleClass;//列标题样式

	private String rowTitleStyleClass = rowHeaderStyleClass;//行标题样式

	private String cellStyleClass = "pv-cell";//单元格样式
	
	private String sumStyleClass = "pv-sum";//合计样式

	private String cornerStyleClass = "pv-corner";//内容样式
	
	public void format(CellSet cellSet, PrintWriter pw) {
		format(cellSet, pw, false, false);
	}

	public void format(CellSet cellSet, PrintWriter pw, boolean sumCol, boolean sumRow) {
		// Compute how many rows are required to display the columns axis.
		// In the example, this is 4 (1997, Q1, space, Unit Sales)
		final CellSetAxis columnsAxis;
		if (cellSet.getAxes().size() > 0) {
			columnsAxis = cellSet.getAxes().get(0);
		} else {
			columnsAxis = null;
		}
		AxisInfo columnsAxisInfo = computeAxisInfo(columnsAxis);

		// Compute how many columns are required to display the rows axis.
		// In the example, this is 3 (the width of USA, CA, Los Angeles)
		final CellSetAxis rowsAxis;
		if (cellSet.getAxes().size() > 1) {
			rowsAxis = cellSet.getAxes().get(1);
		} else {
			rowsAxis = null;
		}
		AxisInfo rowsAxisInfo = computeAxisInfo(rowsAxis);

		if (cellSet.getAxes().size() > 2) {
			int[] dimensions = new int[cellSet.getAxes().size() - 2];
			for (int i = 2; i < cellSet.getAxes().size(); i++) {
				CellSetAxis cellSetAxis = cellSet.getAxes().get(i);
				dimensions[i - 2] = cellSetAxis.getPositions().size();
			}
			for (int[] pageCoords : CoordinateIterator.iterate(dimensions)) {
				formatPage(cellSet, pw, pageCoords, columnsAxis, columnsAxisInfo, rowsAxis, rowsAxisInfo, sumCol, sumRow);
			}
		} else {
			formatPage(cellSet, pw, new int[] {}, columnsAxis, columnsAxisInfo, rowsAxis, rowsAxisInfo, sumCol, sumRow);
		}
	}

	/**
	 * Formats a two-dimensional page.
	 *
	 * @param cellSet
	 *            Cell set
	 * @param pw
	 *            Print writer
	 * @param pageCoords
	 *            Coordinates of page [page, chapter, section, ...]
	 * @param columnsAxis
	 *            Columns axis
	 * @param columnsAxisInfo
	 *            Description of columns axis
	 * @param rowsAxis
	 *            Rows axis
	 * @param rowsAxisInfo
	 *            Description of rows axis
	 */
	private void formatPage(CellSet cellSet, PrintWriter pw, int[] pageCoords, CellSetAxis columnsAxis,
			AxisInfo columnsAxisInfo, CellSetAxis rowsAxis, AxisInfo rowsAxisInfo, boolean sumCol, boolean sumRow) {
		if (pageCoords.length > 0) {
			pw.println();
			for (int i = pageCoords.length - 1; i >= 0; --i) {
				int pageCoord = pageCoords[i];
				final CellSetAxis axis = cellSet.getAxes().get(2 + i);
				pw.print(axis.getAxisOrdinal() + ": ");
				final Position position = axis.getPositions().get(pageCoord);
				int k = -1;
				for (Member member : position.getMembers()) {
					if (++k > 0) {
						pw.print(", ");
					}
					pw.print(member.getUniqueName());
				}
				pw.println();
			}
		}
		// Figure out the dimensions of the blank rectangle in the top left
		// corner.
		final int yOffset = columnsAxisInfo.getWidth();
		final int xOffsset = rowsAxisInfo.getWidth();

		// Populate a string matrix
		Matrix matrix = new Matrix(xOffsset + (columnsAxis == null ? 1 : columnsAxis.getPositions().size())+(sumCol?1:0),
				yOffset + (rowsAxis == null ? 1 : rowsAxis.getPositions().size())+(sumRow?1:0));

		// Populate corner
		for (int x = 0; x < xOffsset; x++) {
			for (int y = 0; y < yOffset; y++) {
				matrix.set(x, y, "", false, x > 0 || y>0, CellType.corner, null, null);
			}
		}
				
		matrix.get(0, 0).setSpan(yOffset, xOffsset);

		// Populate matrix with cells representing axes
		// noinspection SuspiciousNameCombination
		populateAxis(matrix, columnsAxis, columnsAxisInfo, true, xOffsset);
		populateAxis(matrix, rowsAxis, rowsAxisInfo, false, yOffset);

		// Populate cell values
		for (Cell cell : cellIter(pageCoords, cellSet)) {
			final List<Integer> coordList = cell.getCoordinateList();
			int x = xOffsset;
			if (coordList.size() > 0) {
				x += coordList.get(0);
			}
			int y = yOffset;
			if (coordList.size() > 1) {
				y += coordList.get(1);
			}
			matrix.set(x, y, cell.getFormattedValue(), true, false, CellType.data, null, cell.getCoordinateList());
		}

		int[] columnWidths = new int[matrix.width];
		int widestWidth = 0;
		for (int x = 0; x < matrix.width; x++) {
			int columnWidth = 0;
			for (int y = 0; y < matrix.height; y++) {
				MatrixCell cell = matrix.get(x, y);
				if (cell != null) {
					columnWidth = Math.max(columnWidth, cell.value.length());
				}
			}
			columnWidths[x] = columnWidth;
			widestWidth = Math.max(columnWidth, widestWidth);
		}

		// Create a large array of spaces, for efficient printing.
		char[] spaces = new char[widestWidth + 1];
		Arrays.fill(spaces, ' ');
		char[] equals = new char[widestWidth + 1];
		Arrays.fill(equals, '=');
		char[] dashes = new char[widestWidth + 3];
		Arrays.fill(dashes, '-');
		
		//sum
		
		if(sumCol){
			for (int y = 0; y < yOffset; y++) {
				MatrixCell cell = matrix.set(xOffsset + (columnsAxis == null ? 1 : columnsAxis.getPositions().size()), y, (y==0?"&#x5408;&#x8BA1;":""), false, y!=0, CellType.sumData, null, null);
				if(y==0)
					cell.rowSpan = yOffset;
			}
			for(int y=0;y<rowsAxis.getPositionCount();y++){
				int sum = 0;
				for(int x=0;x<columnsAxis.getPositionCount();x++)
					sum += ValueUtil.getIntValue(cellSet.getCell(rowsAxis.getPositions().get(x), columnsAxis.getPositions().get(y)).getValue());
				matrix.set(xOffsset + (columnsAxis == null ? 1 : columnsAxis.getPositions().size()), yOffset + y, ""+sum, false, false, CellType.sumData, null, null);
			}
		}
		
		if(sumRow){
			for (int x = 0; x < xOffsset; x++) {
				MatrixCell cell = matrix.set(x, yOffset + (rowsAxis == null ? 1 : rowsAxis.getPositions().size()), (x==0?"&#x5408;&#x8BA1;":""), false, x!=0, CellType.sumData, null, null);
				if(x==0)
					cell.colSpan = xOffsset;
			}
			for(int x=0;x<columnsAxis.getPositionCount();x++){
				int sum = 0;
				for(int y=0;y<rowsAxis.getPositionCount();y++)
					sum += ValueUtil.getIntValue(cellSet.getCell(rowsAxis.getPositions().get(x), columnsAxis.getPositions().get(y)).getValue());
				matrix.set(xOffsset+x, yOffset + (rowsAxis == null ? 1 : rowsAxis.getPositions().size()), ""+sum, false, false, CellType.sumData, null, null);
			}
		}
		if(sumCol && sumRow){
			int sum = 0;
			for(int x=0;x<columnsAxis.getPositionCount();x++)
				for(int y=0;y<rowsAxis.getPositionCount();y++)
					sum += ValueUtil.getIntValue(cellSet.getCell(rowsAxis.getPositions().get(x), columnsAxis.getPositions().get(y)).getValue());
			matrix.set(xOffsset + (columnsAxis == null ? 1 : columnsAxis.getPositions().size()), yOffset + (rowsAxis == null ? 1 : rowsAxis.getPositions().size()), ""+sum, false, false, CellType.sumData, null, null);
		}
		pw.write("<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#5F9EA0\">");
		for (int y = 0; y < matrix.height; y++) {
			pw.write("<tr"+"calss=\""+rowStyleClass+"\""+">");
			for (int x = 0; x < matrix.width; x++) {
				pw.print(matrix.getHtmlElement(x, y));
			}
			pw.write("</tr>");
		}
		pw.write("</table>");
	}

	/**
	 * Populates cells in the matrix corresponding to a particular axis.
	 *
	 * @param matrix
	 *            Matrix to populate
	 * @param axis
	 *            Axis
	 * @param axisInfo
	 *            Description of axis
	 * @param isColumns
	 *            True if columns, false if rows
	 * @param offset
	 *            Ordinal of first cell to populate in matrix
	 */
	private void populateAxis(Matrix matrix, CellSetAxis axis, AxisInfo axisInfo, boolean isColumns, int offset) {
		if (axis == null) {
			return;
		}
		MatrixCell[] prevMatrixCells = new MatrixCell[axisInfo.getWidth()];
		Member[] prevMembers = new Member[axisInfo.getWidth()];
		Member[] members = new Member[axisInfo.getWidth()];
		for (int i = 0; i < axis.getPositions().size(); i++) {
			final int x = offset + i;
			Position position = axis.getPositions().get(i);
			int yOffset = 0;
			final List<Member> memberList = position.getMembers();
			for (int j = 0; j < memberList.size(); j++) {
				Member member = memberList.get(j);
				final AxisOrdinalInfo ordinalInfo = axisInfo.ordinalInfos.get(j);
				while (member != null) {
					if (member.getDepth() < ordinalInfo.minDepth) {
						break;
					}
					final int y = yOffset + member.getDepth() - ordinalInfo.minDepth;
					members[y] = member;
					member = member.getParentMember();
				}
				yOffset += ordinalInfo.getWidth();
			}
			boolean same = true;
			for (int y = 0; y < members.length; y++) {
				Member member = members[y];
				same = same && i > 0 && Olap4jUtil.equal(prevMembers[y], member);
//				same = same && i > 0 && Olap4jUtil.equal(prevMatrixCells[y].member, member);
				Member captionMember = member!=null?member:(y-1>=0?prevMembers[y-1]:null);
				String value = captionMember == null ? "" : (captionMember.isAll() && y<members.length-1?captionMember.getHierarchy().getCaption():captionMember.getCaption());
				if (isColumns) {
					matrix.set(x, y, value, false, same, CellType.columnHeader, member, null);
					if(!same || i==0)
						prevMatrixCells[y] = matrix.get(x, y);
					if(same)prevMatrixCells[y].incColSpan();
				} else {
					if (same) {
						value = "";
					}
					matrix.set(y, x, value, false, same, CellType.rowHeader, member, null);
					if(!same || i==0)
						prevMatrixCells[y] = matrix.get(y, x);
					if(same)prevMatrixCells[y].incRowSpan();
				}
				prevMembers[y] = member;
				members[y] = null;
			}
		}
	}

	/**
	 * Computes a description of an axis.
	 *
	 * @param axis
	 *            Axis
	 * @return Description of axis
	 */
	private AxisInfo computeAxisInfo(CellSetAxis axis) {
		if (axis == null) {
			return new AxisInfo(0);
		}
		final AxisInfo axisInfo = new AxisInfo(axis.getAxisMetaData().getHierarchies().size());
		int p = -1;
		for (Position position : axis.getPositions()) {
			++p;
			int k = -1;
			for (Member member : position.getMembers()) {
				++k;
				final AxisOrdinalInfo axisOrdinalInfo = axisInfo.ordinalInfos.get(k);
				final int topDepth = member.isAll() ? member.getDepth() : member.getHierarchy().hasAll() ? 1 : 0;
				if (axisOrdinalInfo.minDepth > topDepth || p == 0) {
					axisOrdinalInfo.minDepth = topDepth;
				}
				axisOrdinalInfo.maxDepth = Math.max(axisOrdinalInfo.maxDepth, member.getDepth());
			}
		}
		return axisInfo;
	}

	/**
	 * Returns an iterator over cells in a result.
	 */
	private static Iterable<Cell> cellIter(final int[] pageCoords, final CellSet cellSet) {
		return new Iterable<Cell>() {
			public Iterator<Cell> iterator() {
				int[] axisDimensions = new int[cellSet.getAxes().size() - pageCoords.length];
				assert pageCoords.length <= axisDimensions.length;
				for (int i = 0; i < axisDimensions.length; i++) {
					CellSetAxis axis = cellSet.getAxes().get(i);
					axisDimensions[i] = axis.getPositions().size();
				}
				final CoordinateIterator coordIter = new CoordinateIterator(axisDimensions, true);
				return new Iterator<Cell>() {
					public boolean hasNext() {
						return coordIter.hasNext();
					}

					public Cell next() {
						final int[] ints = coordIter.next();
						final AbstractList<Integer> intList = new AbstractList<Integer>() {
							public Integer get(int index) {
								return index < ints.length ? ints[index] : pageCoords[index - ints.length];
							}

							public int size() {
								return pageCoords.length + ints.length;
							}
						};
						return cellSet.getCell(intList);
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	/**
	 * Description of a particular hierarchy mapped to an axis.
	 */
	private static class AxisOrdinalInfo {
		int minDepth = 1;
		int maxDepth = 0;

		/**
		 * Returns the number of matrix columns required to display this
		 * hierarchy.
		 */
		public int getWidth() {
			return maxDepth - minDepth + 1;
		}
	}

	/**
	 * Description of an axis.
	 */
	private static class AxisInfo {
		final List<AxisOrdinalInfo> ordinalInfos;

		/**
		 * Creates an AxisInfo.
		 *
		 * @param ordinalCount
		 *            Number of hierarchies on this axis
		 */
		AxisInfo(int ordinalCount) {
			ordinalInfos = new ArrayList<AxisOrdinalInfo>(ordinalCount);
			for (int i = 0; i < ordinalCount; i++) {
				ordinalInfos.add(new AxisOrdinalInfo());
			}
		}

		/**
		 * Returns the number of matrix columns required by this axis. The sum
		 * of the width of the hierarchies on this axis.
		 *
		 * @return Width of axis
		 */
		public int getWidth() {
			int width = 0;
			for (AxisOrdinalInfo info : ordinalInfos) {
				width += info.getWidth();
			}
			return width;
		}
	}

	/**
	 * Two-dimensional collection of string values.
	 */
	private class Matrix {
		private final Map<List<Integer>, MatrixCell> map = new HashMap<List<Integer>, MatrixCell>();
		private final int width;
		private final int height;

		/**
		 * Creats a Matrix.
		 *
		 * @param width
		 *            Width of matrix
		 * @param height
		 *            Height of matrix
		 */
		public Matrix(int width, int height) {
			this.width = width;
			this.height = height;
		}

		/**
		 * Sets the value at a particular coordinate
		 *
		 * @param x
		 *            X coordinate
		 * @param y
		 *            Y coordinate
		 * @param value
		 *            Value
		 */
//		void set(int x, int y, String value) {
//			set(x, y, value, false, false);
//		}

		/**
		 * Sets the value at a particular coordinate
		 *
		 * @param x
		 *            X coordinate
		 * @param y
		 *            Y coordinate
		 * @param value
		 *            Value
		 * @param right
		 *            Whether value is right-justified
		 * @param sameAsPrev
		 *            Whether value is the same as the previous value. If true,
		 *            some formats separators between cells
		 */
		MatrixCell set(int x, int y, String value, boolean right, boolean sameAsPrev, CellType type, Member member, List<Integer> coordinates) {
			MatrixCell cell = new MatrixCell(value, right, sameAsPrev, type, member, coordinates);
			map.put(Arrays.asList(x, y), cell);
			assert x >= 0 && x < width : x;
			assert y >= 0 && y < height : y;
			return cell;
		}

		/**
		 * Returns the cell at a particular coordinate.
		 *
		 * @param x
		 *            X coordinate
		 * @param y
		 *            Y coordinate
		 * @return Cell
		 */
		public MatrixCell get(int x, int y) {
			return map.get(Arrays.asList(x, y));
		}
		String getHtmlElement(int x,int y){
			MatrixCell cell = get(x,y);
			if(cell==null)
				return "<td>&nbsp;</td>";
			if(cell.sameAsPrev)return "";
			String style = "class=\""+cellStyleClass+"\"";
			if(cell.type==CellType.corner)
				style = "class=\""+cornerStyleClass+"\"";
			if(cell.type==CellType.columnHeader)
				style = "class=\""+columnHeaderStyleClass+"\"";
			if(cell.type==CellType.rowHeader)
			style = "class=\""+rowHeaderStyleClass+"\"";
			if(cell.type==CellType.sumData)
			style = "class=\""+sumStyleClass+"\"";
			
//			设置单元格字体颜色，针对不同的报表，此类需单独实现
			String fontColor = cell.value;
//			if(x==1){//测试：只对第一个内容单元格设置字体为红色
//				fontColor = "<font color=\"#FF0000\">" + cell.value + "</font>";
//			}
			
			return "<td " + style + " " + (cell.rowSpan > 1 ? " rowspan=\"" + cell.rowSpan + "\"" : "")
					+ (cell.colSpan > 1 ? " colspan=\"" + cell.colSpan + "\"" : "") + ">"
					+ (StringUtils
							.isEmpty(cell.value)
									? "&nbsp;"
									: (cell.type == CellType.data ? "<a href=\"#\" onclick=\"drillThrough('"
											+ cell.ordinal + "');return false\">" + fontColor + "</a>" : cell.value))
					+ "</td>";
		}
	}

	/**
	 * Contents of a cell in a matrix.
	 */
	private static class MatrixCell {
		final Member member;
		final String value;
		final boolean right;
		final boolean sameAsPrev;
		final CellType type;
		final String ordinal;
		int rowSpan = 1;
		int colSpan = 1;

		/**
		 * Creates a matrix cell.
		 *
		 * @param value
		 *            Value
		 * @param right
		 *            Whether value is right-justified
		 * @param sameAsPrev
		 *            Whether value is the same as the previous value. If true,
		 *            some formats separators between cells
		 */
		MatrixCell(String value, boolean right, boolean sameAsPrev, CellType type, Member member, List<Integer> coordinates) {
			this.value = value;
			this.right = right;
			this.sameAsPrev = sameAsPrev;
			this.type = type;
			this.member = member;
			if(coordinates!=null)
				this.ordinal = String.format("%d,%d", coordinates.get(0), coordinates.get(1));
			else
				this.ordinal = "";
		}
		void incRowSpan(){
			rowSpan++;
		}
		void incColSpan(){
			colSpan++;
		}
		void setSpan(int rowSpan, int colSpan){
			this.rowSpan = rowSpan;
			this.colSpan = colSpan;
		}
	}
    public enum CellType {
        corner, columnHeader, rowHeader, data, sumData;
    }
}

// End RectangularCellSetFormatter.java
