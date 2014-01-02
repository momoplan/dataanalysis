package com.ruyicai.dataanalysis.listener.lq;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.cache.CacheService;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.service.dto.lq.ScheduleJclDTO;
import com.ruyicai.dataanalysis.service.lq.GlobalInfoJclService;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * 篮球赛事缓存更新的Jms监听
 * @author Administrator
 *
 */
@Service
public class SchedulesJclCacheUpdateListener {

	private Logger logger = LoggerFactory.getLogger(SchedulesJclCacheUpdateListener.class);
	
	@Autowired
	private GlobalInfoJclService infoService;
	
	@Autowired
	private CacheService cacheService;
	
	public void process(@Body String scheduleId) {
		try {
			long startmillis = System.currentTimeMillis();
			if (StringUtils.isBlank(scheduleId)) {
				return;
			}
			ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJcl(Integer.parseInt(scheduleId));
			if (scheduleJcl==null) {
				return;
			}
			Date matchTime = scheduleJcl.getMatchTime();
			updateCacheByDate(matchTime);
			long endmillis = System.currentTimeMillis();
			logger.info("篮球赛事缓存更新的Jms监听,用时:"+(endmillis - startmillis)+",scheduleId="+scheduleId);
		} catch (Exception e) {
			logger.error("篮球赛事缓存更新的Jms监听发生异常,scheduleId="+scheduleId, e);
		}
	}
	
	public void updateCacheByDate(Date matchTime) {
		if (matchTime==null) {
			return;
		}
		String day = new SimpleDateFormat("yyyyMMdd").format(matchTime);
		String key = StringUtil.join("_", "dadaanalysis", "schedulesByDayLq", day);
		Map<String, List<ScheduleJclDTO>> value = cacheService.get(key);
		if (value==null) {
			return;
		}
		value = infoService.getSchedules(day);
		if (value!=null) {
			cacheService.set(key, value);
		}
	}
	
}
