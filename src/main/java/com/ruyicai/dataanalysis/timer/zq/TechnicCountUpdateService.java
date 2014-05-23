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
					String id = match.elementTextTrim("ID");
					String technicCount = match.elementTextTrim("TechnicCount");
					logger.info("id:"+id+"#technicCount:"+technicCount);
				}
			}
			long endmillis = System.currentTimeMillis();
			logger.info("更新比赛的技术统计结束,用时 " + (endmillis-startmillis));
		} catch(Exception e) {
			logger.error("更新比赛的技术统计发生异常", e);
		}
	}
	
}
