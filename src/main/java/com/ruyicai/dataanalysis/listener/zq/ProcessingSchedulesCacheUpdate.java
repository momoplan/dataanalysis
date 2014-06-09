package com.ruyicai.dataanalysis.listener.zq;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.cache.CacheService;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * 足球-进行中赛事缓存更新
 * @author Administrator
 *
 */
@Service
public class ProcessingSchedulesCacheUpdate {

	private Logger logger = LoggerFactory.getLogger(ProcessingSchedulesCacheUpdate.class);
	
	@Autowired
	private CacheService cacheService;
	
	public void process() {
		try {
			long startMillis = System.currentTimeMillis();
			String key = StringUtil.join("_", "dadaanalysis", "ProcessingSchedules");
			List<Schedule> list = Schedule.findProcessingMatches();
			if (list!=null) {
				cacheService.set(key, 72*60*60, list);
			}
			long endMillis = System.currentTimeMillis();
			logger.info("足球-进行中赛事缓存更新,用时:"+(endMillis-startMillis));
		} catch (Exception e) {
			logger.error("足球-进行中赛事缓存更新发生异常", e);
		}
	}
	
}
