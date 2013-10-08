package com.ruyicai.dataanalysis.consts.lq;

public enum MatchStateJcl {

	weiKai("0", "未开"),
	
	yiJie("1", "一节"),
	
	erJie("2", "二节"),
	
	sanJie("3", "三节"),
	
	siJie("4", "四节"),
	
	wanChang("-1", "完场"),
	
	daiDing("-2", "待定"),
	
	zhongDuan("-3", "中断"),
	
	quXiao("-4", "取消"),
	
	tuiChi("-5", "推迟"),
	
	zhongChang("50", "中场");
	
	public String value;
	
	public String memo;
	
	public String value() {
		return value;
	}
	
	public String memo() {
		return memo;
	}
	
	MatchStateJcl(String value, String memo) {
		this.value = value;
		this.memo = memo;
	}
	
}
