package com.webside.cube.util;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

public class DbUtil {
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public QueryRunner getQueryRunner(){
		return new QueryRunner(dataSource, true);
	}
	
	public long getSeq(String tablename) throws SQLException{
		tablename = tablename.toLowerCase();
		QueryRunner qr = getQueryRunner();
		Number nextVal = qr.query("select nextid from fw_seq where tablename=?", new ScalarHandler<Number>(), tablename);
		if(nextVal==null){
			qr.update("insert into fw_seq (tablename, nextid) values (?,2)", tablename);
			return 1;
		}
		qr.update("update fw_seq set nextid=? where tablename=?", nextVal.longValue()+1, tablename);
		return nextVal.longValue();
	}
	
	public String getSeqValue(String seq) throws SQLException{
		QueryRunner qr = getQueryRunner();
		return qr.query("select "+seq+".nextval from dual", new ScalarHandler<Number>()).toString();
	}
}
