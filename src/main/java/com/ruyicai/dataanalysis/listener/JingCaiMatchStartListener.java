package com.ruyicai.dataanalysis.listener;

import java.util.Date;
import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;

/**
 * 竞彩赛事开售监听
 * @author Administrator
 *
 */
@Service
public class JingCaiMatchStartListener {

	private Logger logger = LoggerFactory.getLogger(JingCaiMatchStartListener.class);
	
	public void process(@Header("EVENT") String event, @Header("ENDTIME") Date endTime) {
		try {
			logger.info("竞彩赛事开售监听 start event="+event+",endTime="+endTime);
			if (StringUtils.isBlank(event)||endTime==null) {
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
	
	private void processLq(String event, Date endTime) {
		ScheduleJcl scheduleJcl = ScheduleJcl.findByEvent(event, false);
		if (scheduleJcl==null) {
			return;
		}
		scheduleJcl.setBetState(1); //开售
		scheduleJcl.setBetEndTime(endTime);
		scheduleJcl.merge();
	}
	
	private void processZq(String event, Date endTime) {
		Schedule schedule = Schedule.findByEvent(event, false);
		if (schedule==null) {
			return;
		}
		schedule.setBetState(1); //开售
		schedule.setBetEndTime(endTime);
		schedule.merge();
	}
	
}
