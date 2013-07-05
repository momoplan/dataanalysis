package com.ruyicai.dataanalysis.util.jcz;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 竞彩足球Jms发送
 * @author Administrator
 *
 */
public class SendJmsJczUtil {

	private Logger logger = LoggerFactory.getLogger(SendJmsJczUtil.class);
	
	@Produce(uri = "jms:queue:scheduleFinish")
	private ProducerTemplate scheduleFinishTemplate;
	
	@Produce(uri = "jms:queue:updateRanking")
	private ProducerTemplate updateRankingTemplate;
	
	/**
	 * 联赛排名更新的Jms
	 * @param body
	 */
	public void sendUpdateRankingJMS(Integer body) {
		try {
			logger.info("updateRankingTemplate start, body={}", body);
			updateRankingTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 比赛完场的JMS
	 * @param event
	 */
	public void sendScheduleFinishJms(String event) {
		try {
			logger.info("scheduleJclFinishTemplate start, event={}", event);
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("EVENT", event);
			scheduleFinishTemplate.sendBodyAndHeaders(null, header);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
