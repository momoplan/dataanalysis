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
	
	@Value("${lotteryUrl}")
	private String lotteryUrl;
	public String getLotteryUrl() {
		return lotteryUrl;
	}
	
	@Value("${analyzeUrl}")
	private String analyzeUrl;
	public String getAnalyzeUrl() {
		return analyzeUrl;
	}
	
}
