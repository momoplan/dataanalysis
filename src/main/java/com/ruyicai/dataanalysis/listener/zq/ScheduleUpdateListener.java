package com.ruyicai.dataanalysis.listener.zq;

import org.apache.camel.Body;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.GlobalCache;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.dto.InfoDTO;
import com.ruyicai.dataanalysis.dto.ScheduleDTO;
import com.ruyicai.dataanalysis.service.AnalysisService;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * 足球赛事更新的Jms监听
 * @author Administrator
 *
 */
@Service
public class ScheduleUpdateListener {

	private Logger logger = LoggerFactory.getLogger(ScheduleUpdateListener.class);
	
	@Autowired
	private AnalysisService analysisService;
	
	public void process(@Body String scheduleId) {
		try {
			logger.info("足球赛事更新的Jms监听 start,scheduleId="+scheduleId);
			long startmillis = System.currentTimeMillis();
			//更新Info缓存
			String key = StringUtil.join("_", "dataanalysis", "Info", scheduleId);
			GlobalCache globalInfo = GlobalCache.findGlobalCache(key);
			if (globalInfo==null) {
				return;
			}
			InfoDTO dto = InfoDTO.fromJsonToInfoDTO(globalInfo.getValue());
			if (dto==null) {
				return;
			}
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleId), true);
			if (schedule==null) {
				return;
			}
			ScheduleDTO scheduleDTO = analysisService.buildDTO(schedule, false);
			dto.setSchedule(scheduleDTO);
			globalInfo.setValue(dto.toJson());
			globalInfo.merge();
			long endmillis = System.currentTimeMillis();
			logger.info("足球赛事更新的Jms监听,用时:"+(endmillis - startmillis)+",scheduleId="+scheduleId);
		} catch (Exception e) {
			logger.error("足球赛事更新的Jms监听发生异常,scheduleId="+scheduleId, e);
		}
	}
	
}
