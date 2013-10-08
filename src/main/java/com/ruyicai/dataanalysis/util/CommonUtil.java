package com.ruyicai.dataanalysis.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.bd.SendJmsBdUtil;
import com.ruyicai.dataanalysis.util.zq.SendJmsJczUtil;

@Service
public class CommonUtil {

	private Logger logger = LoggerFactory.getLogger(CommonUtil.class);
	
	@Autowired
	private SendJmsJczUtil sendJmsJczUtil;
	
	@Autowired
	private SendJmsBdUtil sendJmsBdUtil;
	
	/**
	 * 判断足球的event是否为空
	 * @param schedule
	 * @return
	 */
	public static boolean isZqEventEmpty(Schedule schedule) {
		String event = schedule.getEvent();
		String zcSfcEvent = schedule.getZcSfcEvent();
		String zcJqcEvent = schedule.getZcJqcEvent();
		String zcBqcEvent = schedule.getZcBqcEvent();
		String bdEvent = schedule.getBdEvent();
		if(StringUtils.isBlank(event)&&StringUtils.isBlank(zcSfcEvent)&&StringUtils.isBlank(zcJqcEvent)
				&&StringUtils.isBlank(zcBqcEvent)&&StringUtils.isBlank(bdEvent)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 足球发送完场的Jms
	 * @param schedule
	 */
	public void sendScheduleFinishJms(Schedule schedule) {
		String event = schedule.getEvent(); //竞彩足球
		if (StringUtils.isNotBlank(event)) {
			logger.info("sendScheduleFinishJms,event="+event+";scheduleId="+schedule.getScheduleID()+";homeScore="+schedule.getHomeScore()
					+";guestScore="+schedule.getGuestScore()+";homeHalfScore="+schedule.getHomeHalfScore()
					+";guestHalfScore="+schedule.getGuestHalfScore());
			sendJmsJczUtil.sendScheduleFinishJms(event);
		}
		String bdEvent = schedule.getBdEvent(); //北单
		if (StringUtils.isNotBlank(bdEvent)) {
			logger.info("sendScheduleFinishJms,bdEvent="+bdEvent+";scheduleId="+schedule.getScheduleID()+";homeScore="+schedule.getHomeScore()
					+";guestScore="+schedule.getGuestScore()+";homeHalfScore="+schedule.getHomeHalfScore()
					+";guestHalfScore="+schedule.getGuestHalfScore());
			sendJmsBdUtil.sendScheduleFinishJms(bdEvent);
		}
	}
	
	/**
	 * 足球发送比分变化的Jms
	 * @param schedule
	 */
	public void sendScoreModifyJms(Schedule schedule) {
		String event = schedule.getEvent(); //竞彩足球
		if (StringUtils.isNotBlank(event)) {
			logger.info("sendScoreModifyJms,event="+event+";scheduleId="+schedule.getScheduleID()+";homeScore="+schedule.getHomeScore()
					+";guestScore="+schedule.getGuestScore()+";homeHalfScore="+schedule.getHomeHalfScore()
					+";guestHalfScore="+schedule.getGuestHalfScore());
			sendJmsJczUtil.sendScoreModifyJms(event);
		}
		String bdEvent = schedule.getBdEvent(); //北单
		if (StringUtils.isNotBlank(bdEvent)) {
			logger.info("sendScoreModifyJms,bdEvent="+bdEvent+";scheduleId="+schedule.getScheduleID()+";homeScore="+schedule.getHomeScore()
					+";guestScore="+schedule.getGuestScore()+";homeHalfScore="+schedule.getHomeHalfScore()
					+";guestHalfScore="+schedule.getGuestHalfScore());
			sendJmsBdUtil.sendScoreModifyJms(bdEvent);
		}
	}
	
}
