package com.webside.cube.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DatetimeUtil {
	
	public static final Timestamp DEF_TIME = new Timestamp(DatetimeUtil.parse("1900-01-01 00:00:00").getTime());
	public static final String FORMAT_JDTIME = "yyyyMMdd HHmmss";
	/**
	 * ȫ��ʽyyyy-MM-dd HH:mm:ss
	 */		
	public static final String FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";
	/**
	 * ȫ��ʽ�����룩yyyy-MM-dd HH:mm
	 */		
	public static final String FORMAT_TIME_NOT_SECOND = "yyyy-MM-dd HH:mm";
	/**
	 * ����ʱ��MM-dd HH:mm
	 */		
	public static final String FORMAT_TIME_NOT_YEAR_SECOND = "MM-dd HH:mm";
	/**
	 * ����ʱ����MM-dd HH:mm:ss
	 */		
	public static final String FORMAT_TIME_NOTYEAR = "MM-dd HH:mm:ss";
	/**
	 * ������yyyy-MM-dd
	 */		
	public static final String FORMAT_DATE = "yyyy-MM-dd";
	/**
	 * ����MM-dd
	 */		
	public static final String FORMAT_DATE_NOT_YEAR = "MM-dd";
	/**
	 * ����MMdd
	 */		
	public static final String FORMAT_DATE_NOT_YEAR2 = "MMdd";
	/**
	 * Сʱ���� HH:mm
	 */	
	public static final String FORMAT_TIME_ONLY_HOUR_AND_SECOND = "HH:mm";
	/**
	 * ���ո�ʽyyyyMMddHHmmss
	 */
	public static final String FORMAT_TIME_COMPACT="yyyyMMddHHmmss";
	public static final String FORMAT_DATE_COMPACT="yyyyMMdd";
	
	public static final int FORMAT_STYLE_DOWN = 1;
	public static final int FORMAT_STYLE_UP = 2;
	
	public static final long MSEL_PER_SECOND = 1000;
	
	public static final long MSEL_PER_MINUTE = 60 * MSEL_PER_SECOND;
	
	public static final long MSEL_PER_HOUR = 60 * MSEL_PER_MINUTE;
	
	public static final long MSEL_PER_DATE = 24 * MSEL_PER_HOUR;
	
	public static final long SECOND_PER_HOUR = MSEL_PER_HOUR/MSEL_PER_SECOND;
	/**
	 *  һ�����ʼʱ�� 00:00:00 (����ո�)
	 */	
	public static final String STARTPOINT_OF_DATE = " 00:00:00";
	/**
	 *  һ��Ľ�βʱ�� 23:59:59(����ո�)
	 */
	public static final String ENDPOINT_OF_DATE = " 23:59:59";
	
	
	/**
	 * �Ƿ�ΪĬ��ʱ�� 1900-01-01 00:00:00
	 * @param date
	 * @return
	 */
	public static boolean isDefTime(Date date){
		if(date == null){
			return true;
		}
		return DEF_TIME.getTime() == date.getTime();
	}
	
	/**
	 * ����+ʱ��
	 * @param date
	 * @param time
	 * @return
	 */
	public static Date getDate(Date date, Date time){
		if(date==null || time==null)return null;
		String str = dateStr(date)+" "+timeStr(time);
		return parse(str);
	}
	
	/**
	 * ��ʱ��
	 * @param date Ҫ�����ʱ��
	 * @param field ����ĸ���ʶ���мӷ�����
	 * @param amount ����
	 * @return
	 */
	public static Date add(Date date,int field,int amount){
		if(date==null)return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(field,amount);
		return c.getTime();
	}
	
	public static Date getMiddle(Date dt1,Date dt2){
		if(dt1==null || dt2==null)return null;
		long time = (dt1.getTime()+dt2.getTime())/2;
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		return c.getTime();
	}
	public static String longTimeStr(Date value){
		if(value==null)return null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(value);
	}
	
	public static String shortTimeStr(Date value){
		if(value==null)return "";
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
		return df.format(value);
	}

	public static String dateStr(Date value){
		if(value==null)return "1900-01-01";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(value);
	}
	
	public static String timeStr(Date value){
		if(value==null)return "00:00:00";
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		return df.format(value);
	}
	
	public static String format(Date value,String format){
		if(value == null){
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(value);
	}
	
	public static String timeStrHHMM(Date value){
		if(value==null)return "";
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		return df.format(value);
	}
	public static String timeStrYYYYMM(Date value){
		if(value==null)return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		return df.format(value);
	}
	public static Date parse(String value){
		return parse(value,true);
	}
	
	public static int getItem(Date date,int itemName){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(itemName);
	}
	
	public static boolean after(Date dt1,Date dt2){
		if(dt1==null || dt2==null)return false;
//		Date d1 = toRailtime(dt1);
//		Date d2 = toRailtime(dt2);
		long d1 = dt1.getTime();
		long d2 = dt2.getTime();
		return d1<d2;
	}
	
	/**
	 * �õ���·ʱ��
	 * @param time
	 * @return
	 */
	public static Date toRailtime(Date time){
		if(time==null)return null;
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.set(Calendar.HOUR_OF_DAY, 18);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		if(c.getTime().after(time))
			return add(c.getTime(), Calendar.DAY_OF_YEAR, -1);
		return c.getTime();
	}
	public static boolean isFirstDayOfMonth(Date date){
		if(date==null)return false;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH)==1;
	}
	public static Date firstDayOfMonth(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	public static Date firstDayOfYear(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	public static Date lastDayOfYear(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.YEAR, c.get(Calendar.YEAR)+1);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
//		c.add(Calendar.DAY_OF_YEAR, -1);
		return c.getTime();
	}
	public static Date lastDayOfMonth(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MONTH,c.get(Calendar.MONTH)+1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.DAY_OF_YEAR, -1);
		return c.getTime();
	}
	public static String conditionBeginDate(String sDate){
		Date date = parse(sDate);
		return conditionBeginDate(date);
	}
	public static String conditionBeginDate(Date date){
		date = toRailtime(date);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, -1);
		return longTimeStr(c.getTime());
	}
	public static String conditionEndDate(String sDate){
		Date date = parse(sDate);
		date = toRailtime(date);
		return longTimeStr(date);
	}
	public static String conditionEndDate(Date dDate){
		Date date = toRailtime(dDate);
		return longTimeStr(date);
	}
	/**
	 * ʱ���ַ�תΪʱ��
	 * @param value ���ַ�
	 * @param isNeedAddDay �Ƿ���Ҫ����
	 * @return
	 */
	public static Date parse(String value,boolean isNeedAddDay){
//		if(value.length()<=5 && value.indexOf(".")>0){
//			int h = Integer.parseInt(value.substring(0,value.indexOf(".")));
//			int m = Integer.parseInt(value.substring(value.indexOf(".")+1,value.length()));
//			Calendar c = Calendar.getInstance();
//			c.set(Calendar.HOUR_OF_DAY, h);
//			c.set(Calendar.MINUTE, m);
//			return c.getTime();
//		}
//		return new Date();
		Date ret = null;
		if(value == null){
			return null;
		}
		value = value.replace("T", " ");
		//String likeType = "\\d\\d.\\d\\d";
		SimpleDateFormat df0 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			ret = df0.parse(value);
		} catch (ParseException e) {
		}
		if(ret!=null)return ret;
		if(value.indexOf(" ") != -1 && value.indexOf(":") != -1 && value.indexOf("-") != -1){
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df;
			if (value.indexOf(":") == value.lastIndexOf(":")){
				df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			} else{
				df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}	
			Date date;
			try {
				date = df.parse(value);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				return null;
			}	
			c.setTime(date);
			ret = c.getTime();
			return ret;
		}else if(value.indexOf("-") != -1){
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date date;
			try {
				date = df.parse(value);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				return null;
			}
			c.setTime(date);
			ret = c.getTime();
			return ret;
		}else if(value.indexOf(".") != -1){
			if(value.indexOf(".") == 0){
				value = "0"+value;
			}else if(value.indexOf(".") == value.length()-1){
				value = value+"0";
			}
			
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("HH.mm");
			Date date;
			try {
				date = df.parse(value);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				return null;
			}
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.setTime(date);
			c2.setTime(new Date());
			c1.set(Calendar.MONTH, c2.get(Calendar.MONTH));
			c1.set(Calendar.DATE, c2.get(Calendar.DATE));
			c1.set(Calendar.YEAR, c2.get(Calendar.YEAR));
			if(isNeedAddDay && (c2.getTime().getTime() - c1.getTime().getTime()) > MSEL_PER_HOUR*3)
				c.add(Calendar.DATE, 1);
			c.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c1.get(Calendar.MINUTE));
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			ret = c.getTime();
			return ret;
		}else if(value.indexOf(":") != -1){
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			Date date;
			try {
				date = df.parse(value);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				return null;
			}
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.setTime(date);
			c2.setTime(new Date());
			c1.set(Calendar.MONTH, c2.get(Calendar.MONTH));
			c1.set(Calendar.DATE, c2.get(Calendar.DATE));
			c1.set(Calendar.YEAR, c2.get(Calendar.YEAR));
			if(isNeedAddDay && (c2.getTime().getTime() - c1.getTime().getTime()) > MSEL_PER_HOUR*3)
				c.add(Calendar.DATE, 1);
			c.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c1.get(Calendar.MINUTE));
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			ret = c.getTime();
			return ret;
		}else if(value.length()==15){
			SimpleDateFormat df = new SimpleDateFormat(FORMAT_JDTIME);
			try {
				ret = df.parse(value);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}else{
			try {
				df0= new SimpleDateFormat(FORMAT_TIME_COMPACT);
				ret = df0.parse(value);
			} catch (ParseException e) {
			}
			if(ret!=null)return ret;
		}
		return ret;
	}
	
	public static Date parse(String dateStr,String dateFormate) throws ParseException{
		SimpleDateFormat df = new SimpleDateFormat(dateFormate);
		return df.parse(dateStr);
	}	
	
	public static String getStandardTime(String time){
		return format(parse(time,false),FORMAT_TIME);	
	}
	public static String getWorkTime(String dateStr){
		Date date = new Date();
		try {
			date = parse(dateStr,FORMAT_TIME);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.HOUR_OF_DAY,-6);
			return format(c.getTime(), FORMAT_TIME);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return format(date, FORMAT_TIME);
	}
	

	public static Date sqlToDate(Timestamp date){
		if(date==null)return null;
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date.getTime());
		return c.getTime();
	}
	public static Timestamp toSQLDate(Date date){
		if(date==null)return null;
		return new Timestamp(date.getTime());
	}
	
	/**
	 * ���ʱ���XСʱ
	 * @param date ʱ��
	 * @param hour Сʱ
	 * @return
	 */
	public static Date addHourFrom(Date date,int hour){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR_OF_DAY,hour);
		return c.getTime();
	}
	
	public static Date addMinuteFrom(Date date, int minutes){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, minutes);
		return c.getTime();
	}

	public static String getDataToString0(Date date){
		DateFormat dateFormatterChina = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM);
		  TimeZone timeZoneChina = TimeZone.getTimeZone("Asia/Shanghai");
		  dateFormatterChina.setTimeZone(timeZoneChina);
		  String now = dateFormatterChina.format(new Date());
		return now;
	}

	public static String getDisplayData(String string) throws Exception{
		String ret = "";
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
		ret = df.format(parse(string));
		return ret;
	}
	
	
	public static String getDisplayFullData(String string) throws Exception{
		String ret = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ret = df.format(parse(string));
		return ret;
	}
	
	
	public static String getDisplayFullData(Date date) throws Exception{
		String ret = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ret = df.format(date);
		return ret;
	}
	public static long diffMinutes(Date dt1,Date dt2){
		long m1 = getMinutes(dt1);
		long m2 = getMinutes(dt2);
		return m1-m2;
	}
	public static String getDisplaySimpleData(String string) throws Exception{
		String ret = "";
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		ret = df.format(parse(string));
		return ret;
	}
	
	public static int compareDate(Date startDate,Date endDate){
		return (int) Math.abs((startDate.getTime() - endDate.getTime())/MSEL_PER_DATE); 
	}
	
	public static long getMinutes(Date d){
		if(d==null)return 0;
		return d.getTime()/1000/60;
	}

	public static long getHours(Date d){
		if(d==null)return 0;
		return (d.getTime())/3600000;
	}	
	/**
	 * �õ�����ʱ����������
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static long getDays(Date d1,Date d2){
		return getHours(d1)/24 - getHours(d2)/24;
	}
	public static boolean isNear(Date dt1,Date dt2){
		return isNear(dt1, dt2, 10);
	}
	public static boolean isNear(Date dt1,Date dt2,int delt){
		if(dt1==null || dt2==null)return false;
		long v1 = getMinutes(dt1);
		long v2 = getMinutes(dt2);
		return Math.abs(v1-v2)<=delt;
	}	
	/**
	 * dt2-dt1=���ٷ���
	 * @param dt1
	 * @param dt2
	 * @return
	 */	
	public static long diffMinites(Date dt1,Date dt2){
		if(dt1==null || dt2==null)return 0;
		long diff = dt2.getTime()-dt1.getTime();
		if(diff<0)return 0;
		return diff/(1000 * 60);    
	}
	/**
	 * dt2-dt1=����Сʱ
	 * @param dt1
	 * @param dt2
	 * @return
	 */
	public static float diffHours(Date dt1,Date dt2){
		if(dt1==null || dt2==null)return 0;
		long diff = dt2.getTime()-dt1.getTime();
		if(diff<0)return 0;
		return ValueUtil.round(diff/(1000.0 * 60.0 * 60.0));    
	}
	public static String diffTimeStr(Date dt1,Date dt2){
		if(dt1==null || dt2==null)return "";
		long diff = (dt2.getTime()-dt1.getTime())/1000;
		if(diff<0)return "";
		long h = diff / 3600;
		diff = diff % 3600;
		long m = diff / 60;
		long s = diff % 60;
		return String.format("%02d:%02d:%02d", h,m,s);
	}
	public static String toDDHHMMString(Date date){
		if(date==null)return "";
		DateFormat df = new SimpleDateFormat("dd HH:mm");
		return df.format(date);
	}
	public static String toHHMMString(Date date){
		if(date==null)return "";
		DateFormat df = new SimpleDateFormat("HH:mm");
		return df.format(date);
	}	
	
	public static String toMMDDString(Date date){
		if(date==null)return "";
		DateFormat df = new SimpleDateFormat(FORMAT_DATE_NOT_YEAR);
		return df.format(date);
	}
	public static String toMMDDStringNoSplit(Date date){
		if(date==null)return "";
		DateFormat df = new SimpleDateFormat(FORMAT_DATE_NOT_YEAR2);
		return df.format(date);
	}	
	public static String toShortString(Date date){
		if(date==null)return "";
		DateFormat df = new SimpleDateFormat("MM-dd HH:mm");
		return df.format(date);
	}
	public static String toDateString(Date date){
		if(date==null)return "";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}	
	public static String toJdDateString(Date date){
		if(date==null)return "00000000 000000";
		DateFormat df = new SimpleDateFormat(FORMAT_JDTIME);
		return df.format(date);
	}
	public static Date replaceDate(Date dt,Date date){
		if(dt==null || date==null)return null;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(dt);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date);
		c1.set(Calendar.YEAR,c2.get(Calendar.YEAR));
		c1.set(Calendar.DAY_OF_YEAR,c2.get(Calendar.DAY_OF_YEAR));
		return c1.getTime();
	}
	public static boolean isMiddletime(Date dt,Date dt1,Date dt2){
		if(dt==null || dt1==null || dt2==null)return false;
		dt1 = replaceDate(dt1, dt);
		if(dt1.getTime()>dt.getTime())
			dt1 = add(dt1, Calendar.DAY_OF_YEAR, -1);
		dt2 = replaceDate(dt2, dt1);
		if(dt2.getTime()<dt1.getTime())
			dt2 = add(dt2, Calendar.DAY_OF_YEAR, 1);
		return dt1.getTime()<=dt.getTime() && dt.getTime()<=dt2.getTime();
	}
	public static boolean between(Date dt,Date dt1,Date dt2){
		if(dt==null || dt1==null || dt2==null)return false;
		return dt1.getTime()<=dt.getTime() && dt.getTime()<=dt2.getTime();
	}
	public static boolean isNightwork(Date deptime,Date arrtime){
		Date dt1 = parse("22:00:00");
		Date dt2 = parse("06:00:00");
		
		if(isMiddletime(deptime, dt1, dt2))return true;
		if(isMiddletime(arrtime, dt1, dt2))return true;
		if(isMiddletime(dt1, deptime, arrtime))return true;
		if (diffHours(deptime,arrtime)>=24)return true;
		return false;
	}
	/**
	 * ��ȡ���ո�ʽʱ��
	 * @param date ����
	 * @return yyyyMMddHHmmss
	 */
	public static String toCompactFormat(Date date)
	{
		if(date==null)return "";
		DateFormat df = new SimpleDateFormat(FORMAT_TIME_COMPACT);
		return df.format(date);		
	}
	public static String toCompactDateFormat(Date date)
	{
		if(date==null)return "00000000";
		DateFormat df = new SimpleDateFormat(FORMAT_DATE_COMPACT);
		return df.format(date);		
	}
	public static Date trunkSecond(Date dt){
		if(dt==null)return null;
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}	
	public static double diffMonths(Date dt1,Date dt2){
		if(dt1==null || dt2==null)return 0;
		long diff = dt2.getTime()-dt1.getTime();
		if(diff<0)return 0;
		return diff/(1000.0 * 60.0 * 60.0 * 24.0 * 30.0);    
	}
	public static void main(String[] args){
//		System.out.println(longTimeStr(parse("2013-04-27T02:59:00")));
//		System.out.println(between(DatetimeUtil.parse("2014-01-02"), DatetimeUtil.parse("2014-01-01"), DatetimeUtil.parse("2014-01-03")));
//		System.out.println(between(DatetimeUtil.parse("2014-01-02"), DatetimeUtil.parse("2014-01-03"), DatetimeUtil.parse("2014-01-03")));
//		System.out.println(between(DatetimeUtil.parse("2014-01-02 18:00:00"), DatetimeUtil.parse("2014-01-02 17:55:00"), DatetimeUtil.parse("2014-01-02 18:00:01")));
//		System.out.println(between(DatetimeUtil.parse("2014-01-02 17:00:00"), DatetimeUtil.parse("2014-01-02 17:55:00"), DatetimeUtil.parse("2014-01-02 18:00:01")));
//		System.out.println(between(DatetimeUtil.parse("2014-01-02 19:00:00"), DatetimeUtil.parse("2014-01-02 17:55:00"), DatetimeUtil.parse("2014-01-02 18:00:01")));
		System.out.println(longTimeStr(parse("2013/2/7 2:9:0")));
		Date dt = new Date();
		System.out.println(longTimeStr(addHourFrom(dt, -24)));
		System.out.println(longTimeStr(toRailtime(dt)));
		
	}

}
