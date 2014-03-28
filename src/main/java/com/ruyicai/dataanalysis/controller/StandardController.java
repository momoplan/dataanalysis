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
import com.ruyicai.dataanalysis.service.StandardService;

@RequestMapping("/standard")
@Controller
public class StandardController {

	private Logger logger = LoggerFactory.getLogger(StandardController.class);
	
	@Autowired
	private StandardService standardService;
	
	@RequestMapping(value = "/getUsualStandard", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getUsualStandard(@RequestParam("companyId") String companyId) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			long startMills = System.currentTimeMillis();
			rd.setValue(standardService.getUsualStandards(companyId));
			long endMills = System.currentTimeMillis();
			logger.info("竞足getUsualStandard,用时:"+(endMills-startMills));
		} catch(Exception e) {
			logger.error("竞足getUsualStandard发生异常", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
}
