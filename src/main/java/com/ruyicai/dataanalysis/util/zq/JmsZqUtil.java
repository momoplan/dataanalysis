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
	
	@Produce(uri = "jms:topic:letgoalCacheUpdate")
	private ProducerTemplate letgoalCacheUpdateTemplate;
	
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
	
	@Produce(uri = "jms:topic:standardAvgUpdate")
	private ProducerTemplate standardAvgUpdateTemplate;
	
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
	
	@Produce(uri = "jms:topic:standardCacheUpdate")
	private ProducerTemplate standardCacheUpdateTemplate;
	
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
	
	@Produce(uri = "jms:topic:standardDetailSave")
	private ProducerTemplate standardDetailSaveTemplate;
	
	/**
	 * 发送保存欧赔变化的Jms
	 * @param body
	 */
	public void standardDetailSaveJms(String body) {
		try {
			standardDetailSaveTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error("足球发送保存欧赔变化的Jms发生异常", e);
		}
	}
	
	@Produce(uri = "jms:topic:rankingUpdate")
	private ProducerTemplate rankingUpdateTemplate;
	
	/**
	 * 发送联赛排名更新的Jms
	 * @param body
	 */
	public void rankingUpdateJms(Integer body) {
		try {
			logger.info("rankingUpdateTemplate start, body={}", body);
			rankingUpdateTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error("足球发送联赛排名更新的Jms发生异常", e);
		}
	}
	
	@Produce(uri = "jms:topic:scheduleFinish")
	private ProducerTemplate scheduleFinishTemplate;
	
	/**
	 * 发送比赛完场的Jms
	 * @param event
	 */
	public void scheduleFinishJms(String event) {
		try {
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("EVENT", event);
			
			logger.info("scheduleFinishTemplate start, event={}", event);
			scheduleFinishTemplate.sendBodyAndHeaders(null, header);
		} catch(Exception e) {
			logger.error("足球发送比赛完场的Jms发生异常", e);
		}
	}
	
	@Produce(uri = "jms:topic:scoreModify")
	private ProducerTemplate scoreModifyTemplate;
	
	/**
	 * 发送比分变化的Jms
	 * @param event
	 */
	public void scoreModifyJms(String event) {
		try {
			Map<String, Object> header = new HashMap<String, Object>();
			header.put("EVENT", event);
			
			logger.info("scoreModifyTemplate start, event={}", event);
			scoreModifyTemplate.sendBodyAndHeaders(null, header);
		} catch(Exception e) {
			logger.error("足球发送比分变化的Jms发生异常", e);
		}
	}
	
	@Produce(uri = "jms:topic:schedulesCacheUpdate")
	private ProducerTemplate schedulesCacheUpdateTemplate;
	
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
	
	@Produce(uri = "jms:topic:scheduleUpdate")
	private ProducerTemplate scheduleUpdateTemplate;
	
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
	
	@Produce(uri = "jms:topic:schedulesByEventCacheUpdate")
	private ProducerTemplate schedulesByEventCacheUpdateTemplate;
	
	/**
	 * 发送按event查询赛事缓存更新的Jms
	 * @param body
	 */
	public void schedulesByEventCacheUpdate(String event) {
		try {
			schedulesByEventCacheUpdateTemplate.sendBody(event);
		} catch(Exception e) {
			logger.error("足球发送按event查询赛事缓存更新的Jms发生异常", e);
		}
	}
	
	@Produce(uri = "jms:topic:processingSchedulesCacheUpdate")
	private ProducerTemplate processingSchedulesCacheUpdateTemplate;
	
	/**
	 * 发送进行中赛事缓存更新的Jms
	 * @param body
	 */
	public void processingSchedulesCacheUpdate() {
		try {
			processingSchedulesCacheUpdateTemplate.sendBody(null);
		} catch(Exception e) {
			logger.error("足球发送进行中赛事缓存更新的Jms发生异常", e);
		}
	}
	
	@Produce(uri = "jms:topic:sendAsianCup")
	private ProducerTemplate sendAsianCupTemplate;
	
	/**
	 * 发送杯赛数据更新的Jms
	 * @param league
	 */
	public void sendAsianCupJMS(String league){
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("cupJmsType", league);
		logger.info("sendAsianCupTemplate start, cupJmsType={}", league);
		sendAsianCupTemplate.sendBodyAndHeaders(null, headers);
	}
	
}
