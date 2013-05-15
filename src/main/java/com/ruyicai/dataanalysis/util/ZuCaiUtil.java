package com.ruyicai.dataanalysis.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ruyicai.dataanalysis.consts.LotType;
import com.ruyicai.dataanalysis.domain.QiuTanMatches;
import com.ruyicai.dataanalysis.domain.Schedule;

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
	 * 足彩处理
	 * @param lotteryName
	 * @param issueNum
	 * @param id
	 */
	public static void zcProcess(String lotteryName, String issueNum, String id, String iD_bet007) {
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
	}
	
	/**
	 * 判断足彩对阵是否已发布
	 * @param lotNo
	 * @param flag
	 * @return
	 */
	/*public static boolean isPublish(String lotNo, String flag) {
		String publisString = "";
		if (!StringUtil.isEmpty(flag)) {
			String[] flags = flag.split("\\|");
			if (flags!=null&&flags.length==3) {
				if (lotNo.equals(LotType.ZC_SFC.getLotNo())||lotNo.equals(LotType.ZC_RX9.getLotNo())) { //胜负彩,任选九
					publisString = flags[0];
				} else if (lotNo.equals(LotType.ZC_JQC.getLotNo())) { //进球彩
					publisString = flags[2];
				} else if (lotNo.equals(LotType.ZC_BQC.getLotNo())) { //半全场
					publisString = flags[1];
				}
			}
		}
		if (!StringUtil.isEmpty(publisString)&&publisString.equals("1")) { //已发布
			return true;
		}
		return false;
	}*/
	
	/**
	 * 判断是否足彩
	 * @param lotteryName
	 * @return
	 */
	public static boolean isZuCai(String lotteryName) {
		boolean isZuCai = false;
		if (isZcSfc(lotteryName)||isZcJqc(lotteryName)||isZcBqc(lotteryName)) {
			isZuCai = true;
		}
		return isZuCai;
	}
	
	/**
	 * 判断是否是足彩胜负彩
	 * @param lotteryName
	 * @return
	 */
	public static boolean isZcSfc(String lotteryName) {
		boolean isZcSfc = false;
		if (StringUtils.equals(lotteryName, "14场胜负彩")) {
			isZcSfc = true;
		}
		/*if (lotteryName!=null&&lotteryName.equals("14场胜负彩")) {
			isZcSfc = true;
		}*/
		return isZcSfc;
	}
	
	/**
	 * 判断是否是足彩进球彩
	 * @param lotteryName
	 * @return
	 */
	public static boolean isZcJqc(String lotteryName) {
		boolean isZcJqc = false;
		if (StringUtils.equals(lotteryName, "四场进球彩")) {
			isZcJqc = true;
		}
		/*if (lotteryName!=null&&lotteryName.equals("四场进球彩")) {
			isZcJqc = true;
		}*/
		return isZcJqc;
	}
	
	/**
	 * 判断是否是足彩半全场
	 * @param lotteryName
	 * @return
	 */
	public static boolean isZcBqc(String lotteryName) {
		boolean isZcBqc = false;
		if (StringUtils.equals(lotteryName, "六场半全场")) {
			isZcBqc = true;
		}
		/*if (lotteryName!=null&&lotteryName.equals("六场半全场")) {
			isZcBqc = true;
		}*/
		return isZcBqc;
	}
	
	/**
	 * 根据zcEvent获得彩种编号
	 * @param zcEvent
	 * @return
	 */
	public static String getLotNoByZcEvent(String zcEvent) {
		String lotNo = ""; //彩种编号
		if (!StringUtil.isEmpty(zcEvent)) {
			String[] zcEvents = zcEvent.split("_");
			if (zcEvents!=null&&zcEvents.length>0) {
				lotNo = zcEvents[0];
			}
		}
		return lotNo;
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
