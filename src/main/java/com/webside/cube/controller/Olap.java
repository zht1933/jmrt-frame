package com.webside.cube.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.webside.cube.model.GridData;
import com.webside.cube.model.Result;
import com.webside.cube.service.ReportService;
import com.webside.cube.util.DbUtil;
import com.webside.cube.util.FilterUtil;
import com.webside.cube.util.ListHandler;
import com.webside.cube.util.ShiroUtil;
import com.webside.cube.util.UUIDUtil;
import com.webside.cube.util.ValueUtil;
import com.webside.user.model.UserEntity;

@Controller
@RequestMapping({ "/olap" })
public class Olap {
	@Resource(name = "dbUtil")
	private DbUtil dbUtil;

	@Resource(name = "reportService")
	private ReportService reportService;

	@Resource(name = "shiroUtil")
	private ShiroUtil shiroUtil;

	@Value("#{classReadConfigProperties['Schema.Path']}")
	public String SchemaPath;

	// zht 通过用户id查询用户保存的立方体表格
	@RequestMapping("/getReportType")
	@ResponseBody
	public GridData getReportType() throws SQLException {
		QueryRunner qr = dbUtil.getQueryRunner();
		List<Map<String, Object>> apps = qr.query(
				"select id,title name,sumcol,sumrow,moduleseparate from rp_report where userid=? order by id",
				new ListHandler(), shiroUtil.getLoginUserId());
		return new GridData(apps);
	}

	// zht 获取立方体
	@RequestMapping("/getCubes.html")
	@ResponseBody
	public List<Map<String, Object>> getCubes(HttpServletRequest req) throws SQLException {
		try {
			String catelog = req.getServletContext().getResource(SchemaPath).toString();
			List<Map<String, Object>> cubes = reportService.getCubes(catelog);
			return cubes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// zht 加载立方体的维度和度量
	@RequestMapping("/getCubeDatas.html")
	@ResponseBody
	public List<Map<String, Object>> getCubeDatas(HttpServletRequest req, String cubeid) throws SQLException {
		try {
			String catelog = req.getServletContext().getResource(SchemaPath).toString();
			return reportService.getCubeDatas(catelog, cubeid);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// zht 获取维度成员
	@RequestMapping("/getMembers.html")
	@ResponseBody
	public GridData getMembers(HttpServletRequest req, String cubename, String dimensionname, String hierarchyname,
			String levelname) throws SQLException {
		try {
			String catelog = req.getServletContext().getResource(SchemaPath).toString();
			return new GridData(reportService.getMembers(catelog, cubename, dimensionname, hierarchyname, levelname));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// zht 获取已保存的立方体表格数据
	@RequestMapping("/getDatas")
	@ResponseBody
	public List<Map<String, Object>> getDatas(HttpServletRequest req, HttpServletResponse res, String reportid,
			String mdx) throws IOException {
		String catelog = req.getServletContext().getResource(SchemaPath).toString();
		try {
			List<Map<String, Object>> datas = reportService.getDatas(catelog, reportid, mdx);
			return datas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// zht 获取立方体表格数据
	@RequestMapping("/reportHtml.html")
	public void reportHtml(HttpServletRequest req, HttpServletResponse res, String reportid, String mdx, boolean sumCol,
			boolean sumRow, boolean isChart) throws IOException {
		PrintWriter out = null;
		try {
			String catelog = req.getServletContext().getResource(SchemaPath).toString();
			res.setContentType("text/html; charset=utf-8");
			out = res.getWriter();
			reportService.writeHtml(out, catelog, reportid, mdx, sumCol, sumRow, isChart);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			;
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	// zht 查询下钻数据
	@RequestMapping("/drillThrough.html")
	@ResponseBody
	public GridData drillThrough(HttpServletRequest req, String filter, int start, int limit, String schema)
			throws IOException {
		try {
			if (StringUtils.isEmpty(schema)) {
				Map<String, Object> filters = FilterUtil.getFilter(filter);
				String reportid = ValueUtil.getStringValue(filters.get("reportid"));
				String mdx = ValueUtil.getStringValue(filters.get("mdx"));
				String ordinal = ValueUtil.getStringValue(filters.get("ordinal"));
				String catelog = req.getServletContext().getResource(SchemaPath).toString();
				return reportService.getDrillthroughDatas(catelog, reportid, mdx, ordinal, start, limit);
			} else {
				Map<String, Object> filters = FilterUtil.getFilter(filter);
				String reportid = ValueUtil.getStringValue(filters.get("reportid"));
				String mdx = ValueUtil.getStringValue(filters.get("mdx"));
				String ordinal = ValueUtil.getStringValue(filters.get("ordinal"));
				String catelog = req.getServletContext().getResource("/data/" + schema + ".xml").toString();
				return reportService.getDrillthroughDatas(catelog, reportid, mdx, ordinal, start, limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// zht 立方体分析报表导出到excel
	@RequestMapping("/reportExcel.html")
	public void reportExcel(HttpServletRequest req, HttpServletResponse res, String reportid, String mdx)
			throws IOException {
		ServletOutputStream os = res.getOutputStream();
		try {
			String catelog = req.getServletContext().getResource(SchemaPath).toString();
			res.reset();
			res.setHeader("Content-Disposition", "attachment; filename=report.xls");
			reportService.writeExcel(os, catelog, reportid, mdx);
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
			;
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}

	// zht 保存立方体分析报表到rp_report数据库表中
	@RequestMapping("/saveReport.html")
	@ResponseBody
	public Result saveReport(HttpServletRequest req, HttpServletResponse res, String name, String mdx, boolean sumcol,
			boolean sumrow, String flag) throws IOException {
		try {
			// 通过session回话获取当前登录用户的信息
			UserEntity userEntity = (UserEntity) req.getSession().getAttribute("defUserEntity");

			QueryRunner qr = dbUtil.getQueryRunner();
			String id = UUIDUtil.getId();
			String userid = userEntity.getId().toString();
			qr.update("insert into rp_report (id, title, mdx, userid, sumcol, sumrow,flag) values (?,?,?,?,?,?,?)", id,
					name, mdx, userid, sumcol ? 1 : 0, sumrow ? 1 : 0, flag);
			return new Result(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false);
		}
	}

	// zht 从rp_report数据库表中删除立方体分析报表
	@RequestMapping("/deleteReport.html")
	@ResponseBody
	public Result deleteReport(HttpServletRequest req, HttpServletResponse res, String reportid) throws IOException {
		try {
			QueryRunner qr = dbUtil.getQueryRunner();
			qr.update("delete from rp_report where id ='" + reportid+"'");
			return new Result(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false);
		}
	}

//	查询报表  zht 2016年9月28日
	@RequestMapping("/getUniversalReports.html")
	@ResponseBody
	public GridData getUniversalReports(HttpServletRequest req,String id){
		try {
			// 通过session回话获取当前登录用户的信息
			UserEntity userEntity = (UserEntity) req.getSession().getAttribute("defUserEntity");
			String userid = userEntity.getId().toString();
			GridData gd = new GridData(reportService.getUniversalReports(id,userid));
			return gd;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// likai 拆分立方体 获取独立的立方体
	@RequestMapping("/getCubesSeparate")
	@ResponseBody
	public List<Map<String, Object>> getCubesSeparate(HttpServletRequest req, String schema) throws SQLException {
		try {
			if (StringUtils.isEmpty(schema))
				return null;
			String catelog = req.getServletContext().getResource("/data/" + schema + ".xml").toString();
			return reportService.getCubes(catelog);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// likai加载独立的立方体的维度和度量
	@RequestMapping("/getCubeDatasSeparate")
	@ResponseBody
	public List<Map<String, Object>> getCubeDatasSeparate(HttpServletRequest req, String schema, String cubeid)
			throws SQLException {
		try {
			if (StringUtils.isEmpty(schema))
				return null;
			String catelog = req.getServletContext().getResource("/data/" + schema + ".xml").toString();
			return reportService.getCubeDatas(catelog, cubeid);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// likai 获取独立立方体的维度成员
	@RequestMapping("/getMembersSeparate")
	@ResponseBody
	public GridData getMembersSeparate(HttpServletRequest req, String schema, String cubename, String dimensionname,
			String hierarchyname, String levelname) throws SQLException {
		try {
			if (StringUtils.isEmpty(schema))
				return null;
			String catelog = req.getServletContext().getResource("/data/" + schema + ".xml").toString();
			return new GridData(reportService.getMembers(catelog, cubename, dimensionname, hierarchyname, levelname));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// likai获取已保存的独立立方体表格数据
	@RequestMapping("/getDatasSeparate")
	@ResponseBody
	public List<Map<String, Object>> getDatasSeparate(HttpServletRequest req, HttpServletResponse res, String schema,
			String reportid, String mdx) throws IOException {
		if (StringUtils.isEmpty(schema))
			return null;
		String catelog = req.getServletContext().getResource("/data/" + schema + ".xml").toString();
		try {
			List<Map<String, Object>> datas = reportService.getDatas(catelog, reportid, mdx);
			return datas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// likai 获取独立立方体表格数据
	@RequestMapping("/reportHtmlSeparate.html")
	public void reportHtmlSeparate(HttpServletRequest req, HttpServletResponse res, String schema, String reportid,
			String mdx, boolean sumCol, boolean sumRow, boolean isChart) throws IOException {
		if (StringUtils.isEmpty(schema))
			return;
		PrintWriter out = null;
		try {
			String catelog = req.getServletContext().getResource("/data/" + schema + ".xml").toString();
			res.setContentType("text/html; charset=utf-8");
			out = res.getWriter();
			reportService.writeHtml(out, catelog, reportid, mdx, sumCol, sumRow, isChart);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			;
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	// likai 查询独立立方体的下钻数据
	@RequestMapping("/drillThroughSeparate")
	@ResponseBody
	public GridData drillThroughSeparate(HttpServletRequest req, String schema, String filter, int start, int limit)
			throws IOException {
		if (StringUtils.isEmpty(schema))
			return null;
		try {
			Map<String, Object> filters = FilterUtil.getFilter(filter);
			String reportid = ValueUtil.getStringValue(filters.get("reportid"));
			String mdx = ValueUtil.getStringValue(filters.get("mdx"));
			String ordinal = ValueUtil.getStringValue(filters.get("ordinal"));
			String catelog = req.getServletContext().getResource("/data/" + schema + ".xml").toString();
			return reportService.getDrillthroughDatas(catelog, reportid, mdx, ordinal, start, limit);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// likai 独立立方体分析报表导出到excel
	@RequestMapping("/reportExcelSeparate")
	public void reportExcelSeparate(HttpServletRequest req, HttpServletResponse res, String schema, String reportid,
			String mdx) throws IOException {
		if (StringUtils.isEmpty(schema))
			return;
		ServletOutputStream os = res.getOutputStream();
		try {
			String catelog = req.getServletContext().getResource("/data/" + schema + ".xml").toString();
			res.reset();
			res.setHeader("Content-Disposition", "attachment; filename=report.xls");
			reportService.writeExcel(os, catelog, reportid, mdx);
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
			;
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}

	// likai 保存独立立方体分析报表到rp_report数据库表中
	@RequestMapping("/saveReportSeparate.html")
	@ResponseBody
	public Result saveReportSeparate(HttpServletRequest req, HttpServletResponse res, String name, String mdx,
			boolean sumcol, boolean sumrow, String moduleseparate, String flag) throws IOException {
		try {
			QueryRunner qr = dbUtil.getQueryRunner();
			long id = dbUtil.getSeq("rp_report");
			String userid = shiroUtil.getLoginUserId();
			qr.update(
					"insert into rp_report (id, title, mdx, userid, sumcol, sumrow,moduleseparate,flag) values (?,?,?,?,?,?,?,?)",
					id, name, mdx, userid, sumcol ? 1 : 0, sumrow ? 1 : 0, moduleseparate, flag);
			return new Result(true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false);
		}
	}
}
