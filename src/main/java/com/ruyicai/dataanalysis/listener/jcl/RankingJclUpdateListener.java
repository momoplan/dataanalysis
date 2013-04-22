package com.ruyicai.dataanalysis.listener.jcl;

import org.apache.camel.Body;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.service.jcl.AnalysisJclService;

/**
 * 竞彩篮球-联赛排名更新JMS的处理
 * @author Administrator
 *
 */
@Service
public class RankingJclUpdateListener {
	
	private Logger logger = LoggerFactory.getLogger(RankingJclUpdateListener.class);

	@Autowired
	private AnalysisJclService analysisJclService;
	
	public void update(@Body String scheduleID) {
		try {
			logger.info("竞彩篮球-联赛排名JMS start, scheduleID:"+scheduleID);
			analysisJclService.getRanking(Integer.parseInt(scheduleID), false);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
