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
		StringBuilder builder = new StringBuilder();
		builder.append("lotno=" + lotNo);
		builder.append("&day=" + day);
		builder.append("&weekid=" + weekId);
		builder.append("&teamid=" + teamId);

		String url = propertiesUtil.getLotteryUrl() + "select/getjingcaimatchesWithLetpoint";
		String result = httpUtil.getResponse(url, HttpUtil.POST, HttpUtil.UTF8, builder.toString());
		logger.info("获取竞彩某场比赛的信息的返回:{},paramStr:{}", result, builder.toString());
		return result;
	}
	
	/**
	 * 查询竞彩在卖的赛事日期
	 * @param type
	 * @return
	 */
	public String getjingcaiactivedays(String type) {
		StringBuilder builder = new StringBuilder();
		builder.append("type=" + type);
		
		String url = propertiesUtil.getLotteryUrl() + "select/getjingcaiactivedays";
		String result = httpUtil.getResponse(url, HttpUtil.POST, HttpUtil.UTF8, builder.toString());
		logger.info("查询竞彩在卖的赛事日期返回:{},paramStr:{}", result, builder.toString());
		return result;
	}
	
}
