package com.ruyicai.dataanalysis.util.bd;

import org.apache.commons.lang.StringUtils;

import com.ruyicai.dataanalysis.util.StringUtil;

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
			teamId = StringUtil.fillZero(Integer.valueOf(teamId), 3);
			return StringUtil.join("_", "20"+batchCode, teamId);
		}
		return "";
	}
	
	/*public static void main(String[] args) {
		String bdEvent = getBdEvent("130508", "101");
		System.out.println(bdEvent);
	}*/
	
}
