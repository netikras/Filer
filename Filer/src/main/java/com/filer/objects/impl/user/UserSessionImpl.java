package com.filer.objects.impl.user;

import java.util.Calendar;

import com.filer.objects.dao.user.UserSession;
import com.filer.utils.RANDOM;

public class UserSessionImpl implements UserSession {
	
	
	Calendar sessionExpiresDate;
	Calendar sessionOpenedDate;
	String seed;
	
	
	public UserSessionImpl() {}
	
	public UserSessionImpl(int timeout) {
		startNewSession(timeout);
	}
	
	
	
	@Override
	public boolean isSessionValid() {
		boolean isValid = false;
		
		if (sessionExpiresDate != null && sessionExpiresDate.after(Calendar.getInstance())) {
			isValid = true;
		}
		
		return isValid;
	}
	
	@Override
	public void invalidate(){
		//sessionExpiresDate = null;
		setSessionExpiresDate(Calendar.getInstance());
		seed = "";
	}
	
	
	@Override
	public Calendar getSessionOpenedDate() {
		if (sessionOpenedDate == null) return null;
		return (Calendar) sessionOpenedDate.clone();
	}
	
	@Override
	public void setSessionOpenedDate(Calendar sessionOpenedDate) {
		this.sessionOpenedDate = sessionOpenedDate;
	}
	
	@Override
	public Calendar getSessionExpiresDate() {
		if (sessionExpiresDate == null) return null;
		return (Calendar) sessionExpiresDate.clone();
	}
	
	@Override
	public void setSessionExpiresDate(Calendar sessionExpiresDate) {
		this.sessionExpiresDate = sessionExpiresDate;
	}
	
	@Override
	public void startNewSession (int timeout_sec) {
		setSessionOpenedDate(Calendar.getInstance());
		setSessionExpiresDate(Calendar.getInstance());
		sessionExpiresDate.add(Calendar.SECOND, timeout_sec);
	}

	@Override
	public String getCurrentSeed() {
		return seed;
	}
	
	@Override
	public String renewSeed(int length) {
		
		setSeed(RANDOM.nextString(length));
		
		return getCurrentSeed();
	}
	
	@Override
	public void setSeed(String seed) {
		this.seed = seed;
	}
	
	
	
	
}
