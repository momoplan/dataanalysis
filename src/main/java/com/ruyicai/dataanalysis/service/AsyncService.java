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
	
}
