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
import com.ruyicai.dataanalysis.service.LetgoalService;

/**
 * @Description: 北单亚盘
 * 
 * @author chenchuang   
 * @date 2015年3月20日上午11:20:36
 * @version V1.0   
 *
 */
@RequestMapping("/letgoalBd")
@Controller
public class LetgoalBdController {

	private Logger logger = LoggerFactory.getLogger(LetgoalBdController.class);
	
	@Autowired
	private LetgoalService letgoalService;
	
	@RequestMapping(value = "/findByEvent", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getInfo(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(letgoalService.findByBdEvent(event));
			long endMills = System.currentTimeMillis();
			logger.info("竞足Letgoal-findByEvent,用时:"+(endMills-startMills)+",event="+event);
		} catch(Exception e) {
			logger.error("竞足Letgoal-findByEvent发生异常", e);
		}
		return rd;
	}
}




