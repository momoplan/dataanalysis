package com.ruyicai.dataanalysis.listener;

import java.util.Date;
import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.util.lq.SendJmsJclUtil;
import com.ruyicai.dataanalysis.util.zq.JmsZqUtil;

/**
 * 竞彩赛事开售监听
 * @author Administrator
 *
 */
@Service
public class JingCaiMatchStartListener {

	private Logger logger = LoggerFactory.getLogger(JingCaiMatchStartListener.class);
	
	@Autowired
	private SendJmsJclUtil sendJmsJclUtil;
	
	@Autowired
	private JmsZqUtil jmsZqUtil;
	
	public void process(@Header("EVENT") String event, @Header("ENDTIME") String endTime) {
		try {
			logger.info("竞彩赛事开售监听 start event="+event+",endTime="+endTime);
			if (StringUtils.isBlank(event)||StringUtils.isBlank(endTime)) {
				return;
			}
			String[] events = StringUtils.split(event, "_");
			String jingCaiType = events[0];
			if (StringUtils.equals(jingCaiType, "0")) { //篮球
				processLq(event, endTime);
			} else if (StringUtils.equals(jingCaiType, "1")) { //足球
				processZq(event, endTime);
			}
		} catch (Exception e) {
			logger.error("竞彩赛事开售监听发生异常,event="+event, e);
		}
	}
	
	private void processLq(String event, String endTime) {
		ScheduleJcl scheduleJcl = ScheduleJcl.findByEvent(event, false);
		if (scheduleJcl==null) {
			logger.info("竞彩赛事开售监听-赛事不存在,event="+event);
			return;
		}
		Integer scheduleId = scheduleJcl.getScheduleId();
		Integer betState = scheduleJcl.getBetState();
		Date betEndTime = scheduleJcl.getBetEndTime();
		logger.info("竞彩赛事开售监听,event="+event+",scheduleId="+scheduleId+",betState="+betState+",betEndTime="+betEndTime);
		
		Date endDate = new Date(Long.parseLong(endTime));
		boolean modify = false;
		//投注状态
		if (betState==null || betState==0) {
			scheduleJcl.setBetState(1); //开售
			modify = true;
		}
		//投注截止时间
		if (endDate!=null && (betEndTime==null || betEndTime.getTime()!=endDate.getTime())) {
			scheduleJcl.setBetEndTime(endDate);
			modify = true;
		}
		if (modify) {
			logger.info("竞彩赛事开售监听-更新状态,event="+event);
			scheduleJcl.merge();
			//发送赛事缓存更新的Jms
			sendJmsJclUtil.sendSchedulesCacheUpdateJms(scheduleJcl.getScheduleId());
		}
	}
	
	private void processZq(String event, String endTime) {
		Schedule schedule = Schedule.findByEvent(event, false);
		if (schedule==null) {
			logger.info("竞彩赛事开售监听-赛事不存在,event="+event);
			return;
		}
		int scheduleId = schedule.getScheduleID();
		Integer betState = schedule.getBetState();
		Date betEndTime = schedule.getBetEndTime();
		logger.info("竞彩赛事开售监听,event="+event+",scheduleId="+scheduleId+",betState="+betState+",betEndTime="+betEndTime);
		
		Date endDate = new Date(Long.parseLong(endTime));
		boolean modify = false;
		//投注状态
		if (betState==null || betState==0) {
			schedule.setBetState(1); //开售
			modify = true;
		}
		//投注截止时间
		if (endDate!=null && (betEndTime==null || betEndTime.getTime()!=endDate.getTime())) {
			schedule.setBetEndTime(endDate);
			modify = true;
		}
		if (modify) {
			logger.info("竞彩赛事开售监听-更新状态,event="+event);
			schedule.merge();
			//发送赛事缓存更新的Jms
			jmsZqUtil.schedulesCacheUpdate(schedule.getScheduleID());
		}
	}
	
}
