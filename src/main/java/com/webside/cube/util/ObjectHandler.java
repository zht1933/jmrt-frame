package com.webside.cube.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ObjectHandler implements org.apache.commons.dbutils.ResultSetHandler<Map<String, Object>>{

	public Map<String, Object> handle(ResultSet rs) throws SQLException {
		if(rs.next()){
			return ResultSetUtil.getMapValue(rs);
		}
		return null;
	}
}
