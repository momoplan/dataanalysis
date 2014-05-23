package com.ruyicai.dataanalysis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
	
}
