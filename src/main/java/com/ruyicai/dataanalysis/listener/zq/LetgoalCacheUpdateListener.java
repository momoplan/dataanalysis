package com.ruyicai.dataanalysis.listener.zq;

import java.util.Collection;
import java.util.List;
import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.GlobalCache;
import com.ruyicai.dataanalysis.domain.LetGoal;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.service.GlobalInfoService;
import com.ruyicai.dataanalysis.service.dto.InfoDTO;
import com.ruyicai.dataanalysis.util.CommonUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * 亚赔缓存更新的Jms
 * @author Administrator
 *
 */
@Service
public class LetgoalCacheUpdateListener {

	private Logger logger = LoggerFactory.getLogger(LetgoalCacheUpdateListener.class);
	
	@Autowired
	private GlobalInfoService infoService;
	
	public void update(@Body String scheduleId) {
		try {
			long startmillis = System.currentTimeMillis();
			//logger.info("亚赔缓存更新的Jms start scheduleId="+scheduleId);
			if (StringUtils.isBlank(scheduleId)) {
				return;
			}
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleId));
			if (schedule==null) {
				return;
			}
			if (CommonUtil.isZqEventEmpty(schedule)) { //如果event为空,则不需要更新缓存
				return;
			}
			//更新LetGoal缓存
			updateCache(schedule);
			long endmillis = System.currentTimeMillis();
			logger.info("亚赔缓存更新的Jms end,用时:"+(endmillis-startmillis)+",scheduleId="+scheduleId);
		} catch (Exception e) {
			logger.error("亚赔缓存更新的Jms发生异常,scheduleId="+scheduleId, e);
		}
	}
	
	private void updateCache(Schedule schedule) {
		int scheduleId = schedule.getScheduleID();
		List<LetGoal> letGoalList = LetGoal.findByScheduleID(scheduleId);
		infoService.buildLetGoals(letGoalList);
		GlobalCache letGoal = GlobalCache.findGlobalCache(StringUtil.join("_", "dataanalysis", "LetGoal", String.valueOf(scheduleId)));
		if (letGoal==null) {
			letGoal = new GlobalCache();
			letGoal.setId(StringUtil.join("_", "dataanalysis", "LetGoal", String.valueOf(scheduleId)));
			letGoal.setValue(LetGoal.toJsonArray(letGoalList));
			letGoal.persist();
		} else {
			letGoal.setValue(LetGoal.toJsonArray(letGoalList));
			letGoal.merge();
		}
		//更新Info缓存
		GlobalCache globalInfo = GlobalCache.findGlobalCache(StringUtil.join("_", "dataanalysis", "Info", String.valueOf(schedule.getScheduleID())));
		if (globalInfo==null) {
			globalInfo = new GlobalCache();
			globalInfo.setId(StringUtil.join("_", "dataanalysis", "Info", String.valueOf(schedule.getScheduleID())));
			InfoDTO dto = infoService.getUpdateInfoDTO(schedule);
			globalInfo.setValue(dto.toJson());
			globalInfo.persist();
		} else {
			InfoDTO dto = InfoDTO.fromJsonToInfoDTO(globalInfo.getValue());
			Collection<LetGoal> letGoals = LetGoal.fromJsonArrayToLetGoals(letGoal.getValue());
			//buildLetGoals(letGoals);
			dto.setLetGoals(letGoals);
			globalInfo.setValue(dto.toJson());
			globalInfo.merge();
		}
	}
	
}
