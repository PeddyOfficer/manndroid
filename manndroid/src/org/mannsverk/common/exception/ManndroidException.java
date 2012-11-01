package org.mannsverk.common.exception;

import org.mannsverk.common.enom.ErrorEnum;

public class ManndroidException extends Throwable {
	private ErrorEnum errorEnum;
	private String msg;
	
	public ManndroidException(ErrorEnum errorEnum, String msg){
		this.errorEnum = errorEnum;
		this.msg = msg;
	}

	public ErrorEnum getErrorEnum() {
		return errorEnum;
	}

	public void setErrorEnum(ErrorEnum errorEnum) {
		this.errorEnum = errorEnum;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
