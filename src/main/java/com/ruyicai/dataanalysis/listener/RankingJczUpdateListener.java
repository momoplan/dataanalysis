package com.ruyicai.dataanalysis.listener;

import org.apache.camel.Body;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.service.AnalysisService;

/**
 * 竞彩足球-联赛排名更新JMS的处理
 * @author Administrator
 *
 */
@Service
public class RankingJczUpdateListener {
	
	private Logger logger = LoggerFactory.getLogger(RankingJczUpdateListener.class);

	@Autowired
	private AnalysisService analysisService;
	
	public void update(@Body String scheduleID) {
		try {
			logger.info("竞彩足球-联赛排名JMS start, scheduleID:"+scheduleID);
			analysisService.getRanking(Integer.parseInt(scheduleID), false);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
