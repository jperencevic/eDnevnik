package com.itprobuka.school_registar.controllers.util;

public class RestError {

	private Integer code;
	private String message;

	public RestError(Integer code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
