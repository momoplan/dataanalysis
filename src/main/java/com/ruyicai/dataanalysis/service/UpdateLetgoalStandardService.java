package com.ruyicai.dataanalysis.service;

import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.LetGoal;
import com.ruyicai.dataanalysis.domain.LetGoalDetail;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.jcz.FootBallMapUtil;

@Service
public class UpdateLetgoalStandardService {
	
	private Logger logger = LoggerFactory.getLogger(UpdateLetgoalStandardService.class);
	
	//private Map<String, Boolean> scheduleMap = new HashMap<String, Boolean>();
	//private Map<String, Boolean> letgoalMap = new HashMap<String, Boolean>();

	@Value("${peiluall}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private FootBallMapUtil footBallMapUtil;
	
	/*@Autowired
	private GlobalInfoService globalInfoService;*/
	
	public void process() {
		logger.info("开始更新赔率");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.downfile(url, HttpUtil.UTF8);
			if(StringUtils.isBlank(data)) {
				logger.error("更新赔率获取data为空");
				return ;
			}
			final String[] datas = data.split("\\$");
			processLetGoal(datas[2]);
			//此处不处理欧赔,因为如果某场赛事没有亚赔,那么欧赔也不会返回,这样就不能更新这场赛事的缓存,
			//迁移到插入欧赔数据的时候更新缓存
			//processStandard(datas[3]);
		} catch(Exception e) {
			logger.error("更新赔率出错", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新赔率结束, 共用时 " + (endmillis - startmillis));
	}

	private void processLetGoal(final String data) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("开始更新亚赔");
				long startmillis = System.currentTimeMillis();
				try {
					doLetgoal(data, startmillis);
				} catch(Exception e) {
					logger.error("更新亚赔出错", e);
				}
				//long endmillis = System.currentTimeMillis();
				//logger.info("更新亚赔结束, 共用时 " + (endmillis - startmillis));
			}
		}).start();
	}
	
	private void doLetgoal(String value, long startmillis) {
		String[] datas = value.split("\\;");
		logger.info("亚赔size:{}", new Integer[] {datas.length});
		//Map<Integer, List<LetGoal>> map = new HashMap<Integer, List<LetGoal>>();
		for(String data : datas) {
			buildLetGoal(data);
			
			//LetGoal letGoal = buildLetGoal(data);
			/*if(null != letGoal) {
				Schedule schedule = Schedule.findSchedule(letGoal.getScheduleID());
				if(null != schedule && (!CommonUtil.isZqEventEmpty(schedule))) {
					List<LetGoal> list = map.get(letGoal.getScheduleID());
					if(null == list) {
						list = new LinkedList<LetGoal>();
						map.put(letGoal.getScheduleID(), list);
					}
					list.add(letGoal);
				}
			}*/
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新亚赔结束, 共用时 " + (endmillis - startmillis)+",size="+datas.length);
		//isUpdateInfo(map);
	}
	
	/*public void isUpdateInfo(Map<Integer, List<LetGoal>> map) {
		for(Entry<Integer, List<LetGoal>> entry : map.entrySet()) {
			GlobalCache globalCache = GlobalCache.findGlobalCache(StringUtil.join("_", "dataanalysis", "LetGoal", String.valueOf(entry.getKey())));
			if(null == globalCache) {
				globalCache = new GlobalCache();
				globalCache.setId(StringUtil.join("_", "dataanalysis", "LetGoal", String.valueOf(entry.getKey())));
				globalCache.setValue(LetGoal.toJsonArray(entry.getValue()));
				globalCache.persist();
				globalInfoService.updateInfo(entry.getKey());
			} else {
				Collection<LetGoal> collection = LetGoal.fromJsonArrayToLetGoals(globalCache.getValue());
				if(entry.getValue().size() != collection.size()) {
					globalCache.setValue(LetGoal.toJsonArray(entry.getValue()));
					globalCache.merge();
					globalInfoService.updateInfo(entry.getKey());
				} else {
					List<LetGoal> letGoals = convertLetGoals(collection);
					Collections.sort(letGoals);
					Collections.sort(entry.getValue());
					boolean isupdate = false;
					for(int i = 0; i < letGoals.size(); i ++) {
						LetGoal l1 = letGoals.get(i);
						LetGoal l2 = entry.getValue().get(i);
						if(!l1.equals(l2)) {
							isupdate = true; 
							break;
						}
					}
					if(isupdate) {
						globalCache.setValue(LetGoal.toJsonArray(entry.getValue()));
						globalCache.merge();
						globalInfoService.updateInfo(entry.getKey());
					}
				}
			}
		}
	}*/
	
	/*private List<LetGoal> convertLetGoals(Collection<LetGoal> collection) {
		List<LetGoal> letGoals = new LinkedList<LetGoal>();
		for(LetGoal letGoal : collection) {
			letGoals.add(letGoal);
		}
		return letGoals;
	}*/

	private void buildLetGoal(String data) {
		try {
			long startmillis = System.currentTimeMillis();
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
			long startmillis2 = System.currentTimeMillis();
			Boolean sHasExist = footBallMapUtil.scheduleMap.get(scheduleID);
			if (sHasExist==null||!sHasExist) {
				Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleID));
				sHasExist = schedule==null ? false : true;
				footBallMapUtil.scheduleMap.put(scheduleID, sHasExist);
			}
			long endmillis2 = System.currentTimeMillis();
			logger.info("buildLetGoal,获取Schedule用时 " + (endmillis2 - startmillis2));
			if(!sHasExist) {
				return ;
			}
			/*if (CommonUtil.isZqEventEmpty(schedule)) {
				return null;
			}*/
			long startmillis3 = System.currentTimeMillis();
			String lkey = StringUtil.join("_", scheduleID, companyID);
			Boolean lHasExist = footBallMapUtil.letgoalMap.get(lkey);
			if (lHasExist==null||!lHasExist) {
				LetGoal letGoal = LetGoal.findLetGoal(Integer.parseInt(scheduleID), Integer.parseInt(companyID));
				lHasExist = letGoal==null ? false : true;
				footBallMapUtil.letgoalMap.put(lkey, lHasExist);
			}
			long endmillis3 = System.currentTimeMillis();
			logger.info("buildLetGoal,获取LetGoal用时 " + (endmillis3 - startmillis3));
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
				/*if("True".equals(closePan)) {
					letGoal.setClosePan(1);
				}
				if("True".equals(zhoudi)) {
					letGoal.setZouDi(1);
				}*/
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
			} /*else {
				boolean ismod = false;
				int cp = 0;
				int zd = 0;
				if("True".equals(closePan)) {
					cp = 1;
				}
				if("True".equals(zhoudi)) {
					zd = 1;
				}
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
				if(cp != letGoal.getClosePan() || zd != letGoal.getZouDi()) {
					ismod = true;
					letGoal.setClosePan(cp);
					letGoal.setZouDi(zd);
				}
				if(ismod) {
					letGoal.merge();
				}
			}*/
			/*if(null != letGoal) {
				Company company = Company.findCompany(Integer.parseInt(companyID));
				if(null != company) {
					letGoal.setCompanyName(company.getName_Cn());
					letGoal.setCompanyName_e(company.getName_E());
				}
				letGoal.setFirstGoal_name(CalcUtil.handicap(letGoal.getFirstGoal()));
				letGoal.setGoal_name(CalcUtil.handicap(letGoal.getGoal()));
			}*/
			long endmillis = System.currentTimeMillis();
			logger.info("buildLetGoal用时 " + (endmillis - startmillis));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
