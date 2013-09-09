package com.ruyicai.dataanalysis.service;

import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class PeiLvDetailUpdateService {

	private Logger logger = LoggerFactory.getLogger(PeiLvDetailUpdateService.class);

	@Value("${peiLvDetail}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@SuppressWarnings("unchecked")
	public void process() {
		logger.info("足球赔率变化更新开始");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.downfile(url, HttpUtil.UTF8);
			if (StringUtil.isEmpty(data)) {
				logger.info("足球赔率变化更新时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			Element cElement = doc.getRootElement().element("c");
			List<Element> aList = cElement.elements("a");
			if (aList!=null&&aList.size()>0) {
				Element letGoalElement = aList.get(0); //亚赔（让球盘）变化数据
				processLetGoalDetail(letGoalElement);
			}
		} catch (Exception e) {
			logger.error("足球赔率变化更新发生异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("足球赔率变化更新结束，共用时 " + (endmillis - startmillis));
	}
	
	@SuppressWarnings("unchecked")
	private void processLetGoalDetail(Element element) {
		List<Element> details = element.elements("h");
		for (Element detail : details) {
			logger.info(detail.getText());
		}
	}
	
}
