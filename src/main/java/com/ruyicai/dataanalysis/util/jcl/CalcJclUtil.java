package com.ruyicai.dataanalysis.util.jcl;

import java.math.BigDecimal;

/**
 * 竞彩篮球计算
 * @author Administrator
 *
 */
public class CalcJclUtil {

	/**
	 * 计算主胜率
	 * @param homeWin
	 * @param standoff
	 * @param guestWin
	 * @return
	 */
	public static Double probability_H(Double homeWin, Double guestWin) {
		double probability_H=1 / (1 + homeWin / guestWin) * 100;
		BigDecimal b = new BigDecimal(probability_H);
		b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
		return b.doubleValue();
	}
	
	/**
	 * 计算客胜率
	 * @param homeWin
	 * @param guestWin
	 * @return
	 */
	public static Double probability_G(Double homeWin, Double guestWin) {
		double probability_G=1 / (1 + guestWin / homeWin) * 100;
		BigDecimal b = new BigDecimal(probability_G);
		b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
		return b.doubleValue();
	}
	
	/**
	 * 计算返还率
	 * @param probability_H
	 * @param homeWin
	 * @return
	 */
	public static Double fanhuan(Double probability_H, Double homeWin) {
		double probability_T = probability_H * homeWin;
		BigDecimal b = new BigDecimal(probability_T);
		b = b.setScale(2, BigDecimal.ROUND_DOWN);
		return b.doubleValue();
	}
	
	/**
	 * 计算主队凯利指数
	 * @param homeWinLu
	 * @param avgh
	 * @return
	 */
	public static Double k_h(Double homeWinLu, Double avgh) {
		BigDecimal k_h = new BigDecimal(homeWinLu * avgh / 100);
		k_h = k_h.setScale(2, BigDecimal.ROUND_HALF_UP);
		return k_h.doubleValue();
	}
	
	/**
	 * 计算客队凯利指数
	 * @param guestWinLu
	 * @param avgg
	 * @return
	 */
	public static Double k_g(Double guestWinLu, Double avgg) {
		BigDecimal k_g = new BigDecimal(guestWinLu * avgg / 100);
		k_g = k_g.setScale(2, BigDecimal.ROUND_HALF_UP);
		return k_g.doubleValue();
	}
	
	/**
	 * 获得净得分
	 * @param gainScore
	 * @param loseScore
	 * @param matchcount
	 * @return
	 */
	public static double getScoreDifference(double gainScore, double loseScore, int matchcount) {
		if (matchcount==0) {
			return 0.0;
		}
		BigDecimal scoreDifference = (new BigDecimal(gainScore).subtract(new BigDecimal(loseScore))).divide(
				new BigDecimal(matchcount), 1, BigDecimal.ROUND_HALF_UP);
		return scoreDifference.doubleValue();
	}
	
	/**
	 * 得到格式化的分数
	 * @param score
	 * @param matchcount
	 * @return
	 */
	public static double getFormatScore(double score, int matchcount) {
		if (matchcount==0) {
			return 0.0;
		}
		BigDecimal formatScore = new BigDecimal(score).divide(
				new BigDecimal(matchcount), 1, BigDecimal.ROUND_HALF_UP);
		return formatScore.doubleValue();
	}
	
	/**
	 * 得到胜率
	 * @param winCount
	 * @param matchcount
	 * @return
	 */
	public static double getWinLv(int winCount, int matchcount) {
		if (matchcount==0) {
			return 0.0;
		}
		BigDecimal winLv = new BigDecimal(winCount).multiply(new BigDecimal(100)).divide(
				new BigDecimal(matchcount), 1, BigDecimal.ROUND_HALF_UP);
		return winLv.doubleValue();
	}
	
	/*public static void main(String[] args) {
		double winLv = getWinLv(26, 34);
		System.out.println(winLv);
	}*/
	
}
