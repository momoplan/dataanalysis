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
import com.ruyicai.dataanalysis.service.lq.StandardJclService;

/**
 * @Description: 竞彩篮球欧盘
 * 
 * @author chenchuang   
 * @date 2015年3月18日下午1:20:46
 * @version V1.0   
 *
 */
@RequestMapping("/standardJcl")
@Controller
public class StandardJclController {

	private Logger logger = LoggerFactory.getLogger(StandardJclController.class);
	
	@Autowired
	private StandardJclService standardJclService;
	
	@RequestMapping(value = "/findByEvent", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getInfo(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(standardJclService.findByEvent(event));
			long endMills = System.currentTimeMillis();
			logger.info("竞篮StandardJcl-findByEvent,用时:"+(endMills-startMills)+",event="+event);
		} catch(Exception e) {
			logger.error("竞篮StandardJcl-findByEvent发生异常", e);
		}
		return rd;
	}
	
}




