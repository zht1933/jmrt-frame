package com.webside.cube.util;

import java.io.PrintWriter;

import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.Position;
import org.olap4j.metadata.Member;

public class EChartsCellSetFormatter {
	public void format(CellSet cellSet, PrintWriter pw, boolean sumCol, boolean sumRow) {
		CellSetAxis colAxis = cellSet.getAxes().get(0);
		CellSetAxis rowAxis = cellSet.getAxes().get(1);
		pw.write("<script type=\"text/javascript\">");
		pw.write("var echartOption = {");
		pw.write("tooltip : {");
		pw.write("trigger: 'axis'");
		pw.write("},");
		pw.write("toolbox: {");
		pw.write("orient : 'vertical',");
		pw.write("y : 'center',");
		pw.write("show : true,");
		pw.write("feature : {");
		pw.write("magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},");
		pw.write("}");
		pw.write("},");
		pw.write("legend: {data:[");
		boolean isFirst = true;
		for(Position p:rowAxis.getPositions()){
			if(!isFirst)
				pw.write(",");
			isFirst = false;	
			String c = "";
			for(Member m:p.getMembers()){
				c=m.getCaption();
			}
			pw.write("'"+c+"'");
		}
		pw.write("]},");
		pw.write("xAxis: [{ type: 'category', splitLine: { show: false }, data: [");
		boolean firstCol = true;
		for(Position p:colAxis.getPositions()){
			if(!firstCol)
				pw.write(",");
			String c = "";
			for(Member m:p.getMembers()){
				c = m.getCaption();
			}
			pw.write("'"+c+"'");
			firstCol = false;
		}
		pw.write("]}],");
		pw.write("yAxis: [{ type: 'value' }],");
		pw.write("series: [");
		isFirst = true;
		for(Position p:rowAxis.getPositions()){
			if(!isFirst)
				pw.write(",");
			isFirst = false;	
			pw.write("{");
			String c = "";
			for(Member m:p.getMembers()){
				c=m.getCaption();
			}
			pw.write("name: '"+c+"', type: 'line', stack: '数量',");
//			pw.write("itemStyle: { normal: { barBorderColor: 'rgba(0,0,0,0)', color: 'rgba(0,0,0,0)' }, emphasis: { barBorderColor: 'rgba(0,0,0,0)', color: 'rgba(0,0,0,0)' } },");
			pw.write("data: [");
			firstCol = true;
			for(Position pX:colAxis.getPositions()){
				int v = cellSet.getCell(pX,p)==null?0:ValueUtil.getIntValue(cellSet.getCell(pX,p).getValue());
				pw.write(firstCol?""+v:","+v);
				firstCol = false;
			}
			pw.write("]");
			pw.write("}");
		}
		pw.write("]}");
		pw.write("</script>");
	}
}
