package com.ruyicai.dataanalysis.service;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.cache.CacheService;
import com.ruyicai.dataanalysis.consts.LetgoalCompany;
import com.ruyicai.dataanalysis.consts.StandardCompany;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.dto.LetgoalDto;
import com.ruyicai.dataanalysis.dto.StandardDto;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.jc.JingCaiUtil;

@Service
public class AsyncService {

	private Logger logger = LoggerFactory.getLogger(AsyncService.class);
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private StandardService standardService;
	
	@Autowired
	private LetgoalService letgoalService;
	
	@Async
	public void updateUsualStandardsAvg(Integer scheduleId) {
		try {
			long startMillis = System.currentTimeMillis();
			Schedule schedule = Schedule.findScheduleWOBuild(scheduleId);
			String event = schedule.getEvent();
			if (StringUtils.isBlank(event)) {
				return;
			}
			String day = JingCaiUtil.getDayByEvent(event);
			if (StringUtils.isBlank(day)) {
				return;
			}
			String key = StringUtil.join("_", "dadaanalysis", "UsualStandards", day, "avg");
			Map<String, StandardDto> map = cacheService.get(key);
			if (map==null) {
				return;
			}
			StandardDto standardDto = standardService.getAvgStandardDto(schedule);
			if (standardDto==null) {
				return;
			}
			map.put(event, standardDto);
			cacheService.set(key, map);
			long endMillis = System.currentTimeMillis();
			logger.info("updateUsualStandardsAvg用时:"+(endMillis-startMillis)+",scheduleId="+scheduleId);
		} catch (Exception e) {
			logger.error("updateUsualStandardsAvg发生异常", e);
		}
	}
	
	@Async
	public void updateUsualStandards(Integer scheduleId, String companyId) {
		try {
			long startMillis = System.currentTimeMillis();
			boolean containsCompanyId = StandardCompany.containsCompanyId(companyId);
			if (!containsCompanyId) {
				return;
			}
			Schedule schedule = Schedule.findScheduleWOBuild(scheduleId);
			if (schedule==null) {
				return;
			}
			String event = schedule.getEvent();
			if (StringUtils.isBlank(event)) {
				return;
			}
			String day = JingCaiUtil.getDayByEvent(event);
			if (StringUtils.isBlank(day)) {
				return;
			}
			String key = StringUtil.join("_", "dadaanalysis", "UsualStandards", day, companyId);
			Map<String, StandardDto> map = cacheService.get(key);
			if (map==null) {
				return;
			}
			StandardDto standardDto = standardService.getStandardDtoByCompanyId(scheduleId, Integer.parseInt(companyId));
			if (standardDto==null) {
				return;
			}
			map.put(event, standardDto);
			cacheService.set(key, map);
			long endMillis = System.currentTimeMillis();
			logger.info("updateUsualStandardsByCompanyId用时:"+(endMillis-startMillis)+",scheduleId="+scheduleId);
		} catch (Exception e) {
			logger.error("updateUsualStandardsByCompanyId发生异常", e);
		}
	}
	
	@Async
	public void updateUsualLetgoals(Integer scheduleId, String companyId) {
		try {
			long startMillis = System.currentTimeMillis();
			boolean containsCompanyId = LetgoalCompany.containsCompanyId(companyId);
			if (!containsCompanyId) {
				return;
			}
			Schedule schedule = Schedule.findScheduleWOBuild(scheduleId);
			if (schedule==null) {
				return;
			}
			String event = schedule.getEvent();
			if (StringUtils.isBlank(event)) {
				return;
			}
			String day = JingCaiUtil.getDayByEvent(event);
			if (StringUtils.isBlank(day)) {
				return;
			}
			String key = StringUtil.join("_", "dadaanalysis", "UsualLetgoals", day, companyId);
			Map<String, LetgoalDto> map = cacheService.get(key);
			if (map==null) {
				return;
			}
			LetgoalDto letgoalDto = letgoalService.getLetgoalDtoByCompanyId(scheduleId, Integer.parseInt(companyId));
			if (letgoalDto==null) {
				return;
			}
			map.put(event, letgoalDto);
			cacheService.set(key, map);
			long endMillis = System.currentTimeMillis();
			logger.info("updateUsualLetgoalsByCompanyId用时:"+(endMillis-startMillis)+",scheduleId="+scheduleId);
		} catch (Exception e) {
			logger.error("updateUsualLetgoalsByCompanyId发生异常", e);
		}
	}
	
	/*@Async
	public void updateSchedulesByEventCache(String event) {
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
			logger.info("updateSchedulesByEventCache用时:"+(endMillis-startMillis)+",event="+event);
		} catch (Exception e) {
			logger.error("updateSchedulesByEventCache发生异常,event="+event, e);
		}
	}
	
	@Async
	public void updateProcessingSchedulesCache() {
		try {
			long startMillis = System.currentTimeMillis();
			String key = StringUtil.join("_", "dadaanalysis", "ProcessingSchedules");
			List<Schedule> list = Schedule.findProcessingMatches();
			if (list!=null) {
				cacheService.set(key, 72*60*60, list);
			}
			long endMillis = System.currentTimeMillis();
			logger.info("updateProcessingSchedulesCache用时:"+(endMillis-startMillis));
		} catch (Exception e) {
			logger.error("updateProcessingSchedulesCache发生异常", e);
		}
	}*/
	
	/**
	 * 保存GlobalCache
	 * @param key
	 * @param value
	 */
	/*@Async
	public void saveGlobalCache(String key, String value) {
		try {
			GlobalCache globalInfo = new GlobalCache();
			globalInfo.setId(key);
			globalInfo.setValue(value);
			globalInfo.persist();
		} catch (Exception e) {
			logger.error("saveGlobalCache发生异常", e);
		}
	}*/
	
}
