package com.ruyicai.dataanalysis.controller.jcl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruyicai.dataanalysis.controller.ResponseData;
import com.ruyicai.dataanalysis.service.jcl.AnalysisJclService;
import com.ruyicai.dataanalysis.timer.jcl.PeiLvJclUpdateService;
import com.ruyicai.dataanalysis.timer.jcl.QiuTanMatchesJclUpdateService;
import com.ruyicai.dataanalysis.timer.jcl.ScheduleJclUpdateService;
import com.ruyicai.dataanalysis.timer.jcl.SclassJclUpdateService;
import com.ruyicai.dataanalysis.timer.jcl.StandardJclUpdateService;
import com.ruyicai.dataanalysis.timer.jcl.TeamJclUpdateService;
import com.ruyicai.dataanalysis.timer.jcl.TodayScoreJclUpdateService;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.jcl.SendJmsJclUtil;

@RequestMapping("/systemJcl")
@Controller
public class SystemJclController {

	private Logger logger = LoggerFactory.getLogger(SystemJclController.class);
	
	@Autowired
	private AnalysisJclService analysisJclService;
	
	@Autowired
	private SclassJclUpdateService sclassJclUpdateService;
	
	@Autowired
	private ScheduleJclUpdateService scheduleJclUpdateService;
	
	@Autowired
	private PeiLvJclUpdateService peiLvJclUpdateService;
	
	@Autowired
	private QiuTanMatchesJclUpdateService qiuTanMatchesJclUpdateService;
	
	@Autowired
	private StandardJclUpdateService standardJclUpdateService;
	
	@Autowired
	private TodayScoreJclUpdateService todayScoreJclUpdateService;
	
	@Autowired
	private TeamJclUpdateService teamJclUpdateService;
	
	@Autowired
	private SendJmsJclUtil sendJmsJclUtil;
	
	/**
	 * 更新联赛
	 * @return
	 */
	@RequestMapping(value = "/updateSclass", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updatesclass() {
		ResponseData rd = new ResponseData();
		try {
			sclassJclUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	/**
	 * 更新赛事
	 * @return
	 */
	@RequestMapping(value = "/updateSchedule", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateschedule() {
		ResponseData rd = new ResponseData();
		try {
			scheduleJclUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	/**
	 * 跟据天数获取赛事
	 * @param count(天数)
	 * @param mode(0:after;1:pre)
	 * @return
	 */
	@RequestMapping(value = "/updateScheduleByDays", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateScheduleByDays(@RequestParam("count") int count, @RequestParam("mode") int mode) {
		ResponseData rd = new ResponseData();
		try {
			scheduleJclUpdateService.updateScheduleByDays(count, mode);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	/**
	 * 根据日期更新赛事
	 * @param date
	 * @return
	 */
	@RequestMapping(value = "/updateScheduleByDate", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateScheduleByDate(@RequestParam("date") String date) {
		ResponseData rd = new ResponseData();
		try {
			scheduleJclUpdateService.updateScheduleByDate(date);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	/**
	 * 更新赛事的让分和总分盘
	 * @param event
	 * @return
	 */
	@RequestMapping(value = "/updateScheduleLetScoreTotalScore", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateScheduleLetScoreTotalScore(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			if (!StringUtil.isEmpty(event)) {
				sendJmsJclUtil.sendScheduleFinishJms(event);
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	/**
	 * 更新赔率
	 * @return
	 */
	@RequestMapping(value = "/updatePeiLv", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateletgoalpeilu() {
		ResponseData rd = new ResponseData();
		try {
			peiLvJclUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	/**
	 * 更新球探网比赛
	 * @return
	 */
	@RequestMapping(value = "/updateQiutanMatches", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateqiutanmatches() {
		ResponseData rd = new ResponseData();
		try {
			qiuTanMatchesJclUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	/**
	 * 更新欧赔
	 * @return
	 */
	@RequestMapping(value = "/updateStandard", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updatestandard() {
		ResponseData rd = new ResponseData();
		try {
			standardJclUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	/**
	 * 更新今日比分
	 * @return
	 */
	@RequestMapping(value = "/updateTodayScore", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updatescore() {
		ResponseData rd = new ResponseData();
		try {
			todayScoreJclUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	/**
	 * 更新所有联赛排名
	 * @return
	 */
	@RequestMapping(value = "/updateAllSclassRanking", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateAllRanking() {
		ResponseData rd = new ResponseData();
		try {
			analysisJclService.updateAllRanking();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	/**
	 * 根据联赛编号更新排名
	 * @param sclassId
	 * @return
	 */
	@RequestMapping(value = "/updateRankingBySclassId", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateRanking(@RequestParam("sclassId") int sclassId) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(analysisJclService.getRanking(false, sclassId));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	/**
	 * 球队信息更新
	 * @return
	 */
	@RequestMapping(value = "/updateTeam", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateTeam() {
		ResponseData rd = new ResponseData();
		try {
			teamJclUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
}
