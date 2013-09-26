package com.ruyicai.dataanalysis.listener;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.PostConstruct;
import org.apache.camel.Body;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.EuropeCompany;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Standard;
import com.ruyicai.dataanalysis.domain.StandardDetail;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.ThreadPoolUtil;
import com.ruyicai.dataanalysis.util.jcz.FootBallMapUtil;

/**
 * 足球欧赔更新Jms的处理
 * @author Administrator
 *
 */
@Service
public class StandarJczUpdateListener {
	
	private Logger logger = LoggerFactory.getLogger(StandarJczUpdateListener.class);
	
	private ThreadPoolExecutor standardJmsExecutor;
	
	@PostConstruct
	public void init() {
		standardJmsExecutor = ThreadPoolUtil.createTaskExecutor("standardJms", 20);
	}
	
	@Autowired
	private FootBallMapUtil footBallMapUtil;
	
	public void update(@Body String body) {
		logger.info("足球欧赔更新Jms的处理开始");
		long startmillis = System.currentTimeMillis();
		Document document = null;
		try {
			document = DocumentHelper.parseText(body);
		} catch (Exception e) {
			logger.error("足球欧赔更新Jms的处理-转Document发生异常", e);
			return;
		}
		ProcessStandardThread task = new ProcessStandardThread(document.getRootElement());
		logger.info("standardJmsExecutor,size="+standardJmsExecutor.getQueue().size());
		standardJmsExecutor.execute(task);
		long endmillis = System.currentTimeMillis();
		logger.info("足球欧赔更新Jms的处理结束，用时:"+(endmillis-startmillis));
	}
	
	private class ProcessStandardThread implements Runnable {
		private Element match;
		
		private ProcessStandardThread(Element match) {
			this.match = match;
		}
		
		@Override
		public void run() {
			doProcess(match);
		}
		
	}
	
	/**
	 * 解析数据
	 * @param match
	 */
	@SuppressWarnings("unchecked")
	private void doProcess(Element match) {
		try {
			long startmillis = System.currentTimeMillis();
			String scheduleID = match.elementTextTrim("id");
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleID));
			if(schedule==null) {
				return;
			}
			List<Element> odds = match.element("odds").elements("o");
			Double t_h = 0D;
			Double t_s = 0D;
			Double t_g = 0D;
			for(Element odd : odds) {
				String o = odd.getTextTrim();
				String[] values = o.split("\\,");
				String companyID = values[0]; //博彩公司ID
				String companyName = values[1]; //博彩公司名
				String firstHomeWin = values[2]; //初盘主胜
				String firstStandoff = values[3]; //初盘和局
				String firstGuestWin = values[4]; //初盘客胜
				String homeWin = values[5]; //主胜
				String standoff = values[6]; //和局
				String guestWin = values[7]; //客胜
				String modTime = values[8]; //变化时间
				if(!StringUtil.isEmpty(homeWin) && !StringUtil.isEmpty(standoff) && !StringUtil.isEmpty(guestWin)) {
					t_h = t_h + new Double(homeWin);
					t_s = t_s + new Double(standoff);
					t_g = t_g + new Double(guestWin);
				} else {
					t_h = t_h + new Double(firstHomeWin);
					t_s = t_s + new Double(firstStandoff);
					t_g = t_g + new Double(firstGuestWin);
				}
				Boolean cHasExist = footBallMapUtil.europeCompanyMap.get(companyID);
				if (cHasExist==null||!cHasExist) {
					EuropeCompany company = EuropeCompany.findEuropeCompany(Integer.parseInt(companyID));
					cHasExist = company==null ? false : true;
					footBallMapUtil.europeCompanyMap.put(companyID, cHasExist);
				}
				if(!cHasExist) {
					EuropeCompany company = new EuropeCompany();
					company.setCompanyID(Integer.parseInt(companyID));
					company.setName_Cn(companyName);
					company.setName_E(companyName);
					company.setIsPrimary(0);
					company.setIsExchange(0);
					company.persist();
				}
				String skey = StringUtil.join("_", scheduleID, companyID);
				Boolean sHasExist = footBallMapUtil.standardMap.get(skey);
				if (sHasExist==null||!sHasExist) {
					Standard standard = Standard.findStandard(Integer.parseInt(scheduleID), Integer.parseInt(companyID));
					sHasExist = standard==null ? false : true;
					footBallMapUtil.standardMap.put(skey, sHasExist);
				}
				if(!sHasExist) {
					Standard standard = new Standard();
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
			long endmillis = System.currentTimeMillis();
			logger.info("StandarJczUpdateListener-doProcess，共用时 " + (endmillis - startmillis)+",scheduleId="+scheduleID+",size="+odds.size());
		} catch(Exception e) {
			logger.error("足球欧赔更新Jms的处理-解析数据发生异常", e);
		}
	}
	
}
