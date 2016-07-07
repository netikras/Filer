package com.filer.exceptions;


import com.filer.objects.meta.ErrorBody;
import com.filer.utils.HttpStatus;


public class CustomException extends Exception{
	
	private static final long serialVersionUID = -7777687824412011184L;
	
	
	int statusCode;
	int errorCode;
	String message1;
	String message2;
	String probableCause;
	String url;
	String developerMessage;
	
	
	
	
	public CustomException(){}
	
	public CustomException(String message){
		setMessage1(message);
	}
	
	public CustomException(int statusCode){
		setStatusCode(statusCode);
	}
	
	public CustomException(String message, int statusCode){
		setMessage1(message);
		setStatusCode(statusCode);
	}
	
	public CustomException(String message1, String message2, int statusCode, String probableCause){
		setMessage1(message1);
		setMessage2(message2);
		setStatusCode(statusCode);
		setProbableCause(probableCause);
	}
	
	public CustomException(String message1, String message2, int statusCode, String probableCause, String url, String developerMessage, int errorCode){
		setMessage1(message1);
		setMessage2(message2);
		setStatusCode(statusCode);
		setProbableCause(probableCause);
		setUrl(url);
		setDeveloperMessage(developerMessage);
		setErrorCode(errorCode);
	}
	
	
	
	
	
	public void setStatusCode(HttpStatus code) {
		this.statusCode = code.getCode();
	}
	
	public void setStatusCode(org.springframework.http.HttpStatus code) {
		this.statusCode = code.value();
	}
	
	public void setStatusCode(int code) {
		this.statusCode = code;
	}
	
	public void setMessage1(String message) {
		this.message1 = message;
	}
	
	public void setMessage2(String message) {
		this.message2 = message;
	}
	
	public void setProbableCause(String cause) {
		this.probableCause = cause;
	}
	
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	public void setDeveloperMessage(String message) {
		this.developerMessage = message;
	}
	
	public void setErrorCode(int code){
		this.errorCode = code;
	}
	
	
	
	
	
	
	
	public String getUrl() {
		return url;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getMessage1() {
		return message1;
	}

	public String getMessage2() {
		return message2;
	}

	public String getProbableCause() {
		return probableCause;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}
	
	public int getErrorCode(){
		return errorCode;
	}
	
	
	
	
	
	
	
	public static final ErrorBody digestToErrorBody(Exception exception) {
		ErrorBody errorBody = new ErrorBody();
		CustomException customException;
		
		
		if (exception == null) return null;
		
		try {
			customException = (CustomException) exception;
			
			errorBody.setCausedBy(customException.getProbableCause());
			
			errorBody.setStatus(customException.getStatusCode());
			errorBody.setMessage1(customException.getMessage1());
			errorBody.setMessage2(customException.getMessage2());
			errorBody.setDeveloperMessage(customException.getDeveloperMessage());
			errorBody.setCode(customException.getErrorCode());
			errorBody.setUrl(customException.getUrl());
			
			
		} catch (ClassCastException e) {
			System.out.println(e.getLocalizedMessage());
			
			errorBody.setMessage1(exception.getLocalizedMessage());
			
		} catch (Exception e) {
			System.out.println("Cannot digest exception to ErrorBody: "+e.getLocalizedMessage());
			errorBody.setMessage1(exception.getLocalizedMessage());
		}
		
		
		
		
		return errorBody;
	}
	
	
}
