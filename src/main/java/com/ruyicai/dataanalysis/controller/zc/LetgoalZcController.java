package com.ruyicai.dataanalysis.controller.zc;

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
 * @Description: 足彩亚盘
 * 
 * @author chenchuang   
 * @date 2015年3月20日下午3:01:36
 * @version V1.0   
 *
 */
@RequestMapping("/letgoalZc")
@Controller
public class LetgoalZcController {

	private Logger logger = LoggerFactory.getLogger(LetgoalZcController.class);

	@Autowired
	private LetgoalService letgoalService;
	
	@RequestMapping(value = "/findByEvent", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getInfo(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(letgoalService.findByZcEvent(event));
			long endMills = System.currentTimeMillis();
			logger.info("足彩LetgoalZc-findByEvent,用时:"+(endMills-startMills)+",event="+event);
		} catch(Exception e) {
			logger.error("足彩LetgoalZc-findByEvent发生异常", e);
		}
		return rd;
	}
	
}




