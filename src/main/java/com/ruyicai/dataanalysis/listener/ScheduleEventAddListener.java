package com.ruyicai.dataanalysis.listener;

import java.util.Date;
import net.sf.json.JSONObject;
import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.service.back.LotteryService;
import com.ruyicai.dataanalysis.util.lq.SendJmsJclUtil;
import com.ruyicai.dataanalysis.util.zq.SendJmsJczUtil;

/**
 * event增加的Jms监听
 * @author Administrator
 *
 */
@Service
public class ScheduleEventAddListener {

	private Logger logger = LoggerFactory.getLogger(ScheduleEventAddListener.class);
	
	@Autowired
	private LotteryService lotteryService;
	
	@Autowired
	private SendJmsJclUtil sendJmsJclUtil;
	
	@Autowired
	private SendJmsJczUtil sendJmsJczUtil;
	
	public void process(@Body String event) {
		try {
			logger.info("event增加的Jms监听 start event="+event);
			if (StringUtils.isBlank(event)) {
				return;
			}
			String[] events = StringUtils.split(event, "_");
			String jingCaiType = events[0];
			if (StringUtils.equals(jingCaiType, "0")) { //篮球
				processLq(event);
			} else if (StringUtils.equals(jingCaiType, "1")) { //足球
				processZq(event);
			}
		} catch (Exception e) {
			logger.error("event增加的Jms监听发生异常", e);
		}
	}

	private void processLq(String event) {
		ScheduleJcl scheduleJcl = ScheduleJcl.findByEvent(event, false);
		if (scheduleJcl==null) {
			logger.info("event增加的Jms监听-赛事不存在,event="+event);
			return;
		}
		Integer scheduleId = scheduleJcl.getScheduleId();
		Integer betState = scheduleJcl.getBetState();
		Date betEndTime = scheduleJcl.getBetEndTime();
		logger.info("event增加的Jms监听,event="+event+",scheduleId="+scheduleId+",betState="+betState+",betEndTime="+betEndTime);
		
		String[] events = StringUtils.split(event, "_");
		String result = lotteryService.getJingcaimatches("J00005", events[1], events[2], events[3]);
		if (StringUtils.isBlank(result)) {
			return;
		}
		JSONObject fromObject = JSONObject.fromObject(result);
		if (fromObject==null) {
			return;
		}
		String errorCode = fromObject.getString("errorCode");
		String valueString = fromObject.getString("value");
		if (!StringUtils.equals(errorCode, "0")||StringUtils.equals(valueString, "null")) {
			return;
		}
		JSONObject valueObject = fromObject.getJSONObject("value");
		String matchesString = valueObject.getString("matches");
		if (StringUtils.equals(matchesString, "null")) {
			return;
		}
		JSONObject matchesObject = valueObject.getJSONObject("matches");
		
		boolean isModify = false;
		String state = matchesObject.getString("state");
		if (StringUtils.equals(state, "0")&&(betState==null||betState==0)) {
			isModify = true;
			scheduleJcl.setBetState(1); //开售
		}
		String endtime = matchesObject.getString("endtime");
		if (StringUtils.isNotBlank(endtime)&&!StringUtils.equals(endtime, "null")) {
			Date endDate = new Date(Long.parseLong(endtime));
			if (betEndTime==null||betEndTime.getTime()!=endDate.getTime()) {
				isModify = true;
				scheduleJcl.setBetEndTime(endDate);
			}
		}
		if (isModify) {
			scheduleJcl.merge();
			//发送赛事缓存更新的Jms
			sendJmsJclUtil.sendSchedulesCacheUpdateJms(scheduleJcl.getScheduleId());
		}
	}

	private void processZq(String event) {
		Schedule schedule = Schedule.findByEvent(event, false);
		if (schedule==null) {
			logger.info("event增加的Jms监听-赛事不存在,event="+event);
			return;
		}
		int scheduleId = schedule.getScheduleID();
		Integer betState = schedule.getBetState();
		Date betEndTime = schedule.getBetEndTime();
		logger.info("event增加的Jms监听,event="+event+",scheduleId="+scheduleId+",betState="+betState+",betEndTime="+betEndTime);
		
		String[] events = StringUtils.split(event, "_");
		String result = lotteryService.getJingcaimatches("J00001", events[1], events[2], events[3]);
		if (StringUtils.isBlank(result)) {
			return;
		}
		JSONObject fromObject = JSONObject.fromObject(result);
		if (fromObject==null) {
			return;
		}
		String errorCode = fromObject.getString("errorCode");
		String valueString = fromObject.getString("value");
		if (!StringUtils.equals(errorCode, "0")||StringUtils.equals(valueString, "null")) {
			return;
		}
		JSONObject valueObject = fromObject.getJSONObject("value");
		String matchesString = valueObject.getString("matches");
		if (StringUtils.equals(matchesString, "null")) {
			return;
		}
		JSONObject matchesObject = valueObject.getJSONObject("matches");
		
		boolean isModify = false;
		String state = matchesObject.getString("state");
		if (StringUtils.equals(state, "0")&&(betState==null||betState==0)) {
			isModify = true;
			schedule.setBetState(1); //开售
		}
		String endtime = matchesObject.getString("endtime");
		if (StringUtils.isNotBlank(endtime)&&!StringUtils.equals(endtime, "null")) {
			Date endDate = new Date(Long.parseLong(endtime));
			if (betEndTime==null||betEndTime.getTime()!=endDate.getTime()) {
				isModify = true;
				schedule.setBetEndTime(endDate);
			}
		}
		if (isModify) {
			schedule.merge();
			//发送赛事缓存更新的Jms
			sendJmsJczUtil.sendSchedulesCacheUpdateJms(schedule.getScheduleID());
		}
	}
	
}
