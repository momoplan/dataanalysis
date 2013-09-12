package com.ruyicai.dataanalysis.util.bd;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 北单-发送JMS公共类
 * @author Administrator
 *
 */
@Service
public class SendJmsBdUtil {

	private Logger logger = LoggerFactory.getLogger(SendJmsBdUtil.class);
	
	@Produce(uri = "jms:topic:scheduleBdFinish")
	private ProducerTemplate scheduleBdFinishTemplate;
	
	@Produce(uri = "jms:topic:scoreBdModify")
	private ProducerTemplate scoreBdModifyTemplate;
	
	/**
	 * 赛果更新的JMS
	 * @param event
	 */
	public void sendScheduleFinishJms(String event) {
		try {
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("EVENT", event);
			
			logger.info("scheduleBdFinishTemplate start, event={}", event);
			scheduleBdFinishTemplate.sendBodyAndHeaders(null, header);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 比分变化的JMS
	 * @param event
	 */
	public void sendScoreModifyJms(String event) {
		try {
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("EVENT", event);
			
			logger.info("scoreBdModifyTemplate start, event={}", event);
			scoreBdModifyTemplate.sendBodyAndHeaders(null, header);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
