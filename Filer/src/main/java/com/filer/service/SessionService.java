package com.filer.service;

import java.util.Set;

public interface SessionService {
	
	public Set<String> getAllActiveUserSessions();
	
	public void terminateUserSession(String username);
	
	
}
