package com.ruyicai.dataanalysis.controller.bd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.dataanalysis.controller.ResponseData;
import com.ruyicai.dataanalysis.service.StandardService;

@RequestMapping("/standardBd")
@Controller
public class StandardBdController {

	private Logger logger = LoggerFactory.getLogger(StandardBdController.class);
	
	@Autowired
	private StandardService standardService;
	
	@RequestMapping(value = "/findByEvent", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getInfo(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(standardService.findByBdEvent(event));
			long endMills = System.currentTimeMillis();
			logger.info("足彩StandardBd-findByEvent,用时:"+(endMills-startMills)+",event="+event);
		} catch(Exception e) {
			logger.error("足彩StandardBd-findByEvent发生异常", e);
		}
		return rd;
	}
	
}




