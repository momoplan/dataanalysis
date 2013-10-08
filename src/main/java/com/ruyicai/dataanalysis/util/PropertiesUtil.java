package com.ruyicai.dataanalysis.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PropertiesUtil {

	@Value("${dataanalysisIp}")
	private String dataanalysisIp;
	public String getDataanalysisIp() {
		return dataanalysisIp;
	}
	
}
