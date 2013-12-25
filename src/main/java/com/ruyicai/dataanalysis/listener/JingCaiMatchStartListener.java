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
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.lq.SendJmsJclUtil;
import com.ruyicai.dataanalysis.util.zq.SendJmsJczUtil;

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
	private SendJmsJczUtil sendJmsJczUtil;
	
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
			return;
		}
		Integer betState = scheduleJcl.getBetState();
		Date betEndTime = scheduleJcl.getBetEndTime();
		
		Date endDate = DateUtil.parse("yyyy-MM-dd HH:mm:ss", endTime);
		boolean modify = false;
		//投注状态
		if (betState==null || betState!=1) {
			scheduleJcl.setBetState(1); //开售
			modify = true;
		}
		//投注截止时间
		if (endDate!=null && (betEndTime==null || betEndTime.getTime()!=endDate.getTime())) {
			scheduleJcl.setBetEndTime(endDate);
			modify = true;
		}
		if (modify) {
			scheduleJcl.merge();
			//发送赛事缓存更新的Jms
			sendJmsJclUtil.sendSchedulesCacheUpdateJms(scheduleJcl.getScheduleId());
		}
	}
	
	private void processZq(String event, String endTime) {
		Schedule schedule = Schedule.findByEvent(event, false);
		if (schedule==null) {
			return;
		}
		Integer betState = schedule.getBetState();
		Date betEndTime = schedule.getBetEndTime();
		
		Date endDate = DateUtil.parse("yyyy-MM-dd HH:mm:ss", endTime);
		boolean modify = false;
		//投注状态
		if (betState==null || betState!=1) {
			schedule.setBetState(1); //开售
			modify = true;
		}
		//投注截止时间
		if (endDate!=null && (betEndTime==null || betEndTime.getTime()!=endDate.getTime())) {
			schedule.setBetEndTime(endDate);
			modify = true;
		}
		if (modify) {
			schedule.merge();
			//发送赛事缓存更新的Jms
			sendJmsJczUtil.sendSchedulesCacheUpdateJms(schedule.getScheduleID());
		}
	}
	
}
