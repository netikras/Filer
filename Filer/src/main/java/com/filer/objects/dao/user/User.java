package com.filer.objects.dao.user;

import java.util.Set;

import com.filer.exceptions.AuthenticationException;
import com.filer.objects.impl.user.Bookmark;

public interface User {
	
	
	
	public void setUsername(String username);
	
	public void setPassword(String passwordPlainText);
	
	public void setPasswordHash(String passwordHash);
	
	public void setEmail(String email);
	
	
	
	public String getUsername();
	
	public String getPassword();
	
	public String getPasswordHash();
	
	public String getEmail();
	
	public String encryptPassword(String password);
	
	
	
	public Set<Bookmark> getBookmarks();
	
	public void addBookmark(Bookmark bookmark);
	
	public void setBookmarks(Set<Bookmark> bookmarks);
	
	
	
	public Set<String> getGroups();
	
	public void addGroup(String group);
	
	public void setGroups(Set<String> groups);
	
	
	
	public boolean isLoggedIn();
	
	public boolean login(String passwordPlainText) throws AuthenticationException;
	
	public boolean login(String passwordPlainText, int session_timeout_sec) throws AuthenticationException;
	
	public void logout();
	
	
	public UserSession getSession();

	public void removeGroup(String group);
	
	
}
