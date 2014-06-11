package com.ruyicai.dataanalysis.consts;

public enum MatchState {

	WEIKAI(0, "未开"),
	
	SHANGBANCHANG(1, "上半场"),
	
	ZHONGCHANG(2, "中场"),
	
	XIABANCHANG(3, "下半场"),
	
	JIASHI(4, "加时"),
	
	DAIDING(-11, "待定"),
	
	YAOZHAN(-12, "腰斩"),
	
	ZHONGDUAN(-13, "中断"),
	
	TUICHI(-14, "推迟"),
	
	WANCHANG(-1, "完场"),
	
	QUXIAO(-10, "取消");
	
	public int value;
	
	public String memo;
	
	MatchState(int value, String memo) {
		this.value = value;
		this.memo = memo;
	}
}
