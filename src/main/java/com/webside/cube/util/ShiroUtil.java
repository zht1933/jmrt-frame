package com.webside.cube.util;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

//import com.jmrt.commons.service.DbUtil;
//import com.jmrt.commons.utils.ListHandler;
//import com.jmrt.commons.utils.ObjectHandler;
//import com.jmrt.commons.utils.SetHandler;
//import com.jmrt.commons.utils.ValueUtil;

public class ShiroUtil {
	private DbUtil dbUtil;
	
	public void setDbUtil(DbUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	public Map<String, Object> getLoginUserData() {
		String sql = "select jb.cwy_ry0sfzh, jb.cwy_ry0gh, jb.cwy_ry0id,  u.id userid,u.name username,o.id orgid,o.name orgname,o.orglevel,o.seq orgseq"
				+ " from fw_user u"
				+ " left join fw_org o on o.id=u.orgid  left join cwy_rydyb jb on jb.cwy_ry0id =  u.id"
				+ " where u.loginid=?";
		String userId = getLoginUserId();
		if ("admin".equalsIgnoreCase(userId)) {
			sql = "select  -1  as cwy_ry0sfzh, -1 as cwy_ry0gh, -1 as cwy_ry0id,  u.id userid,u.name username,o.id orgid,o.name orgname,o.orglevel,o.seq orgseq"
					+ " from fw_user u" + " left join fw_org o on o.id=u.orgid " + " where u.loginid=?";
		}
		QueryRunner qr = dbUtil.getQueryRunner();
		try {
			return qr.query(sql, new ObjectHandler(), userId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// lzh 2016/11/26 通知
	public Map<String, Object> getRwtzData() {
		String sql = "select (select count(*) from sw_swcl where SQLX=0 and sqr=? or SQLX=0 and spr=?) tzsl, (select count(*) from sw_swcl where SQLX=1 and sqr=? or SQLX=1 and spr=?) yjsl , (select count(*) from sw_swcl where SQLX=2 and sqr=? or SQLX=2 and spr=?) dbsl"
				+ " from dual";
		String userId = getUserid();
		QueryRunner qr = dbUtil.getQueryRunner();
		try {
			return qr.query(sql, new ObjectHandler(), userId, userId, userId, userId, userId, userId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// lzh 2016/11/26 通知标题
	public List<Map<String, Object>> getTzbtData(String sqlx, String cwy_ry0id) throws SQLException {
		StringBuffer sb = new StringBuffer(
				"select t.title,t.nr,jb.cwy_ry0xm as sqr,dy.cwy_ry0xm as spr,t.sqrq,t.sprq,a.cd_dmmc as spzt,b.cd_dmmc as sqlx,t.spnr,t.fbzt,t.djrq,  t.bz from sw_swcl t left join cwy_rydyb jb on t.sqr = jb.cwy_ry0id left join cwy_rydyb dy on dy.cwy_ry0id = t.spr left join cd_spzt a on a.cd_dmz = t.spzt left join cd_sqlx b on b.cd_dmz = t.sqlx where t.SQR = '"
						+ cwy_ry0id + "' and t.sqlx = '" + sqlx + "' order by t.id ");
		QueryRunner qr = dbUtil.getQueryRunner();
		return qr.query(sb.toString(), new ListHandler());

	}

	// lzh 2016/11/26 预警标题
	public Set<String> getYjbtData() {
		String sql1 = "select TITLE  yjbt from sw_swcl where SQLX=1 and sqr=? or spr=? ";
		String userId = getUserid();
		QueryRunner qr = dbUtil.getQueryRunner();
		try {
			Set<String> set = new HashSet<String>();
			Set<String> s = qr.query(sql1, new SetHandler(), userId, userId);
			set.addAll(s);
			return set;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// lzh 2016/11/26 代办标题
	public Set<String> getDbbtData() {
		String sql1 = "select TITLE  dbbt from sw_swcl where SQLX=3 and sqr=? or spr=? ";
		String userId = getUserid();
		QueryRunner qr = dbUtil.getQueryRunner();
		try {
			Set<String> set = new HashSet<String>();
			Set<String> s = qr.query(sql1, new SetHandler(), userId, userId);
			set.addAll(s);
			return set;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * //lzh 2016/11/26 应急指挥 (select TITLE from sw_swcl where SQLX=1 and sqr=?
	 * or spr=?) yjbt , (select TITLE from sw_swcl where SQLX=2 and sqr=? or
	 * spr=?) dbbt public Map<String, Object> getJcsgzgData(){ String sql =
	 * "select count(*) yjzhsl" +" from sw_yjzh" +" where tzr=? or jlr=?";
	 * String userId = getUserid(); QueryRunner qr = dbUtil.getQueryRunner();
	 * try { return qr.query(sql, new ObjectHandler(),userId,userId); } catch
	 * (SQLException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); return null; } }
	 */

	public String getUserid() {
		Map<String, Object> data = getLoginUserData();
		return ValueUtil.getStringValue(data.get("userid"));
	}

	public String getUserDeptid() {
		Map<String, Object> data = getLoginUserData();
		return ValueUtil.getStringValue(data.get("depid"));
	}

	public String getOrgid() {
		Map<String, Object> data = getLoginUserData();
		return ValueUtil.getStringValue(data.get("orgid"));
	}

	public String getOrgname() {
		Map<String, Object> data = getLoginUserData();
		return ValueUtil.getStringValue(data.get("orgname"));
	}

	public int getLoginLevel() {
		Map<String, Object> data = getLoginUserData();
		int level = ValueUtil.getIntValue(data.get("orglevel"));
		if (level == 0)
			level = 1;
		return level;
	}

	public String getLoginOrgseq() {
		Map<String, Object> data = getLoginUserData();
		return ValueUtil.getStringValue(data.get("orgseq"));
	}

	public String getPasswd(String loginid) throws SQLException {
		QueryRunner qr = dbUtil.getQueryRunner();
		return qr.query("select password from FW_USER where loginid=?", new ScalarHandler<String>(), loginid);
	}

	// zht 2016年7月5日 增加登录类型
	public String getPasswd(String loginid, String type) throws SQLException {
		QueryRunner qr = dbUtil.getQueryRunner();

		String sql = "select t.password from FW_USER t left join cwy_rydyb d on d.cwy_ry0xm=t.name where rownum=1 and ";

		if (type.equals("1")) {
			sql += "loginid=?";
		} else if (type.equals("2")) {
			sql += "cwy_ry0xm=?";
		} else if (type.equals("3")) {
			sql += "cwy_ry0gh=?";
		} else if (type.equals("4")) {
			sql += "cwy_ry0sfzh=?";
		} else if (type.equals("5")) {
			sql += "cwy_ry0gzh=?";
		}

		return qr.query(sql, new ScalarHandler<String>(), loginid);
	}

	// zht 2016年7月5日 获取真实loginId
	public String getLoginId(String loginid, String type) throws SQLException {
		QueryRunner qr = dbUtil.getQueryRunner();

		String sql = "select t.loginid from FW_USER t left join cwy_rydyb d on d.cwy_ry0xm=t.name where rownum=1 and ";

		if (type.equals("2")) {
			sql += "cwy_ry0xm=?";
		} else if (type.equals("3")) {
			sql += "cwy_ry0gh=?";
		} else if (type.equals("4")) {
			sql += "cwy_ry0sfzh=?";
		} else if (type.equals("5")) {
			sql += "cwy_ry0gzh=?";
		}

		return qr.query(sql, new ScalarHandler<String>(), loginid);
	}

	public String getLoginUserId() {
		Subject subject = SecurityUtils.getSubject();
		String username = (String) subject.getPrincipal();
		return username;
	}

	public Set<String> getRoles() throws SQLException {
		return getRolesForUser(getLoginUserId());
	}

	public Set<String> getPermissions() throws SQLException {
		return getPermissions(getLoginUserId(), null);
	}

	public Set<String> getRolesForUser(String loginid) throws SQLException {
		String sqlUserRole = "select ap.path || '/' || r.name from fw_user u"
				+ " join fw_user_role ur on ur.userid=u.id" + " join fw_role r on r.id=ur.roleid"
				+ " join fw_app ap on ap.id=r.appid" + " where u.loginid=?";
		String sqlOrgRole = "select ap.path || '/' || r.name name from fw_user u"
				+ " join fw_org_role o on o.orgid=u.orgid" + " join fw_role r on r.id=o.roleid"
				+ " left join fw_app ap on ap.id=r.appid" + " where u.loginid=?";
		String sqlGroupRole = "select ap.path || '/' || r.name name from fw_user u"
				+ " join fw_group_user gu on gu.userid=u.id" + " join fw_group_role o on o.groupid=gu.groupid"
				+ " join fw_role r on r.id=o.roleid" + " left join fw_app ap on ap.id=r.appid" + " where u.loginid=?";
		QueryRunner qr = dbUtil.getQueryRunner();
		Set<String> set = new HashSet<String>();
		Set<String> s = qr.query(sqlUserRole, new SetHandler(), loginid);
		set.addAll(s);
		s = qr.query(sqlOrgRole, new SetHandler(), loginid);
		set.addAll(s);
		s = qr.query(sqlGroupRole, new SetHandler(), loginid);
		set.addAll(s);
		return set;
	}

	public Set<String> getPermissions(String loginid, Collection<String> roleNames) throws SQLException {
		String sqlUserPerm = "select ap.path || '/' || p.name from fw_user u"
				+ " join fw_user_perm up on up.userid=u.id" + " join fw_perm p on p.id=up.permid"
				+ " left join fw_app ap on ap.id=p.appid" + " where u.loginid=?";
		String sqlUserRolePerm = "select ap.path || '/' || p.name from fw_user u"
				+ " join fw_user_role r on r.userid=u.id" + " join fw_role_perm rp on rp.roleid=r.roleid"
				+ " join fw_perm p on p.id=rp.permid" + " left join fw_app ap on ap.id=p.appid" + " where u.loginid=?";
		String sqlUserOrgPerm = "select app.path || '/' || p.name name from fw_user u"
				+ " join fw_org o on o.id=u.orgid" + " join fw_org_perm op on op.orgid=o.id"
				+ " join fw_perm p on p.id=op.permid" + " left join fw_app app on app.id=p.appid"
				+ " where u.loginid=?";
		String sqlUserGroupPerm = "select ap.path || '/' || p.name from fw_user u"
				+ " join fw_group_user gu on gu.userid=u.id" + " join fw_group_perm gp on gp.groupid=gu.groupid"
				+ " join fw_perm p on p.id=gp.permid" + " left join fw_app ap on ap.id=p.appid" + " where u.loginid=?";
		QueryRunner qr = dbUtil.getQueryRunner();
		Set<String> set = new HashSet<String>();
		// 获取权限
		Set<String> s = qr.query(sqlUserPerm, new SetHandler(), loginid);
		set.addAll(s);

		// 获取部门的权限
		s = qr.query(sqlUserRolePerm, new SetHandler(), loginid);
		set.addAll(s);

		s = qr.query(sqlUserOrgPerm, new SetHandler(), loginid);
		set.addAll(s);

		s = qr.query(sqlUserGroupPerm, new SetHandler(), loginid);
		set.addAll(s);

		return set;
	}
}
