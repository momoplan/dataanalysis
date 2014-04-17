package com.ruyicai.dataanalysis.util.zq;

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
public class JmsZqUtil {

	private Logger logger = LoggerFactory.getLogger(JmsZqUtil.class);
	
	@Produce(uri = "jms:topic:scheduleFinish")
	private ProducerTemplate scheduleFinishTemplate;
	
	@Produce(uri = "jms:topic:scoreModify")
	private ProducerTemplate scoreModifyTemplate;
	
	@Produce(uri = "jms:topic:rankingUpdate")
	private ProducerTemplate rankingUpdateTemplate;
	
	@Produce(uri = "jms:topic:standardAvgUpdate")
	private ProducerTemplate standardAvgUpdateTemplate;
	
	@Produce(uri = "jms:topic:standardCacheUpdate")
	private ProducerTemplate standardCacheUpdateTemplate;
	
	@Produce(uri = "jms:topic:standardDetailSave")
	private ProducerTemplate standardDetailSaveTemplate;
	
	@Produce(uri = "jms:topic:letgoalCacheUpdate")
	private ProducerTemplate letgoalCacheUpdateTemplate;
	
	@Produce(uri = "jms:topic:schedulesCacheUpdate")
	private ProducerTemplate schedulesCacheUpdateTemplate;
	
	@Produce(uri = "jms:topic:scheduleUpdate")
	private ProducerTemplate scheduleUpdateTemplate;
	
	/**
	 * 发送亚赔缓存更新的Jms
	 * @param body
	 */
	public void letgoalCacheUpdate(String body) {
		try {
			letgoalCacheUpdateTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error("足球发送亚赔缓存更新的Jms发生异常", e);
		}
	}
	
	/**
	 * 发送更新平均欧赔的Jms
	 * @param body
	 */
	public void standardAvgUpdate(String body) {
		try {
			standardAvgUpdateTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error("足球发送更新平均欧赔的Jms发生异常", e);
		}
	}
	
	/**
	 * 发送更新欧赔缓存的Jms
	 * @param body
	 */
	public void standardCacheUpdate(String body) {
		try {
			standardCacheUpdateTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error("足球发送更新欧赔缓存的Jms发生异常", e);
		}
	}
	
	/**
	 * 发送保存欧赔变化的Jms
	 * @param body
	 */
	public void sendStandardDetailSaveJms(String body) {
		try {
			standardDetailSaveTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error("足球发送保存欧赔变化的Jms发生异常", e);
		}
	}
	
	/**
	 * 发送联赛排名更新的Jms
	 * @param body
	 */
	public void sendRankingUpdateJms(Integer body) {
		try {
			logger.info("rankingUpdateTemplate start, body={}", body);
			rankingUpdateTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error("足球发送联赛排名更新的Jms发生异常", e);
		}
	}
	
	/**
	 * 发送比赛完场的Jms
	 * @param event
	 */
	public void sendScheduleFinishJms(String event) {
		try {
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("EVENT", event);
			
			logger.info("scheduleFinishTemplate start, event={}", event);
			scheduleFinishTemplate.sendBodyAndHeaders(null, header);
		} catch(Exception e) {
			logger.error("足球发送比赛完场的Jms发生异常", e);
		}
	}
	
	/**
	 * 发送比分变化的Jms
	 * @param event
	 */
	public void sendScoreModifyJms(String event) {
		try {
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("EVENT", event);
			
			logger.info("scoreModifyTemplate start, event={}", event);
			scoreModifyTemplate.sendBodyAndHeaders(null, header);
		} catch(Exception e) {
			logger.error("足球发送比分变化的Jms发生异常", e);
		}
	}
	
	/**
	 * 发送赛事缓存更新的Jms
	 * @param body
	 */
	public void schedulesCacheUpdate(Integer body) {
		try {
			schedulesCacheUpdateTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error("足球发送赛事缓存更新的Jms发生异常", e);
		}
	}
	
	/**
	 * 赛事更新
	 * @param body
	 */
	public void scheduleUpdate(String body) {
		try {
			scheduleUpdateTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error("足球发送赛事更新的Jms发生异常", e);
		}
	}
	
}
