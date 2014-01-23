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
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Standard;
import com.ruyicai.dataanalysis.service.GlobalInfoService;
import com.ruyicai.dataanalysis.service.dto.InfoDTO;
import com.ruyicai.dataanalysis.util.CommonUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * 足球欧赔缓存更新的Jms
 * @author Administrator
 *
 */
@Service
public class StandardCacheUpdateListener {

	private Logger logger = LoggerFactory.getLogger(StandardCacheUpdateListener.class);
	
	@Autowired
	private GlobalInfoService infoService;
	
	public void process(@Body String scheduleId) {
		try {
			long startmillis = System.currentTimeMillis();
			//logger.info("足球欧赔缓存更新的Jms start scheduleId="+scheduleId);
			if (StringUtils.isBlank(scheduleId)) {
				return;
			}
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleId));
			if (schedule==null) {
				return;
			}
			if (CommonUtil.isZqEventEmpty(schedule)) {
				return;
			}
			//更新欧赔缓存
			updateStandardCache(schedule);
			long endmillis = System.currentTimeMillis();
			logger.info("足球欧赔缓存更新的Jms,用时:"+(endmillis-startmillis)+",scheduleId="+scheduleId);
		} catch (Exception e) {
			logger.error("足球欧赔缓存更新的Jms发生异常", e);
		}
	}
	
	private void updateStandardCache(Schedule schedule) {
		try {
			boolean zqEventEmpty = CommonUtil.isZqEventEmpty(schedule);
			if (zqEventEmpty) { //如果event为空,则不需要更新缓存
				return ;
			}
			//更新Standard缓存
			Integer scheduleId = schedule.getScheduleID();
			List<Standard> standardList = Standard.findByScheduleID(scheduleId);
			infoService.buildStandards(schedule, standardList);
			GlobalCache standard = GlobalCache.findGlobalCache(StringUtil.join("_", "dataanalysis", "Standard", String.valueOf(scheduleId)));
			if (standard==null) {
				standard = new GlobalCache();
				standard.setId(StringUtil.join("_", "dataanalysis", "Standard", String.valueOf(scheduleId)));
				standard.setValue(Standard.toJsonArray(standardList));
				standard.persist();
			} else {
				standard.setValue(Standard.toJsonArray(standardList));
				standard.merge();
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
				Collection<Standard> standards = Standard.fromJsonArrayToStandards(standard.getValue());
				dto.setStandards(standards);
				globalInfo.setValue(dto.toJson());
				globalInfo.merge();
			}
		} catch (Exception e) {
			logger.error("足球欧赔缓存更新的Jms-updateStandardCache发生异常", e);
		}
	}
	
}
