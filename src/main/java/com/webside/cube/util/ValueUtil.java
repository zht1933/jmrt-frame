package com.webside.cube.util;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class ValueUtil {
	public static float parseFloat(String s){
		try{
			return Float.parseFloat(s);
		}catch(Exception e){
			return 0;
		}
	}
	public static int parseInt(String s){
		try{
			if(s.contains("."))
				return (int)Double.parseDouble(s);
			return Integer.parseInt(s);
		}catch(Exception e){
			return 0;
		}
	}
	public static double parsedouble(String v){
		try{
			return Double.parseDouble(v);
		}catch(Exception e){
			return 0;
		}
	}
	public static long parseLong(String s){
		try{
			return Long.parseLong(s);
		}catch(Exception e){
			return 0;
		}
	}
	public static String formatFloat(float v){
		return String.format("%.3f", v);
	}
	public static float round(double v){
		return ((int)(v*1000.0f+0.5))/1000.0f;
	}
	public static int getIntValue(Object v){
		if(v instanceof Number)return ((Number)v).intValue();
		if(v instanceof String)return parseInt((String)v);
		if(v instanceof Boolean)return ((Boolean)v)?1:0;
		return 0;
	}
	public static double getDoubleValue(Object v){
		if(v instanceof Number)return ((Number)v).doubleValue();
		if(v instanceof String)return parsedouble((String)v);
		if(v instanceof Boolean)return ((Boolean)v)?1l:0l;
		return 0;
	}
	public static long getLongValue(Object v){
		if(v instanceof Number)return ((Number)v).longValue();
		if(v instanceof String)return parseLong((String)v);
		if(v instanceof Boolean)return ((Boolean)v)?1l:0l;
		return 0;
	}	
	public static String getStringValue(Object v){
		if(v==null)return "";
		if(v instanceof Number)return ((Number)v).toString();
		if(v instanceof Date)return DatetimeUtil.longTimeStr((Date)v);
		if(v instanceof String)return (String)v;
		if(v instanceof Boolean)return ((Boolean)v)?"true":"false";
		return "";
	}
	public static Date getDateValue(Object v){
		if(v instanceof String)return DatetimeUtil.parse((String)v);
		if(v instanceof Timestamp)return (Timestamp)v;
		if(v instanceof Date)return (Date)v;
		return null;
	}
	public static boolean getBooleanValue(Object v){
		if(v==null)return false;
		if(v instanceof String)return ((String)v).toUpperCase().equals("TRUE")?true:false;
		if(v instanceof Number)return ((Number)v).intValue()!=0?true:false;
		if(v instanceof Boolean)return (Boolean)v;
		return false;
	}
	public static String getNumStr(String src){
		if(StringUtils.isEmpty(src))return "0";
		return src.replaceAll("[^\\d]","");
	}
	public static void main(String[] args){
		System.out.println(getBooleanValue(0));
	}
}
