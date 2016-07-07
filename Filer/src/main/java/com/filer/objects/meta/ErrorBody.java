package com.filer.objects.meta;

public class ErrorBody {
	
	int status = 500;
	int code = 0;
	String message1 = "";
	String message2 = "";
	String developerMessage = "";
	String causedBy = "";
	String url = "";
	
	
	
	
	
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage1() {
		return message1;
	}
	public void setMessage1(String message) {
		this.message1 = message;
	}
	public String getMessage2() {
		return message2;
	}
	public void setMessage2(String message) {
		this.message2 = message;
	}
	public String getDeveloperMessage() {
		return developerMessage;
	}
	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}
	public String getCausedBy() {
		return causedBy;
	}
	public void setCausedBy(String causedBy) {
		this.causedBy = causedBy;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "ErrorBody [getStatus()=" + getStatus() + ", getCode()=" + getCode() + ", getMessage1()="
				+ getMessage1() + ", getMessage2()=" + getMessage2() + ", getDeveloperMessage()="
				+ getDeveloperMessage() + ", getCausedBy()=" + getCausedBy() + ", getUrl()=" + getUrl() + "]";
	}
	
	
	
	
	
}
