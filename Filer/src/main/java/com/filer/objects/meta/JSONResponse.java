package com.filer.objects.meta;

public class JSONResponse {
	int code;
	int errorCode; // 0=noError; 1=Error
	String message1;
	String message2;
	String redirectUrl;
	String redirectPage;
	Object content;
	
	
	public JSONResponse() {
		code=0;
		errorCode=0;
		message1="";
		message2="";
		content=null;
	}


	public int getCode() {
		return code;
	}


	public void setCode(int code) {
		this.code = code;
	}


	public int getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}


	public String getMessage1() {
		return message1;
	}


	public void setMessage1(String message1) {
		this.message1 = message1;
	}


	public String getMessage2() {
		return message2;
	}


	public void setMessage2(String message2) {
		this.message2 = message2;
	}


	public String getRedirectUrl() {
		return redirectUrl;
	}


	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}


	public String getRedirectPage() {
		return redirectPage;
	}


	public void setRedirectPage(String redirectPage) {
		this.redirectPage = redirectPage;
	}


	public Object getContent() {
		return content;
	}


	public void setContent(Object content) {
		this.content = content;
	}
	
	
	
	
	
}
