package com.ruyicai.dataanalysis.listener;

import org.apache.camel.Body;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.service.GlobalInfoService;

/**
 * 数据分析更新的Jms
 * @author Administrator
 *
 */
@Service
public class InfoJczUpdateListener {

	private Logger logger = LoggerFactory.getLogger(InfoJczUpdateListener.class);
	
	@Autowired
	private GlobalInfoService infoService;
	
	public void update(@Body String scheduleID) {
		try {
			long startmillis = System.currentTimeMillis();
			logger.info("数据分析更新的Jms的处理开始,scheduleID="+scheduleID);
			infoService.updateInfo(Integer.parseInt(scheduleID));
			long endmillis = System.currentTimeMillis();
			logger.info("数据分析更新的Jms结束，共用时:"+(endmillis - startmillis)+",scheduleID="+scheduleID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
