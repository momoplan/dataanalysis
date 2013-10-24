package com.ruyicai.dataanalysis.timer.zq;

import java.util.Date;
import java.util.List;
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
import com.ruyicai.dataanalysis.service.GlobalInfoService;
import com.ruyicai.dataanalysis.util.CommonUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.ThreadPoolUtil;
import com.ruyicai.dataanalysis.util.zq.SendJmsJczUtil;

/**
 * 足球亚赔Detail更新
 * @author Administrator
 *
 */
@Service
public class LetgoalDetailUpdateService {

	private Logger logger = LoggerFactory.getLogger(LetgoalDetailUpdateService.class);
	
	private ThreadPoolExecutor letgoalDetailUpdateExecutor;

	@Value("${peiLvDetail}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private SendJmsJczUtil sendJmsJczUtil;
	
	@Autowired
	private GlobalInfoService infoService;
	
	@PostConstruct
	public void init() {
		letgoalDetailUpdateExecutor = ThreadPoolUtil.createTaskExecutor("letgoalDetailUpdate", 50);
	}
	
	@SuppressWarnings("unchecked")
	public void process() {
		try {
			logger.info("足球亚赔Detail更新开始");
			long startmillis = System.currentTimeMillis();
			String data = httpUtil.downfile(url, HttpUtil.UTF8);
			if (StringUtils.isBlank(data)) {
				logger.info("足球亚赔Detail更新时获取数据为空");
				return;
			}
			//去掉特殊字符(参考:http://blog.csdn.net/haffun_dao/article/details/7792820)
			data = data.replaceAll("[^\\x20-\\x7e]", "");
			Document doc = DocumentHelper.parseText(data);
			Element rootElement = doc.getRootElement();
			List<Element> aList = rootElement.elements("a");
			if (aList!=null&&aList.size()>0) {
				Element letGoalElement = aList.get(0); //亚赔（让球盘）变化数据
				List<Element> detailElements = letGoalElement.elements("h");
				logger.info("足球亚赔Detail更新,size="+(detailElements==null ? 0 : detailElements.size()));
				if (detailElements!=null && detailElements.size()>0) {
					for (Element detailElement : detailElements) {
						ProcessLetGoalDetailThread task = new ProcessLetGoalDetailThread(detailElement.getText());
						letgoalDetailUpdateExecutor.execute(task);
					}
				}
			}
			long endmillis = System.currentTimeMillis();
			logger.info("足球亚赔Detail更新结束,用时" + (endmillis-startmillis));
		} catch (Exception e) {
			logger.error("足球亚赔Detail更新发生异常", e);
		}
	}
	
	private class ProcessLetGoalDetailThread implements Runnable {
		private String data;
		
		private ProcessLetGoalDetailThread(String data) {
			this.data = data;
		}
		
		@Override
		public void run() {
			try {
				doLetGoalDetail(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void doLetGoalDetail(String data) {
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
		
		LetGoal letGoal = LetGoal.findLetGoal(Integer.parseInt(scheduleId), Integer.parseInt(companyId));
		if (letGoal==null) {
			return ;
		}
		boolean letgoalModify = false;
		boolean detailModify = false;
		if ((StringUtils.isNotBlank(goal)&&!NumberUtil.compare(new Double(goal).toString(), letGoal.getGoal()))
				||(StringUtils.isNotBlank(upOdds)&&!NumberUtil.compare(upOdds, letGoal.getUpOdds()))
				||(StringUtils.isNotBlank(downOdds)&&!NumberUtil.compare(downOdds, letGoal.getDownOdds()))) {
			letgoalModify = true;
			detailModify = true;
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
			letgoalModify = true;
			letGoal.setClosePan(cpInt);
			letGoal.setZouDi(zdInt);
		}
		if(letgoalModify) {
			letGoal.merge();
		}
		if (detailModify) {
			updateLetGoalCache(Integer.parseInt(scheduleId));
		}
		long endmillis = System.currentTimeMillis();
		logger.info("足球亚赔Detail更新-doLetGoalDetail,用时:"+(endmillis-startmillis)+",scheduleId="+scheduleId
				+",threadPoolSize="+letgoalDetailUpdateExecutor.getQueue().size());
	}
	
	private void updateLetGoalCache(Integer scheduleId) {
		Schedule schedule = Schedule.findSchedule(scheduleId);
		if (schedule==null||CommonUtil.isZqEventEmpty(schedule)) { //如果event为空,则不需要更新缓存
			return ;
		}
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
		sendJmsJczUtil.sendInfoUpdateJMS(String.valueOf(scheduleId));
	}
	
}
