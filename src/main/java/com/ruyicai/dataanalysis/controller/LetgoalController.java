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
import com.ruyicai.dataanalysis.service.LetgoalService;

@RequestMapping("/letgoal")
@Controller
public class LetgoalController {

	private Logger logger = LoggerFactory.getLogger(LetgoalController.class);
	
	@Autowired
	private LetgoalService letgoalService;
	
	@RequestMapping(value = "/getUsualLetgoal", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getUsualStandard(@RequestParam("day") String day, 
			@RequestParam("companyId") String companyId) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(letgoalService.getUsualLetgoal(day, companyId));
			long endMills = System.currentTimeMillis();
			logger.info("竞足getUsualLetgoal,用时:"+(endMills-startMills)+",day="+day+",companyId="+companyId);
		} catch(Exception e) {
			logger.error("竞足getUsualLetgoal发生异常", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
	@RequestMapping(value = "/findByEvent", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getInfo(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(letgoalService.findByEvent(event));
			long endMills = System.currentTimeMillis();
			logger.info("竞足Letgoal-findByEvent,用时:"+(endMills-startMills)+",event="+event);
		} catch(Exception e) {
			logger.error("竞足Letgoal-findByEvent发生异常", e);
		}
		return rd;
	}
	
}
