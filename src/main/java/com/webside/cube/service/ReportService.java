package com.webside.cube.service;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.OlapDataSource;
import org.pivot4j.PivotModel;
import org.pivot4j.datasource.SimpleOlapDataSource;
import org.pivot4j.impl.PivotModelImpl;
import org.pivot4j.transform.NonEmpty;
import org.pivot4j.ui.poi.ExcelExporter;
import org.pivot4j.ui.table.TableRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.webside.cube.model.GridData;
import com.webside.cube.util.DbUtil;
import com.webside.cube.util.EChartsCellSetFormatter;
import com.webside.cube.util.HtmlCellSetFormatter;
import com.webside.cube.util.ListHandler;
import com.webside.cube.util.OlapUtil;
import com.webside.cube.util.ValueUtil;

@Service
public class ReportService {
	@Resource(name = "dbUtil")
	private DbUtil dbUtil;

	@Value("#{classReadConfigProperties['mondrian.Jdbc']}")
	public String jdbcUrl;//="jdbc:oracle:thin:ys/sa@127.0.0.1:1521:orcl";

	// zht 通过配置文件获取数据库连接
	private OlapDataSource getDataSource(String catelog) throws ClassNotFoundException {
		String url = "jdbc:mondrian:Jdbc=" + jdbcUrl + ";Catalog=" + catelog + ";";
		Class.forName("mondrian.olap4j.MondrianOlap4jDriver");
		SimpleOlapDataSource dataSource = new SimpleOlapDataSource();
		dataSource.setConnectionString(url);
		return dataSource;
	}

	// zht 查询已保存的立方体报表
	private PivotModel getModel(String catelog, String reportid, String mdx) throws Exception {
		String strMdx = mdx;
		if (StringUtils.isEmpty(mdx)) {
			QueryRunner qr = dbUtil.getQueryRunner();
			strMdx = qr.query("select mdx from cwy_report where id=?", new ScalarHandler<String>(), reportid);
		}
		PivotModel model = new PivotModelImpl(getDataSource(catelog));
		model.setMdx(strMdx);
		model.initialize();

		NonEmpty transform = model.getTransform(NonEmpty.class);
		transform.setNonEmpty(true);
		return model;
	}

	// zht 通过Schema获取立方体
	public List<Map<String, Object>> getCubes(String catelog) throws ClassNotFoundException, SQLException {
		OlapConnection conn = null;
		try {
			conn = getDataSource(catelog).getConnection();
			return OlapUtil.getCubes(conn);
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	// zht 获取立方体的维度和度量
	public List<Map<String, Object>> getCubeDatas(String catelog, String cubeId)
			throws ClassNotFoundException, SQLException {
		OlapConnection conn = null;
		try {
			conn = getDataSource(catelog).getConnection();
			return OlapUtil.getCubeDatas(conn, cubeId);
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	// zht 获取维度成员
	public List<Map<String, Object>> getMembers(String catelog, String cubeName, String dimensionName,
			String hierarchyName, String levelName) throws ClassNotFoundException, SQLException {
		OlapConnection conn = null;
		try {
			conn = getDataSource(catelog).getConnection();
			return OlapUtil.getMembers(conn, cubeName, dimensionName, hierarchyName, levelName);
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	// zht 获取已保存的立方体表格数据
	public List<Map<String, Object>> getDatas(String catelog, String reportid, String mdx) throws Exception {
		PivotModel model = getModel(catelog, reportid, mdx);
		return OlapUtil.cellSetToList(model.getCellSet());
	}

	// zht 下载表格查询语句
	public GridData getDrillthroughDatas(String catelog, String reportid, String mdx, String ordinal, int start,
			int limit) throws Exception {
		CellSet cellSet = getModel(catelog, reportid, mdx).getCellSet();
		String sql = null;
		String temp = cellSet.getMetaData().getCube().getName();
		if ("USER_CUB".equals(cellSet.getMetaData().getCube().getName()))
			sql = "SELECT  t.u_id,t.u_name,t.u_description,t.u_creator_name FROM tb_user t  "
					+ " where t.u_id in (";
		
		String[] v = StringUtils.split(ordinal, ",");
		List<Integer> coordinates = new ArrayList<>();
		coordinates.add(ValueUtil.parseInt(v[0]));
		coordinates.add(ValueUtil.parseInt(v[1]));
		Cell cell = cellSet.getCell(coordinates);
		ResultSet rs = cell.drillThrough();
		List<String> ids = new ArrayList<>();
		
		int count = 0;
		while (rs.next()) {
			if (start <= count && count < start + limit) {
				String str = "'" + rs.getString("amount") + "'";
					ids.add(str);
			}
			count++;
		}
		rs.close();
		QueryRunner qr = dbUtil.getQueryRunner();
		List<Map<String, Object>> datas = qr.query(sql + StringUtils.join(ids, ",") + ")", new ListHandler());
		return new GridData(datas, count);
	}

	// zht 获取立方体表格数据 ，并设置下钻表格类型
	public void writeHtml(PrintWriter writer, String catelog, String reportid, String mdx, boolean sumCol,
			boolean sumRow, boolean isChart) throws Exception {
		CellSet cellSet = getModel(catelog, reportid, mdx).getCellSet();
		String panelName = "";
		// zht 自定义下载显示的数据表格
		if ("USER_CUB".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "cube.cubeDown.WinUSERGrid";
		writer.write("<script type=\"text/javascript\">");
		writer.write("var getDrillThroughWindow = function(){");
		writer.write("	return '" + panelName + "';");
		writer.write("}");
		writer.write("</script>");
		if (!isChart) {
			HtmlCellSetFormatter formatter = new HtmlCellSetFormatter();// 输出html页面信息（可修改表格样式）
			formatter.format(cellSet, writer, sumCol, sumRow);
		} else {
			EChartsCellSetFormatter formatter = new EChartsCellSetFormatter();// 输出EChart图页面信息
			formatter.format(cellSet, writer, sumCol, sumRow);
		}
	}

	// zht 将立方体报表导出到excel中
	public void writeExcel(ServletOutputStream out, String catelog, String reportid, String mdx) throws Exception {

		TableRenderer renderer = new TableRenderer();

		renderer.setShowDimensionTitle(false); // Optionally hide the dimension
												// title headers.
		renderer.setShowParentMembers(false); // Optionally make the parent
												// members visible.

		renderer.render(getModel(catelog, reportid, mdx), new ExcelExporter(out)); // Render
																					// the
																					// result
																					// as
																					// a
																					// HTML
																					// page.
	}

//	查询报表
	public List<Map<String, Object>> getUniversalReports(String flag,String userid) throws Exception{
		QueryRunner qr = dbUtil.getQueryRunner();
		return qr.query("SELECT t.*  FROM v_cwy_report t where t.parentid=? and t.userid=? and t.flag = ? ", new ListHandler(), "0",userid,flag);
	}
}
