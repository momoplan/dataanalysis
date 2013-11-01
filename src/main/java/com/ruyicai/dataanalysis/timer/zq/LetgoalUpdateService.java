package com.ruyicai.dataanalysis.timer.zq;

import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.LetGoal;
import com.ruyicai.dataanalysis.domain.LetGoalDetail;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.ThreadPoolUtil;
import com.ruyicai.dataanalysis.util.zq.FootBallMapUtil;

/**
 * 足球亚赔更新
 * @author Administrator
 *
 */
@Service
public class LetgoalUpdateService {

	private Logger logger = LoggerFactory.getLogger(LetgoalUpdateService.class);
	
	private ThreadPoolExecutor letgoalUpdateExecutor;
	
	@Value("${peiluall}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private FootBallMapUtil footBallMapUtil;
	
	@PostConstruct
	public void init() {
		letgoalUpdateExecutor = ThreadPoolUtil.createTaskExecutor("letgoalUpdate", 10);
	}
	
	public void process() {
		try {
			logger.info("足球亚赔更新开始");
			//long startmillis = System.currentTimeMillis();
			String data = httpUtil.downfile(url, HttpUtil.UTF8);
			if(StringUtils.isBlank(data)) {
				logger.error("足球亚赔更新获取数据为空");
				return ;
			}
			final String[] datas = data.split("\\$");
			processLetGoal(datas[2]);
			//此处不处理欧赔,因为如果某场赛事没有亚赔,那么欧赔也不会返回,这样就不能更新这场赛事的缓存,
			//迁移到插入欧赔数据的时候更新缓存
			//processStandard(datas[3]);
			//long endmillis = System.currentTimeMillis();
			//logger.info("足球亚赔更新结束, 共用时 " + (endmillis - startmillis));
		} catch(Exception e) {
			logger.error("足球亚赔更新发生异常", e);
		}
	}

	private void processLetGoal(String value) {
		ProcessLetGoalThread task = new ProcessLetGoalThread(value);
		letgoalUpdateExecutor.execute(task);
		
		/*String[] datas = value.split("\\;");
		logger.info("足球亚赔更新,size="+(datas==null ? 0 : datas.length));
		for(String data : datas) {
			ProcessLetGoalThread task = new ProcessLetGoalThread(data);
			letgoalUpdateExecutor.execute(task);
		}*/
	}
	
	private final class ProcessLetGoalThread implements Runnable {
		private String data;
		
		public ProcessLetGoalThread(String data) {
			this.data = data;
		}

		@Override
		public void run() {
			try {
				long startmillis = System.currentTimeMillis();
				String[] datas = data.split("\\;");
				logger.info("足球亚赔更新,size="+(datas==null ? 0 : datas.length));
				for(String data : datas) {
					doLetGoal(data);
				}
				long endmillis = System.currentTimeMillis();
				logger.info("足球亚赔更新结束, 共用时 " + (endmillis - startmillis));
				
				//doLetGoal(data);
			} catch (Exception e) {
				logger.error("足球亚赔更新-ProcessLetGoalThread,发生异常", e);
			}
		}
	}
	
	private void doLetGoal(String data) {
		//long startmillis = System.currentTimeMillis();
		String[] values = data.split("\\,");
		String scheduleID = (values!=null&&values.length>=1) ? values[0] : "";
		String companyID = (values!=null&&values.length>=2) ? values[1] : "";
		String firstGoal = (values!=null&&values.length>=3) ? values[2] : "";
		String firstUpOdds = (values!=null&&values.length>=4) ? values[3] : "";
		String firstDownOdds = (values!=null&&values.length>=5) ? values[4] : "";
		String goal = (values!=null&&values.length>=6) ? values[5] : "";
		String upOdds = (values!=null&&values.length>=7) ? values[6] : "";
		String downOdds = (values!=null&&values.length>=8) ? values[7] : "";
		String closePan = (values!=null&&values.length>=9) ? values[8] : "";
		Integer cp = StringUtils.equals(closePan, "True") ? 1 : 0;
		String zhoudi = (values!=null&&values.length>=10) ? values[9] : "";
		Integer zd = StringUtils.equals(zhoudi, "True") ? 1 : 0;
		
		String lkey = StringUtil.join("_", scheduleID, companyID);
		Boolean lHasExist = footBallMapUtil.letgoalMap.get(lkey);
		if (lHasExist==null||!lHasExist) {
			LetGoal letGoal = LetGoal.findLetGoal(Integer.parseInt(scheduleID), Integer.parseInt(companyID));
			lHasExist = letGoal==null ? false : true;
			footBallMapUtil.letgoalMap.put(lkey, lHasExist);
		}
		if(!lHasExist) {
			LetGoal letGoal = new LetGoal();
			letGoal.setScheduleID(Integer.parseInt(scheduleID));
			letGoal.setCompanyID(Integer.parseInt(companyID));
			letGoal.setFirstGoal(new Double(firstGoal));
			letGoal.setFirstUpodds(new Double(firstUpOdds));
			letGoal.setFirstDownodds(new Double(firstDownOdds));
			letGoal.setGoal(StringUtil.isEmpty(goal) ? null : new Double(goal));
			letGoal.setUpOdds(StringUtil.isEmpty(upOdds) ? null : new Double(upOdds));
			letGoal.setDownOdds(StringUtil.isEmpty(downOdds) ? null : new Double(downOdds));
			letGoal.setClosePan(cp);
			letGoal.setZouDi(zd);
			letGoal.persist();
			
			LetGoalDetail detail = new LetGoalDetail();
			detail.setOddsID(letGoal.getOddsID());
			detail.setGoal(new Double(firstGoal));
			detail.setUpOdds(new Double(firstUpOdds));
			detail.setDownOdds(new Double(firstDownOdds));
			detail.setIsEarly(1);
			detail.setModifyTime(new Date());
			detail.persist();
			if(!StringUtil.isEmpty(goal) && !StringUtil.isEmpty(upOdds) && !StringUtil.isEmpty(downOdds)) {
				detail = new LetGoalDetail();
				detail.setOddsID(letGoal.getOddsID());
				detail.setGoal(new Double(goal));
				detail.setUpOdds(new Double(upOdds));
				detail.setDownOdds(new Double(downOdds));
				detail.setIsEarly(0);
				detail.setModifyTime(new Date());
				detail.persist();
			}
		}
		//long endmillis = System.currentTimeMillis();
		//logger.info("足球亚赔更新-doLetGoal结束,用时:"+(endmillis-startmillis)+",scheduleID="+scheduleID
				//+",threadPoolSize="+letgoalUpdateExecutor.getQueue().size());
	}
	
}
