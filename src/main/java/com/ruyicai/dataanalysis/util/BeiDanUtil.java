package com.ruyicai.dataanalysis.util;

import org.apache.commons.lang.StringUtils;

/**
 * 北单公共类
 * @author Administrator
 *
 */
public class BeiDanUtil {

	public static boolean isBeiDan(String lotteryName) {
		boolean isBeiDan = false;
		if (StringUtils.equals(lotteryName, "单场让球胜平负")) {
			isBeiDan = true;
		}
		return isBeiDan;
	}
	
	public static String getBdEvent(String batchCode, String teamId) {
		if (StringUtils.isNotBlank(batchCode)&&StringUtils.isNotBlank(teamId)) {
			return StringUtil.join("_", "20"+batchCode, teamId);
		}
		return "";
	}
	
}
