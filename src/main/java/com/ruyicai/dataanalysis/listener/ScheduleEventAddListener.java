package com.ruyicai.dataanalysis.listener;

import java.util.Date;
import net.sf.json.JSONObject;
import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.service.back.LotteryService;

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
		String result = lotteryService.getJingcaimatches("J00001", events[1], events[2], events[3]);
		if (StringUtils.isBlank(result)) {
			return;
		}
		JSONObject matchObject = JSONObject.fromObject(result);
		if (matchObject==null) {
			return;
		}
		String errorCode = matchObject.getString("errorCode");
		if (StringUtils.equals(errorCode, "0")) {
			
		}
	}

	private void processZq(String event) {
		
	}
	
}
