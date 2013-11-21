package com.ruyicai.dataanalysis.util.zq;

import java.math.BigDecimal;

public class CalcUtil {

	public static Double probability_H(Double homeWin, Double standoff, Double guestWin) {
		double probability_H=1 / (1 + homeWin / standoff + homeWin / guestWin) * 100;
		BigDecimal b = new BigDecimal(probability_H);
		b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
		return b.doubleValue();
	}
	
	public static Double probability_S(Double homeWin, Double standoff, Double guestWin) {
		double probability_S=1 / (1 + standoff / homeWin + standoff / guestWin) * 100;
		BigDecimal b = new BigDecimal(probability_S);
		b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
		return b.doubleValue();
	}
	
	public static Double probability_G(Double homeWin, Double standoff, Double guestWin) {
		double probability_G=1 / (1 + guestWin / homeWin + guestWin / standoff) * 100;
		BigDecimal b = new BigDecimal(probability_G);
		b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
		return b.doubleValue();
	}
	
	public static Double fanhuan(Double probability_H, Double homeWin) {
		double probability_T = probability_H * homeWin;
		BigDecimal b = new BigDecimal(probability_T);
		//b = b.setScale(2, BigDecimal.ROUND_DOWN);
		b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
		return b.doubleValue();
	}
	
	public static Double k_h(Double homeWinLu, Double avgh) {
		BigDecimal k_h = new BigDecimal(homeWinLu * avgh / 100);
		k_h = k_h.setScale(2, BigDecimal.ROUND_HALF_UP);
		return k_h.doubleValue();
	}
	
	public static Double k_s(Double standoffLu, Double avgs) {
		BigDecimal k_s = new BigDecimal(standoffLu* avgs / 100);
		k_s = k_s.setScale(2, BigDecimal.ROUND_HALF_UP);
		return k_s.doubleValue();
	}
	
	public static Double k_g(Double guestWinLu, Double avgg) {
		BigDecimal k_g = new BigDecimal(guestWinLu * avgg / 100);
		k_g = k_g.setScale(2, BigDecimal.ROUND_HALF_UP);
		return k_g.doubleValue();
	}
	
	static String[] goalCn = { "0", "0/0.5", "0.5", "0.5/1", "1", "1/1.5", "1.5", "1.5/2", "2", "2/2.5", "2.5", "2.5/3", "3", "3/3.5", "3.5", "3.5/4", "4", "4/4.5", "4.5", "4.5/5", "5", "5/5.5", "5.5", "5.5/6", "6", "6/6.5", "6.5", "6.5/7", "7", "7/7.5", "7.5", "7.5/8", "8", "8/8.5", "8.5", "8.5/9", "9", "9/9.5", "9.5", "9.5/10", "10", "10/10.5", "10.5", "10.5/11", "11", "11/11.5", "11.5", "11.5/12", "12", "12/12.5", "12.5", "12.5/13", "13", "13/13.5", "13.5", "13.5/14", "14" };
	static String[] goalCn2 = { "0", "0/-0.5", "-0.5", "-0.5/-1", "-1", "-1/-1.5", "-1.5", "-1.5/-2", "-2", "-2/-2.5", "-2.5", "-2.5/-3", "-3", "-3/-3.5", "-3.5", "-3.5/-4", "-4", "-4/-4.5", "-4.5", "-4.5/-5", "-5", "-5/-5.5", "-5.5", "-5.5/-6", "-6", "-6/-6.5", "-6.5", "-6.5/-7", "-7", "-7/-7.5", "-7.5", "-7.5/-8", "-8", "-8/-8.5", "-8.5", "-8.5/-9", "-9", "-9/-9.5", "-9.5", "-9.5/-10", "-10" };
	static String[] goalCn3 = { "平手", "平/半", "半球", "半/一", "一球", "一/球半", "球半", "球半/两", "两球", "两/两球半", "两球半", "两球半/三", "三球", "三/三球半", "三球半", "三球半/四球", "四球", "四球/四球半", "四球半", "四球半/五球", "五球", "五/五球半", "五球半", "五球半/六", "六球", "六/六球半", "六球半", "六球半/七", "七球", "七/七球半", "七球半", "七球半/八", "八球", "八/八球半", "八球半", "八球半/九", "九球", "九/九球半", "九球半", "九球半/十", "十球" };

	public static String handicap(Double goal) { // 数字盘口转汉汉字
		try {
			if (null == goal) {
				return "";
			} else {
				if (goal >= 0) {
					return goalCn3[new Double(goal * 4).intValue()];
				} else {
					return "受"
							+ goalCn3[new Double(Math.abs(goal) * 4).intValue()];
				}
			}
		} catch (Exception e) {
			return goal + "";
		}
	}

	public static String overUnder(Double goal) {
		try {
			if (null == goal) {
				return "";
			} else {
				if (goal >= 0) {
					return goalCn[new Double(goal * 4).intValue()];
				} else {
					return goalCn2[new Double(Math.abs(goal * 4)).intValue()];
				}
			}
		} catch (Exception e) {
			return goal + "";
		}
	}
}
