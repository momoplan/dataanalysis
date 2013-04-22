package com.ruyicai.dataanalysis.consts;

public enum ErrorCode {

	OK("0", "成功"), ERROR("500", "服务器错误"), PARAMTER_ERROR("501", "参数错误"),
	
	NotHaveRecord("100001","查询结果不存在");
	
	public String value;
	
	public String memo;
	
	ErrorCode(String value, String memo) {
		this.value = value;
		this.memo = memo;
	}
	
}
