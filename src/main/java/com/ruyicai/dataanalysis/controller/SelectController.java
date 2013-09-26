package com.ruyicai.dataanalysis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruyicai.dataanalysis.service.AnalysisService;
import com.ruyicai.dataanalysis.service.GlobalInfoService;

@RequestMapping("/select")
@Controller
public class SelectController {
	
	private Logger logger = LoggerFactory.getLogger(SelectController.class);
	
	@Autowired
	private GlobalInfoService infoService;
	
	@Autowired
	private AnalysisService analysisService;
	
	@RequestMapping(value = "/getInfo", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getInfo(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(infoService.getInfo(event));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getRanking", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getRanking(@RequestParam("scheduleID") int scheduleID) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(analysisService.getRanking(scheduleID, true));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getPreHomeSchedules", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getPreHomeSchedules(@RequestParam("scheduleID") int scheduleID) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(analysisService.getPreHomeSchedules(scheduleID, null));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getPreGuestSchedules", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getPreGuestSchedules(@RequestParam("scheduleID") int scheduleID) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(analysisService.getPreGuestSchedules(scheduleID, null));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getAfterHomeSchedules", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getAfterHomeSchedules(@RequestParam("scheduleID") int scheduleID) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(analysisService.getAfterHomeSchedules(scheduleID, null));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getAfterGuestSchedules", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getAfterGuestSchedules(@RequestParam("scheduleID") int scheduleID) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(analysisService.getAfterGuestSchedules(scheduleID, null));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getPreClashSchedules", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getPreClashSchedules(@RequestParam("scheduleID") int scheduleID) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(analysisService.getPreClashSchedules(scheduleID, null));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getImmediateScores", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getImmediateScores(@RequestParam("day") String day,
			@RequestParam(value = "state", required = false, defaultValue = "0") int state) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(infoService.getImmediateScores(day, state));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getImmediateScore", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getImmediateScore(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(infoService.getImmediateScore(event));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getProcessingMatches", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getProcessingMatches() {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(infoService.getProcessingMatches());
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getScheduleByEvent", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getScheduleByEvent(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(infoService.getScheduleDtoByEvent(event));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getStandardDetails", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getStandardDetails(@RequestParam("oddsId") String oddsId) {
		ResponseData rd = new ResponseData();
		try {
			long startmillis = System.currentTimeMillis();
			rd.setValue(infoService.getStandardDetails(oddsId));
			long endmillis = System.currentTimeMillis();
			logger.info("查询欧赔变化,用时:"+(endmillis - startmillis));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getLetGoalDetails", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getLetGoalDetails(@RequestParam("oddsId") String oddsId) {
		ResponseData rd = new ResponseData();
		try {
			long startmillis = System.currentTimeMillis();
			rd.setValue(infoService.getLetGoalDetails(oddsId));
			long endmillis = System.currentTimeMillis();
			logger.info("查询亚赔变化,用时:"+(endmillis - startmillis));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
}
