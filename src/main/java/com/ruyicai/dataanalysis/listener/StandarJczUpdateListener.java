package com.ruyicai.dataanalysis.listener;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.camel.Body;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.EuropeCompany;
import com.ruyicai.dataanalysis.domain.GlobalCache;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Standard;
import com.ruyicai.dataanalysis.domain.StandardDetail;
import com.ruyicai.dataanalysis.service.GlobalInfoService;
import com.ruyicai.dataanalysis.util.CommonUtil;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.jcz.CalcUtil;

/**
 * 足球欧赔更新Jms的处理
 * @author Administrator
 *
 */
@Service
public class StandarJczUpdateListener {
	
	private Logger logger = LoggerFactory.getLogger(StandarJczUpdateListener.class);
	
	@Autowired
	private GlobalInfoService globalInfoService;
	
	public void update(@Body String body) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(body);
		} catch (Exception e) {
			logger.error("足球欧赔更新Jms的处理-转Document发生异常", e);
			return;
		}
		doProcess(document.getRootElement());
	}
	
	/**
	 * 解析数据
	 * @param match
	 */
	@SuppressWarnings("unchecked")
	private void doProcess(Element match) {
		try {
			String scheduleID = match.elementTextTrim("id");
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleID));
			if(null == schedule) {
				return;
			}
			if (CommonUtil.isZqEventEmpty(schedule)) {
				return;
			}
			List<Element> odds = match.element("odds").elements("o");
			Double t_h = 0D;
			Double t_s = 0D;
			Double t_g = 0D;
			for(Element odd : odds) {
				String o = odd.getTextTrim();
				String[] values = o.split("\\,");
				String companyID = values[0];
				logger.info("足球欧赔更新Jms的处理,scheduleID="+scheduleID+";companyID="+companyID);
				String companyName = values[1];
				String firstHomeWin = values[2];
				String firstStandoff = values[3];
				String firstGuestWin = values[4];
				String homeWin = values[5];
				String standoff = values[6];
				String guestWin = values[7];
				String modTime = values[8];
				if(!StringUtil.isEmpty(homeWin) && !StringUtil.isEmpty(standoff) && !StringUtil.isEmpty(guestWin)) {
					t_h = t_h + new Double(homeWin);
					t_s = t_s + new Double(standoff);
					t_g = t_g + new Double(guestWin);
				} else {
					t_h = t_h + new Double(firstHomeWin);
					t_s = t_s + new Double(firstStandoff);
					t_g = t_g + new Double(firstGuestWin);
				}
				EuropeCompany company = EuropeCompany.findEuropeCompany(Integer.parseInt(companyID));
				if(null == company) {
					company = new EuropeCompany();
					company.setCompanyID(Integer.parseInt(companyID));
					company.setName_Cn(companyName);
					company.setName_E(companyName);
					company.setIsPrimary(0);
					company.setIsExchange(0);
					company.persist();
				}
				Standard standard = Standard.findStandard(Integer.parseInt(scheduleID), Integer.parseInt(companyID));
				if(null == standard) {
					standard = new Standard();
					standard.setScheduleID(Integer.parseInt(scheduleID));
					standard.setCompanyID(Integer.parseInt(companyID));
					standard.setFirstHomeWin(new Double(firstHomeWin));
					standard.setFirstStandoff(new Double(firstStandoff));
					standard.setFirstGuestWin(new Double(firstGuestWin));
					standard.setHomeWin(StringUtil.isEmpty(homeWin) ? null : new Double(homeWin));
					standard.setStandoff(StringUtil.isEmpty(standoff) ? null : new Double(standoff));
					standard.setGuestWin(StringUtil.isEmpty(guestWin) ? null : new Double(guestWin));
					standard.setModifyTime(DateUtil.parse("yyyy/MM/dd HH:mm:ss", modTime));
					standard.persist();
					StandardDetail detail = new StandardDetail();
					detail.setOddsID(standard.getOddsID());
					detail.setIsEarly(1);
					detail.setHomeWin(new Double(firstHomeWin));
					detail.setStandoff(new Double(firstStandoff));
					detail.setGuestWin(new Double(firstGuestWin));
					detail.setModifyTime(standard.getModifyTime());
					detail.persist();
					if(null != standard.getHomeWin()) {
						detail = new StandardDetail();
						detail.setOddsID(standard.getOddsID());
						detail.setIsEarly(0);
						detail.setHomeWin(new Double(homeWin));
						detail.setStandoff(new Double(standoff));
						detail.setGuestWin(new Double(guestWin));
						detail.setModifyTime(standard.getModifyTime());
						detail.persist();
					}
				} else {
					if((!StringUtil.isEmpty(homeWin) && !NumberUtil.compare(homeWin, standard.getHomeWin())) ||
							(!StringUtil.isEmpty(standoff) && !NumberUtil.compare(standoff, standard.getStandoff())) ||
							(!StringUtil.isEmpty(guestWin) && !NumberUtil.compare(guestWin, standard.getGuestWin()))) {
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
				}
			}
			if(null != schedule && odds.size() > 0) {
				BigDecimal b = new BigDecimal(t_h / odds.size());
				b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
				schedule.setAvgH(b.doubleValue());
				b = new BigDecimal(t_s / odds.size());
				b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
				schedule.setAvgS(b.doubleValue());
				b = new BigDecimal(t_g / odds.size());
				b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
				schedule.setAvgG(b.doubleValue());
				schedule.merge();
			}
			//查看是否需要更新缓存
			updateCache(Integer.parseInt(scheduleID));
		} catch(Exception e) {
			logger.error("足球欧赔更新Jms的处理-解析数据发生异常", e);
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
