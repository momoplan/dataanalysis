package com.ruyicai.dataanalysis.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.GlobalCache;
import com.ruyicai.dataanalysis.domain.LetGoal;
import com.ruyicai.dataanalysis.domain.LetGoalDetail;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.CommonUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.ThreadPoolUtil;
import com.ruyicai.dataanalysis.util.jcz.FootBallMapUtil;
import com.ruyicai.dataanalysis.util.jcz.SendJmsJczUtil;

@Service
public class PeiLvDetailUpdateService {

	private Logger logger = LoggerFactory.getLogger(PeiLvDetailUpdateService.class);
	
	//private Map<String, Boolean> scheduleMap = new HashMap<String, Boolean>();
	private ThreadPoolExecutor peiLvDetailUpdateExecutor;

	@Value("${peiLvDetail}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private FootBallMapUtil footBallMapUtil;
	
	@Autowired
	private SendJmsJczUtil sendJmsJczUtil;
	
	@Autowired
	private GlobalInfoService infoService;
	
	@PostConstruct
	public void init() {
		peiLvDetailUpdateExecutor = ThreadPoolUtil.createTaskExecutor("peiLvDetailUpdate", 10);
	}
	
	@SuppressWarnings("unchecked")
	public void process() {
		logger.info("足球赔率变化更新开始");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.downfile(url, HttpUtil.UTF8);
			if (StringUtils.isBlank(data)) {
				logger.info("足球赔率变化更新时获取数据为空");
				return;
			}
			//去掉特殊字符(参考:http://blog.csdn.net/haffun_dao/article/details/7792820)
			data = data.replaceAll("[^\\x20-\\x7e]", "");
			Document doc = DocumentHelper.parseText(data);
			Element rootElement = doc.getRootElement();
			List<Element> aList = rootElement.elements("a");
			if (aList!=null&&aList.size()>0) {
				Element letGoalElement = aList.get(0); //亚赔（让球盘）变化数据
				ProcessLetGoalDetailThread task = new ProcessLetGoalDetailThread(letGoalElement);
				logger.info("peiLvDetailUpdateExecutor,size="+peiLvDetailUpdateExecutor.getQueue().size());
				peiLvDetailUpdateExecutor.execute(task);
				//processLetGoalDetail(letGoalElement);
			}
		} catch (Exception e) {
			logger.error("足球赔率变化更新发生异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("足球赔率变化更新结束，共用时 " + (endmillis - startmillis));
	}
	
	private class ProcessLetGoalDetailThread implements Runnable {
		private Element element;
		
		private ProcessLetGoalDetailThread(Element element) {
			this.element = element;
		}
		
		@Override
		public void run() {
			logger.info("足球赔率变化更新-letGoalDetail开始");
			long startmillis = System.currentTimeMillis();
			try {
				doLetgoalDetail(element);
			} catch(Exception e) {
				logger.error("足球赔率变化更新-letGoalDetail发生异常", e);
			}
			long endmillis = System.currentTimeMillis();
			logger.info("足球赔率变化更新-letGoalDetail结束, 共用时 " + (endmillis - startmillis));
		}
	}
	
	/*private void processLetGoalDetail(final Element element) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("足球赔率变化更新-letGoalDetail开始");
				long startmillis = System.currentTimeMillis();
				try {
					doLetgoalDetail(element);
				} catch(Exception e) {
					logger.error("足球赔率变化更新-letGoalDetail发生异常", e);
				}
				long endmillis = System.currentTimeMillis();
				logger.info("足球赔率变化更新-letGoalDetail结束, 共用时 " + (endmillis - startmillis));
			}
		}).start();
	}*/
	
	@SuppressWarnings("unchecked")
	private void doLetgoalDetail(Element element) {
		List<Element> detailElements = element.elements("h");
		logger.info("足球赔率变化更新-letGoalDetail,size="+detailElements.size());
		List<Integer> scheduleIds = new ArrayList<Integer>();
		for (Element detailElement : detailElements) {
			String data = detailElement.getText();
			Integer scheduleId = buildLetGoal(data);
			if (scheduleId!=null && !scheduleIds.contains(scheduleId)) {
				scheduleIds.add(scheduleId);
			}
		}
		for (Integer scheduleId : scheduleIds) {
			long startmillis = System.currentTimeMillis();
			updateLetGoalCache(scheduleId);
			long endmillis = System.currentTimeMillis();
			logger.info("updateLetGoalCache用时 " + (endmillis - startmillis));
		}
	}
	
	public void updateLetGoalCache(Integer scheduleId) {
		long startmillis = System.currentTimeMillis();
		List<LetGoal> letGoals = LetGoal.findByScheduleID(scheduleId);
		infoService.buildLetGoals(letGoals);
		GlobalCache letGoal = GlobalCache.findGlobalCache(StringUtil.join("_", "dataanalysis", "LetGoal", String.valueOf(scheduleId)));
		if (letGoal==null) {
			letGoal = new GlobalCache();
			letGoal.setId(StringUtil.join("_", "dataanalysis", "LetGoal", String.valueOf(scheduleId)));
			letGoal.setValue(LetGoal.toJsonArray(letGoals));
			letGoal.persist();
		} else {
			letGoal.setValue(LetGoal.toJsonArray(letGoals));
			letGoal.merge();
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新亚赔缓存,用时 " + (endmillis - startmillis));
		long startmillis2 = System.currentTimeMillis();
		//infoService.updateInfo(scheduleId);
		sendJmsJczUtil.sendInfoUpdateJMS(String.valueOf(scheduleId));
		long endmillis2 = System.currentTimeMillis();
		logger.info("updateLetGoalCache-updateInfo,用时 " + (endmillis2 - startmillis2));
	}
	
	private Integer buildLetGoal(String data) {
		try {
			long startmillis = System.currentTimeMillis();
			//<h>649557,35,-0.75,0.91,0.98,False,True</h>
			String[] values = StringUtils.split(data, ",");
			String scheduleId = values[0]; //比赛ID
			String companyId = values[1]; //公司ID
			String goal = values[2]; //即时盘口
			String upOdds = values[3]; //主队即时赔率
			String downOdds = values[4]; //客队即时赔率
			String closePan = values[5]; //是否封盘 True or False
			String zhoudi = values[6]; //是否走地True or False
			int cpInt = StringUtils.equals(closePan, "True") ? 1 : 0;
			int zdInt = StringUtils.equals(zhoudi, "True") ? 1 : 0;
			long startmillis2 = System.currentTimeMillis();
			Boolean sHasExist = footBallMapUtil.scheduleMap.get(scheduleId);
			if (sHasExist==null||!sHasExist) {
				Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleId));
				sHasExist = schedule==null ? false : true;
				footBallMapUtil.scheduleMap.put(scheduleId, sHasExist);
			}
			long endmillis2 = System.currentTimeMillis();
			logger.info("buildLetGoal,获取Schedule用时 " + (endmillis2 - startmillis2));
			if (!sHasExist) {
				return null;
			}
			/*if (CommonUtil.isZqEventEmpty(schedule)) {
				return null;
			}*/
			long startmillis3 = System.currentTimeMillis();
			LetGoal letGoal = LetGoal.findLetGoal(Integer.parseInt(scheduleId), Integer.parseInt(companyId));
			long endmillis3 = System.currentTimeMillis();
			logger.info("buildLetGoal,获取LetGoal用时 " + (endmillis3 - startmillis3));
			if (letGoal==null) {
				return null;
			}
			boolean ismod = false;
			if ((StringUtils.isNotBlank(goal)&&!NumberUtil.compare(new Double(goal).toString(), letGoal.getGoal()))
					||(StringUtils.isNotBlank(upOdds)&&!NumberUtil.compare(upOdds, letGoal.getUpOdds()))
					||(StringUtils.isNotBlank(downOdds)&&!NumberUtil.compare(downOdds, letGoal.getDownOdds()))) {
				ismod = true;
				letGoal.setGoal(new Double(goal));
				letGoal.setUpOdds(new Double(upOdds));
				letGoal.setDownOdds(new Double(downOdds));
				letGoal.setModifyTime(new Date());
				LetGoalDetail detail = new LetGoalDetail();
				detail.setOddsID(letGoal.getOddsID());
				detail.setGoal(new Double(goal));
				detail.setUpOdds(new Double(upOdds));
				detail.setDownOdds(new Double(downOdds));
				detail.setIsEarly(0);
				detail.setModifyTime(new Date());
				detail.persist();
			}
			if(cpInt!=letGoal.getClosePan() || zdInt!=letGoal.getZouDi()) {
				ismod = true;
				letGoal.setClosePan(cpInt);
				letGoal.setZouDi(zdInt);
			}
			if(ismod) {
				letGoal.merge();
				return letGoal.getScheduleID();
			}
			long endmillis = System.currentTimeMillis();
			logger.info("buildLetGoal共用时 " + (endmillis - startmillis));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
}
