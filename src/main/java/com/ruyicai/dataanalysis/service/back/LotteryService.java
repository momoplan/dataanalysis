package com.ruyicai.dataanalysis.service.back;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.PropertiesUtil;

@Service
public class LotteryService {

	private Logger logger = LoggerFactory.getLogger(LotteryService.class);
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private PropertiesUtil propertiesUtil;
	
	/**
	 * 获取竞彩某场比赛的信息
	 * 
	 * @param lotno
	 * @param day
	 * @param weekid
	 * @param teamid
	 * @return
	 */
	public String getJingcaimatches(String lotNo, String day, String weekId, String teamId) {
		StringBuffer paramStr = new StringBuffer();
		paramStr.append("lotno=" + lotNo);
		paramStr.append("&day=" + day);
		paramStr.append("&weekid=" + weekId);
		paramStr.append("&teamid=" + teamId);

		String url = propertiesUtil.getLotteryUrl() + "select/getjingcaimatchesWithLetpoint";
		String result = httpUtil.getResponse(url, HttpUtil.POST, HttpUtil.UTF8, paramStr.toString());
		logger.info("获取竞彩某场比赛的信息的返回:{},paramStr:{}", result, paramStr.toString());
		return result;
	}
	
}
