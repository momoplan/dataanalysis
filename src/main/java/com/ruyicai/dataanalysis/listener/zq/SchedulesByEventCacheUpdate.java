package com.ruyicai.dataanalysis.listener.zq;

import java.util.List;
import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.cache.CacheService;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.jc.JingCaiUtil;

/**
 * 按event查询赛事缓存的更新
 * @author Administrator
 *
 */
@Service
public class SchedulesByEventCacheUpdate {

	private Logger logger = LoggerFactory.getLogger(SchedulesByEventCacheUpdate.class);
	
	@Autowired
	private CacheService cacheService;
	
	public void process(@Body String event) {
		try {
			long startMillis = System.currentTimeMillis();
			if (StringUtils.isBlank(event)) {
				return;
			}
			String day = JingCaiUtil.getDayByEvent(event);
			if (StringUtils.isBlank(day)) {
				return;
			}
			String key = StringUtil.join("_", "dadaanalysis", "SchedulesByEvent", day);
			List<Schedule> list = Schedule.findByEventAndDay(day);
			if (list!=null&&list.size()>0) {
				cacheService.set(key, 72*60*60, list);
			}
			long endMillis = System.currentTimeMillis();
			logger.info("按event查询赛事缓存的更新用时:"+(endMillis-startMillis)+",event="+event);
		} catch (Exception e) {
			logger.error("updateSchedulesByEventCache发生异常,event="+event, e);
		}
	}
	
}
