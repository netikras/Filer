package com.filer.exceptions.excHandlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import com.filer.exceptions.CustomException;
import com.filer.exceptions.UserCreationException;
import com.filer.objects.meta.ErrorBody;

@ControllerAdvice
public class UserCreationExceptionHandler {
	
	@ExceptionHandler(UserCreationException.class)
	public @ResponseBody ErrorBody userCreationError (HttpServletResponse response, HttpServletRequest request, Exception exception) {
		
		ErrorBody errorBody = new ErrorBody();
		
		
		errorBody = CustomException.digestToErrorBody(exception);
		
		request.removeAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
		
		response.setContentType("application/json");
		response.setStatus(errorBody.getStatus() < 100 ? 500 : errorBody.getStatus());
		
		return errorBody;
		
	}
	
}
