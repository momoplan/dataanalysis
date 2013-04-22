package com.ruyicai.dataanalysis.util.jcl;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 竞彩篮球-发送JMS公共类
 * @author Administrator
 *
 */
@Service
public class SendJmsJclUtil {
	
	private Logger logger = LoggerFactory.getLogger(SendJmsJclUtil.class);

	@Produce(uri = "jms:queue:rankingJclUpdate")
	private ProducerTemplate rankingJclUpdateTemplate;
	
	@Produce(uri = "jms:queue:scheduleJclUpdate")
	private ProducerTemplate scheduleJclUpdateTemplate;
	
	/**
	 * 联赛排名的JMS
	 * @param body
	 */
	public void sendRankingUpdateJms(Integer body) {
		try {
			//logger.info("rankingJclUpdateTemplate start");
			rankingJclUpdateTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 赛果更新的JMS
	 * @param event
	 */
	public void sendScheduleUpdateJms(String event) {
		try {
			logger.info("scheduleJclUpdateTemplate start, event="+event);
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("EVENT", event);
			scheduleJclUpdateTemplate.sendBodyAndHeaders(null, header);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
