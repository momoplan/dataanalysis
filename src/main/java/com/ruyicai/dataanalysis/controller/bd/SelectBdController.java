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
import com.ruyicai.dataanalysis.service.GlobalInfoService;
import com.ruyicai.dataanalysis.service.bd.SelectBdService;

/**
 * 北单查询
 * @author Administrator
 *
 */
@RequestMapping("/selectBd")
@Controller
public class SelectBdController {

private Logger logger = LoggerFactory.getLogger(SelectBdController.class);
	
	@Autowired
	private SelectBdService selectBdService;
	
	@Autowired
	private GlobalInfoService globalInfoService;
	
	/**
	 * 足彩数据分析
	 * @param event
	 * @return
	 */
	@RequestMapping(value = "/getInfo", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getInfo(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(globalInfoService.getBdInfo(event));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	/**
	 * 即时比分列表
	 * @param lotno
	 * @param batchcode
	 * @param state
	 * @return
	 */
	@RequestMapping(value = "/getImmediateScores", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getImmediateScores(@RequestParam("batchcode") String batchcode,
			@RequestParam(value = "state", required = false, defaultValue = "0") int state) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(selectBdService.getImmediateScores(batchcode, state));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	/**
	 * 即时比分详细
	 * @param event
	 * @return
	 */
	@RequestMapping(value = "/getImmediateScore", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getImmediateScore(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(selectBdService.getImmediateScore(event));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	/**
	 * 进行中比赛查询
	 * @return
	 */
	@RequestMapping(value = "/getProcessingMatches", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getProcessingMatches() {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(selectBdService.getProcessingMatches());
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
}
