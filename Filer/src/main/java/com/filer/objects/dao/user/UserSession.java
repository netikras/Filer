package com.filer.objects.dao.user;

import java.util.Calendar;

public interface UserSession {
	
	
	
	public boolean isSessionValid();
	
	public void invalidate();
	
	
	public Calendar getSessionOpenedDate();
	
	public void setSessionOpenedDate(Calendar sessionOpenedDate);
	
	public Calendar getSessionExpiresDate();
	
	public void setSessionExpiresDate(Calendar sessionExpiresDate);
	
	public void startNewSession (int timeout_sec);
	
	public String getCurrentSeed();
	
	public void setSeed(String seed);
	
	public String renewSeed(int length);
	
}
