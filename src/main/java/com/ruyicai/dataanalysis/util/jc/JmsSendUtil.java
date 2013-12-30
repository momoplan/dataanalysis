package com.ruyicai.dataanalysis.util.jc;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JmsSendUtil {
	
	private Logger logger = LoggerFactory.getLogger(JmsSendUtil.class);

	@Produce(uri = "jms:topic:scheduleEventAdd")
	private ProducerTemplate scheduleEventAddTemplate;
	
	/**
	 * 发送event增加的Jms
	 * @param event
	 */
	public void scheduleEventAdd(String event) {
		try {
			scheduleEventAddTemplate.sendBody(event);
		} catch(Exception e) {
			logger.error("足球发送event增加的Jms发生异常", e);
		}
	}
	
}
