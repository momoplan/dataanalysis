package com.ruyicai.dataanalysis.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import com.ruyicai.dataanalysis.domain.Company;
import com.ruyicai.dataanalysis.domain.GlobalCache;
import com.ruyicai.dataanalysis.domain.LetGoal;
import com.ruyicai.dataanalysis.domain.LetGoalDetail;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.CalcUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class UpdateLetgoalStandardService {
	
	private Logger logger = LoggerFactory.getLogger(UpdateLetgoalStandardService.class);

	@Value("${peiluall}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private GlobalInfoService globalInfoService;
	
	public void process() {
		logger.info("开始更新赔率");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.downfile(url, HttpUtil.UTF8);
			if(StringUtil.isEmpty(data)) {
				logger.error("更新赔率获取data为空");
			} else {
				final String[] datas = data.split("\\$");
				processLetGoal(datas[2]);
				//此处不处理欧赔,因为如果某场赛事没有亚赔,那么欧赔也不会返回,这样就不能更新这场赛事的缓存,
				//迁移到插入欧赔数据的时候更新缓存
				//processStandard(datas[3]);
			}
		} catch(Exception e) {
			logger.error("更新赔率出错", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新赔率结束, 共用时 " + (endmillis - startmillis));
	}

	/*private void processStandard(final String data) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("开始更新欧赔");
				long startmillis = System.currentTimeMillis();
				try {
					doStandard(data);
				} catch(Exception e) {
					logger.error("更新欧赔出错", e);
				}
				long endmillis = System.currentTimeMillis();
				logger.info("更新欧赔结束, 共用时 " + (endmillis - startmillis));
			}
		}).start();
	}*/

	private void processLetGoal(final String data) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("开始更新亚赔");
				long startmillis = System.currentTimeMillis();
				try {
					doLetgoal(data);
				} catch(Exception e) {
					logger.error("更新亚赔出错", e);
				}
				long endmillis = System.currentTimeMillis();
				logger.info("更新亚赔结束, 共用时 " + (endmillis - startmillis));
			}
		}).start();
	}
	
	/*private void doStandard(String value) {
		String[] datas = value.split("\\;");
		logger.info("欧赔size:{}", new Integer[] {datas.length});
		Set<Integer> scheduleIDs = new HashSet<Integer>();
		for(String data : datas) {
			String scheduleID = buildStandard(data);
			if(!StringUtil.isEmpty(scheduleID)) {
				scheduleIDs.add(Integer.parseInt(scheduleID));
			}
		}
		for(Integer scheduleID : scheduleIDs) {
			String id = StringUtil.join("_", "dataanalysis", "Standard", String.valueOf(scheduleID));
			GlobalCache globalCache = GlobalCache.findGlobalCache(id);
			List<Standard> list = Standard.findByScheduleID(scheduleID);
			buildStandards(Schedule.findSchedule(scheduleID), list);
			if(null == globalCache) {
				globalCache = new GlobalCache();
				globalCache.setId(id);
				globalCache.setValue(Standard.toJsonArray(list));
				globalCache.persist();
				globalInfoService.updateInfo(scheduleID);
			} else {
				Collection<Standard> collection = Standard.fromJsonArrayToStandards(globalCache.getValue());
				if(list.size() != collection.size()) {
					globalCache.setValue(Standard.toJsonArray(list));
					globalCache.merge();
					globalInfoService.updateInfo(scheduleID);
				} else {
					List<Standard> standards = convertStandards(collection);
					Collections.sort(standards);
					Collections.sort(list);
					boolean isupdate = false;
					for(int i = 0; i < standards.size(); i ++) {
						Standard s1 = standards.get(i);
						Standard s2 = list.get(i);
						if(!s1.equals(s2)) {
							isupdate = true; 
							break;
						}
					}
					if(isupdate) {
						globalCache.setValue(Standard.toJsonArray(list));
						globalCache.merge();
						globalInfoService.updateInfo(scheduleID);
					}
				}
			}
		}
	}*/
	
	/*private void buildStandards(Schedule schedule, Collection<Standard> standards) {
		if(null != standards && !standards.isEmpty()) {
			for(Standard standard : standards) {
				EuropeCompany company = EuropeCompany.findEuropeCompany(standard.getCompanyID());
				if(null != company) {
					standard.setCompanyName(company.getName_Cn());
					standard.setCompanyName_e(company.getName_E());
					standard.setIsPrimary(company.getIsPrimary());
					standard.setIsExchange(company.getIsExchange());
				}
				standard.setHomeWin(null == standard.getHomeWin() ? standard.getFirstHomeWin() : standard.getHomeWin());
				standard.setStandoff(null == standard.getStandoff() ? standard.getFirstStandoff() : standard.getStandoff());
				standard.setGuestWin(null == standard.getGuestWin() ? standard.getFirstGuestWin() : standard.getGuestWin());
				standard.setHomeWinLu(CalcUtil.probability_H(standard.getHomeWin(), standard.getStandoff(), standard.getGuestWin()));
				standard.setStandoffLu(CalcUtil.probability_S(standard.getHomeWin(), standard.getStandoff(), standard.getGuestWin()));
				standard.setGuestWinLu(CalcUtil.probability_G(standard.getHomeWin(), standard.getStandoff(), standard.getGuestWin()));
				standard.setFanHuanLu(CalcUtil.fanhuan(standard.getHomeWinLu(), standard.getHomeWin()));
				if(null != schedule.getAvgH()) {
					standard.setK_h(CalcUtil.k_h(standard.getHomeWinLu(), schedule.getAvgH()));
				}
				if(null != schedule.getAvgS()) {
					standard.setK_s(CalcUtil.k_s(standard.getStandoffLu(), schedule.getAvgS()));
				}
				if(null != schedule.getAvgG()) {
					standard.setK_g(CalcUtil.k_g(standard.getGuestWinLu(), schedule.getAvgG()));
				}
			}
		}
	}*/
	
	/*private List<Standard> convertStandards(Collection<Standard> collection) {
		List<Standard> standards = new LinkedList<Standard>();
		for(Standard standard : collection) {
			standards.add(standard);
		}
		return standards;
	}*/

	/*private String buildStandard(String data) {
		try {
			String[] datas = data.split("\\,");
			String scheduleID = datas[0];
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleID));
			if(null == schedule) {
				return null;
			}
			String event = schedule.getEvent();
			String zcSfcEvent = schedule.getZcSfcEvent();
			String zcJqcEvent = schedule.getZcJqcEvent();
			String zcBqcEvent = schedule.getZcBqcEvent();
			if(StringUtil.isEmpty(event)&&StringUtil.isEmpty(zcSfcEvent)&&StringUtil.isEmpty(zcJqcEvent)
					&&StringUtil.isEmpty(zcBqcEvent)) {
				return null;
			}
			return scheduleID;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}*/

	private void doLetgoal(String value) {
		String[] datas = value.split("\\;");
		logger.info("亚赔size:{}", new Integer[] {datas.length});
		Map<Integer, List<LetGoal>> map = new HashMap<Integer, List<LetGoal>>();
		for(String data : datas) {
			LetGoal letGoal = buildLetGoal(data);
			if(null != letGoal) {
				Schedule schedule = Schedule.findSchedule(letGoal.getScheduleID());
				String event = schedule.getEvent();
				String zcSfcEvent = schedule.getZcSfcEvent();
				String zcJqcEvent = schedule.getZcJqcEvent();
				String zcBqcEvent = schedule.getZcBqcEvent();
				if(null != schedule && (!StringUtil.isEmpty(event)||!StringUtil.isEmpty(zcSfcEvent) 
						||!StringUtil.isEmpty(zcJqcEvent)||!StringUtil.isEmpty(zcBqcEvent))) {
					List<LetGoal> list = map.get(letGoal.getScheduleID());
					if(null == list) {
						list = new LinkedList<LetGoal>();
						map.put(letGoal.getScheduleID(), list);
					}
					list.add(letGoal);
				}
			}
		}
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
	}
	
	private List<LetGoal> convertLetGoals(Collection<LetGoal> collection) {
		List<LetGoal> letGoals = new LinkedList<LetGoal>();
		for(LetGoal letGoal : collection) {
			letGoals.add(letGoal);
		}
		return letGoals;
	}

	private LetGoal buildLetGoal(String data) {
		try {
			String[] values = data.split("\\,");
			String scheduleID = values[0];
			String companyID = values[1];
			String firstGoal = values[2];
			String firstUpOdds = values[3];
			String firstDownOdds = values[4];
			String goal = values[5];
			String upOdds = values[6];
			String downOdds = values[7];
			String closePan = values[8];
			String zhoudi = values[9];
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleID));
			if(null == schedule) {
				return null;
			}
			String event = schedule.getEvent();
			String zcSfcEvent = schedule.getZcSfcEvent();
			String zcJqcEvent = schedule.getZcJqcEvent();
			String zcBqcEvent = schedule.getZcBqcEvent();
			if(StringUtil.isEmpty(event)&&StringUtil.isEmpty(zcSfcEvent)&&StringUtil.isEmpty(zcJqcEvent)
					&&StringUtil.isEmpty(zcBqcEvent)) {
				return null;
			}
			LetGoal letGoal = LetGoal.findLetGoal(Integer.parseInt(scheduleID), Integer.parseInt(companyID));
			if(null == letGoal) {
				letGoal = new LetGoal();
				letGoal.setScheduleID(Integer.parseInt(scheduleID));
				letGoal.setCompanyID(Integer.parseInt(companyID));
				letGoal.setFirstGoal(new Double(firstGoal));
				letGoal.setFirstUpodds(new Double(firstUpOdds));
				letGoal.setFirstDownodds(new Double(firstDownOdds));
				letGoal.setGoal(StringUtil.isEmpty(goal) ? null : new Double(goal));
				letGoal.setUpOdds(StringUtil.isEmpty(upOdds) ? null : new Double(upOdds));
				letGoal.setDownOdds(StringUtil.isEmpty(downOdds) ? null : new Double(downOdds));
				letGoal.setClosePan(0);
				letGoal.setZouDi(0);
				if("True".equals(closePan)) {
					letGoal.setClosePan(1);
				}
				if("True".equals(zhoudi)) {
					letGoal.setZouDi(1);
				}
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
			} else {
				boolean ismod = false;
				int cp = 0;
				int zd = 0;
				if("True".equals(closePan)) {
					cp = 1;
				}
				if("True".equals(zhoudi)) {
					zd = 1;
				}
				if((!StringUtil.isEmpty(goal) && !NumberUtil.compare(goal, letGoal.getGoal())) || 
						(!StringUtil.isEmpty(upOdds) && !NumberUtil.compare(upOdds, letGoal.getUpOdds())) ||
						(!StringUtil.isEmpty(downOdds) && !NumberUtil.compare(downOdds, letGoal.getDownOdds()))) {
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
			}
			if(null != letGoal) {
				Company company = Company.findCompany(Integer.parseInt(companyID));
				if(null != company) {
					letGoal.setCompanyName(company.getName_Cn());
					letGoal.setCompanyName_e(company.getName_E());
				}
				letGoal.setFirstGoal_name(CalcUtil.handicap(letGoal.getFirstGoal()));
				letGoal.setGoal_name(CalcUtil.handicap(letGoal.getGoal()));
			}
			return letGoal;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
