package com.ruyicai.dataanalysis.exception;

import com.ruyicai.dataanalysis.consts.ErrorCode;

public class RuyicaiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private ErrorCode errorCode;

	public RuyicaiException(String msg) {
		super(msg);
	}

	public RuyicaiException(String msg, Throwable e) {
		super(msg, e);
	}

	public RuyicaiException(ErrorCode errorCode) {
		super(errorCode.memo);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
}
