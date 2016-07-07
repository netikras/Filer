package com.filer.service.auth;

import javax.servlet.http.HttpServletRequest;

import com.filer.exceptions.AuthenticationException;
import com.filer.exceptions.AuthorizationException;
import com.filer.objects.dao.file.FilesystemItem;
import com.filer.objects.dao.user.User;
import com.filer.objects.dao.user.UserSession;

public class AuthenticationServiceImpl implements AuthenticationService {

	@Override
	public boolean isAuthenticated(User user, String token) {
		boolean authenticated = false;
		
		UserSession userSession;
		String seed;
		
		if (user != null && token != null) {
			userSession = user.getSession();
			seed = userSession.getCurrentSeed();
			
			if (seed != null && !seed.isEmpty()) {
				if (seed.equals(token)) {
					if (userSession.isSessionValid()) {
						authenticated = true;
					} else {
						System.err.println("User session is not valid");
					}
				} else {
					System.err.println("Seed != token: "+seed+" != "+token);
				}
			} else {
				System.err.println("SEED has not been set for the user");
			}
		} else {
			System.err.println("USER is "+user+"; token is "+token);
		}
		
		
		return authenticated;
	}
	
	
	@Override
	public boolean isAuthenticated(HttpServletRequest request) {
		boolean authenticated = false;
		User user;
		String token;
		
		
		if (request != null) {
			
			user = (User) request.getSession().getAttribute("user");
			
			if (user != null) {
				
				token = request.getParameter("token");
				
				authenticated = isAuthenticated(user, token);
				
			} else {
				System.err.println("USER not found in the request attributes");
			}
			
		} else {
			System.err.println("REQUEST is null");
		}
		
		
		return authenticated;
	}
	
	@Override
	public User assumeIsAuthenticated(HttpServletRequest request) throws AuthenticationException {
		
		AuthenticationException authenticationException;
		User user;
		
		if (! isAuthenticated(request)) {
			authenticationException = new AuthenticationException();
			authenticationException.setMessage1("User is not authenticated");
			authenticationException.setMessage2("HTTP request is not authorized");
			authenticationException.setStatusCode(com.filer.utils.HttpStatus.UNAUTHORIZED);
			
			if (request != null && ( user = (User) request.getSession().getAttribute("user")) != null ) {
				authenticationException.setProbableCause(user.getUsername());
			} else {
				authenticationException.setDeveloperMessage("Session has no trace of authenticated user");
			}
			
			throw authenticationException;
		}
		
		user = (User) request.getSession().getAttribute("user");
		
		return user;
	}
	
	@Override
	public User assumeIsAuthorized(User requestorUser, User targetUser, FilesystemItem targetFilesystemItem, ActionType action) throws AuthorizationException {
		
		AuthorizationException authorizationException;
		User user = requestorUser;
		
		if (! isAuthorized(requestorUser, targetUser, targetFilesystemItem, action)) {
			String target = "unknown";
			
			if (targetUser != null) {
				target = "user:"+targetUser.getUsername();
			} else {
				if (targetFilesystemItem != null) {
					target = "filesystemItem:"+targetFilesystemItem.getAbsolutePath();
				}
			}
			
			authorizationException = new AuthorizationException();
			authorizationException.setMessage1("User is not authorized to perform action");
			authorizationException.setMessage2("HTTP request is not authorized");
			authorizationException.setStatusCode(com.filer.utils.HttpStatus.UNAUTHORIZED);
			
			
			authorizationException.setProbableCause(requestorUser.getUsername());
			authorizationException.setDeveloperMessage("User "+requestorUser.getUsername()+" is unauthorized to perform action "+action.toString()+" on target ["+target+"]");
			
			throw authorizationException;
		}
		
		
		return user;
	}
	
	
	
	@Override
	public void authenticate(User user) throws AuthenticationException {
		
		
	}

	
	
	
	
	
	@Override
	public void deauthenticate(HttpServletRequest request) {
		
		User user;
		
		if (request != null) {
			user = (User) request.getSession().getAttribute("user");
			
			if (user != null) {
				user.logout();
			}
			
		}
		
	}

	
	
	
	
	
	@Override
	public boolean isAuthorized(User requestorUser, User targetUser, FilesystemItem targetFilesystemItem, ActionType action) {
		boolean authorized = false;
		
		if (requestorUser != null && action != null) {
				
				if (targetFilesystemItem != null) {
					
					switch (action) {
						case READ:
							authorized = targetFilesystemItem.canRead(targetUser);
						break;
						
						case WRITE:
							authorized = targetFilesystemItem.canWrite(targetUser);
						break;
						
						case EXECUTE:
							authorized = targetFilesystemItem.canExecute(targetUser);
						break;
						
						default:
							
						break;
					}
					
				} else {
					if (targetUser != null){
						if (requestorUser.getGroups().contains("admin")){
							authorized = true;
						} else {
							if (targetUser != null && targetUser.getUsername().equals(requestorUser.getUsername())) {
								authorized = true;
							}
						}
					}
				}
			
		}
		
		return authorized;
	}

}
