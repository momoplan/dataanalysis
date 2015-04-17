package com.ruyicai.dataanalysis.dto.lq;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.dataanalysis.dto.BetNumDto;
import com.ruyicai.dataanalysis.dto.BetRatioDto;
import com.ruyicai.dataanalysis.dto.LetgoalDto;
import com.ruyicai.dataanalysis.dto.RankingDTO;
import com.ruyicai.dataanalysis.dto.ScheduleDTO;

/**
 * @Description: 竞篮数据分析
 * 
 * @author chenchuang   
 * @date 2015年3月13日上午11:54:19
 * @version V1.0   
 *
 */
@RooJavaBean
@RooJson
public class ClasliAnalysisJclDto {

	private ScheduleJclDTO schedule = new ScheduleJclDTO();
	
	private BetRatioDto betRatio = new BetRatioDto();
	
	private BetNumDto betNum = new BetNumDto();
	
	private Collection<ScheduleJclDTO> preClashSchedules = new ArrayList<ScheduleJclDTO>();
	
	private Collection<RankingJclDTO> rankings = new ArrayList<RankingJclDTO>();
	
	private LetgoalJclDto letgoal = new LetgoalJclDto();
	
}




