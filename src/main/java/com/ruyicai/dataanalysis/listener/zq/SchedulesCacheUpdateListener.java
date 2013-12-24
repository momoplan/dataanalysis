package com.ruyicai.dataanalysis.listener.zq;

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
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.service.GlobalInfoService;
import com.ruyicai.dataanalysis.service.dto.ScheduleDTO;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * 足球赛事缓存更新的Jms监听
 * @author Administrator
 *
 */
@Service
public class SchedulesCacheUpdateListener {

	private Logger logger = LoggerFactory.getLogger(SchedulesCacheUpdateListener.class);
	
	@Autowired
	private GlobalInfoService infoService;
	
	@Autowired
	private CacheService cacheService;
	
	public void process(@Body String scheduleId) {
		try {
			long startmillis = System.currentTimeMillis();
			if (StringUtils.isBlank(scheduleId)) {
				return;
			}
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleId));
			if (schedule==null) {
				return;
			}
			Date matchTime = schedule.getMatchTime();
			if (matchTime==null) {
				return;
			}
			String day = new SimpleDateFormat("yyyyMMdd").format(matchTime);
			String key = StringUtil.join("_", "dadaanalysis", "schedulesByDayZq", day);
			Map<String, List<ScheduleDTO>> value = cacheService.get(key);
			if (value==null) {
				return;
			}
			value = infoService.getSchedules(day);
			if (value!=null) {
				cacheService.set(key, value);
			}
			long endmillis = System.currentTimeMillis();
			logger.info("足球赛事缓存更新的Jms监听,用时:"+(endmillis - startmillis)+",scheduleId="+scheduleId+",day="+day);
		} catch (Exception e) {
			logger.error("足球赛事缓存更新的Jms监听发生异常,scheduleId="+scheduleId, e);
		}
	}
	
}
