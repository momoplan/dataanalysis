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
import com.ruyicai.dataanalysis.util.bd.SendJmsBdUtil;

@RequestMapping("/systemBd")
@Controller
public class SystemBdController {

	private Logger logger = LoggerFactory.getLogger(SystemBdController.class);
	
	@Autowired
	private SendJmsBdUtil sendJmsBdUtil;
	
	/**
	 * 发送赛事完场的Jms
	 * @param event
	 * @return
	 */
	@RequestMapping(value = "/scheduleFinishJms", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData scheduleFinishJms(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			sendJmsBdUtil.sendScheduleFinishJms(event);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
}
