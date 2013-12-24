package com.ruyicai.dataanalysis.listener;

import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.util.lq.SendJmsJclUtil;
import com.ruyicai.dataanalysis.util.zq.SendJmsJczUtil;

/**
 *  竞彩赛事停售监听
 * @author Administrator
 *
 */
@Service
public class JingCaiMatchEndListener {

	private Logger logger = LoggerFactory.getLogger(JingCaiMatchEndListener.class);
	
	@Autowired
	private SendJmsJclUtil sendJmsJclUtil;
	
	@Autowired
	private SendJmsJczUtil sendJmsJczUtil;
	
	public void process(@Header("EVENT") String event) {
		try {
			logger.info("竞彩赛事停售监听 start event="+event);
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
			logger.error("竞彩赛事停售监听发生异常,event="+event, e);
		}
	}
	
	private void processLq(String event) {
		ScheduleJcl scheduleJcl = ScheduleJcl.findByEvent(event, false);
		if (scheduleJcl==null) {
			return;
		}
		scheduleJcl.setBetState(0); //停售
		scheduleJcl.merge();
		//发送赛事缓存更新的Jms
		sendJmsJclUtil.sendSchedulesCacheUpdateJms(scheduleJcl.getScheduleId());
	}
	
	private void processZq(String event) {
		Schedule schedule = Schedule.findByEvent(event, false);
		if (schedule==null) {
			return;
		}
		schedule.setBetState(0); //停售
		schedule.merge();
		//发送赛事缓存更新的Jms
		sendJmsJczUtil.sendSchedulesCacheUpdateJms(schedule.getScheduleID());
	}
	
}
