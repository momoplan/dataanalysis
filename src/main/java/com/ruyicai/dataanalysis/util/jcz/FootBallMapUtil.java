package com.ruyicai.dataanalysis.util.jcz;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FootBallMapUtil {

	private Logger logger = LoggerFactory.getLogger(FootBallMapUtil.class);
	
	public Map<String, Boolean> scheduleMap = new HashMap<String, Boolean>();
	
	public Map<String, Boolean> letgoalMap = new HashMap<String, Boolean>();
	
	public Map<String, Boolean> europeCompanyMap = new HashMap<String, Boolean>();
	
	public Map<String, Boolean> standardMap = new HashMap<String, Boolean>();
	
	public void clearMap() {
		logger.info("清空足球Map开始");
		try {
			scheduleMap.clear();
			letgoalMap.clear();
			europeCompanyMap.clear();
			standardMap.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("清空足球Map结束");
	}
	
}
