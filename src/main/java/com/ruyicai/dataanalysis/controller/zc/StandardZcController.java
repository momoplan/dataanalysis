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
import com.ruyicai.dataanalysis.service.StandardService;

/**
 * @Description: 足彩欧盘
 * 
 * @author chenchuang   
 * @date 2015年3月20日下午2:36:41
 * @version V1.0   
 *
 */
@RequestMapping("/standardZc")
@Controller
public class StandardZcController {

	private Logger logger = LoggerFactory.getLogger(StandardZcController.class);
	
	@Autowired
	private StandardService standardService;
	
	@RequestMapping(value = "/findByEvent", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getInfo(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(standardService.findByZcEvent(event));
			long endMills = System.currentTimeMillis();
			logger.info("足彩StandardZc-findByEvent,用时:"+(endMills-startMills)+",event="+event);
		} catch(Exception e) {
			logger.error("足彩StandardZc-findByEvent发生异常", e);
		}
		return rd;
	}
	
}




