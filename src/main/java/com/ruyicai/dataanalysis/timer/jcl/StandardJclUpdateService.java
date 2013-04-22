package com.ruyicai.dataanalysis.timer.jcl;

import java.util.List;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.jcl.ScheduleJcl;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * 竞彩篮球-百家欧赔更新
 * @author Administrator
 *
 */
@Service
public class StandardJclUpdateService {

	private Logger logger = LoggerFactory.getLogger(StandardJclUpdateService.class);

	@Value("${standardJclUrl}")
	private String standardJclUrl;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Produce(uri = "jms:queue:standardJclUpdate")
	private ProducerTemplate standardJclUpdateTemplate;
	
	@SuppressWarnings("unchecked")
	public void process() {
		logger.info("竞彩篮球-百家欧赔更新开始");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.getResponse(standardJclUrl, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("竞彩篮球-百家欧赔更新时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> matches = doc.getRootElement().elements("h");
			for(Element match : matches) {
				String scheduleID = match.elementTextTrim("id");
				ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJcl(Integer.parseInt(scheduleID));
				if(scheduleJcl==null||StringUtil.isEmpty(scheduleJcl.getEvent())) {
					continue;
				}
				sendJMS(match.asXML());
			}
		} catch(Exception e) {
			logger.error("竞彩篮球-百家欧赔更新异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("竞彩篮球-百家欧赔更新结束，共用时 " + (endmillis - startmillis));
	}
	
	/**
	 * 发送JMS
	 * @param body
	 */
	public void sendJMS(String body) {
		try {
			//logger.info("standardJclUpdateTemplate start");
			standardJclUpdateTemplate.sendBody(body);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
