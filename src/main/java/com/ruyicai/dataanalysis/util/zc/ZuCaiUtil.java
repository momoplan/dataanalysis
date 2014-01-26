package com.ruyicai.dataanalysis.util.zc;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ruyicai.dataanalysis.consts.LotType;
import com.ruyicai.dataanalysis.domain.QiuTanMatches;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * 足彩公共类
 * @author Administrator
 *
 */
public class ZuCaiUtil {

	/**
	 * 得到足彩的event
	 * @param lotteryName
	 * @param issueNum
	 * @param id
	 * @return
	 */
	public static String getZcEvent(String lotteryName, String issueNum, String id) {
		String lotNo = "";
		if (ZuCaiUtil.isZcSfc(lotteryName)) { //足彩胜负彩
			lotNo = LotType.ZC_SFC.getLotNo();
		} else if (ZuCaiUtil.isZcJqc(lotteryName)) { //足彩进球彩
			lotNo = LotType.ZC_JQC.getLotNo();
		} else if (ZuCaiUtil.isZcBqc(lotteryName)) { //足彩半全场
			lotNo = LotType.ZC_BQC.getLotNo();
		}
		if (!StringUtil.isEmpty(lotNo)) {
			String event = StringUtil.join("_", lotNo, issueNum, id);
			return event;
		}
		return null;
	}
	
	/**
	 * 胜负彩处理
	 * @param event
	 * @param iD_bet007
	 */
	public static void sfcProcess(String event, String iD_bet007) {
		QiuTanMatches qiuTanMatches = QiuTanMatches.findByZcSfcEvent(event);
		if (qiuTanMatches!=null) {
			Integer scheduleId = qiuTanMatches.getID_bet007();
			//防止同一个彩种同一个期号同一个场次有重复的比赛，将之前赛事的event置为空，以最新数据为准
			if (scheduleId!=null&&!StringUtil.isEmpty(iD_bet007)&&scheduleId!=Integer.parseInt(iD_bet007)) {
				qiuTanMatches.setZcSfcEvent(null);
				qiuTanMatches.merge();
				Schedule schedule = Schedule.findSchedule(scheduleId, true);
				if (schedule!=null) {
					schedule.setZcSfcEvent(null);
					schedule.merge();
				}
			}
		}
	}
	
	/**
	 * 进球彩处理
	 * @param event
	 * @param iD_bet007
	 */
	public static void jqcProcess(String event, String iD_bet007) {
		QiuTanMatches qiuTanMatches = QiuTanMatches.findByZcJqcEvent(event);
		if (qiuTanMatches!=null) {
			Integer scheduleId = qiuTanMatches.getID_bet007();
			//防止同一个彩种同一个期号同一个场次有重复的比赛，将之前赛事的event置为空，以最新数据为准
			if (scheduleId!=null&&!StringUtil.isEmpty(iD_bet007)&&scheduleId!=Integer.parseInt(iD_bet007)) {
				qiuTanMatches.setZcJqcEvent(null);
				qiuTanMatches.merge();
				Schedule schedule = Schedule.findSchedule(scheduleId, true);
				if (schedule!=null) {
					schedule.setZcJqcEvent(null);
					schedule.merge();
				}
			}
		}
	}
	
	/**
	 * 半全场处理
	 * @param event
	 * @param iD_bet007
	 */
	public static void bqcProcess(String event, String iD_bet007) {
		QiuTanMatches qiuTanMatches = QiuTanMatches.findByZcBqcEvent(event);
		if (qiuTanMatches!=null) {
			Integer scheduleId = qiuTanMatches.getID_bet007();
			//防止同一个彩种同一个期号同一个场次有重复的比赛，将之前赛事的event置为空，以最新数据为准
			if (scheduleId!=null&&!StringUtil.isEmpty(iD_bet007)&&scheduleId!=Integer.parseInt(iD_bet007)) {
				qiuTanMatches.setZcBqcEvent(null);
				qiuTanMatches.merge();
				Schedule schedule = Schedule.findSchedule(scheduleId, true);
				if (schedule!=null) {
					schedule.setZcBqcEvent(null);
					schedule.merge();
				}
			}
		}
	}
	
	/**
	 * 足彩处理
	 * @param lotteryName
	 * @param issueNum
	 * @param id
	 */
	/*public static void zcProcess(String lotteryName, String issueNum, String id, String iD_bet007) {
		QiuTanMatches qiuTanMatches = QiuTanMatches.findByLotteryName_issueNum_id(lotteryName, issueNum, id);
		if (qiuTanMatches!=null) {
			Integer scheduleId = qiuTanMatches.getID_bet007(); //根据名称、期号、场次获得的赛事id
			if (scheduleId!=null&&!StringUtil.isEmpty(iD_bet007)&&scheduleId!=Integer.parseInt(iD_bet007)) {
				//防止同一个彩种同一个期号同一个场次有重复的比赛，将之前赛事的event置为空，以最新数据为准
				if (ZuCaiUtil.isZcSfc(lotteryName)) { //足彩胜负彩
					qiuTanMatches.setZcSfcEvent(null);
					qiuTanMatches.merge();
					Schedule schedule = Schedule.findSchedule(scheduleId);
					if (schedule!=null) {
						schedule.setZcSfcEvent(null);
						schedule.merge();
					}
				} else if (ZuCaiUtil.isZcJqc(lotteryName)) { //足彩进球彩
					qiuTanMatches.setZcJqcEvent(null);
					qiuTanMatches.merge();
					Schedule schedule = Schedule.findSchedule(scheduleId);
					if (schedule!=null) {
						schedule.setZcJqcEvent(null);
						schedule.merge();
					}
				} else if (ZuCaiUtil.isZcBqc(lotteryName)) { //足彩半全场
					qiuTanMatches.setZcBqcEvent(null);
					qiuTanMatches.merge();
					Schedule schedule = Schedule.findSchedule(scheduleId);
					if (schedule!=null) {
						schedule.setZcBqcEvent(null);
						schedule.merge();
					}
				}
			}
		}
	}*/
	
	/**
	 * 判断是否足彩
	 * @param lotteryName
	 * @return
	 */
	public static boolean isZuCai(String lotteryName) {
		if (isZcSfc(lotteryName)||isZcJqc(lotteryName)||isZcBqc(lotteryName)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否是足彩胜负彩
	 * @param lotteryName
	 * @return
	 */
	public static boolean isZcSfc(String lotteryName) {
		if (StringUtils.equals(lotteryName, "14场胜负彩")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否是足彩进球彩
	 * @param lotteryName
	 * @return
	 */
	public static boolean isZcJqc(String lotteryName) {
		if (StringUtils.equals(lotteryName, "四场进球彩")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否是足彩半全场
	 * @param lotteryName
	 * @return
	 */
	public static boolean isZcBqc(String lotteryName) {
		if (StringUtils.equals(lotteryName, "六场半全场")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 根据zcEvent获得彩种编号
	 * @param zcEvent
	 * @return
	 */
	public static String getLotNoByZcEvent(String zcEvent) {
		if (StringUtils.isNotBlank(zcEvent)) {
			String[] zcEvents = zcEvent.split("_");
			if (zcEvents!=null&&zcEvents.length>0) {
				return zcEvents[0];
			}
		}
		return "";
	}
	
	/**
	 * 根据彩种编号获得足彩赛事信息
	 * @param lotNo
	 * @param zcEvent
	 * @return
	 */
	public static Schedule getZcScheduleByLotNo(String lotNo, String zcEvent) {
		Schedule schedule = null;
		if (lotNo.equals(LotType.ZC_SFC.getLotNo())) { //足彩胜负彩
			schedule = Schedule.findByZcSfcEvent(zcEvent);
		} else if (lotNo.equals(LotType.ZC_JQC.getLotNo())) { //足彩进球彩
			schedule = Schedule.findByZcJqcEvent(zcEvent);
		} else if (lotNo.equals(LotType.ZC_BQC.getLotNo())) { //足彩半全场
			schedule = Schedule.findByZcBqcEvent(zcEvent);
		}
		return schedule;
	}
	
	/**
	 * 根据彩种和期号获得足彩赛事信息
	 * @param lotNo
	 * @param batchCode
	 * @return
	 */
	public static List<Schedule> getZcScheduleByLotNoAndBatchCode(String lotNo, String batchCode) {
		List<Schedule> schedules = null;
		if (StringUtils.equals(lotNo, LotType.ZC_SFC.getLotNo())) { //足彩胜负彩
			schedules = Schedule.findByZcSfcEventAndLotNoAndBatchCode(lotNo, batchCode);
		} else if (StringUtils.equals(lotNo, LotType.ZC_JQC.getLotNo())) { //足彩进球彩
			schedules = Schedule.findByZcJqcEventAndLotNoAndBatchCode(lotNo, batchCode);
		} else if (StringUtils.equals(lotNo, LotType.ZC_BQC.getLotNo())) { //足彩半全场
			schedules = Schedule.findByZcBqcEventAndLotNoAndBatchCode(lotNo, batchCode);
		}
		return schedules;
	}
	
}
