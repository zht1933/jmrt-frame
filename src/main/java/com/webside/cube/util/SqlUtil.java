package com.webside.cube.util;

import org.apache.commons.lang.StringUtils;

/**
 * sql��乤��
 * @author Administrator
 *
 */
public class SqlUtil {
	/**
	 * ����'value'
	 * @param value
	 * @return
	 */
	public static String quotedStr(String value)
	{
		if (!StringUtils.isEmpty(value))
		{
			value=value.replace("'", "''");
			return "'"+value+"'";
		}else
		{
			return "''";
		}
	}
	/**
	 * ����'value' ������ֵΪnull �򷵻�null
	 * @param value
	 * @return
	 */
	public static String quotedStrNull(String value)
	{
		if (value!=null)
		{
			if (!StringUtils.isEmpty(value))
			{
				value=value.replace("'", "''");
				return "'"+value+"'";				
			}else
			{
				return "''";
			}
		}else
		{
			return "null";
		}

	}
	
	/**
	 * ��ô���Oracleʱ���ַ��ʽ
	 * @param datetime
	 * @return to_date(datetime,'yyyy-MM-dd HH24:MI:ss')
	 */
	public static String toOracleDatetime(String datetime)
	{
		return "to_date("+quotedStr(datetime)+",'yyyy-MM-dd HH24:MI:ss')";
		
		
		
	}
	
}
