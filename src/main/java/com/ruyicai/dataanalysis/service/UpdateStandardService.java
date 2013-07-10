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
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.CommonUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.jcz.SendJmsJczUtil;

@Service
public class UpdateStandardService {
	
	private Logger logger = LoggerFactory.getLogger(UpdateStandardService.class);

	@Value("${baijiaoupei}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private SendJmsJczUtil sendJmsJczUtil;
	
	@SuppressWarnings("unchecked")
	public void process() {
		logger.info("开始更新百家欧赔");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.getResponse(url, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("更新百家欧赔时获取数据为空");
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
			logger.error("更新百家欧赔出错", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新百家欧赔结束，共用时 " + (endmillis - startmillis));
	}

}
