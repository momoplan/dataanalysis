package com.ruyicai.dataanalysis.service.bd;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.DetailResult;
import com.ruyicai.dataanalysis.domain.GlobalCache;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Sclass;
import com.ruyicai.dataanalysis.service.dto.ScheduleDTO;
import com.ruyicai.dataanalysis.util.BeanUtilsEx;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class SelectBdService {

	// private Logger logger = LoggerFactory.getLogger(SelectZcService.class);

	/**
	 * 即时比分列表
	 * 
	 * @param lotNo
	 * @param batchCode
	 * @param state
	 * @return
	 * @throws Exception
	 */
	public List<ScheduleDTO> getImmediateScores(String batchCode, int state) throws Exception {
		List<ScheduleDTO> dtos = new ArrayList<ScheduleDTO>();
		List<Schedule> schedules = Schedule.findByBdEventAndBatchCode(batchCode);
		if (schedules == null) {
			return dtos;
		}
		for (Schedule s : schedules) {
			ScheduleDTO dto = new ScheduleDTO();
			if (state == 1) { // 未开
				if (s.getMatchState() != 0) {
					continue;
				}
			}
			if (state == 2) { // 比赛中
				if (s.getMatchState() == 0 || s.getMatchState() == -1
						|| s.getMatchState() == -10) {
					continue;
				}
			}
			if (state == 3) { // 完场
				if (s.getMatchState() != -1 && s.getMatchState() != -10) {
					continue;
				}
			}
			Sclass sclass = Sclass.findSclass(s.getSclassID());
			BeanUtilsEx.copyProperties(dto, s);
			dto.setSclassName(sclass.getName_J());
			dto.setSclassName_j(sclass.getName_JS());
			String id = StringUtil.join("_", "dataanalysis", "DetailResult",
					String.valueOf(s.getScheduleID()));
			GlobalCache globalCache = GlobalCache.findGlobalCache(id);
			if (null == globalCache) {
				List<DetailResult> detailResults = DetailResult
						.findDetailResults(s.getScheduleID());
				globalCache = new GlobalCache();
				globalCache.setId(id);
				globalCache.setValue(DetailResult.toJsonArray(detailResults));
				globalCache.persist();
				dto.setDetailResults(detailResults);
			} else {
				dto.setDetailResults(DetailResult
						.fromJsonArrayToDetailResults(globalCache.getValue()));
			}
			dtos.add(dto);
		}
		return dtos;
	}

	/**
	 * 查询即时比分详细
	 * 
	 * @param zcEvent
	 * @return
	 * @throws Exception
	 */
	public ScheduleDTO getImmediateScore(String bdEvent) throws Exception {
		Schedule schedule = Schedule.findByBdEvent(bdEvent);
		if (schedule == null) {
			return null;
		}
		ScheduleDTO dto = new ScheduleDTO();
		Sclass sclass = Sclass.findSclass(schedule.getSclassID());
		BeanUtilsEx.copyProperties(dto, schedule);
		dto.setSclassName(sclass.getName_J());
		dto.setSclassName_j(sclass.getName_JS());
		String id = StringUtil.join("_", "dataanalysis", "DetailResult",
				String.valueOf(schedule.getScheduleID()));
		GlobalCache globalCache = GlobalCache.findGlobalCache(id);
		if (null == globalCache) {
			List<DetailResult> detailResults = DetailResult
					.findDetailResults(schedule.getScheduleID());
			globalCache = new GlobalCache();
			globalCache.setId(id);
			globalCache.setValue(DetailResult.toJsonArray(detailResults));
			globalCache.persist();
			dto.setDetailResults(detailResults);
		} else {
			dto.setDetailResults(DetailResult
					.fromJsonArrayToDetailResults(globalCache.getValue()));
		}
		return dto;
	}

}
