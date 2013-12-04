package com.ruyicai.dataanalysis.util.lq;

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

	@Produce(uri = "jms:topic:scheduleJclFinish")
	private ProducerTemplate scheduleJclFinishTemplate;
	
	@Produce(uri = "jms:topic:rankingJclUpdate")
	private ProducerTemplate rankingJclUpdateTemplate;
	
	@Produce(uri = "jms:topic:standardJclUpdate")
	private ProducerTemplate standardJclUpdateTemplate;
	
	@Produce(uri = "jms:topic:scoreModifyJcl")
	private ProducerTemplate scoreModifyJclTemplate;
	
	/**
	 * 欧赔更新的JMS
	 * @param body
	 */
	public void sendStandardUpdateJMS(String body) {
		try {
			//logger.info("standardJclUpdateTemplate start");
			standardJclUpdateTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 联赛排名的JMS
	 * @param body
	 */
	public void sendRankingUpdateJms(Integer body) {
		try {
			logger.info("rankingJclUpdateTemplate start, body={}", body);
			rankingJclUpdateTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 赛果更新的JMS
	 * @param event
	 */
	public void sendScheduleFinishJms(String event) {
		try {
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("EVENT", event);
			
			logger.info("scheduleJclFinishTemplate start, event={}", event);
			scheduleJclFinishTemplate.sendBodyAndHeaders(null, header);
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
			
			logger.info("scoreModifyJclTemplate start, event={}", event);
			scoreModifyJclTemplate.sendBodyAndHeaders(null, header);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
