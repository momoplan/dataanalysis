package com.ruyicai.dataanalysis.timer.zq;

import java.math.BigDecimal;
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
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Standard;
import com.ruyicai.dataanalysis.domain.StandardDetail;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.ThreadPoolUtil;
import com.ruyicai.dataanalysis.util.zq.FootBallMapUtil;

/**
 * 足球欧赔更新
 * @author Administrator
 *
 */
@Service
public class StandardUpdateService {

	private Logger logger = LoggerFactory.getLogger(StandardUpdateService.class);
	
	private ThreadPoolExecutor standardUpdateExecutor;
	
	@Value("${baijiaoupei}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private FootBallMapUtil footBallMapUtil;
	
	@PostConstruct
	public void init() {
		standardUpdateExecutor = ThreadPoolUtil.createTaskExecutor("standardUpdate", 20);
	}
	
	@SuppressWarnings("unchecked")
	public void process() {
		logger.info("足球欧赔更新开始");
		long startmillis = System.currentTimeMillis();
		try {
			//String data = httpUtil.getResponse(url+"?day=2", HttpUtil.GET, HttpUtil.UTF8, "");
			String data = httpUtil.getResponse(url, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtils.isBlank(data)) {
				logger.info("足球欧赔更新时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> matches = doc.getRootElement().elements("h");
			logger.info("足球欧赔,size="+matches.size());
			for(Element match : matches) {
				//此处不判断Schedule是否存在,防止错过获取欧赔初盘的变化时间,因为赛事的定时任务5小时一次,欧赔是5分钟
				ProcessStandardThread task = new ProcessStandardThread(match);
				standardUpdateExecutor.execute(task);
			}
		} catch(Exception e) {
			logger.error("足球欧赔更新发生异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("足球欧赔更新结束，共用时 " + (endmillis - startmillis));
	}
	
	private class ProcessStandardThread implements Runnable {
		
		private Element match;
		
		private ProcessStandardThread(Element match) {
			this.match = match;
		}
		
		@Override
		public void run() {
			doStandard(match);
		}
		
	}
	
	/**
	 * 解析数据
	 * @param match
	 */
	@SuppressWarnings("unchecked")
	private void doStandard(Element match) {
		try {
			long startmillis = System.currentTimeMillis();
			String scheduleID = match.elementTextTrim("id");
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleID));
			//此处不判断Schedule是否存在,防止错过获取欧赔初盘的变化时间,因为赛事的定时任务5小时一次,欧赔是5分钟
			/*if(schedule==null) {
				return;
			}*/
			List<Element> odds = match.element("odds").elements("o");
			Double t_h = 0D;
			Double t_s = 0D;
			Double t_g = 0D;
			int validSize = 0;
			for(Element odd : odds) {
				String o = odd.getTextTrim();
				String[] values = o.split("\\,");
				String companyID = values[0]; //博彩公司ID
				//String companyName = values[1]; //博彩公司名
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
				Boolean isValid = footBallMapUtil.europeCompanyMap.get(companyID);
				if (isValid==null) {
					EuropeCompany company = EuropeCompany.findEuropeCompany(Integer.parseInt(companyID));
					if (company!=null) {
						String name_Cn = company.getName_Cn();
						Integer isPrimary = company.getIsPrimary();
						//只保存接口需要的公司欧赔
						if (StringUtils.isNotBlank(name_Cn)&&isPrimary!=null&&isPrimary==1) {
							isValid = true;
						} else {
							isValid = false;
						}
					} else {
						isValid = false;
					}
					footBallMapUtil.europeCompanyMap.put(companyID, isValid);
				}
				if (!isValid) {
					continue;
				}
				validSize++;
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
			//更新赛事的平均赔率
			updateScheduleAvg(schedule, t_h, t_s, t_g, odds.size());
			long endmillis = System.currentTimeMillis();
			logger.info("足球欧赔更新-doStandard，共用时 "+(endmillis-startmillis)+",scheduleId="+scheduleID+",size="+odds.size()
					+"validSize="+validSize+",threadPoolSize="+standardUpdateExecutor.getQueue().size());
		} catch(Exception e) {
			logger.error("足球欧赔更新Jms的处理-解析数据发生异常", e);
		}
	}
	
	private void updateScheduleAvg(Schedule schedule, Double t_h, Double t_s, Double t_g, int oddsSize) {
		if(schedule!=null && oddsSize>0) {
			boolean isModify = false;
			BigDecimal b = new BigDecimal(t_h / oddsSize);
			b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
			Double avgH = schedule.getAvgH();
			if (!NumberUtil.compare(b.toString(), avgH)) {
				isModify = true;
				schedule.setAvgH(b.doubleValue());
			}
			b = new BigDecimal(t_s / oddsSize);
			b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
			Double avgS = schedule.getAvgS();
			if (!NumberUtil.compare(b.toString(), avgS)) {
				isModify = true;
				schedule.setAvgS(b.doubleValue());
			}
			b = new BigDecimal(t_g / oddsSize);
			b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
			Double avgG = schedule.getAvgG();
			if (!NumberUtil.compare(b.toString(), avgG)) {
				isModify = true;
				schedule.setAvgG(b.doubleValue());
			}
			if (isModify) {
				schedule.merge();
			}
		}
	}
	
}
