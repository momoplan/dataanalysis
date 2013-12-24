package com.ruyicai.dataanalysis.listener.zq;

import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.dataanalysis.domain.StandardDetail;

/**
 * 足球保存欧赔变化的Jms
 * @author Administrator
 *
 */
@Service
public class StandardDetailSaveListener {
	
	private Logger logger = LoggerFactory.getLogger(StandardDetailSaveListener.class);

	public void process(@Body String body) {
		try {
			if (StringUtils.isBlank(body)) {
				logger.info("足球保存欧赔变化的Jms,body为空");
				return;
			}
			StandardDetail standardDetail = StandardDetail.fromJsonToStandardDetail(body);
			standardDetail.persist();
		} catch (Exception e) {
			logger.error("足球保存欧赔变化的Jms发生异常", e);
		}
	}
	
}
