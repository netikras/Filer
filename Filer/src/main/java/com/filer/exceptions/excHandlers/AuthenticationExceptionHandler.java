package com.filer.exceptions.excHandlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import com.filer.exceptions.AuthenticationException;
import com.filer.exceptions.CustomException;
import com.filer.objects.meta.ErrorBody;
import com.filer.utils.HttpStatus;

@ControllerAdvice
public class AuthenticationExceptionHandler {
	
	public AuthenticationExceptionHandler() {
		System.out.println("Loading AuthenticationExceptionHandler");
	}
	
	
	@ExceptionHandler(AuthenticationException.class)
	public @ResponseBody ErrorBody authenticationError(HttpServletResponse response, HttpServletRequest request, Exception exception){
		
		ErrorBody errorBody = null;
		
		errorBody = CustomException.digestToErrorBody(exception);
		
		request.removeAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
		
		response.setContentType("application/json");
		
		response.setStatus(errorBody.getStatus() < 100 ? HttpStatus.UNAUTHORIZED.getCode() : errorBody.getStatus());
		
		
		System.out.println("Returning errorBody: "+errorBody);
		
		return errorBody;
	}
	
}
