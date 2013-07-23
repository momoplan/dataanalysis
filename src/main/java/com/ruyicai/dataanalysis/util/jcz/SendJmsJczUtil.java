package com.ruyicai.dataanalysis.util.jcz;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 竞彩足球Jms发送
 * @author Administrator
 *
 */
@Service
public class SendJmsJczUtil {

	private Logger logger = LoggerFactory.getLogger(SendJmsJczUtil.class);
	
	@Produce(uri = "jms:topic:scheduleFinish")
	private ProducerTemplate scheduleFinishTemplate;
	
	@Produce(uri = "jms:topic:rankingUpdate")
	private ProducerTemplate rankingUpdateTemplate;
	
	@Produce(uri = "jms:topic:standardUpdate")
	private ProducerTemplate standardUpdateTemplate;
	
	/**
	 * 更新欧赔的JMS
	 * @param body
	 */
	public void sendStandardUpdateJMS(String body) {
		try {
			standardUpdateTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 联赛排名更新的Jms
	 * @param body
	 */
	public void sendRankingUpdateJMS(Integer body) {
		try {
			logger.info("rankingUpdateTemplate start, body={}", body);
			rankingUpdateTemplate.sendBody(body);
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
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("EVENT", event);
			
			logger.info("scheduleJclFinishTemplate start, event={}", event);
			scheduleFinishTemplate.sendBodyAndHeaders(null, header);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
