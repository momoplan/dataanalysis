package com.ruyicai.dataanalysis.util.jc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ruyicai.dataanalysis.domain.QiuTanMatches;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.util.StringUtil;

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
		return isJcLq;
	}
	
	/**
	 * 竞彩足球处理
	 * @param event
	 * @param iD_bet007
	 */
	public static void jczProcess(String event, String iD_bet007) {
		QiuTanMatches qiuTanMatches = QiuTanMatches.findByEvent(event);
		if (qiuTanMatches!=null) {
			Integer scheduleId = qiuTanMatches.getID_bet007();
			//防止同一个彩种同一个期号同一个场次有重复的比赛，将之前赛事的event置为空，以最新数据为准
			if (scheduleId!=null&&!StringUtil.isEmpty(iD_bet007)&&scheduleId!=Integer.parseInt(iD_bet007)) {
				qiuTanMatches.setEvent(null);
				qiuTanMatches.merge();
				Schedule schedule = Schedule.findSchedule(scheduleId, true);
				if (schedule!=null) {
					schedule.setEvent(null);
					schedule.merge();
				}
			}
		}
	}
	
	/**
	 * 竞彩篮球处理
	 * @param event
	 * @param iD_bet007
	 */
	public static void jclProcess(String event, String iD_bet007) {
		QiuTanMatches qiuTanMatches = QiuTanMatches.findByEvent(event);
		if (qiuTanMatches!=null) {
			Integer scheduleId = qiuTanMatches.getID_bet007();
			//防止同一个彩种同一个期号同一个场次有重复的比赛，将之前赛事的event置为空，以最新数据为准
			if (scheduleId!=null&&!StringUtil.isEmpty(iD_bet007)&&scheduleId!=Integer.parseInt(iD_bet007)) {
				qiuTanMatches.setEvent(null);
				qiuTanMatches.merge();
				ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJcl(scheduleId);
				if (scheduleJcl!=null) {
					scheduleJcl.setEvent(null);
					scheduleJcl.merge();
				}
			}
		}
	}
	
	public static String getDayByEvent(String event) {
		String[] separator = StringUtils.splitByWholeSeparator(event, "_"); //1_20140330_7_001
		if (separator!=null&&separator.length==4) {
			return separator[1];
		}
		return null;
	}
	
	/*public static void main(String[] args) {
		String string = getDayByEvent("1_20140330_7_001");
		System.out.println(string);
	}*/
	
}
