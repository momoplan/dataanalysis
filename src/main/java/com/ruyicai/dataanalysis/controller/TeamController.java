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
import com.ruyicai.dataanalysis.exception.RuyicaiException;
import com.ruyicai.dataanalysis.service.TeamService;

@RequestMapping("/team")
@Controller
public class TeamController {

	private Logger logger = LoggerFactory.getLogger(TeamController.class);
	
	@Autowired
	private TeamService teamService;
	
	@RequestMapping(value = "/support", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData support(@RequestParam("teamid") String teamid) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			long startMills = System.currentTimeMillis();
			teamService.support(teamid);
			long endMills = System.currentTimeMillis();
			logger.info("竞足支持球队,用时:"+(endMills-startMills)+",teamid="+teamid);
		} catch (RuyicaiException e) {
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("竞足支持球队发生异常", e);
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
}
