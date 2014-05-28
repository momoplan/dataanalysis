package com.ruyicai.dataanalysis.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	private static Date date_1000 = null;
	
	static {
		try {
			date_1000 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			.parse("1000-01-01 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static Date get1000Date() {
		return date_1000;
	}

	public static Date parse(String timeStr) {
		return parse("yyyy-MM-dd HH:mm:ss", timeStr);
	}
	
	public static Date parse(String pattern, String timeStr) {
		if(StringUtil.isEmpty(pattern) || StringUtil.isEmpty(timeStr)) {
			return null;
		}
		try {
			return new SimpleDateFormat(pattern).parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String format(Date date) {
		if(null == date) {
			return null;
		}
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
	
	public static String format(String pattern, Date date) {
		if(null == date) {
			return null;
		}
		return new SimpleDateFormat(pattern).format(date);
	}
	
	/**
	 * 取得下一个更新统计缓存时间，即明日0点
	 * @return
	 */
	/*public static Date nextTaskTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DATE, 1);
		return calendar.getTime();
	}*/
	
	/**
	 * 取得当前月份
	 * @return
	 */
	/*public static Date getCurrentMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}*/
	
	/**
	 * 取得当日0点
	 * @return
	 */
	/*public static Date getCurrentDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}*/
	
	/*public static int getDayWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}*/
	
	public static Date getPreDate(int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 0 - n);
		return calendar.getTime();
	}
	
	public static Date getAfterDate(int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, n);
		return calendar.getTime();
	}
}
