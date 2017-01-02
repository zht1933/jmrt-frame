package com.webside.cube.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class SetHandler implements org.apache.commons.dbutils.ResultSetHandler<Set<String>>{

	public Set<String> handle(ResultSet rs) throws SQLException {
		Set<String> datas = new HashSet<String>();
		while(rs.next()){
			datas.add(rs.getString(1));
		}
		return datas;
	}

}
