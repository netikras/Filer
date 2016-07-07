package com.filer.service.auth;

import javax.servlet.http.HttpServletRequest;

import com.filer.exceptions.AuthenticationException;
import com.filer.exceptions.AuthorizationException;
import com.filer.objects.dao.file.FilesystemItem;
import com.filer.objects.dao.user.User;
import com.filer.service.auth.AuthenticationService.ActionType;

public interface AuthenticationService {
	
	public static enum ActionType {
		READ, WRITE, EXECUTE,
		MODIFY_USER, VIEW_USER,
	}
	
	
	
	public boolean isAuthenticated(User user, String token);
	
	public boolean isAuthenticated(HttpServletRequest request);
	
	public User assumeIsAuthenticated(HttpServletRequest request) throws AuthenticationException;
	
	public void authenticate(User user) throws AuthenticationException;
	
	public void deauthenticate(HttpServletRequest request);
	
	public boolean isAuthorized(User requestorUser, User user, FilesystemItem filesystemItem, ActionType action);

	User assumeIsAuthorized(User requestorUser, User targetUser, FilesystemItem targetFilesystemItem, ActionType action)
			throws AuthorizationException;
	
}
