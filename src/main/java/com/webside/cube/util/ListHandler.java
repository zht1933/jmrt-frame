package com.webside.cube.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListHandler implements org.apache.commons.dbutils.ResultSetHandler<List<Map<String, Object>>>{

	public List<Map<String, Object>> handle(ResultSet rs) throws SQLException {
		List<Map<String, Object>> datas = new ArrayList<Map<String,Object>>();
		while(rs.next()){
			datas.add(ResultSetUtil.getMapValue(rs));
		}
		return datas;
	}

}
