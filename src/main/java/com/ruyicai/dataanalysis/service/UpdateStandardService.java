package com.ruyicai.dataanalysis.service;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
import com.ruyicai.dataanalysis.util.CommonUtil;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.jcz.CalcUtil;
import com.ruyicai.dataanalysis.util.jcz.SendJmsJczUtil;

/**
 * 足球欧赔更新
 * @author Administrator
 *
 */
@Service
public class UpdateStandardService {
	
	private Logger logger = LoggerFactory.getLogger(UpdateStandardService.class);

	@Value("${baijiaoupei}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private SendJmsJczUtil sendJmsJczUtil;
	
	@Autowired
	private GlobalInfoService globalInfoService;
	
	@SuppressWarnings("unchecked")
	public void process() {
		logger.info("足球欧赔更新开始");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.getResponse(url, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("足球欧赔更新时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> matches = doc.getRootElement().elements("h");
			for(Element match : matches) {
				String scheduleID = match.elementTextTrim("id");
				Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleID));
				if(null == schedule) {
					continue;
				}
				if (CommonUtil.isZqEventEmpty(schedule)) {
					continue;
				}
				sendJmsJczUtil.sendStandardUpdateJMS(match.asXML());
			}
		} catch(Exception e) {
			logger.error("足球欧赔更新发生异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("足球欧赔更新结束，共用时 " + (endmillis - startmillis));
	}

	@SuppressWarnings("unchecked")
	public void processByMinute() {
		logger.info("足球欧赔-processByMinute更新开始");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.getResponse(url+"?min=1", HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("足球欧赔-processByMinute更新时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> matches = doc.getRootElement().elements("h");
			for(Element match : matches) {
				processStandard(match);
			}
		} catch (Exception e) {
			logger.error("足球欧赔-processByMinute更新时发生异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("足球欧赔-processByMinute更新结束，共用时 " + (endmillis - startmillis));
	}
	
	@SuppressWarnings("unchecked")
	private void processStandard(Element match) {
		try {
			String scheduleId = match.elementTextTrim("id");
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleId));
			if(schedule==null) {
				return;
			}
			if (CommonUtil.isZqEventEmpty(schedule)) {
				return;
			}
			List<Element> odds = match.element("odds").elements("o");
			for(Element odd : odds) {
				processOdd(scheduleId, odd);
			}
			//查看是否需要更新缓存
			updateCache(Integer.parseInt(scheduleId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void processOdd(String scheduleId, Element odd) {
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
			Standard standard = Standard.findStandard(Integer.parseInt(scheduleId), Integer.parseInt(companyId));
			if (standard==null) {
				return ;
			}
			if ((StringUtils.isNotBlank(homeWin) && !NumberUtil.compare(homeWin, standard.getHomeWin()))
					||(StringUtils.isNotBlank(standoff) && !NumberUtil.compare(standoff, standard.getStandoff()))
					||(StringUtils.isNotBlank(guestWin) && !NumberUtil.compare(guestWin, standard.getGuestWin()))) {
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateCache(Integer scheduleID) {
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
	
	private void buildStandards(Schedule schedule, Collection<Standard> standards) {
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
	}
	
	private List<Standard> convertStandards(Collection<Standard> collection) {
		List<Standard> standards = new LinkedList<Standard>();
		for(Standard standard : collection) {
			standards.add(standard);
		}
		return standards;
	}
	
}
