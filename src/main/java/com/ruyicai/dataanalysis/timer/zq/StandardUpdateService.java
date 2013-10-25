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
import com.ruyicai.dataanalysis.util.zq.SendJmsJczUtil;

/**
 * 足球欧赔更新
 * @author Administrator
 *
 */
@Service
public class StandardUpdateService {

	private Logger logger = LoggerFactory.getLogger(StandardUpdateService.class);
	
	@Value("${baijiaoupei}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private SendJmsJczUtil sendJmsJczUtil;
	
	@SuppressWarnings("unchecked")
	public void process() {
		try {
			long startmillis = System.currentTimeMillis();
			logger.info("足球欧赔更新开始");
			String data = httpUtil.getResponse(url+"?min=3", HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtils.isBlank(data)) {
				logger.info("足球欧赔更新时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> matches = doc.getRootElement().elements("h");
			logger.info("足球欧赔更新,size="+(matches==null ? 0 : matches.size()));
			if (matches!=null && matches.size()>0) {
				for(Element match : matches) {
					sendJmsJczUtil.sendStandardUpdateJms(match.asXML());
				}
			}
			long endmillis = System.currentTimeMillis();
			logger.info("足球欧赔更新结束,用时:"+(endmillis-startmillis));
		} catch (Exception e) {
			logger.error("足球欧赔更新时发生异常", e);
		}
	}
	
}
