package com.filer.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.filer.objects.dao.mappers.UserBeanConf;
import com.filer.objects.dao.user.UserDAO;
import com.filer.service.auth.AuthenticationService;
import com.filer.service.auth.AuthenticationServiceImpl;
import com.filer.service.user.UserService;
import com.filer.service.user.UserServiceImpl;


public class GlobalFactory {
	
	private static UserDAO userDAO;
	private static UserService userManagementService;
	private static AuthenticationService authenticationService;
	
	
	
	public static UserDAO getUserDao_singlet() {
		if (userDAO == null) {
			ApplicationContext context = new AnnotationConfigApplicationContext(UserBeanConf.class);
			
			userDAO = (UserDAO) context.getBean("UserDAOBean"); 
		}
		
		return userDAO;
	}
	
	
	public static UserService getUserManagementService_singlet() {
		if (userManagementService == null) {
			userManagementService = UserServiceImpl.getInstance();
		}
		
		return userManagementService;
	}
	
	
	public static AuthenticationService getAuthenticationService_singlet() {
		if (authenticationService == null) {
			authenticationService = new AuthenticationServiceImpl();
		}
		
		return authenticationService;
	}
	
	
}
