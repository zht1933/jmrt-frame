package com.webside.cube.util;


import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlobResultSetHandler implements org.apache.commons.dbutils.ResultSetHandler<Blob>{

	public Blob handle(ResultSet rs) throws SQLException {
		if(rs.next()){
			return (Blob)rs.getBlob(1);
		}
		return null;
	}
}
