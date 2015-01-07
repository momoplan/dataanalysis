package com.ruyicai.dataanalysis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruyicai.dataanalysis.consts.ErrorCode;
import com.ruyicai.dataanalysis.exception.RuyicaiException;
import com.ruyicai.dataanalysis.service.ScheduleService;

@RequestMapping("/schedule")
@Controller
public class ScheduleController {

	private Logger logger = LoggerFactory.getLogger(ScheduleController.class);
	
	@Autowired
	private ScheduleService scheduleService;
	
	@RequestMapping(value = "/findInstantScores", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData findInstantScores(@RequestParam("state") int state) {
		ResponseData rd = new ResponseData();
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(scheduleService.findInstantScores(state));
			long endMills = System.currentTimeMillis();
			logger.info("竞足findInstantScores,用时:"+(endMills-startMills)+",state="+state);
		} catch(Exception e) {
			logger.error("竞足findInstantScores发生异常", e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/findTechnicCount", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData findTechnicCount(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(scheduleService.findTechnicCount(event));
			long endMills = System.currentTimeMillis();
			logger.info("竞足findTechnicCount,用时:"+(endMills-startMills)+",event="+event);
		} catch(Exception e) {
			logger.error("竞足findTechnicCount发生异常", e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/findClasliAnalysis", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData findClasliAnalysis(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(scheduleService.findClasliAnalysis(event));
			long endMills = System.currentTimeMillis();
			logger.info("竞足findClasliAnalysis,用时:"+(endMills-startMills)+",event="+event);
		} catch(Exception e) {
			logger.error("竞足findClasliAnalysis发生异常", e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/findClasliSchedules", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData findClasliTeam() {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(scheduleService.findClasliSchedules());
			long endMills = System.currentTimeMillis();
			logger.info("竞足findClasliSchedules,用时:"+(endMills-startMills));
		} catch (RuyicaiException e) {
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("竞足查询对阵里的赛事信息发生异常", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
	@RequestMapping(value = "/findScheduleByEvents", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData findScheduleByEvents(@RequestParam("events") String events) {
		ResponseData rd = new ResponseData();
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(scheduleService.findScheduleByEvents(events));
			long endMills = System.currentTimeMillis();
			logger.info("竞足findScheduleByEvents,用时:"+(endMills-startMills)+",events="+events);
		} catch(Exception e) {
			logger.error("竞足findScheduleByEvents发生异常", e);
		}
		return rd;
	}
	
	/**
	 * 获取杯赛相关赛程
	 * @param league
	 * @return
	 */
	@RequestMapping(value = "/findScheduleByLeague", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData findScheduleByLeague(@RequestParam("league") String league,@RequestParam(value="grouping",required=false) String grouping) {
		logger.info("竞足findScheduleByLeague,league:{},grouping:{}", new Object[] { league, grouping });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(scheduleService.getScheduleByLeague(league,grouping));
			long endMills = System.currentTimeMillis();
			logger.info("竞足findScheduleByLeague,用时:"+(endMills-startMills));
		} catch(Exception e) {
			logger.error("竞足findScheduleByLeague发生异常", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
	@RequestMapping(value = "/getLeagueCupRanking", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getLeagueCupRanking(@RequestParam(value = "league", required = false) String league,
			@RequestParam(value = "season", required = false) String season) {
		logger.info("竞足getLeagueCupRanking,league:{},season:{}", new Object[] { league, season });
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(scheduleService.getCupMatchRanking(league,season));
			long endMills = System.currentTimeMillis();
			logger.info("竞足getLeagueCupRanking,用时:"+(endMills-startMills));
		} catch(Exception e) {
			logger.error("竞足getLeagueCupRanking发生异常", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
	@RequestMapping(value = "/getCupMatchJifenRanking", method = RequestMethod.POST)
	public @ResponseBody ResponseData getCupMatchJifenRanking(@RequestParam("grouping") String grouping){
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(scheduleService.getCupMatchJifenRanking(grouping));
			long endMills = System.currentTimeMillis();
			logger.info("竞足getLeagueCupRanking,用时:"+(endMills-startMills));
		} catch(Exception e) {
			logger.error("竞足getLeagueCupRanking发生异常", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
}
