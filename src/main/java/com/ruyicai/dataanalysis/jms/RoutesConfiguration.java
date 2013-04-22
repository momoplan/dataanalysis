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

	Logger logger = LoggerFactory.getLogger(RoutesConfiguration.class);

	@Resource(name = "camelContext")
	private CamelContext camelContext;
	
	@Resource(name = "lotteryCamelContext")
	private CamelContext lotteryCamelContext;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("init camel routes");
		try {
			camelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					deadLetterChannel("jms:queue:dead").maximumRedeliveries(-1)
					.redeliveryDelay(3000);
					//竞彩足球
					from("jms:queue:updateStandard?concurrentConsumers=5").to("bean:standarJczUpdateListener?method=update").routeId("竞彩足球-百家欧赔更新");
					from("jms:queue:updateRanking?concurrentConsumers=5").to("bean:rankingJczUpdateListener?method=update").routeId("竞彩足球-联赛排名更新");
					//竞彩篮球
					from("jms:queue:standardJclUpdate?concurrentConsumers=5").to("bean:standarJclUpdateListener?method=update").routeId("竞彩篮球-百家欧赔更新");
					from("jms:queue:rankingJclUpdate?concurrentConsumers=5").to("bean:rankingJclUpdateListener?method=update").routeId("竞彩篮球-联赛排名更新");
					from("jms:queue:scheduleJclUpdate?concurrentConsumers=5").to("bean:scheduleJclUpdateListener?method=update").routeId("竞彩篮球-赛事让分、预设总分更新");
				}
			});
		} catch (Exception e) {
			logger.error("camel context start failed", e.getMessage());
			e.printStackTrace();
		}
		
		logger.info("init lotteryCamel routes");
		try {
			lotteryCamelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					deadLetterChannel("jmsLottery:queue:dead").maximumRedeliveries(-1)
					.redeliveryDelay(3000);
					from("jmsLottery:queue:VirtualTopicConsumers.dataanalysis.jingcairesult-topic").to("bean:scheduleJclUpdateListener?method=update").routeId("竞彩赛果更新通知");
				}
			});
		} catch (Exception e) {
			logger.error("lotteryCamel context start failed", e.getMessage());
			e.printStackTrace();
		}
	}

}
