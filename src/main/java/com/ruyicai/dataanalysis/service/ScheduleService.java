package com.ruyicai.dataanalysis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.consts.MatchState;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Sclass;
import com.ruyicai.dataanalysis.dto.ScheduleDTO;
import com.ruyicai.dataanalysis.service.back.LotteryService;
import com.ruyicai.dataanalysis.util.BeanUtilsEx;

@Service
public class ScheduleService {

	@Autowired
	private LotteryService lotteryService;
	
	/**
	 * 查询即时比分
	 * @param state
	 * @return
	 */
	public Map<String, List<ScheduleDTO>> findInstantScores(int state) {
		List<String> activedays = getActivedays("1");
		if (activedays==null||activedays.size()<=0) {
			return null;
		}
		Map<String, List<ScheduleDTO>> resultMap = new HashMap<String, List<ScheduleDTO>>();
		for (String activeday : activedays) {
			List<ScheduleDTO> dtos = new ArrayList<ScheduleDTO>();
			List<Schedule> schedules = Schedule.findByEventAndDay(activeday);
			for (Schedule schedule : schedules) {
				Integer matchState = schedule.getMatchState();
				if (matchState==null) {
					continue;
				}
				if (state==1) { //未开赛
					if (matchState!=MatchState.WEIKAI.value&&matchState!=MatchState.DAIDING.value
							&&matchState!=MatchState.TUICHI.value) {
						continue;
					}
				}
				if (state==2) { //比赛中
					if (matchState!=MatchState.SHANGBANCHANG.value&&matchState!=MatchState.ZHONGCHANG.value
							&&matchState!=MatchState.XIABANCHANG.value&&matchState!=MatchState.ZHONGDUAN.value) {
						continue;
					}
				}
				if(state==3) { // 完场
					if (matchState!=MatchState.YAOZHAN.value&&matchState!=MatchState.WANCHANG.value
							&&matchState!=MatchState.QUXIAO.value) {
						continue;
					}
				}
				ScheduleDTO dto = new ScheduleDTO();
				BeanUtilsEx.copyProperties(dto, schedule);
				Sclass sclass = Sclass.findSclass(schedule.getSclassID());
				dto.setSclassName(sclass.getName_J());
				dto.setSclassName_j(sclass.getName_JS());
				dtos.add(dto);
			}
			if (dtos!=null&&dtos.size()>0) {
				resultMap.put(activeday, dtos);
			}
		}
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getActivedays(String type) {
		String result = lotteryService.getjingcaiactivedays(type);
		if (StringUtils.isBlank(result)) {
			return null;
		}
		JSONObject fromObject = JSONObject.fromObject(result);
		if (fromObject==null) {
			return null;
		}
		String errorCode = fromObject.getString("errorCode");
		if (StringUtils.equals(errorCode, "0")) {
			JSONArray valueArray = fromObject.getJSONArray("value");
			if (valueArray!=null&&valueArray.size()>0) {
				return (List<String>)JSONArray.toCollection(valueArray, List.class);
			}
		}
		return null;
	}
	
}
