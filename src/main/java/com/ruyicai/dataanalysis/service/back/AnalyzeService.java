package com.ruyicai.dataanalysis.service.back;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.PropertiesUtil;

@Service
public class AnalyzeService {

	private Logger logger = LoggerFactory.getLogger(AnalyzeService.class);
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private PropertiesUtil propertiesUtil;
	
	/**
	 * 查询投注比例
	 * @param event
	 * @return
	 */
	public String getJingcaieventbetcount(String event) {
		StringBuilder builder = new StringBuilder();
		builder.append("event=" + event);
		
		String url = propertiesUtil.getAnalyzeUrl() + "select/jingcaieventbetcount";
		String result = httpUtil.getResponse(url, HttpUtil.POST, HttpUtil.UTF8, builder.toString());
		logger.info("查询投注比例返回:{},paramStr:{}", result, builder.toString());
		return result;
	}
	
}
