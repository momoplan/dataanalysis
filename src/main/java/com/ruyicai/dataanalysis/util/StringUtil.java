package com.ruyicai.dataanalysis.util;

import org.apache.commons.lang.StringUtils;

public class StringUtil {

	public static boolean isEmpty(String str) {
		if (StringUtils.isEmpty(str))
			return true;
		if ("".equals(str.trim()))
			return true;
		return false;
	}

	public static boolean isEmpty(Character c) {
		if (null == c)
			return true;
		if ("".equals(c))
			return true;
		return false;
	}

	public static boolean isInt(String str) {
		return str.matches("^[0-9]*$");
	}

	// 嗖付支付传过来的金额是"分"
	public static boolean isFen(String str) {
		return str.matches("^[0-9]+$");
	}
	
	public static String join(String split, String... values) {
		StringBuilder builder = new StringBuilder();
		for(String s : values) {
			builder.append(s).append(split);
		}
		if(!isEmpty(split)) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString().trim();
	}
	
	public static String fillZero(int num, int width) {
		if (num < 0)
			return "";
		StringBuffer sb = new StringBuffer();
		String s = "" + num;
		if (s.length() < width) {
			int addNum = width - s.length();
			for (int i = 0; i < addNum; i++) {
				sb.append("0");
			}
			sb.append(s);
		} else {
			return s.substring(s.length() - width, s.length());
		}
		return sb.toString();
	}
	
	public static String fillZero(String preix, int num, int width) {
		if (num < 0)
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append(preix);
		String s = "" + num;
		if (s.length() < width) {
			int addNum = width - s.length();
			for (int i = 0; i < addNum; i++) {
				sb.append("0");
			}
			sb.append(s);
		} else {
			return s.substring(s.length() - width, s.length());
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String fillZero = fillZero(111, 3);
		System.out.println(fillZero);
		String key = StringUtil.join("_", "dataanalysis", "DetailResult", String.valueOf(996641));
		System.out.println(key);
	}
	
}