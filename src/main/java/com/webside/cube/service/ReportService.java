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

	@Value("#{classReadConfigProperties['jdbc_url']}")
	public String jdbcUrl;//="jdbc:oracle:thin:ys/sa@127.0.0.1:1521:orcl";

	@Value("#{classReadConfigProperties['jdbc_username']}")
	public String userName;

	@Value("#{classReadConfigProperties['jdbc_password']}")
	public String password;

	// zht 通过配置文件获取数据库连接
	private OlapDataSource getDataSource(String catelog) throws ClassNotFoundException {
		String url = "jdbc:mondrian:Jdbc=" + jdbcUrl + ";Catalog=" + catelog + ";";
		Class.forName("mondrian.olap4j.MondrianOlap4jDriver");
		SimpleOlapDataSource dataSource = new SimpleOlapDataSource();
		dataSource.setUserName(userName);
		dataSource.setPassword(password);
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
		if ("CubeWorkTime".equals(cellSet.getMetaData().getCube().getName()))
			sql = "SELECT b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       a.sjbdxxb_bdrq, "
					+ "       a.sjbdxxb_jiec, " + "       a.sjbdxxb_jiaoc, "
					+ "       DECODE(ROUND(TO_NUMBER(a.sjbdxxb_jiaoc - a.sjbdxxb_jiec) * 24), " + "              null, "
					+ "              0, "
					+ "              ROUND(TO_NUMBER(a.sjbdxxb_jiaoc - a.sjbdxxb_jiec) * 24)) as single_way_work_time, "
					+ "       CASE " + "         WHEN (TO_NUMBER(to_char(a.sjbdxxb_jiec, 'hh24')) >= 20 or "
					+ "              TO_NUMBER(to_char(a.sjbdxxb_jiec, 'hh24')) <= 6) then " + "          '夜班' "
					+ "         else " + "          '白班' " + "       end as work_type "
					+ "  FROM cwy_sjbdxxb a, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z "
					+ " where b.cwy_ry0id = a.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and a.SFYX = '1' and a.sjbdxxb_id in (";

		if ("Cube LWInfo".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       dj.cd_dmmc rylwxxb_lwdj, "
					+ "       lx.cd_dmmc rylwxxb_lwlb, " + "       cd.cd_dmmc rylwxxb_lwxd, "
					+ "       t.rylwxxb_lwsj, " + "       t.rylwxxb_lwdd, " + "       CASE "
					+ "         WHEN (TO_NUMBER(to_char(t.RYLWXXB_LWSJ, 'hh24')) >= 20 or "
					+ "              TO_NUMBER(to_char(t.RYLWXXB_LWSJ, 'hh24')) <= 6) then " + "          '夜班' "
					+ "         else " + "          '白班' " + "       end as work_type " + "  from CWY_RYLWXXB t, "
					+ "       cwy_rydyb   b, " + "       cwy_ryjbxxb j, " + "       v_zzjg      z, "
					+ "       CD_LWXD     cd, " + "       CD_LWLX     lx, " + "       cd_lwdj     dj "
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and cd.cd_dmz = t.rylwxxb_lwxd "
					+ "   and lx.cd_dmz = t.rylwxxb_lwlb " + "   and dj.cd_dmz = t.rylwxxb_lwdj "
					+ "   and t.SFYX = '1' and t.rylwxxb_id in (";

		if ("SxszCube".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       t.rysxszxxb_khrq, "
					+ "       t.rysxszxxb_khsm, " + "       t.rysxszxxb_khcj "
					+ "  from CWY_RYSXSZXXB t, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z "
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and j.cwy_ry0id = t.cwy_ry0id "
					+ "   and z.cd_dmz = j.ryjbxxb_ryzzjg " + "   and t.SFYX = '1' and RYSXSZXXB_ID in (";

		if ("TjxxCube".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       x.cd_dmmc xb, "
					+ "       t.rytjxxb_tjsj, " + "       t.rytjxxb_tjdd, " + "       jk.cd_dmmc rytjxxb_stztzk, "
					+ "       t.rytjxxb_sg, " + "       t.rytjxxb_tz, " + "       tl.cd_dmmc rytjxxb_tldj, "
					+ "       sl.cd_dmmc rytjxxb_sldj, "
					+ "       decode(t.rytjxxb_gxy, '', '无', t.rytjxxb_gxy) rytjxxb_gxy, "
					+ "       decode(t.rytjxxb_xzb, '', '无', t.rytjxxb_xzb) rytjxxb_xzb, "
					+ "       decode(t.rytjxxb_tnb, '', '无', t.rytjxxb_tnb) rytjxxb_tnb, "
					+ "       decode(t.rytjxxb_dx, '', '无', t.rytjxxb_dx) rytjxxb_dx, "
					+ "       decode(t.rytjxxb_gg, '', '正常', t.rytjxxb_gg) rytjxxb_gg, "
					+ "       decode(t.rytjxxb_xt, '', '正常', t.rytjxxb_xt) rytjxxb_xt " + "  from cwy_rytjxxb t, "
					+ "       cwy_rydyb   b, " + "       cwy_ryjbxxb j, " + "       v_zzjg      z, "
					+ "       cd_xb       x, " + "       cd_jkzk     jk, " + "       cd_sldj     sl, "
					+ "       cd_tldj     tl " + " where t.cwy_ry0id = b.cwy_ry0id "
					+ "   and t.cwy_ry0id = j.cwy_ry0id " + "   and j.sfyx = '1' "
					+ "   and z.cd_dmz = j.ryjbxxb_ryzzjg " + "   and j.ryjbxxb_xb = x.cd_dmz "
					+ "   and jk.cd_dmz = t.rytjxxb_stztzk " + "   and sl.cd_dmz = t.rytjxxb_sldj "
					+ "   and tl.cd_dmz = t.rytjxxb_tldj " + "   and t.rytjxxb_id in (";
		if ("SrxxCube".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       t.rysrxxb_bdrq, "
					+ "       t.rysrxxb_cc, " + "       t.rysrxxb_yxqd, " + "       t.rysrxxb_yxsj, "
					+ "       t.rysrxxb_zxgl, " + "       t.rysrxxb_zdgl, " + "       t.rysrxxb_sr, "
					+ "       t.rysrxxb_jc " + "  from CWY_RYSRXXB t, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z "
					+ " where b.cwy_ry0id = t.cwy_ry0id "
					+ "   and b.cwy_ry0id = j.cwy_ry0id and z.cd_dmz=j.ryjbxxb_ryzzjg" + "   and t.RYSRXXB_ID in (";
		if ("SzjfCube".equals(cellSet.getMetaData().getCube().getName()))
				sql ="select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       t.ryszjfxxb_kfrq, "
						+ "       t.ryszjfxxb_kfz, " + "       t.ryszjfxxb_kfsm "
						+ "  from CWY_RYSZJFXXB t, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z "
						+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
						+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and t.SFYX = '1' and t.ryszjfxxb_id in (";
		if ("CWY_SZJFYCub".equals(cellSet.getMetaData().getCube().getName()))	
				sql ="select t.* from ( " +
								"select rownum rn, b.cwy_ry0xm xm, " + 
								"       z.cd_dmmc zzjg, " + 
								"       jf.KFRQ||'月度' ryszjfxxb_kfrq, " + 
								"       sum(jf.RYSZJFXXB_KFZ) ryszjfxxb_kfz, " + 
								"       case " + 
								"         when sum(jf.RYSZJFXXB_KFZ) <= 30 then " + 
								"          '扣分小于30分' " + 
								"         when sum(jf.RYSZJFXXB_KFZ) <= 50 then " + 
								"          '扣分30-50分' " + 
								"         when sum(jf.RYSZJFXXB_KFZ) <= 80 then " + 
								"          '扣分50-80分' " + 
								"         when sum(jf.RYSZJFXXB_KFZ) <= 100 then " + 
								"          '扣分80-100分' " + 
								"         when sum(jf.RYSZJFXXB_KFZ) <= 150 then " + 
								"          '扣分100-150分' " + 
								"         when sum(jf.RYSZJFXXB_KFZ) <= 200 then " + 
								"          '扣分150-200分' " + 
								"         when sum(jf.RYSZJFXXB_KFZ) <= 250 then " + 
								"          '扣分200-250分' " + 
								"         else " + 
								"          '扣分接近300分' " + 
								"       end ryszjfxxb_kfsm, " + 
								"       jf.CWY_RY0ID " + 
								"  from (select b.ryjbxxb_xb, " + 
								"               b.ryjbxxb_mz, " + 
								"               b.ryjbxxb_zzmm, " + 
								"               b.ryjbxxb_jkqk, " + 
								"               b.ryjbxxb_hyqk, " + 
								"               b.ryjbxxb_gbgrbs, " + 
								"               b.ryjbxxb_ryzzjg as ZZJG, " + 
								"               t.CWY_RY0ID, " + 
								"               to_char(t.RYSZJFXXB_KFRQ, 'yyyyMM') KFRQ, " + 
								"               t.RYSZJFXXB_KFZ " + 
								"          from CWY_RYSZJFXXB t " + 
								"          join cwy_ryjbxxb b on b.cwy_ry0id = t.cwy_ry0id " + 
								"         where t.SFYX = '1') jf, " + 
								"       cwy_rydyb b, " + 
								"       cwy_ryjbxxb j, " + 
								"       v_zzjg z " + 
								" where b.cwy_ry0id = jf.cwy_ry0id " + 
								"   and b.cwy_ry0id = j.cwy_ry0id " + 
								"   and j.ryjbxxb_ryzzjg = z.cd_dmz " + 
								" group by jf.KFRQ, jf.CWY_RY0ID, z.cd_dmmc, b.cwy_ry0xm,rownum) t where t.rn in (";
		if ("CWY_SZJFQCub".equals(cellSet.getMetaData().getCube().getName()))	
			sql ="select t.* from ( " +
					"select rownum rn, b.cwy_ry0xm xm, " + 
					"       z.cd_dmmc zzjg, " + 
					"       jf.KFRQ||'季度' ryszjfxxb_kfrq, " + 
					"       sum(jf.RYSZJFXXB_KFZ) ryszjfxxb_kfz, " + 
					"       case " + 
					"         when sum(jf.RYSZJFXXB_KFZ) <= 30 then " + 
					"          '扣分小于30分' " + 
					"         when sum(jf.RYSZJFXXB_KFZ) <= 50 then " + 
					"          '扣分30-50分' " + 
					"         when sum(jf.RYSZJFXXB_KFZ) <= 80 then " + 
					"          '扣分50-80分' " + 
					"         when sum(jf.RYSZJFXXB_KFZ) <= 100 then " + 
					"          '扣分80-100分' " + 
					"         when sum(jf.RYSZJFXXB_KFZ) <= 150 then " + 
					"          '扣分100-150分' " + 
					"         when sum(jf.RYSZJFXXB_KFZ) <= 200 then " + 
					"          '扣分150-200分' " + 
					"         when sum(jf.RYSZJFXXB_KFZ) <= 250 then " + 
					"          '扣分200-250分' " + 
					"         else " + 
					"          '扣分接近300分' " + 
					"       end ryszjfxxb_kfsm, " + 
					"       jf.CWY_RY0ID " + 
					"  from (select b.ryjbxxb_xb, " + 
					"               b.ryjbxxb_mz, " + 
					"               b.ryjbxxb_zzmm, " + 
					"               b.ryjbxxb_jkqk, " + 
					"               b.ryjbxxb_hyqk, " + 
					"               b.ryjbxxb_gbgrbs, " + 
					"               b.ryjbxxb_ryzzjg as ZZJG, " + 
					"               t.CWY_RY0ID, " + 
					"               to_char(t.RYSZJFXXB_KFRQ, 'yyyyQ') KFRQ, " + 
					"               t.RYSZJFXXB_KFZ " + 
					"          from CWY_RYSZJFXXB t " + 
					"          join cwy_ryjbxxb b on b.cwy_ry0id = t.cwy_ry0id " + 
					"         where t.SFYX = '1') jf, " + 
					"       cwy_rydyb b, " + 
					"       cwy_ryjbxxb j, " + 
					"       v_zzjg z " + 
					" where b.cwy_ry0id = jf.cwy_ry0id " + 
					"   and b.cwy_ry0id = j.cwy_ry0id " + 
					"   and j.ryjbxxb_ryzzjg = z.cd_dmz " + 
					" group by jf.KFRQ, jf.CWY_RY0ID, z.cd_dmmc, b.cwy_ry0xm,rownum) t where t.rn in (";

		if ("JypxCube".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       t.ryjypxxxb_xxqssj, "
					+ "       t.ryjypxxxb_xxjssj, " + "       t.ryjypxxxb_zbdw, " + "       t.ryjypxxxb_hdzs, "
					+ "       xz.cd_dmmc ryjypxxxb_jypxxz, " + "       fs.cd_dmmc ryjypxxxb_xxfs, "
					+ "       t.ryjypxxxb_pxnr, " + "       t.ryjypxxxb_jxpxcbdw, "
					+ "       hg.cd_dmmc ryjypxxxb_sfhg " + "  from CWY_RYJYPXXXB t, " + "       cwy_rydyb     b, "
					+ "       cwy_ryjbxxb   j, " + "       v_zzjg        z, " + "       cd_jypxxz     xz, "
					+ "       cd_xxfs       fs, " + "       cd_sfhg       hg " + " where b.cwy_ry0id = t.cwy_ry0id "
					+ "   and b.cwy_ry0id = j.cwy_ry0id " + "   and j.ryjbxxb_ryzzjg = z.cd_dmz "
					+ "   and xz.cd_dmz = t.ryjypxxxb_jypxxz " + "   and t.ryjypxxxb_xxfs = fs.cd_dmz "
					+ "   and hg.cd_dmz = t.ryjypxxxb_sfhg " + "   and t.SFYX = '1' and t.ryjypxxxb_id in (";
		if ("cwy_ryjnjdxxbCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       zs.cd_dmmc ryjnjdxxb_zjlb, "
					+ "       zy.cd_dmmc ryjnjdxxb_zy, " + "       js.cd_dmmc ryjnjdxxb_jsdj, "
					+ "       t.ryjnjdxxb_fzrq, " + "       t.ryjnjdxxb_fzjg, " + "       t.ryjnjdxxb_zsbh, "
					+ "       t.ryjnjdxxb_jdjg, " + "       zsz.cd_dmmc ryjnjdxxb_zszysffhgwyq, "
					+ "       jn.cd_dmmc ryjnjdxxb_jsdjsffhgwyq " + "  from cwy_ryjnjdxxb   t, "
					+ "       cwy_rydyb       b, " + "       cwy_ryjbxxb     j, " + "       v_zzjg          z, "
					+ "       cd_zslb         zs, " + "       cd_zy           zy, " + "       cd_jsdj         js, "
					+ "       cd_zszysffhgwyq zsz, " + "       cd_jndjsffhgwyq jn "
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and zs.cd_dmz = t.ryjnjdxxb_zjlb "
					+ "   and zy.cd_dmz = t.ryjnjdxxb_zy " + "   and js.cd_dmz = t.ryjnjdxxb_jsdj "
					+ "   and zsz.cd_dmz = t.ryjnjdxxb_zszysffhgwyq " + "   and jn.cd_dmz = t.ryjnjdxxb_jsdjsffhgwyq "
					+ "   and t.ryjnjdxxb_fzjg is not null " + "   and t.sfyx = '1' and t.ryjnjdxxb_id in(";
		if ("CWY_RYNJXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       zj.cd_dmmc rynjxxb_zjjx, "
					+ "       t.RYNJXXB_JSZH, " + "       nj.cd_dmmc rynjxxb_njlb, " + "       t.RYNJXXB_NJMC, "
					+ "       t.RYNJXXB_NJCJ " + "  from CWY_RYNJXXB t, " + "       cwy_rydyb   b, "
					+ "       cwy_ryjbxxb j, " + "       v_zzjg      z, " + "       cd_zjjx     zj, "
					+ "       cd_njlb     nj " + " where b.cwy_ry0id = t.cwy_ry0id "
					+ "   and b.cwy_ry0id = j.cwy_ry0id " + "   and j.ryjbxxb_ryzzjg = z.cd_dmz "
					+ "   and zj.cd_dmz = t.rynjxxb_zjjx " + "   and nj.cd_dmz = t.rynjxxb_njlb "
					+ "   and t.SFYX = '1' and t.rynjxxb_id in (";
		if ("CWY_RYGWBDXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       dq.cd_dmmc rygwbdxxb_dqgwmc, "
					+ "       t.rygwbdxxb_csxgwsj, " + "       t.rygwbdxxb_lkxgwsj, " + "       t.rygwbdxxb_twjg "
					+ "  from CWY_RYGWBDXXB t, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z, cd_dqgwmc dq "
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and dq.cd_dmz = t.rygwbdxxb_dqgwmc "
					+ "   and t.RYGWBDXXB_DQGWMC is not null " + "   and t.RYGWBDXXB_CSXGWSJ is not null "
					+ "   and t.SFYX = '1' and t.rygwbdxxb_id in (";
		if ("CWY_RYCTQXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       ctq.cd_dmmc ryctqxxb_ctqlb, "
					+ "       t.ryctqxxb_ctqsj, " + "       cj.cd_dmmc ryctqxxb_cjbs, " + "       t.ryctqxxb_jhbh, "
					+ "       gw.cd_dmmc ryctqxxb_dzgw, " + "       t.ryctqxxb_ctqdjfs " + "  from CWY_RYCTQXXB t, "
					+ "       cwy_rydyb    b, " + "       cwy_ryjbxxb  j, " + "       v_zzjg       z, "
					+ "       cd_ctqlb     ctq, " + "       cd_cjbs      cj, " + "       cd_dqgw      gw "
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and ctq.cd_dmz = t.ryctqxxb_ctqlb "
					+ "   and cj.cd_dmz = t.ryctqxxb_cjbs " + "   and gw.cd_dmz = t.ryctqxxb_dzgw "
					+ "   and t.sfyx = '1' and t.ryctqxxb_id in (";
		if ("CWY_JCNHXXBCub".equals(cellSet.getMetaData().getCube().getName()) || "CubCWYJCNHXXB".equalsIgnoreCase(temp))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       t.jcnhxxb_bdh, "
					+ "       t.jcnhxxb_bdrq, " + "       t.jcnhxxb_jx, " + "       t.jcnhxxb_ch, "
					+ "       t.jcnhxxb_bznh, " + "       t.jcnhxxb_sjnh, " + "       t.jcnhxxb_zxgl "
					+ "  from CWY_JCNHXXB t, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z "
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and t.SFYX = '1' and t.jcnhxxb_id in (";
		if ("CWY_RYWHCDXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       xl.cd_dmmc rywhcdxxb_xl, "
					+ "       t.rywhcdxxb_sxzy, " + "       zy.cd_dmmc rywhcdxxb_zylb, " + "       t.rywhcdxxb_byxx, "
					+ "       t.rywhcdxxb_ksxxsj, " + "       t.rywhcdxxb_bysj, " + "       xs.cd_dmmc rywhcdxxb_xxxs "
					+ "  from CWY_RYWHCDXXB t, " + "       cwy_rydyb     b, " + "       cwy_ryjbxxb   j, "
					+ "       v_zzjg        z, " + "       cd_xwhcd      xl, " + "       cd_zylb       zy, "
					+ "       cd_xxxs       xs " + " where b.cwy_ry0id = t.cwy_ry0id "
					+ "   and b.cwy_ry0id = j.cwy_ry0id " + "   and j.ryjbxxb_ryzzjg = z.cd_dmz "
					+ "   and xl.cd_dmz = t.rywhcdxxb_xl " + "   and zy.cd_dmz = t.rywhcdxxb_zylb "
					+ "   and xs.cd_dmz = t.rywhcdxxb_xxxs " + "   and t.SFYX = '1' and t.rywhcdxxb_id in (";
		if ("CWY_RYTXJJXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       jb.cd_dmmc rytxjjxxb_dqjb, "
					+ "       t.rytxjjxxb_txnrjysm, " + "       t.rytxjjxxb_txrq, " + "       t.rytxjjxxb_gcqx, "
					+ "       xg.cd_dmmc rytxjjxxb_txxg, " + "       jj.cd_dmmc rytxjjxxb_sfjj "
					+ "  from CWY_RYTXJJXXB t, " + "       cwy_rydyb     b, " + "       cwy_ryjbxxb   j, "
					+ "       v_zzjg        z, " + "       cd_dqjb       jb, " + "       cd_txxg       xg, "
					+ "       cd_sfjj       jj " + " where b.cwy_ry0id = t.cwy_ry0id "
					+ "   and b.cwy_ry0id = j.cwy_ry0id " + "   and j.ryjbxxb_ryzzjg = z.cd_dmz "
					+ "   and jb.cd_dmz = t.rytxjjxxb_dqjb " + "   and xg.cd_dmz = t.rytxjjxxb_txxg "
					+ "   and jj.cd_dmz = t.rytxjjxxb_sfjj " + "   and t.SFYX = '1' and t.rytxjjxxb_id in (";
		if ("CWY_RYXJXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       jb.cd_dmmc ryxjjlb_jb, "
					+ "       t.ryxjjlb_ts, " + "       t.ryxjjlb_qjsj, " + "       t.ryxjjlb_xjsj "
					+ "  from cwy_ryxjjlb t, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z, cd_xjlb jb "
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and jb.cd_dmz = t.ryxjjlb_jb "
					+ "   and t.sfyx = '1' and t.ryxjjlb_id in (";
		if ("CWY_RYDCXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       t.rydcxxb_jbh, "
					+ "       t.rydcxxb_rysj, " + "       t.rydcxxb_lysj, " + "       t.rydcxxb_ryfs, "
					+ "       c.cd_dmmc   as rydcxxb_cjqk, " + "       t.rydcxxb_fjid, " + "       t.rydcxxb_cwid, "
					+ "       t.rydcxxb_dcxxsj," + "       a.cd_dmmc  as rydcxxb_xyjb, " + "       t.rydcxxb_lyfs    "
					+ "  from CWY_RYDCXXB t, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z ,CD_XYJB a , CD_CJQK c"
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and t.SFYX = '1'  and a.CD_DMZ = t.RYDCXXB_XYJB   and c.CD_DMZ = t.RYDCXXB_CJQK  and t.RYDCXXB_ID in (";
		if ("CWY_RYJZXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "       t.RYJZXXB_CFZRQ, "
					+ "       t.RYJZXXB_FZRQ, " + "       t.RYJZXXB_XJSZBH, " + "       c.cd_dmmc  as RYJZXXB_XZLB, "
					+ "       t.RYJZXXB_YXJZ, " + "       t.RYJZXXB_YJSZBH, " + "       a.cd_dmmc  as RYJZXXB_LZLB, "
					+ "       t.RYJZXXB_JSZPW "
					+ "  from CWY_RYJZXXB t, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z ,CD_LZLB a , CD_XZLB c"
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and t.SFYX = '1'  and a.CD_DMZ = t.RYJZXXB_LZLB   and c.CD_DMZ = t.RYJZXXB_XZLB  and t.RYJZXXB_ID in (";
		if ("CWY_RYSGGZXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "     a.cd_dmmc as RYSGGZXXB_GZDJ, "
					+ "     c.cd_dmmc  as  RYSGGZXXB_GZLB, " + "     d.cd_dmmc  as  RYSGGZXXB_SGGZXD, " + "      e.cd_dmmc as  RYSGGZXXB_FSSJ, "
					+ "       t.RYSGGZXXB_FSDD, " + "       t.RYSGGZXXB_BZ, " + "      RYSGGZXXB_CX, "
					+ "       t.RYSGGZXXB_CC ,"   + "       t.RYSGGZXXB_CWXL, "  + "       a.cd_dmmc  as RYSGGZXXB_SGGZSM, " + "t.RYSGGZXXB_SFJJ, t.RYSGGZXXB_JJRQ"
					+ "  from CWY_RYSGGZXXB t, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z ,CD_GZDJ a , CD_GZLB c , CD_SGGZXD d , CD_SFJJ e"
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and t.SFYX = '1'  and a.CD_DMZ = t.RYSGGZXXB_GZDJ   and c.CD_DMZ = t.RYSGGZXXB_GZLB  and t.RYSGGZXXB_SGGZXD = d.CD_DMZ  and  e.CD_DMZ =t.RYSGGZXXB_SFJJ  and t.RYSGGZXXB_ID in (";	
		if ("CWY_KSXTXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "     a.cd_dmmc as KSXTXXB_CODE, "
					+ "    t.KSXTXXB_EMPID, " + "    t.KSXTXXB_IDCODE, " + "    t.KSXTXXB_PAPERCODE,  "
					+ "     t.KSXTXXB_PAPERNAME,   " + "     t.KSXTXXB_COMPUTERRESULT,   " + "   t.KSXTXXB_READRESULT,    "
					+ "    t.KSXTXXB_TOTALRESULT,  "   + "    t.KSXTXXB_OLDRESULT,    "  + "   t.KSXTXXB_EMPEXAMTIMES,    " + "  t.KSXTXXB_STARTTIME,  t.KSXTXXB_ENDTIME   "
					+ "  from   CWY_KSXTXXB t, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z ,CD_SJLX a "
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and t.SFYX = '1'  and a.CD_DMZ = t.KSXTXXB_CODE       and t.KSXTXXB_ID in (";
		if ("CWY_RYLSXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg, " + "     a.cd_dmmc as RYLSXXB_ZW, "
					+ "    t.RYLSXXB_DWXL, " + "    t.RYLSXXB_CWRQ, " + "    t.RYLSXXB_ZLSS,  "
					+ "     t.RYLSXXB_ZLSF,   " + "     t.RYLSXXB_TS,   " + "   t.RYLSXXB_TLS,    "
					+ "    t.RYLSXXB_ZTS,  "   + "    t.RYLSXXB_HJLS,    "  + "   t.RYLSXXB_PJLS,    " + "  t.RYLSXXB_CPJLSYJ   "
					+ "  from   CWY_RYLSXXB t, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z ,CD_ZW a "
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and t.SFYX = '1'  and a.CD_DMZ = t.RYLSXXB_ZW       and t.RYLSXXB_ID in (";
		if ("CWY_JGSLSTJCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg,  "
				    + "    JGSLSTJ_BDRQ,       JGSLSTJ_JX,       JGSLSTJ_CH,       JGSLSTJ_CB,       JGSLSTJ_QJ,       JGSLSTJ_GL,       JGSLSTJ_CC,       to_char(JGSLSTJ_FCSJ, 'HH24:mi') JGSLSTJ_FCSJ,       to_char(JGSLSTJ_DDSJ, 'HH24:mi') JGSLSTJ_DDSJ,       JGSLSTJ_YXSJ,       JGSLSTJ_FJSJ,       JGSLSTJ_GZSJ,       JGSLSTJ_CZSJ,       JGSLSTJ_CZY,       JGSLSTJ_CWF,       to_char(JGSLSTJ_CKSJ, 'HH24:mi') jgslstj_cksj,       to_char(JGSLSTJ_RKSJ, 'HH24:mi') jgslstj_rksj,       JGSLSTJ_CL,       JGSLSTJ_CKCL,       JGSLSTJ_RKCL,       JGSLSTJ_YXCL,       JGSLSTJ_CKCLSJ,       JGSLSTJ_RKCLSJ,       JGSLSTJ_YXCLSJ    " 
					+ "  from   CWY_JGSLSTJ t, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z  "
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and t.SFYX = '1'         and t.JGSLSTJ_ID in (";
		if ("CWY_GJRCub".equals(cellSet.getMetaData().getCube().getName()))
			sql = "select b.cwy_ry0xm xm, " + "       z.cd_dmmc zzjg,  "
				    + "    t.ph,       t.zf,       t.ykdj,       substr(t.zrxx, 0, instr(t.zrxx, '/') - 1) zrxx,       substr(t.jy, 0, instr(t.jy, '/') - 1) jy,       substr(t.jk, 0, instr(t.jk, '/') - 1) jk,       substr(t.zy, 0, instr(t.zy, '/') - 1) zy,       substr(t.kh, 0, instr(t.kh, '/') - 1) kh,       t.zrxx_jc,       t.zrxx_xj,        substr(t.zrxx_xl, 0, instr(t.zrxx_xl, '-') - 1) zrxx_xl,       t.jy_jnjd,       t.jy_jz,       t.jy_nj,       t.jy_jypx,       t.jy_ks,       t.jk_sxsz,       t.jk_tjxx,       t.zy_lw,       t.zy_cl,       t.kh_szjf,       t.kh_txjj   " 
					+ "  from   gjr_xxph t, cwy_rydyb b, cwy_ryjbxxb j, v_zzjg z  "
					+ " where b.cwy_ry0id = t.cwy_ry0id " + "   and b.cwy_ry0id = j.cwy_ry0id "
					+ "   and j.ryjbxxb_ryzzjg = z.cd_dmz " + "   and b.SFYX = '1'         and t.cwy_ry0id in (";
		String[] v = StringUtils.split(ordinal, ",");
		List<Integer> coordinates = new ArrayList<>();
		coordinates.add(ValueUtil.parseInt(v[0]));
		coordinates.add(ValueUtil.parseInt(v[1]));
		Cell cell = cellSet.getCell(coordinates);
		ResultSet rs = cell.drillThrough();
		List<String> ids = new ArrayList<>();
		
//		zht 2016年8月21日 因休假信息数据重复过多，因此需要判断过滤。
		boolean flag = false;
		if ("CWY_RYXJXXBCub".equals(cellSet.getMetaData().getCube().getName())) {
			flag = true;
		} else {
			flag = false;
		}
		int count = 0;
		while (rs.next()) {
			if (start <= count && count < start + limit) {
				String str = "'" + rs.getString("amount") + "'";
				if (flag) {
					boolean b = ids.contains(str);
					if (!b) {
						ids.add(str);
					} else {
						continue;
					}
				} else {
					ids.add(str);
				}

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
		if ("CubeWorkTime".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinLsxxGrid";
		if ("Cube LWInfo".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinLwxxGrid";
		if ("SxszCube".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinSxszGrid";
		if ("TjxxCube".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinTjxxGrid";
		if ("SrxxCube".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinSrxxGrid";
		if ("SzjfCube".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinSzjfGrid";
		if ("CWY_SZJFYCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinSzjfYGrid";
		if ("CWY_SZJFQCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinSzjfQGrid";
		if ("JypxCube".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinJypxGrid";
		if ("cwy_ryjnjdxxbCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinJnjdGrid";
		if ("CWY_RYNJXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinNjxxGrid";
		if ("CWY_RYGWBDXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinGwbdGrid";
		if ("CWY_RYCTQXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinCtqGrid";
		if ("CWY_JCNHXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinJcnhGrid";
		if ("CWY_RYWHCDXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinWhcdGrid";
		if ("CWY_RYTXJJXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinTxjjGrid";
		if ("CWY_RYXJXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinXjxxGrid";
		if ("CWY_RYDCXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinrydcGrid";
		if ("CWY_RYJZXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinRYJZXXGrid";
		if ("CWY_RYSGGZXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinRYSGGZGrid";
		if ("CWY_KSXTXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinKSXTGrid";
		if ("CWY_RYLSXXBCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinRYLSGrid";
		if ("CWY_JGSLSTJCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinJGSLSTJGrid";
		if ("CWY_GJRCub".equals(cellSet.getMetaData().getCube().getName()))
			panelName = "jwapp.cwy.fxyj.WinGJRGrid";
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
}
