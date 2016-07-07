package com.filer.exceptions.excHandlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import com.filer.exceptions.AuthorizationException;
import com.filer.exceptions.CustomException;
import com.filer.objects.meta.ErrorBody;

@ControllerAdvice
public class AuthorizationExceptionHandler {
	
	@ExceptionHandler(AuthorizationException.class)
	public @ResponseBody ErrorBody actionAuthorizationError (HttpServletResponse response, HttpServletRequest request, Exception exception) {
		
		ErrorBody errorBody;
		
		errorBody = CustomException.digestToErrorBody(exception);
		errorBody.setUrl("/login");
		
		request.removeAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
		
		response.setContentType("application/json");
		response.setStatus(errorBody.getStatus() < 100 ? 500 : errorBody.getStatus());
		
		return errorBody;
		
	}
	
}
