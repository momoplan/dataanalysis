package com.ruyicai.dataanalysis.util.bd;

import org.apache.commons.lang.StringUtils;

import com.ruyicai.dataanalysis.domain.QiuTanMatches;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * 北单公共类
 * @author Administrator
 *
 */
public class BeiDanUtil {

	public static boolean isBeiDan(String lotteryName) {
		boolean isBeiDan = false;
		if (StringUtils.equals(lotteryName, "单场让球胜平负")) {
			isBeiDan = true;
		}
		return isBeiDan;
	}
	
	public static String getBdEvent(String batchCode, String teamId) {
		if (StringUtils.isNotBlank(batchCode)&&StringUtils.isNotBlank(teamId)) {
			teamId = StringUtil.fillZero(Integer.valueOf(teamId), 3);
			return StringUtil.join("_", "20"+batchCode, teamId);
		}
		return "";
	}
	
	/**
	 * 北单处理
	 * @param event
	 * @param iD_bet007
	 */
	public static void bdProcess(String event, String iD_bet007) {
		QiuTanMatches qiuTanMatches = QiuTanMatches.findByBdEvent(event);
		if (qiuTanMatches!=null) {
			Integer scheduleId = qiuTanMatches.getID_bet007();
			//防止同一个彩种同一个期号同一个场次有重复的比赛，将之前赛事的event置为空，以最新数据为准
			if (scheduleId!=null&&!StringUtil.isEmpty(iD_bet007)&&scheduleId!=Integer.parseInt(iD_bet007)) {
				qiuTanMatches.setBdEvent(null);
				qiuTanMatches.merge();
				Schedule schedule = Schedule.findSchedule(scheduleId, true);
				if (schedule!=null) {
					schedule.setBdEvent(null);
					schedule.merge();
				}
			}
		}
	}
	
}
