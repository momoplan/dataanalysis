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
import com.ruyicai.dataanalysis.service.GlobalInfoService;
import com.ruyicai.dataanalysis.service.zc.SelectZcService;

/**
 * 足彩查询
 * @author Administrator
 *
 */
@RequestMapping("/selectZc")
@Controller
public class SelectZcController {

	private Logger logger = LoggerFactory.getLogger(SelectZcController.class);
	
	@Autowired
	private SelectZcService selectZcService;
	
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
			rd.setValue(globalInfoService.getZcInfo(event));
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
	ResponseData getImmediateScores(@RequestParam("lotno") String lotno, @RequestParam("batchcode") String batchcode,
			@RequestParam(value = "state", required = false, defaultValue = "0") int state) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(selectZcService.getImmediateScores(lotno, batchcode, state));
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
			rd.setValue(selectZcService.getImmediateScore(event));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
}
