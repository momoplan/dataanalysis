package com.ruyicai.dataanalysis.timer.zq;

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
import com.ruyicai.dataanalysis.domain.Standard;
import com.ruyicai.dataanalysis.domain.StandardDetail;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.ThreadPoolUtil;
import com.ruyicai.dataanalysis.util.zq.SendJmsJczUtil;

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
	private SendJmsJczUtil sendJmsJczUtil;
	
	@PostConstruct
	public void init() {
		standardUpdateExecutor = ThreadPoolUtil.createTaskExecutor("standardUpdate", 10);
	}
	
	public void process() {
		try {
			long startmillis = System.currentTimeMillis();
			logger.info("足球欧赔更新开始");
			String data = httpUtil.getResponse(url+"?min=2", HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtils.isBlank(data)) {
				logger.info("足球欧赔更新时获取数据为空");
				return;
			}
			ProcessStandardThread task = new ProcessStandardThread(data);
			standardUpdateExecutor.execute(task);
			long endmillis = System.currentTimeMillis();
			logger.info("足球欧赔更新结束,用时:"+(endmillis-startmillis));
		} catch (Exception e) {
			logger.error("足球欧赔更新时发生异常", e);
		}
	}
	
	private final class ProcessStandardThread implements Runnable {
		private String data;
		
		public ProcessStandardThread(String data) {
			this.data = data;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			try {
				Document doc = DocumentHelper.parseText(data);
				List<Element> matches = doc.getRootElement().elements("h");
				logger.info("足球欧赔更新,size="+(matches==null ? 0 : matches.size()));
				if (matches!=null && matches.size()>0) {
					for(Element match : matches) {
						doProcess(match);
					}
				}
			} catch (Exception e) {
				logger.error("足球欧赔更新-ProcessStandardThread发生异常", e);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void doProcess(Element match) {
		try {
			long startmillis = System.currentTimeMillis();
			String scheduleId = match.elementTextTrim("id");
			List<Element> odds = match.element("odds").elements("o");
			//logger.info("足球欧赔更新,scheduleId="+scheduleId+",oddsSize="+odds.size());
			boolean isModify = false; //欧赔是否发生变化
			for(Element odd : odds) {
				boolean modify = doOdd(scheduleId, odd);
				if (!isModify&&modify) {
					isModify = true;
				}
			}
			if (isModify) {
				sendJmsJczUtil.sendStandardAvgUpdateJMS(scheduleId); //更新平均欧赔
			}
			long endmillis = System.currentTimeMillis();
			logger.info("足球欧赔更新-doProcess,用时:"+(endmillis-startmillis)+",scheduleId="+scheduleId+",oddsSize="+odds.size()+
					",threadPoolSize="+standardUpdateExecutor.getQueue().size());
		} catch (Exception e) {
			logger.error("足球欧赔更新-doProcess发生异常", e);
		}
	}
	
	private boolean doOdd(String scheduleId, Element odd) {
		try {
			String o = odd.getTextTrim();
			String[] values = o.split("\\,");
			String companyId = values[0]; //博彩公司ID
			//String companyName = values[1]; //博彩公司名
			String firstHomeWin = values[2]; //初盘主胜
			String firstStandoff = values[3]; //初盘和局
			String firstGuestWin = values[4]; //初盘客胜
			String homeWin = values[5]; //主胜
			String standoff = values[6]; //和局
			String guestWin = values[7]; //客胜
			String modTime = values[8]; //变化时间
			
			//Standard standard = Standard.findStandard(Integer.parseInt(scheduleId), Integer.parseInt(companyId));
			Standard standard = Standard.findByScheduleIdCompanyId(Integer.parseInt(scheduleId), Integer.parseInt(companyId));
			if (standard==null) {
				standard = new Standard();
				standard.setScheduleID(Integer.parseInt(scheduleId));
				standard.setCompanyID(Integer.parseInt(companyId));
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
				if(standard.getHomeWin()!=null) {
					detail = new StandardDetail();
					detail.setOddsID(standard.getOddsID());
					detail.setIsEarly(0);
					detail.setHomeWin(new Double(homeWin));
					detail.setStandoff(new Double(standoff));
					detail.setGuestWin(new Double(guestWin));
					detail.setModifyTime(standard.getModifyTime());
					detail.persist();
				}
				return true;
			} else {
				if ((StringUtils.isNotBlank(homeWin) && !NumberUtil.compare(homeWin, standard.getHomeWin()))
						||(StringUtils.isNotBlank(standoff) && !NumberUtil.compare(standoff, standard.getStandoff()))
						||(StringUtils.isNotBlank(guestWin) && !NumberUtil.compare(guestWin, standard.getGuestWin()))) {
					/*logger.info("scheduleId="+scheduleId+";companyId="+companyId+";homeWin="+homeWin+";standard.getHomeWin()="+standard.getHomeWin()
							+";standoff="+standoff+";standard.getStandoff()="+standard.getStandoff()
							+";guestWin="+guestWin+";standard.getGuestWin()="+standard.getGuestWin());*/
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
			}
		} catch (Exception e) {
			logger.error("足球欧赔更新-doOdd,发生异常", e);
		}
		return false;
	}
	
	public void processAll() {
		try {
			long startmillis = System.currentTimeMillis();
			logger.info("足球欧赔-processAll更新开始");
			String data = httpUtil.getResponse(url, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtils.isBlank(data)) {
				logger.info("足球欧赔-processAl更新时获取数据为空");
				return;
			}
			ProcessStandardThread task = new ProcessStandardThread(data);
			standardUpdateExecutor.execute(task);
			long endmillis = System.currentTimeMillis();
			logger.info("足球欧赔-processAll更新结束,用时:"+(endmillis-startmillis));
		} catch (Exception e) {
			logger.error("足球欧赔-processAl更新时发生异常", e);
		}
	}
	
}
