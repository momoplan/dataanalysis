package com.ruyicai.dataanalysis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.cache.CacheService;
import com.ruyicai.dataanalysis.domain.LetGoal;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.dto.LetgoalDto;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class LetgoalService {

	@Autowired
	private CacheService cacheService;
	
	public List<LetgoalDto> getUsualLetgoal(String day, String companyId) {
		List<LetgoalDto> resultList = new ArrayList<LetgoalDto>();
		try {
			String[] days = StringUtils.splitByWholeSeparator(day, ",");
			for (String dayStr : days) {
				if (StringUtils.isNotBlank(dayStr)) {
					Map<String, LetgoalDto> map = getUsualLetgoalByDayCompanyId(dayStr, companyId);
					if (map!=null&&map.size()>0) {
						for(Map.Entry<String, LetgoalDto> entry : map.entrySet()) {
							String event = entry.getKey();
							LetgoalDto dto = entry.getValue();
							dto.setEvent(event);
							resultList.add(dto);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	private Map<String, LetgoalDto> getUsualLetgoalByDayCompanyId(String day, String companyId) {
		try {
			String key = StringUtil.join("_", "dadaanalysis", "UsualLetgoals", day, companyId);
			Map<String, LetgoalDto> map = cacheService.get(key);
			if (map==null) {
				map = new HashMap<String, LetgoalDto>();
				List<Schedule> list = Schedule.findByEventAndDay(day);
				if (list!=null&&list.size()>0) {
					for (Schedule schedule : list) {
						if (StringUtils.isBlank(schedule.getEvent())) {
							continue;
						}
						LetgoalDto dto = getLetgoalDtoByCompanyId(schedule.getScheduleID(), Integer.parseInt(companyId));
						if (dto!=null) {
							map.put(schedule.getEvent(), dto);
						}
					}
				}
				if (map.size()>0) {
					cacheService.set(key, map);
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public LetgoalDto getLetgoalDtoByCompanyId(int scheduleId, int companyId) {
		LetGoal letGoal = LetGoal.findLetGoal(scheduleId, companyId);
		if (letGoal==null) {
			return null;
		}
		LetgoalDto dto = new LetgoalDto();
		dto.setGoal(letGoal.getGoal());
		dto.setUpOdds(letGoal.getUpOdds());
		dto.setDownOdds(letGoal.getDownOdds());
		return dto;
	}
	
}
