package com.ruyicai.dataanalysis.listener.lq;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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

import com.ruyicai.dataanalysis.domain.lq.EuropeCompanyJcl;
import com.ruyicai.dataanalysis.domain.lq.GlobalCacheJcl;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.domain.lq.StandardJcl;
import com.ruyicai.dataanalysis.service.lq.GlobalInfoJclService;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.lq.CalcJclUtil;

/**
 * 竞彩篮球-百家欧赔更新JMS的处理
 * @author Administrator
 *
 */
@Service
public class StandarJclUpdateListener {
	
	private Logger logger = LoggerFactory.getLogger(StandarJclUpdateListener.class);
	
	@Autowired
	private GlobalInfoJclService globalInfoJclService;
	
	public void update(@Body String body) {
		Document document = null;
		try {
			//logger.info("竞彩篮球-百家欧赔JMS start");
			document = DocumentHelper.parseText(body);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return;
		}
		doProcess(document.getRootElement());
	}
	
	/**
	 * 解析百家欧赔数据
	 * @param match
	 */
	@SuppressWarnings("unchecked")
	private void doProcess(Element match) {
		try {
			String scheduleId = match.elementTextTrim("id");
			ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJcl(Integer.parseInt(scheduleId));
			if(scheduleJcl==null||StringUtil.isEmpty(scheduleJcl.getEvent())) {
				return;
			}
			List<Element> odds = match.element("odds").elements("o");
			Double t_h = 0D;
			Double t_g = 0D;
			for(Element odd : odds) {
				String info = odd.getTextTrim();
				String[] infos = info.split("\\,");
				String companyId = infos[0];
				String companyName = infos[1];
				String firstHomeWin = infos[2];
				String firstGuestWin = infos[3];
				String homeWin = infos[4];
				String guestWin = infos[5];
				String modifyTime = infos[6];
				
				if(!StringUtil.isEmpty(homeWin) && !StringUtil.isEmpty(guestWin)) {
					t_h = t_h + new Double(homeWin);
					t_g = t_g + new Double(guestWin);
				} else {
					t_h = t_h + new Double(firstHomeWin);
					t_g = t_g + new Double(firstGuestWin);
				}
				
				EuropeCompanyJcl europeCompanyJcl = EuropeCompanyJcl.findEuropeCompanyJcl(Integer.parseInt(companyId));
				if(null == europeCompanyJcl) {
					europeCompanyJcl = new EuropeCompanyJcl();
					europeCompanyJcl.setCompanyId(Integer.parseInt(companyId));
					europeCompanyJcl.setNameE(companyName);
					europeCompanyJcl.setNameC(companyName);
					europeCompanyJcl.persist();
				}
				StandardJcl standardJcl = StandardJcl.findStandardJcl(Integer.parseInt(scheduleId), Integer.parseInt(companyId));
				if (standardJcl==null) {
					standardJcl = new StandardJcl();
					standardJcl.setCompanyId(Integer.parseInt(companyId));
					standardJcl.setFirstHomeWin(new Double(firstHomeWin));
					standardJcl.setFirstGuestWin(new Double(firstGuestWin));
					standardJcl.setHomeWin(new Double(homeWin));
					standardJcl.setGuestWin(new Double(guestWin));
					standardJcl.setScheduleId(Integer.parseInt(scheduleId));
					standardJcl.setModifyTime(DateUtil.parse("yyyy/MM/dd HH:mm:ss", modifyTime));
					standardJcl.persist();
				} else {
					boolean isMofify = false;
					
					if (!StringUtil.isEmpty(firstHomeWin) && !NumberUtil.compare(firstHomeWin, standardJcl.getFirstHomeWin())) {
						isMofify = true;
						standardJcl.setFirstHomeWin(new Double(firstHomeWin));
					}
					if (!StringUtil.isEmpty(firstGuestWin) && !NumberUtil.compare(firstGuestWin, standardJcl.getFirstGuestWin())) {
						isMofify = true;
						standardJcl.setFirstGuestWin(new Double(firstGuestWin));
					}
					if (!StringUtil.isEmpty(homeWin) && !NumberUtil.compare(homeWin, standardJcl.getHomeWin())) {
						isMofify = true;
						standardJcl.setHomeWin(new Double(homeWin));
					}
					if (!StringUtil.isEmpty(guestWin) && !NumberUtil.compare(guestWin, standardJcl.getGuestWin())) {
						isMofify = true;
						standardJcl.setGuestWin(new Double(guestWin));
					}
					Date modifyTime_old = standardJcl.getModifyTime();
					if (!StringUtil.isEmpty(modifyTime) && (modifyTime_old==null||!modifyTime.equals(DateUtil.format("yyyy/MM/dd HH:mm:ss", modifyTime_old)))) {
						isMofify = true;
						standardJcl.setModifyTime(DateUtil.parse("yyyy/MM/dd HH:mm:ss", modifyTime));
					}
					if (isMofify) {
						standardJcl.merge();
					}
				}
			}
			if (scheduleJcl!=null&&odds.size()>0) {
				BigDecimal b = new BigDecimal(t_h / odds.size());
				b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
				scheduleJcl.setAvgH(b.doubleValue());
				
				b = new BigDecimal(t_g / odds.size());
				b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
				scheduleJcl.setAvgG(b.doubleValue());
				scheduleJcl.merge();
			}
			//查看是否需要更新缓存
			updateCache(Integer.parseInt(scheduleId));
		} catch (Exception e) {
			logger.error("解析竞彩篮球-百家欧赔异常", e);
		}
	}
	
	private void updateCache(Integer scheduleId) {
		String id = StringUtil.join("_", "dataAnalysisJcl", "Standard", String.valueOf(scheduleId));
		GlobalCacheJcl globalCache = GlobalCacheJcl.findGlobalCache(id);
		List<StandardJcl> list = StandardJcl.findByScheduleID(scheduleId);
		buildStandards(ScheduleJcl.findScheduleJcl(scheduleId), list);
		if(null == globalCache) {
			globalCache = new GlobalCacheJcl();
			globalCache.setId(id);
			globalCache.setValue(StandardJcl.toJsonArray(list));
			globalCache.persist();
			globalInfoJclService.updateInfo(scheduleId);
		} else {
			Collection<StandardJcl> collection = StandardJcl.fromJsonArrayToStandardJcls(globalCache.getValue());
			if(list.size() != collection.size()) {
				globalCache.setValue(StandardJcl.toJsonArray(list));
				globalCache.merge();
				globalInfoJclService.updateInfo(scheduleId);
			} else {
				List<StandardJcl> standards = convertStandardJcls(collection);
				Collections.sort(standards);
				Collections.sort(list);
				boolean isUpdate = false;
				for(int i = 0; i < standards.size(); i ++) {
					StandardJcl s1 = standards.get(i);
					StandardJcl s2 = list.get(i);
					if(!s1.equals(s2)) {
						isUpdate = true; 
						break;
					}
				}
				if(isUpdate) {
					globalCache.setValue(StandardJcl.toJsonArray(list));
					globalCache.merge();
					globalInfoJclService.updateInfo(scheduleId);
				}
			}
		}
	}
	
	/**
	 * 设置欧赔的返还率和凯利指数
	 * @param schedule
	 * @param standards
	 */
	private void buildStandards(ScheduleJcl scheduleJcl, Collection<StandardJcl> standards) {
		if(null != standards && !standards.isEmpty()) {
			for(StandardJcl standardJcl : standards) {
				EuropeCompanyJcl europeCompanyJcl = EuropeCompanyJcl.findEuropeCompanyJcl(standardJcl.getCompanyId());
				if (europeCompanyJcl!=null) {
					standardJcl.setCompanyName(europeCompanyJcl.getNameC());
				}
				standardJcl.setHomeWin(standardJcl.getHomeWin()==null ? standardJcl.getFirstHomeWin() : standardJcl.getHomeWin());
				standardJcl.setGuestWin(standardJcl.getGuestWin()==null ? standardJcl.getFirstGuestWin() : standardJcl.getGuestWin());
				standardJcl.setHomeWinLv(CalcJclUtil.probability_H(standardJcl.getHomeWin(), standardJcl.getGuestWin()));
				standardJcl.setGuestWinLv(CalcJclUtil.probability_G(standardJcl.getHomeWin(), standardJcl.getGuestWin()));
				standardJcl.setFanHuanLv(CalcJclUtil.fanhuan(standardJcl.getHomeWinLv(), standardJcl.getHomeWin()));
				
				if (scheduleJcl.getAvgH()!=null) {
					standardJcl.setK_h(CalcJclUtil.k_h(standardJcl.getHomeWinLv(), scheduleJcl.getAvgH()));
				}
				if (scheduleJcl.getAvgG()!=null) {
					standardJcl.setK_g(CalcJclUtil.k_g(standardJcl.getGuestWinLv(), scheduleJcl.getAvgG()));
				}
			}
		}
	}
	
	/**
	 * 转换欧赔
	 * @param collection
	 * @return
	 */
	private List<StandardJcl> convertStandardJcls(Collection<StandardJcl> collection) {
		List<StandardJcl> standardJcls = new LinkedList<StandardJcl>();
		for(StandardJcl standardJcl : collection) {
			standardJcls.add(standardJcl);
		}
		return standardJcls;
	}
	
}
