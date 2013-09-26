package com.ruyicai.dataanalysis.listener;

import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;

import org.apache.camel.Body;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.service.GlobalInfoService;
import com.ruyicai.dataanalysis.util.ThreadPoolUtil;

/**
 * 数据分析更新的Jms
 * @author Administrator
 *
 */
@Service
public class InfoJczUpdateListener {

	private Logger logger = LoggerFactory.getLogger(InfoJczUpdateListener.class);
	
	private ThreadPoolExecutor infoUpdateJmsExecutor;
	
	@Autowired
	private GlobalInfoService infoService;
	
	@PostConstruct
	public void init() {
		infoUpdateJmsExecutor = ThreadPoolUtil.createTaskExecutor("infoUpdateJms", 10);
	}
	
	public void update(@Body String scheduleID) {
		try {
			long startmillis = System.currentTimeMillis();
			logger.info("数据分析更新的Jms的处理开始,scheduleID="+scheduleID);
			ProcessThread task = new ProcessThread(scheduleID);
			logger.info("infoUpdateJmsExecutor,size="+infoUpdateJmsExecutor.getQueue().size());
			infoUpdateJmsExecutor.execute(task);
			long endmillis = System.currentTimeMillis();
			logger.info("数据分析更新的Jms结束，共用时:"+(endmillis - startmillis)+",scheduleID="+scheduleID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class ProcessThread implements Runnable {
		private String scheduleId;
		
		private ProcessThread(String scheduleId) {
			this.scheduleId = scheduleId;
		}
		
		@Override
		public void run() {
			long startmillis = System.currentTimeMillis();
			logger.info("InfoJczUpdateListener-ProcessThread开始,scheduleId="+scheduleId);
			infoService.updateInfo(Integer.parseInt(scheduleId));
			long endmillis = System.currentTimeMillis();
			logger.info("InfoJczUpdateListener-ProcessThread结束,用时:"+(endmillis-startmillis)+"scheduleId="+scheduleId);
		}
		
	}
	
}
