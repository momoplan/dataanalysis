package com.ruyicai.dataanalysis.timer.lq;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.dataanalysis.domain.lq.CompanyJcl;
import com.ruyicai.dataanalysis.domain.lq.GlobalCacheJcl;
import com.ruyicai.dataanalysis.domain.lq.LetGoalJcl;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.domain.lq.TotalScoreJcl;
import com.ruyicai.dataanalysis.service.lq.GlobalInfoJclService;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * 竞彩篮球-赔率更新
 * @author Administrator
 *
 */
@Service
public class PeiLvJclUpdateService {

	private Logger logger = LoggerFactory.getLogger(PeiLvJclUpdateService.class);
	
	@Autowired
	private GlobalInfoJclService globalInfoJclService;

	@Value("${peiLvJclUrl}")
	private String peiLvJclUrl;
	
	@Autowired
	private HttpUtil httpUtil;
	
	public void process() {
		logger.info("竞彩篮球-赔率更新开始");
		long startmillis = System.currentTimeMillis();
		try {
//			String data = httpUtil.downfile(peiLvJclUrl, HttpUtil.UTF8);
			String data = httpUtil.getResponse(peiLvJclUrl, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("竞彩篮球-赔率更新时获取数据为空");
				return;
			}
			final String[] datas = data.split("\\$");
			//防止数据为空(data=$$$$$$)时出现数组越界的异常
			if (datas!=null&&datas.length>=2) {
				processLetGoal(datas[2]); //亚赔-让分盘
			}
			//此处不处理欧赔,因为如果某场赛事没有亚赔,那么欧赔也不会返回,这样就不能更新这场赛事的缓存,
			//迁移到插入欧赔数据的时候更新缓存
			//processStandard(datas[3]); //欧赔
			if (datas!=null&&datas.length>=4) {
				processTotalScore(datas[4]); //亚赔-总分盘
			}
		} catch(Exception e) {
			logger.error("竞彩篮球-赔率更新异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("竞彩篮球-赔率更新结束, 共用时 " + (endmillis - startmillis));
	}

	/**
	 * 竞彩篮球-亚赔-让分盘更新
	 * @param data
	 */
	private void processLetGoal(final String data) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("竞彩篮球-亚赔-让分盘更新开始");
				long startmillis = System.currentTimeMillis();
				try {
					doLetgoal(data);
				} catch(Exception e) {
					logger.error("竞彩篮球-亚赔-让分盘更新异常", e);
				}
				long endmillis = System.currentTimeMillis();
				logger.info("竞彩篮球-亚赔-让分盘更新结束, 共用时 " + (endmillis - startmillis));
			}
		}).start();
	}
	
	/**
	 * 竞彩篮球-亚赔-总分盘更新
	 * @param data
	 */
	private void processTotalScore(final String data) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("竞彩篮球-亚赔-总分盘更新开始");
				long startmillis = System.currentTimeMillis();
				try {
					doTotalScore(data);
				} catch(Exception e) {
					logger.error("竞彩篮球-亚赔-总分盘更新异常", e);
				}
				long endmillis = System.currentTimeMillis();
				logger.info("竞彩篮球-亚赔-总分盘更新结束, 共用时 " + (endmillis - startmillis));
			}
		}).start();
	}

	/**
	 * 解析亚盘-让分盘数据
	 * @param value
	 */
	private void doLetgoal(String value) {
		String[] datas = value.split("\\;");
		Map<Integer, List<LetGoalJcl>> map = new HashMap<Integer, List<LetGoalJcl>>();
		for(String data : datas) {
			LetGoalJcl letGoalJcl = buildLetGoal(data);
			if(null != letGoalJcl) {
				ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJcl(letGoalJcl.getScheduleId());
				if(null != scheduleJcl && !StringUtil.isEmpty(scheduleJcl.getEvent())) {
					List<LetGoalJcl> list = map.get(letGoalJcl.getScheduleId());
					if(null == list) {
						list = new LinkedList<LetGoalJcl>();
						map.put(letGoalJcl.getScheduleId(), list);
					}
					list.add(letGoalJcl);
				}
			}
		}
		for(Entry<Integer, List<LetGoalJcl>> entry : map.entrySet()) {
			GlobalCacheJcl globalCache = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "LetGoal", String.valueOf(entry.getKey())));
			if(null == globalCache) {
				globalCache = new GlobalCacheJcl();
				globalCache.setId(StringUtil.join("_", "dataAnalysisJcl", "LetGoal", String.valueOf(entry.getKey())));
				globalCache.setValue(LetGoalJcl.toJsonArray(entry.getValue()));
				globalCache.persist();
				globalInfoJclService.updateInfo(entry.getKey());
			} else {
				Collection<LetGoalJcl> collection = LetGoalJcl.fromJsonArrayToLetGoalJcls(globalCache.getValue());
				if(entry.getValue().size() != collection.size()) {
					globalCache.setValue(LetGoalJcl.toJsonArray(entry.getValue()));
					globalCache.merge();
					globalInfoJclService.updateInfo(entry.getKey());
				} else {
					List<LetGoalJcl> letGoalJcls = convertLetGoalJcls(collection);
					Collections.sort(letGoalJcls);
					Collections.sort(entry.getValue());
					boolean isUpdate = false;
					for(int i = 0; i < letGoalJcls.size(); i ++) {
						LetGoalJcl l1 = letGoalJcls.get(i);
						LetGoalJcl l2 = entry.getValue().get(i);
						if(!l1.equals(l2)) {
							isUpdate = true; 
							break;
						}
					}
					if(isUpdate) {
						globalCache.setValue(LetGoalJcl.toJsonArray(entry.getValue()));
						globalCache.merge();
						globalInfoJclService.updateInfo(entry.getKey());
					}
				}
			}
		}
	}
	
	/**
	 * 转换亚赔-让分盘
	 * @param collection
	 * @return
	 */
	private List<LetGoalJcl> convertLetGoalJcls(Collection<LetGoalJcl> collection) {
		List<LetGoalJcl> letGoalJcls = new LinkedList<LetGoalJcl>();
		for(LetGoalJcl letGoal : collection) {
			letGoalJcls.add(letGoal);
		}
		return letGoalJcls;
	}
	
	/**
	 * 解析亚赔-让分盘数据
	 * @param data
	 * @return
	 */
	private LetGoalJcl buildLetGoal(String data) {
		try {
			String[] infos = data.split("\\,");
			String scheduleId = infos[0]; //比赛ID
			String companyId = infos[1]; //公司ID
			String firstGoal = infos[2]; //初盘盘口
			String firstUpodds = infos[3]; //主队初盘赔率
			String firstDownodds = infos[4]; //客队初盘赔率
			String goal = infos[5]; //即时盘口
			String upOdds = infos[6]; //主队即时赔率
			String downOdds = infos[7]; //客队即时赔率
			ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJcl(Integer.parseInt(scheduleId));
			if (scheduleJcl==null||StringUtil.isEmpty(scheduleJcl.getEvent())) {
				return null;
			}
			LetGoalJcl letGoalJcl = LetGoalJcl.findLetGoalJcl(Integer.parseInt(scheduleId), Integer.parseInt(companyId));
			if (letGoalJcl==null) {
				letGoalJcl = new LetGoalJcl();
				letGoalJcl.setScheduleId(Integer.parseInt(scheduleId));
				letGoalJcl.setCompanyId(Integer.parseInt(companyId));
				letGoalJcl.setFirstGoal(new Double(firstGoal));
				letGoalJcl.setFirstUpodds(new Double(firstUpodds));
				letGoalJcl.setFirstDownodds(new Double(firstDownodds));
				letGoalJcl.setGoal(new Double(goal));
				letGoalJcl.setUpOdds(new Double(upOdds));
				letGoalJcl.setDownOdds(new Double(downOdds));
				letGoalJcl.persist();
			} else {
				boolean isModfify = false;
				if (!StringUtil.isEmpty(firstGoal) && !NumberUtil.compare(firstGoal, letGoalJcl.getFirstGoal())) {
					isModfify = true;
					letGoalJcl.setFirstGoal(new Double(firstGoal));
				}
				if (!StringUtil.isEmpty(firstUpodds) && !NumberUtil.compare(firstUpodds, letGoalJcl.getFirstUpodds())) {
					isModfify = true;
					letGoalJcl.setFirstUpodds(new Double(firstUpodds));
				}
				if (!StringUtil.isEmpty(firstDownodds) && !NumberUtil.compare(firstDownodds, letGoalJcl.getFirstDownodds())) {
					isModfify = true;
					letGoalJcl.setFirstDownodds(new Double(firstDownodds));
				}
				if (!StringUtil.isEmpty(goal) && !NumberUtil.compare(goal, letGoalJcl.getGoal())) {
					isModfify = true;
					letGoalJcl.setGoal(new Double(goal));
				}
				if (!StringUtil.isEmpty(upOdds) && !NumberUtil.compare(upOdds, letGoalJcl.getUpOdds())) {
					isModfify = true;
					letGoalJcl.setUpOdds(new Double(upOdds));
				}
				if (!StringUtil.isEmpty(downOdds) && !NumberUtil.compare(downOdds , letGoalJcl.getDownOdds())) {
					isModfify = true;
					letGoalJcl.setDownOdds(new Double(downOdds));
				}
				if (isModfify) {
					letGoalJcl.merge();
				}
			}
			if (letGoalJcl!=null) {
				CompanyJcl companyJcl = CompanyJcl.findCompanyJcl(Integer.parseInt(companyId));
				if(null != companyJcl) {
					letGoalJcl.setCompanyName(companyJcl.getCompanyName());
				}
			}
			return letGoalJcl;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 解析亚盘-总分盘数据
	 * @param value
	 */
	private void doTotalScore(String value) {
		String[] datas = value.split("\\;");
		Map<Integer, List<TotalScoreJcl>> map = new HashMap<Integer, List<TotalScoreJcl>>();
		for(String data : datas) {
			TotalScoreJcl totalScoreJcl = buildTotalScore(data);
			if(null != totalScoreJcl) {
				ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJcl(totalScoreJcl.getScheduleId());
				if(null != scheduleJcl && !StringUtil.isEmpty(scheduleJcl.getEvent())) {
					List<TotalScoreJcl> list = map.get(totalScoreJcl.getScheduleId());
					if(null == list) {
						list = new LinkedList<TotalScoreJcl>();
						map.put(totalScoreJcl.getScheduleId(), list);
					}
					list.add(totalScoreJcl);
				}
			}
		}
		for(Entry<Integer, List<TotalScoreJcl>> entry : map.entrySet()) {
			GlobalCacheJcl globalCache = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "TotalScore", String.valueOf(entry.getKey())));
			if(null == globalCache) {
				globalCache = new GlobalCacheJcl();
				globalCache.setId(StringUtil.join("_", "dataAnalysisJcl", "TotalScore", String.valueOf(entry.getKey())));
				globalCache.setValue(TotalScoreJcl.toJsonArray(entry.getValue()));
				globalCache.persist();
				globalInfoJclService.updateInfo(entry.getKey());
			} else {
				Collection<TotalScoreJcl> collection = TotalScoreJcl.fromJsonArrayToTotalScoreJcls(globalCache.getValue());
				if(entry.getValue().size() != collection.size()) {
					globalCache.setValue(TotalScoreJcl.toJsonArray(entry.getValue()));
					globalCache.merge();
					globalInfoJclService.updateInfo(entry.getKey());
				} else {
					List<TotalScoreJcl> totalScoreJcls = convertTotalScoreJcls(collection);
					Collections.sort(totalScoreJcls);
					Collections.sort(entry.getValue());
					boolean isUpdate = false;
					for(int i = 0; i < totalScoreJcls.size(); i ++) {
						TotalScoreJcl l1 = totalScoreJcls.get(i);
						TotalScoreJcl l2 = entry.getValue().get(i);
						if(!l1.equals(l2)) {
							isUpdate = true; 
							break;
						}
					}
					if(isUpdate) {
						globalCache.setValue(TotalScoreJcl.toJsonArray(entry.getValue()));
						globalCache.merge();
						globalInfoJclService.updateInfo(entry.getKey());
					}
				}
			}
		}
	}
	
	/**
	 * 解析亚赔-总分盘数据
	 * @param data
	 * @return
	 */
	private TotalScoreJcl buildTotalScore(String data) {
		try {
			String[] infos = data.split("\\,");
			String scheduleId = infos[0]; //比赛ID
			String companyId = infos[1]; //公司ID
			String firstGoal = infos[2]; //初盘盘口
			String firstUpodds = infos[3]; //初盘大分赔率
			String firstDownodds = infos[4]; //初盘小分赔率
			String goal = infos[5]; //即时盘盘口
			String upOdds = infos[6]; //即时盘大分赔率
			String downOdds = infos[7]; //即时盘小分赔率
			ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJcl(Integer.parseInt(scheduleId));
			if (scheduleJcl==null||StringUtil.isEmpty(scheduleJcl.getEvent())) {
				return null;
			}
			TotalScoreJcl totalScoreJcl = TotalScoreJcl.findTotalScoreJcl(Integer.parseInt(scheduleId), Integer.parseInt(companyId));
			if (totalScoreJcl==null) {
				totalScoreJcl = new TotalScoreJcl();
				totalScoreJcl.setScheduleId(Integer.parseInt(scheduleId));
				totalScoreJcl.setCompanyId(Integer.parseInt(companyId));
				totalScoreJcl.setFirstGoal(new Double(firstGoal));
				totalScoreJcl.setFirstUpodds(new Double(firstUpodds));
				totalScoreJcl.setFirstDownodds(new Double(firstDownodds));
				totalScoreJcl.setGoal(new Double(goal));
				totalScoreJcl.setUpOdds(new Double(upOdds));
				totalScoreJcl.setDownOdds(new Double(downOdds));
				totalScoreJcl.persist();
			} else {
				boolean isModify = false;
				if (!StringUtil.isEmpty(firstGoal) && !NumberUtil.compare(firstGoal, totalScoreJcl.getFirstGoal())) {
					isModify = true;
					totalScoreJcl.setFirstGoal(new Double(firstGoal));
				}
				if (!StringUtil.isEmpty(firstUpodds) && !NumberUtil.compare(firstUpodds, totalScoreJcl.getFirstUpodds())) {
					isModify = true;
					totalScoreJcl.setFirstUpodds(new Double(firstUpodds));
				}
				if (!StringUtil.isEmpty(firstDownodds) && !NumberUtil.compare(firstDownodds, totalScoreJcl.getFirstDownodds())) {
					isModify = true;
					totalScoreJcl.setFirstDownodds(new Double(firstDownodds));
				}
				if (!StringUtil.isEmpty(goal) && !NumberUtil.compare(goal, totalScoreJcl.getGoal())) {
					isModify = true;
					totalScoreJcl.setGoal(new Double(goal));
				}
				if (!StringUtil.isEmpty(upOdds) && !NumberUtil.compare(upOdds, totalScoreJcl.getUpOdds())) {
					isModify = true;
					totalScoreJcl.setUpOdds(new Double(upOdds));
				}
				if (!StringUtil.isEmpty(downOdds) && !NumberUtil.compare(downOdds, totalScoreJcl.getDownOdds())) {
					isModify = true;
					totalScoreJcl.setDownOdds(new Double(downOdds));
				}
				if (isModify) {
					totalScoreJcl.merge();
				}
			}
			if (totalScoreJcl!=null) {
				CompanyJcl companyJcl = CompanyJcl.findCompanyJcl(Integer.parseInt(companyId));
				if(null != companyJcl) {
					totalScoreJcl.setCompanyName(companyJcl.getCompanyName());
				}
			}
			return totalScoreJcl;
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 转换亚赔-总分盘
	 * @param collection
	 * @return
	 */
	private List<TotalScoreJcl> convertTotalScoreJcls(Collection<TotalScoreJcl> collection) {
		List<TotalScoreJcl> totalScoreJcls = new LinkedList<TotalScoreJcl>();
		for(TotalScoreJcl totalScoreJcl : collection) {
			totalScoreJcls.add(totalScoreJcl);
		}
		return totalScoreJcls;
	}
	
}
