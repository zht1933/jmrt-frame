package com.webside.cube.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;

public class ResultSetUtil {
	public static Map<String,Object> getMapValue(ResultSet rs) throws SQLException{
		Map<String, Object> data = new HashMap<String, Object>();
		ResultSetMetaData metaData = rs.getMetaData();
		for(int i=1;i<=metaData.getColumnCount();i++){
			switch (metaData.getColumnType(i)) {
	            case Types.DATE:
	            case Types.TIME:
	            case Types.TIMESTAMP:
	                data.put(metaData.getColumnName(i).toLowerCase(), DatetimeUtil.longTimeStr(DatetimeUtil.sqlToDate(rs.getTimestamp(i))));
	                break;
	            case Types.BLOB:
	            	Blob blob = rs.getBlob(i);
	            	if(blob!=null){
	            		InputStream in = null;
	            		try {
		            		in = blob.getBinaryStream();
		            		StringBuffer sb = new StringBuffer();
		            		byte[] buff = new byte[512];
							int size = 0;
							while((size = in.read(buff))>0){
								byte[] bs = new byte[size];
								System.arraycopy(buff, 0, bs, 0, size);
								sb.append(Hex.encodeHexString(bs));
							}
			            	data.put(metaData.getColumnName(i).toLowerCase(), sb.toString());
						} catch (IOException e) {
							e.printStackTrace();
						}finally{
							if(in!=null)
								try {
									in.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
						}
	            	}
	            	break;
	            default:
	            	data.put(metaData.getColumnName(i).toLowerCase(), rs.getString(i));
			}
		}
		return data;
	}
	public static void save(final QueryRunner qr,String tablename, final String keyName, final Map<String,Object> data) throws Exception{
		save(qr, tablename, new String[]{keyName}, data);
	}

	
	public static int update(final QueryRunner qr,String tablename,final String[] keyNames, final Map<String,Object> data) throws Exception{
		Map<String, String> sqls = getSqls(qr, tablename, keyNames, data);
		String sql = sqls.get("UPDATESQL");
		if(sql==null)throw new Exception("cannot found updatesql.");
		return qr.update(sql);
	}
	public static int insert(final QueryRunner qr,String tablename,final String[] keyNames, final Map<String,Object> data) throws Exception{
		Map<String, String> sqls = getSqls(qr, tablename, keyNames, data);
		String sql = sqls.get("INSERTSQL");
		if(sql==null)throw new Exception("cannot found insertsql.");
		return qr.update(sql);
	}
	public static void save(final QueryRunner qr,String tablename,final String[] keyNames, final Map<String,Object> data) throws Exception{
		Map<String, String> sqls = getSqls(qr, tablename, keyNames, data);
		String sql = sqls.get("UPDATESQL");
		if(sql==null)throw new Exception("cannot found updatesql.");
		int num = qr.update(sql);
		if (num==0){
			sql = sqls.get("INSERTSQL");
			if(sql==null)throw new Exception("cannot found insertsql.");
			num=qr.update(sql);
		}
		String wheresql=sqls.get("WHERESQL");
		
		//处理Blob字段
		ArrayList<String> bfields=qr.query(sqls.get("SELECTSQL"), new ResultSetHandler<ArrayList<String>>(){

			public ArrayList<String> handle(ResultSet rs) throws SQLException {
				ArrayList<String> ls=new ArrayList<String>();
				while(rs.next()){
					ResultSetMetaData metaData = rs.getMetaData();
					for(int i = 1; i <= metaData.getColumnCount(); i++){
						if(metaData.getColumnType(i)==Types.BLOB && data.get(metaData.getColumnName(i))!= null){
							ls.add(metaData.getColumnName(i));							
						}
					}
				}
				return ls;
			}
			
		});	
		if (bfields!=null && bfields.size()>0)
		{
			for (Iterator<String> iterator = bfields.iterator(); iterator.hasNext();) {
				String bfield = iterator.next();
				Connection conn=qr.getDataSource().getConnection();
				try {
					conn.setAutoCommit(false);
					qr.update("UPDATE " + tablename + " SET " + bfield
							+ "=EMPTY_BLOB() " + wheresql);
					String sqlPgList = "select " + bfield + " from  "
							+ tablename + wheresql + " for update";
					Blob blob = qr.query(conn, sqlPgList,
							new BlobResultSetHandler());
					/*
					OutputStream out = blob.getBinaryStream();
					String value = (String) data.get(bfield);
					try {
						out.write(Hex.decodeHex(value.toCharArray()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						try {
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					*/
					conn.commit();
				
				}catch (Exception e) {
					// TODO: handle exception
					conn.rollback();
		
				} finally {
					conn.setAutoCommit(true);
//					conn.close();
				}

				
			}
			
			


			
			
		}
		
		
		
	/*	暂时注释bolb 处理  怀疑原处理未对数据表进行解锁
	
		//处理Blob字段
		qr.query(sqls.get("SELECTSQL")+ " for update", new ResultSetHandler<Object>(){

			@Override
			public Object handle(ResultSet rs) throws SQLException {
				while(rs.next()){
					ResultSetMetaData metaData = rs.getMetaData();
					for(int i = 1; i <= metaData.getColumnCount(); i++){
						if(metaData.getColumnType(i)==Types.BLOB && data.get(metaData.getColumnName(i))!= null){
							BLOB blob = (BLOB) rs.getBlob(i);
							OutputStream out = blob.getBinaryOutputStream();
							String value = (String) data.get(metaData.getColumnName(i));
							try {
								out.write(Hex.decodeHex(value.toCharArray()));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}finally{
								try {
									out.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				}
				return null;
			}
			
		});
		*/
	}
	public static void delete(QueryRunner qr,String tablename,String keyname, Object value) throws SQLException{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(keyname.toUpperCase(), value);
		delete(qr, tablename, new String[]{keyname}, map);
	}
	public static void delete(QueryRunner qr,String tablename,String[] keynames, Map<String,Object> data) throws SQLException{
		Map<String, String> sqls = getSqls(qr, tablename, keynames, data);
		qr.update(sqls.get("DELETESQL"));
	}
	private static Map<String, String> getSqls(QueryRunner qr, final String tablename, final String keyName, final Map<String,Object> data) throws SQLException{
		return getSqls(qr, tablename, new String[]{keyName}, data);
	}

	private static boolean hasKey(String[] keyNames,String keyName){
		for(String s : keyNames){
			if(keyName.equalsIgnoreCase(s))return true;
		}
		return false;
	}
	private static boolean hasField(Map<String,Object> data,String fieldname){
		for(String key:data.keySet()){
			if(key.equalsIgnoreCase(fieldname))return true;
		}
		return false;
	}
	private static Map<String, String> getSqls(QueryRunner qr, final String tablename, final String[] keyNames, final Map<String,Object> data) throws SQLException{
		return qr.query("select * from "+tablename+" where 1<>1", new ResultSetHandler<Map<String, String>>(){
			public Map<String, String> handle(ResultSet rs)
					throws SQLException {
				StringBuffer sqlInsField = new StringBuffer();
				StringBuffer sqlInsValue = new StringBuffer();
				StringBuffer sqlUpdate = new StringBuffer();
				StringBuffer where = new StringBuffer();
				Map<String, String> map = new HashMap<String, String>();
				ResultSetMetaData metaData = rs.getMetaData();
				boolean isFirstWhere = true;
				for(int i = 1; i <= metaData.getColumnCount(); i++){
					String fn = metaData.getColumnName(i).toLowerCase();
					String value = ValueUtil.getStringValue(data.get(fn));
					if(data.get(fn) instanceof Date)
						value = DatetimeUtil.longTimeStr((Date)(data.get(fn)));
		        	if (hasField(data, fn))
		        	{
						sqlInsField.append(metaData.getColumnName(i)+",");
						switch (metaData.getColumnType(i)) {
		            		case Types.DATE:
		            		case Types.TIME:
		            		case Types.TIMESTAMP:	            			 
		            			if(!StringUtils.isEmpty(value)){
		            				value = "to_date('"+value+"','yyyy-MM-dd hh24:mi:ss')";
		            			}else{
		            				value = "null";
		            			}
		            			sqlInsValue.append(value+",");
		            			sqlUpdate.append(metaData.getColumnName(i)+"="+value+",");
		            			if(hasKey(keyNames, metaData.getColumnName(i))){
		            				if(isFirstWhere)
		            					where.append("where "+metaData.getColumnName(i) + "="+value);
		            				else
		            					where.append(" and "+metaData.getColumnName(i) + "="+value);
		            				isFirstWhere = false;
		            			}
		            			break;
		                    case Types.TINYINT:
		                    case Types.SMALLINT:
		                    case Types.INTEGER:
		                    case Types.BIGINT:
		                    case Types.FLOAT:
		                    case Types.REAL:
		                    case Types.DOUBLE:
		                    case Types.NUMERIC:
		                    case Types.DECIMAL:
		                    	if(StringUtils.isEmpty(value)){
		                    		value = "null";
		                    	}
		                    	sqlInsValue.append(value+",");
		                    	sqlUpdate.append(metaData.getColumnName(i)+"="+value+",");
		            			if(hasKey(keyNames, metaData.getColumnName(i))){
		            				if(isFirstWhere)
		            					where.append(" where "+metaData.getColumnName(i) + "=" + value);
		            				else
		            					where.append(" and "+metaData.getColumnName(i) + "=" + value);
		            				isFirstWhere = false;
		            			}
		                        break;
		                    case Types.BLOB:
		                    	sqlInsValue.append("empty_blob(),");
		                    	sqlUpdate.append(metaData.getColumnName(i)+"=empty_blob(),");
		                        break;
		            		default:
		            			if(!StringUtils.isEmpty(value)){
		            				value =SqlUtil.quotedStr(value);
		            			}else{
		            				value = "null";
		            			}
		            			sqlInsValue.append(value+",");
		            			sqlUpdate.append(metaData.getColumnName(i)+"="+value+",");
		            			if(hasKey(keyNames, metaData.getColumnName(i))){
		            				if(isFirstWhere)
		            					where.append(" where "+metaData.getColumnName(i)+"="+value);
		            				else
		            					where.append(" and "+metaData.getColumnName(i)+"="+value);
		            				isFirstWhere = false;
		            			}
						}
		        	}
				}
				if(sqlInsField.length()>0 && sqlInsValue.length()>0){
					sqlInsField.deleteCharAt(sqlInsField.length()-1);
					sqlInsField.insert(0, "insert into "+tablename+" (");
					sqlInsField.append(")");
					
					sqlInsValue.deleteCharAt(sqlInsValue.length()-1);
					sqlInsValue.insert(0, " values(");
					sqlInsValue.append(")");
					map.put("INSERTSQL", sqlInsField.toString()+sqlInsValue.toString());	
				}
				
				sqlUpdate.deleteCharAt(sqlUpdate.length()-1);
				sqlUpdate.insert(0, "update "+tablename+" set ");
				sqlUpdate.append(where.toString());
				
				if (!StringUtils.isEmpty(where.toString()))
				{
					map.put("DELETESQL", "delete from "+tablename+where.toString());
					map.put("UPDATESQL", sqlUpdate.toString());
					map.put("SELECTSQL", "select * from "+tablename+where.toString());
					map.put("WHERESQL",where.toString());
				}else
				{
					map.put("DELETESQL", "delete from "+tablename+" where 1=2");
					map.put("UPDATESQL", sqlUpdate.toString()+" where 1=2");
					map.put("SELECTSQL", "select * from "+tablename+" where 1=2");
					map.put("WHERESQL"," where 1=2 ");
				}
				
				
				return map;
			}
		});
	}
}
