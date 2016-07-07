package com.filer.exceptions;

public class AuthorizationException extends CustomException{
	
	
	public AuthorizationException(){
		super();
	}
	
	public AuthorizationException(String message){
		super(message);
	}
	
}
