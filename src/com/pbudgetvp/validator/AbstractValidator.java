package com.monyrama.validator;

abstract class AbstractValidator implements Validator {
	private String message;

	@Override
	public String getMessage() {
		return message;
	}
	
	void setMessage(String message) {
		this.message = message;
	}
}
