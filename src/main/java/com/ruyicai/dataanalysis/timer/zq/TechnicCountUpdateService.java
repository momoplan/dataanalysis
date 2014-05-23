package com.ruyicai.dataanalysis.timer.zq;

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
import com.ruyicai.dataanalysis.domain.TechnicCount;
import com.ruyicai.dataanalysis.util.HttpUtil;

@Service
public class TechnicCountUpdateService {

	private Logger logger = LoggerFactory.getLogger(TechnicCountUpdateService.class);
	
	@Value("${technicCountUrl}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil; 
	
	@SuppressWarnings("unchecked")
	public void process() {
		try {
			logger.info("更新比赛的技术统计开始");
			long startmillis = System.currentTimeMillis();
			String result = httpUtil.getResponse(url, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtils.isBlank(result)) {
				logger.info("更新比赛的技术统计时获取数据为空");
				return;
			}
			Document document = DocumentHelper.parseText(result);
			List<Element> matchs = document.getRootElement().elements("match");
			if (matchs!=null&&matchs.size()>0) {
				for (Element match : matchs) {
					processMatchElement(match);
				}
			}
			long endmillis = System.currentTimeMillis();
			logger.info("更新比赛的技术统计结束,用时 " + (endmillis-startmillis));
		} catch(Exception e) {
			logger.error("更新比赛的技术统计发生异常", e);
		}
	}
	
	private void processMatchElement(Element match) {
		try {
			String id = match.elementTextTrim("id");
			if (StringUtils.isBlank(id)) {
				return;
			}
			String technicCountText = match.elementTextTrim("TechnicCount");
			//logger.info("id:"+id+"#technicCount:"+technicCount);
			if (StringUtils.isBlank(technicCountText)) {
				return;
			}
			boolean isSave = false;
			TechnicCount technicCount = new TechnicCount();
			technicCount.setScheduleId(Integer.parseInt(id));
			String[] separator = StringUtils.splitByWholeSeparator(technicCountText, ";");
			for (String string : separator) {
				String type = StringUtils.substringBefore(string, ",");
				String value = StringUtils.substringAfter(string, ",");
				if (StringUtils.isBlank(type)||StringUtils.isBlank(value)) {
					continue;
				}
				if (StringUtils.equals(type, "14")) { //控球时间
					isSave = true;
					technicCount.setTrapTime(value);
				}
				if (StringUtils.equals(type, "3")) { //射门次数
					isSave = true;
					technicCount.setShootCount(value);
				}
				if (StringUtils.equals(type, "4")) { //射中次数
					isSave = true;
					technicCount.setHitCount(value);
				}
				if (StringUtils.equals(type, "9")) { //越位次数
					isSave = true;
					technicCount.setOffsideCount(value);
				}
				if (StringUtils.equals(type, "6")) { //角球次数
					isSave = true;
					technicCount.setCornerkickCount(value);
				}
				if (StringUtils.equals(type, "5")) { //犯规次数
					isSave = true;
					technicCount.setFoulCount(value);
				}
				if (StringUtils.equals(type, "11")) { //黄牌数
					isSave = true;
					technicCount.setYellowcardCount(value);
				}
				if (StringUtils.equals(type, "13")) { //红牌数
					isSave = true;
					technicCount.setRedcardCount(value);
				}
			}
			if (isSave) {
				technicCount.persist();
			}
		} catch (Exception e) {
			logger.error("更新比赛的技术统计-processMatchElement发生异常", e);
		}
	}
	
}
