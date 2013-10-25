package com.ruyicai.dataanalysis.jms;

import javax.annotation.Resource;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class RoutesConfiguration implements ApplicationListener<ContextRefreshedEvent> {

	private Logger logger = LoggerFactory.getLogger(RoutesConfiguration.class);

	@Resource(name = "camelContext")
	private CamelContext camelContext;
	
	@Resource(name = "lotteryCamelContext")
	private CamelContext lotteryCamelContext;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("init dataanalysis camel routes");
		try {
			camelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					deadLetterChannel("jms:queue:dead").maximumRedeliveries(-1)
					.redeliveryDelay(3000);
					//足球
					from("jms:queue:VirtualTopicConsumers.dataanalysis.standardUpdate?concurrentConsumers=20").to(
							"bean:standardUpdateListener?method=update").routeId("足球-欧赔更新");
					from("jms:queue:VirtualTopicConsumers.dataanalysis.standardAvgUpdate?concurrentConsumers=20").to(
							"bean:standardAvgUpdateListener?method=update").routeId("足球-平均欧赔更新");
					from("jms:queue:VirtualTopicConsumers.dataanalysis.rankingUpdate?concurrentConsumers=10").to(
							"bean:rankingJczUpdateListener?method=update").routeId("足球-联赛排名更新");
					from("jms:queue:VirtualTopicConsumers.dataanalysis.letgoalCacheUpdate?concurrentConsumers=20").to(
							"bean:letgoalCacheUpdateListener?method=update").routeId("足球-亚赔缓存更新");
					//篮球
					from("jms:queue:VirtualTopicConsumers.dataanalysis.standardJclUpdate?concurrentConsumers=10").to(
							"bean:standarJclUpdateListener?method=update").routeId("竞彩篮球-百家欧赔更新");
					from("jms:queue:VirtualTopicConsumers.dataanalysis.rankingJclUpdate?concurrentConsumers=10").to(
							"bean:rankingJclUpdateListener?method=update").routeId("竞彩篮球-联赛排名更新");
					from("jms:queue:VirtualTopicConsumers.dataanalysis.scheduleJclFinish?concurrentConsumers=10").to(
							"bean:scheduleJclFinishListener?method=update").routeId("竞彩篮球-完场的监听");
				}
			});
		} catch (Exception e) {
			logger.error("dataanalysis camel context start failed", e.getMessage());
			e.printStackTrace();
		}
		
		logger.info("init lottery camel routes");
		try {
			lotteryCamelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					deadLetterChannel("jmsLottery:queue:dead").maximumRedeliveries(-1)
					.redeliveryDelay(3000);
					from("jmsLottery:queue:VirtualTopicConsumers.dataanalysis.jingcairesult-topic?concurrentConsumers=10").to(
							"bean:scheduleJclFinishListener?method=update").routeId("竞彩赛果更新通知");
				}
			});
		} catch (Exception e) {
			logger.error("lottery camel context start failed", e.getMessage());
			e.printStackTrace();
		}
	}

}
