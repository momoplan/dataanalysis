package com.ruyicai.dataanalysis.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 竞彩公共类
 * @author Administrator
 *
 */
@Component
public class JingCaiUtil {
	
	public static final Map<String, BigDecimal> WEEKID = new HashMap<String, BigDecimal>();
	
	public static final Map<Integer, Integer> WEEK = new HashMap<Integer, Integer>();
	
	static {
		WEEKID.put("周一", new BigDecimal(1));
		WEEKID.put("周二", new BigDecimal(2));
		WEEKID.put("周三", new BigDecimal(3));
		WEEKID.put("周四", new BigDecimal(4));
		WEEKID.put("周五", new BigDecimal(5));
		WEEKID.put("周六", new BigDecimal(6));
		WEEKID.put("周日", new BigDecimal(7));
		
		WEEK.put(1, 2);
		WEEK.put(2, 3);
		WEEK.put(3, 4);
		WEEK.put(4, 5);
		WEEK.put(5, 6);
		WEEK.put(6, 7);
		WEEK.put(7, 1);
	}

	/**
	 * 得到竞彩的event
	 * @param lotteryName
	 * @param iD
	 * @param time
	 * @return
	 */
	public static String getEvent(String lotteryName, String iD, Date time) {
		//logger.info("lotteryName="+lotteryName+",iD="+iD+",time="+time);
		BigDecimal weekid = WEEKID.get(iD.substring(0, 2));
		String day = getDay(weekid.intValue(), time);
		String teamid = iD.substring(2);
		String type = "null";
		if(lotteryName.endsWith("足球")) {
			type = "1";
		}
		if(lotteryName.endsWith("篮球")) {
			type = "0";
		}
		String event = StringUtil.join("_", type, day, String.valueOf(weekid.intValue()), teamid);
		return event;
	}
	
	/**
	 * 得到比赛时间
	 * @param weekid
	 * @param time
	 * @return
	 */
	public static String getDay(int weekid, Date time) {
		while(WEEK.get(weekid) != getWeekid(time)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);
			calendar.add(Calendar.DAY_OF_WEEK, -1);
			time = calendar.getTime();
		}
		return new SimpleDateFormat("yyyyMMdd").format(time);
	}
	
	/**
	 * 得到比赛星期
	 * @param time
	 * @return
	 */
	public static int getWeekid(Date time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * 判断是否是竞彩足球
	 * @param lotteryName
	 * @return
	 */
	public static boolean isJcZq(String lotteryName) {
		boolean isJcZq = false;
		if (StringUtils.equals(lotteryName, "竞彩足球")) {
			isJcZq = true;
		}
		/*if (lotteryName!=null&&lotteryName.equals("竞彩足球")) {
			isJcZq = true;
		}*/
		return isJcZq;
	}
	
	/**
	 * 判断是否是竞彩篮球
	 * @param lotteryName
	 * @return
	 */
	public static boolean isJcLq(String lotteryName) {
		boolean isJcLq = false;
		if (StringUtils.equals(lotteryName, "竞彩篮球")) {
			isJcLq = true;
		}
		/*if (lotteryName!=null&&lotteryName.equals("竞彩篮球")) {
			isJcLq = true;
		}*/
		return isJcLq;
	}
	
}
