package com.ruyicai.dataanalysis.controller.lq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.dataanalysis.consts.ErrorCode;
import com.ruyicai.dataanalysis.controller.ResponseData;
import com.ruyicai.dataanalysis.service.lq.GlobalInfoJclService;

@RequestMapping("/selectJcl")
@Controller
public class SelectJclController {

	private Logger logger = LoggerFactory.getLogger(SelectJclController.class);
	
	@Autowired
	private GlobalInfoJclService infoJclService;
	
	@RequestMapping(value = "/getInfo", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getInfo(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(infoJclService.getInfo(event));
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
			rd.setValue(infoJclService.getImmediateScores(day, state));
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
			rd.setValue(infoJclService.getImmediateScore(event));
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
			rd.setValue(infoJclService.getProcessingMatches());
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
			rd.setValue(infoJclService.getScheduleDtoByEvent(event));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getSchedules", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getSchedules(@RequestParam("day") String day) {
		ResponseData rd = new ResponseData();
		try {
			long startmillis = System.currentTimeMillis();
			rd.setErrorCode(ErrorCode.OK.value);
			rd.setValue(infoJclService.getSchedulesByDay(day)); 
			long endmillis = System.currentTimeMillis();
			logger.info("查询篮球赛事,用时:"+(endmillis - startmillis)+",day="+day);
		} catch(Exception e) {
			rd.setErrorCode(ErrorCode.ERROR.value);
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
}
