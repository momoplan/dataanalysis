package com.ruyicai.dataanalysis.controller.lq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.dataanalysis.controller.ResponseData;
import com.ruyicai.dataanalysis.service.lq.LetgoalJclService;

/**
 * @Description: 竞彩篮球亚盘
 * 
 * @author chenchuang   
 * @date 2015年3月18日下午1:40:16
 * @version V1.0   
 *
 */
@RequestMapping("/letgoalJcl")
@Controller
public class LetgoalJclController {

	private Logger logger = LoggerFactory.getLogger(LetgoalJclController.class);
	
	@Autowired
	private LetgoalJclService letgoalJclService;
	
	@RequestMapping(value = "/findByEvent", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getInfo(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(letgoalJclService.findByEvent(event));
			long endMills = System.currentTimeMillis();
			logger.info("竞篮LetgoalJcl-findByEvent,用时:"+(endMills-startMills)+",event="+event);
		} catch(Exception e) {
			logger.error("竞篮LetgoalJcl-findByEvent发生异常", e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getTotalScore", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getTotalScore(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(letgoalJclService.getTotalScore(event));
			long endMills = System.currentTimeMillis();
			logger.info("竞篮LetgoalJcl-findByEvent,用时:"+(endMills-startMills)+",event="+event);
		} catch(Exception e) {
			logger.error("竞篮LetgoalJcl-findByEvent发生异常", e);
		}
		return rd;
	}
	
}




