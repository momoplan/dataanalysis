package com.ruyicai.dataanalysis.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruyicai.dataanalysis.service.AnalysisService;
import com.ruyicai.dataanalysis.service.GlobalInfoService;
import com.ruyicai.dataanalysis.timer.news.FetchNewsService;
import com.ruyicai.dataanalysis.timer.zq.LetgoalDetailUpdateService;
import com.ruyicai.dataanalysis.timer.zq.LetgoalUpdateService;
import com.ruyicai.dataanalysis.timer.zq.StandardUpdateService;
import com.ruyicai.dataanalysis.timer.zq.DetailResultUpdateService;
import com.ruyicai.dataanalysis.timer.zq.QiuTanMatchesUpdateService;
import com.ruyicai.dataanalysis.timer.zq.ScheduleUpdateService;
import com.ruyicai.dataanalysis.timer.zq.SclassUpdateService;
import com.ruyicai.dataanalysis.timer.zq.TodayScoreUpdateService;
import com.ruyicai.dataanalysis.timer.zq.TeamUpdateService;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.PropertiesUtil;
import com.ruyicai.dataanalysis.util.jc.JmsSendUtil;
import com.ruyicai.dataanalysis.util.zq.FootBallMapUtil;
import com.ruyicai.dataanalysis.util.zq.SendJmsJczUtil;

@RequestMapping("/system")
@Controller
public class SystemController {
	
	private Logger logger = LoggerFactory.getLogger(SystemController.class);
	
	@Autowired
	private SclassUpdateService sclassUpdateService;
	
	@Autowired
	private TeamUpdateService teamUpdateService;
	
	@Autowired
	private LetgoalUpdateService letgoalUpdateService;
	
	@Autowired
	private DetailResultUpdateService detailResultUpdateService;
	
	@Autowired
	private QiuTanMatchesUpdateService qiuTanMatchesUpdateService;

	@Autowired
	private ScheduleUpdateService scheduleUpdateService;
	
	@Autowired
	private StandardUpdateService standardUpdateService;
	
	@Autowired
	private TodayScoreUpdateService todayScoreUpdateService;
	
	@Autowired
	private LetgoalDetailUpdateService letgoalDetailUpdateService;
	
	@Autowired
	private AnalysisService analysisService;
	
	@Autowired
	private GlobalInfoService globalInfoService;
	
	@Autowired
	private FetchNewsService fetchNewsService;
	
	@Autowired
	private SendJmsJczUtil sendJmsJczUtil;
	
	@Autowired
	private FootBallMapUtil footBallMapUtil;
	
	@Autowired
	private PropertiesUtil propertiesUtil;
	
	@Autowired
	private JmsSendUtil jmsSendUtil;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@RequestMapping(value = "/updatesclass", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updatesclass() {
		ResponseData rd = new ResponseData();
		try {
			sclassUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updateteam", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateteam() {
		ResponseData rd = new ResponseData();
		try {
			teamUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/letgoalUpdate", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData letgoalUpdate() {
		ResponseData rd = new ResponseData();
		try {
			letgoalUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updatedetailresult", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updatedetailresult() {
		ResponseData rd = new ResponseData();
		try {
			detailResultUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updateDetailResultByDate", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateDetailResultByDate(@RequestParam("date") String date) {
		ResponseData rd = new ResponseData();
		try {
			detailResultUpdateService.processByDate(date);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updateqiutanmatches", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateqiutanmatches() {
		ResponseData rd = new ResponseData();
		try {
			qiuTanMatchesUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updateschedule", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateschedule() {
		ResponseData rd = new ResponseData();
		try {
			scheduleUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updateschedulemore", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateschedulemore(@RequestParam("count") int count,
			@RequestParam("mode") int mode) {
		ResponseData rd = new ResponseData();
		try {
			scheduleUpdateService.processCount(count, mode, true);
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
			Date d = DateUtil.parse("yyyy-MM-dd", date);
			scheduleUpdateService.processDateAndSclassID(d, null, false);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updateScheduleById", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateScheduleById(@RequestParam("id") String id) {
		ResponseData rd = new ResponseData();
		try {
			Set<String> set = new HashSet<String>();
			scheduleUpdateService.updateScheduleById(id, set);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updateStandardByMin", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateStandardByMin() {
		ResponseData rd = new ResponseData();
		try {
			standardUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updateStandardAll", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateStandardAll() {
		ResponseData rd = new ResponseData();
		try {
			standardUpdateService.processAll();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updatescore", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updatescore() {
		ResponseData rd = new ResponseData();
		try {
			todayScoreUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/getAllScheduleBySclass", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getAllScheduleBySclass() {
		ResponseData rd = new ResponseData();
		try {
			scheduleUpdateService.getAllScheduleBySclass();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updateAllRanking", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateAllRanking() {
		ResponseData rd = new ResponseData();
		try {
			analysisService.updateAllRanking();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updateRanking", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateRanking(@RequestParam("sclassID") int sclassID) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(analysisService.getRanking(false, sclassID));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/processFile", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData processFile(@RequestParam("filename") String filename) {
		ResponseData rd = new ResponseData();
		try {
			qiuTanMatchesUpdateService.processFile(filename);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updateImmediateScore", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateImmediateScore(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			globalInfoService.updateImmediateScore(event);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
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
			sendJmsJczUtil.sendScheduleFinishJms(event);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	/**
	 * 发送比分变化的Jms
	 * @param event
	 * @return
	 */
	@RequestMapping(value = "/scoreModifyJms", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData scoreModifyJms(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			sendJmsJczUtil.sendScoreModifyJms(event);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/letgoalDetailUpdate", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData peiLvDetailUpdate() {
		ResponseData rd = new ResponseData();
		try {
			letgoalDetailUpdateService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/flushMap", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData flushMap() {
		ResponseData rd = new ResponseData();
		try {
			String dataanalysisIp = propertiesUtil.getDataanalysisIp();
			if (StringUtils.isNotBlank(dataanalysisIp)) {
				String[] lotserverIps = StringUtils.split(dataanalysisIp, ",");
				for (String ip : lotserverIps) {
					String url = ip + "/dataanalysis/system/clearMap";
					String result = httpUtil.getResponse(url, HttpUtil.POST, HttpUtil.UTF8, "");
					logger.info("刷新Map返回:{},url:{}", result, url);
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/clearMap", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData clearMap() {
		ResponseData rd = new ResponseData();
		try {
			footBallMapUtil.clearMap();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/fetchNews", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData fetchNews() {
		ResponseData rd = new ResponseData();
		try {
			fetchNewsService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/standardAvgJms", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData standardAvgJms(@RequestParam("scheduleId") String scheduleId) {
		ResponseData rd = new ResponseData();
		try {
			sendJmsJczUtil.sendStandardAvgUpdateJms(scheduleId);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rd;
	}
	
	@RequestMapping(value = "/scheduleEventAddJms", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData scheduleEventAddJms(@RequestParam("event") String event) {
		ResponseData rd = new ResponseData();
		try {
			jmsSendUtil.scheduleEventAdd(event);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/updateWeiKaiSchedule", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateWeiKaiSchedule(@RequestParam("startdate") String startdate, 
			@RequestParam("enddate") String enddate) {
		ResponseData rd = new ResponseData();
		try {
			Date startD = DateUtil.parse("yyyy-MM-dd", startdate);
			Date endD = DateUtil.parse("yyyy-MM-dd", enddate);
			scheduleUpdateService.updateWeiKaiByMatchtime(startD, endD);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
}
