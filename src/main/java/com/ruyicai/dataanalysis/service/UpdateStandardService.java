package com.ruyicai.dataanalysis.service;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
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
import com.ruyicai.dataanalysis.domain.EuropeCompany;
import com.ruyicai.dataanalysis.domain.GlobalCache;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Standard;
import com.ruyicai.dataanalysis.domain.StandardDetail;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.ThreadPoolUtil;
import com.ruyicai.dataanalysis.util.jcz.CalcUtil;
import com.ruyicai.dataanalysis.util.jcz.FootBallMapUtil;
import com.ruyicai.dataanalysis.util.jcz.SendJmsJczUtil;

/**
 * 足球欧赔更新
 * @author Administrator
 *
 */
@Service
public class UpdateStandardService {
	
	private Logger logger = LoggerFactory.getLogger(UpdateStandardService.class);
	
	private ThreadPoolExecutor standardUpdateExecutor;

	@Value("${baijiaoupei}")
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
		standardUpdateExecutor = ThreadPoolUtil.createTaskExecutor("standardUpdate", 10);
	}
	
	@SuppressWarnings("unchecked")
	public void process() {
		logger.info("足球欧赔更新开始");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.getResponse(url+"?day=1", HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("足球欧赔更新时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> matches = doc.getRootElement().elements("h");
			logger.info("足球欧赔,size="+matches.size());
			int size = 0;
			for(Element match : matches) {
				String scheduleID = match.elementTextTrim("id");
				Boolean sHasExist = footBallMapUtil.scheduleMap.get(scheduleID);
				if (sHasExist==null||!sHasExist) {
					Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleID));
					sHasExist = schedule==null ? false : true;
					footBallMapUtil.scheduleMap.put(scheduleID, sHasExist);
				}
				if(!sHasExist) {
					continue;
				}
				/*if (CommonUtil.isZqEventEmpty(schedule)) {
					continue;
				}*/
				sendJmsJczUtil.sendStandardUpdateJMS(match.asXML());
				size++;
			}
			logger.info("欧赔size="+size);
		} catch(Exception e) {
			logger.error("足球欧赔更新发生异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("足球欧赔更新结束，共用时 " + (endmillis - startmillis));
	}

	public void processByMinute() {
		logger.info("足球欧赔-processByMinute更新开始");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.getResponse(url+"?min=2", HttpUtil.GET, HttpUtil.UTF8, "");
			long endmillis2 = System.currentTimeMillis();
			logger.info("足球欧赔-processByMinute更新时获取数据,用时"+(endmillis2 - startmillis));
			if (StringUtils.isBlank(data)) {
				logger.info("足球欧赔-processByMinute更新时获取数据为空");
				return;
			}
			ProcessStandardThread task = new ProcessStandardThread(data);
			logger.info("standardUpdateExecutor,size="+standardUpdateExecutor.getQueue().size());
			standardUpdateExecutor.execute(task);
			//processStandard(data);
			long endmillis = System.currentTimeMillis();
			logger.info("足球欧赔-processByMinute更新结束，共用时 " + (endmillis - startmillis));
		} catch (Exception e) {
			logger.error("足球欧赔-processByMinute更新时发生异常", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private class ProcessStandardThread implements Runnable {
		private String data;
		
		private ProcessStandardThread(String data) {
			this.data = data;
		}
		
		@Override
		public void run() {
			logger.info("足球欧赔更新-processByMinute-processStandard开始");
			long startmillis = System.currentTimeMillis();
			try {
				Document doc = DocumentHelper.parseText(data);
				List<Element> matches = doc.getRootElement().elements("h");
				logger.info("足球欧赔更新-processByMinute-processStandard,size="+matches.size());
				for(Element match : matches) {
					doStandard(match);
				}
				long endmillis = System.currentTimeMillis();
				logger.info("足球欧赔更新-processByMinute-processStandard结束, 共用时 " + (endmillis - startmillis)+",size="+matches.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/*@SuppressWarnings("unchecked")
	private void processStandard(final String data) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("足球欧赔更新-processByMinute-processStandard开始");
				long startmillis = System.currentTimeMillis();
				try {
					Document doc = DocumentHelper.parseText(data);
					List<Element> matches = doc.getRootElement().elements("h");
					logger.info("足球欧赔更新-processByMinute-processStandard,size="+matches.size());
					for(Element match : matches) {
						doStandard(match);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				long endmillis = System.currentTimeMillis();
				logger.info("足球欧赔更新-processByMinute-processStandard结束, 共用时 " + (endmillis - startmillis));
			}
		}).start();
	}*/
	
	@SuppressWarnings("unchecked")
	private void doStandard(Element match) {
		try {
			long startmillis = System.currentTimeMillis();
			String scheduleId = match.elementTextTrim("id");
			long startmillis2 = System.currentTimeMillis();
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleId));
			long endmillis2 = System.currentTimeMillis();
			logger.info("doStandard,获取Schedule用时："+(endmillis2-startmillis2));
			if(schedule==null) {
				return;
			}
			/*if (CommonUtil.isZqEventEmpty(schedule)) {
				return;
			}*/
			boolean isModify = false; //欧赔是否发生变化
			List<Element> odds = match.element("odds").elements("o");
			for(Element odd : odds) {
				boolean modify = doOdd(scheduleId, odd);
				if (!isModify&&modify) {
					isModify = true;
				}
			}
			if (isModify) {
				long startmillis3 = System.currentTimeMillis();
				updateStandardCache(schedule);
				long endmillis3 = System.currentTimeMillis();
				logger.info("updateStandardCache,用时:"+(endmillis3-startmillis3));
			}
			//查看是否需要更新缓存
			//updateCache(Integer.parseInt(scheduleId));
			long endmillis = System.currentTimeMillis();
			logger.info("doStandard,用时:"+(endmillis-startmillis));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean doOdd(String scheduleId, Element odd) {
		long startmillis = System.currentTimeMillis();
		try {
			String o = odd.getTextTrim();
			String[] values = o.split("\\,");
			String companyId = values[0]; //博彩公司ID
			//String companyName = values[1]; //博彩公司名
			//String firstHomeWin = values[2]; //初盘主胜
			//String firstStandoff = values[3]; //初盘和局
			//String firstGuestWin = values[4]; //初盘客胜
			String homeWin = values[5]; //主胜
			String standoff = values[6]; //和局
			String guestWin = values[7]; //客胜
			String modTime = values[8]; //变化时间
			long startmillis2 = System.currentTimeMillis();
			Standard standard = Standard.findStandard(Integer.parseInt(scheduleId), Integer.parseInt(companyId));
			long endmillis2 = System.currentTimeMillis();
			logger.info("doOdd,获取Standard用时:"+(endmillis2-startmillis2));
			if (standard==null) {
				return false;
			}
			if ((StringUtils.isNotBlank(homeWin) && !NumberUtil.compare(homeWin, standard.getHomeWin()))
					||(StringUtils.isNotBlank(standoff) && !NumberUtil.compare(standoff, standard.getStandoff()))
					||(StringUtils.isNotBlank(guestWin) && !NumberUtil.compare(guestWin, standard.getGuestWin()))) {
				logger.info("scheduleId="+scheduleId+";companyId="+companyId+";homeWin="+homeWin+";standard.getHomeWin()="+standard.getHomeWin()
						+";standoff="+standoff+";standard.getStandoff()="+standard.getStandoff()
						+";guestWin="+guestWin+";standard.getGuestWin()="+standard.getGuestWin());
				standard.setHomeWin(new Double(homeWin));
				standard.setStandoff(new Double(standoff));
				standard.setGuestWin(new Double(guestWin));
				standard.setModifyTime(DateUtil.parse("yyyy/MM/dd HH:mm:ss", modTime));
				standard.merge();
				StandardDetail detail = new StandardDetail();
				detail.setOddsID(standard.getOddsID());
				detail.setIsEarly(0);
				detail.setHomeWin(new Double(homeWin));
				detail.setStandoff(new Double(standoff));
				detail.setGuestWin(new Double(guestWin));
				detail.setModifyTime(standard.getModifyTime());
				detail.persist();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endmillis = System.currentTimeMillis();
		logger.info("doOdd,用时:"+(endmillis-startmillis));
		return false;
	}
	
	private void updateStandardCache(Schedule schedule) {
		long startmillis1 = System.currentTimeMillis();
		Integer scheduleId = schedule.getScheduleID();
		Collection<Standard> standards = Standard.findByScheduleID(scheduleId);
		infoService.buildStandards(schedule, standards);
		GlobalCache standard = GlobalCache.findGlobalCache(StringUtil.join("_", "dataanalysis", "Standard", String.valueOf(scheduleId)));
		if (standard==null) {
			standard = new GlobalCache();
			standard.setId(StringUtil.join("_", "dataanalysis", "Standard", String.valueOf(scheduleId)));
			standard.setValue(Standard.toJsonArray(standards));
			standard.persist();
		} else {
			standard.setValue(Standard.toJsonArray(standards));
			standard.merge();
		}
		long endmillis1 = System.currentTimeMillis();
		logger.info("更新Standard缓存，用时:"+(endmillis1-startmillis1));
		long startmillis2 = System.currentTimeMillis();
		//infoService.updateInfo(scheduleId);
		sendJmsJczUtil.sendInfoUpdateJMS(String.valueOf(scheduleId));
		long endmillis2 = System.currentTimeMillis();
		logger.info("updateStandardCache-updateInfo，用时:"+(endmillis2-startmillis2));
	}
	
	/*private void updateCache(Integer scheduleID) {
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
	
}
