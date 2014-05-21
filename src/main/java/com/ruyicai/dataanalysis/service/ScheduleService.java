package com.ruyicai.dataanalysis.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.dto.ScheduleDTO;
import com.ruyicai.dataanalysis.service.back.LotteryService;

@Service
public class ScheduleService {

	@Autowired
	private LotteryService lotteryService;
	
	/**
	 * 查询即时比分
	 * @param state
	 * @return
	 */
	public List<ScheduleDTO> findInstantScores(int state) {
		getActivedays("1");
		
		
		
		
		List<ScheduleDTO> dtos = new ArrayList<ScheduleDTO>();
		
		return dtos;
	}
	
	private List<String> getActivedays(String type) {
		List<String> resultList = new ArrayList<String>();
		String result = lotteryService.getjingcaiactivedays(type);
		System.out.println(result);
		return resultList;
	}
	
}
