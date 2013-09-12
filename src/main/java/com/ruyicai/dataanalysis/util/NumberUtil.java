package com.ruyicai.dataanalysis.util;

import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;

public class NumberUtil {

	public static Integer parseInt(String s, int def) {
		if(StringUtil.isEmpty(s)) {
			return def;
		}
		try {
			return Integer.parseInt(s);
		} catch(Exception e) {
		}
		return def;
	}
	
	public static String parseString(String s, String def) {
		if (StringUtils.isBlank(s)) {
			return def;
		}
		return s;
	}
	
	public static boolean compare(String nums, Double bd) {
		if(StringUtil.isEmpty(nums) && null == bd) {
			return true;
		}
		if(StringUtil.isEmpty(nums) && null != bd) {
			return false;
		}
		if(!StringUtil.isEmpty(nums) && null == bd) {
			return false;
		}
		return removeWei0(nums).equals(removeWei0(bd.toString()));
	} 
	
	public static String removeWei0(String s) {
		if(s.indexOf(".") > 0){  
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }
		return s;
	}
	
	public static BigDecimal setScale(BigDecimal bd, int scale, int mode) {
		if(null != bd) {
			bd = bd.setScale(scale, mode);
			return bd;
		}
		return bd;
	}
	
}
