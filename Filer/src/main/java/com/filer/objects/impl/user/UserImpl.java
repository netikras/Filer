package com.filer.objects.impl.user;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/*
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;*/






import com.filer.exceptions.AuthenticationException;
import com.filer.objects.dao.user.User;
import com.filer.objects.dao.user.UserSession;
import com.filer.utils.HttpStatus;


public class UserImpl implements User {
	
	
	
	UserSession userSession;
	
	
	
	private String username;
	private String passwordHash;
	private String password;
	private String email;
	
	
	Set<Bookmark> bookmarks;
	
	Set<String> groups;

	
	public UserImpl(){
		setGroups(new HashSet<String>());
		setBookmarks(new HashSet<Bookmark>());
		setUsername("");
		setPassword("");
		setEmail("");
	}
	
	
	@Override
	public boolean isLoggedIn() {
		
		boolean loggedIn = false;
		
		if (getSession() != null) {
			
			if (getSession().isSessionValid()) {
				loggedIn = true;
			}
		}
		
		return loggedIn;
	}
	
	
	
	@Override
	public void setUsername(String username) {
		this.username = username;
	}



	@Override
	public void setPassword(String passwordPlainText) {
		setPasswordHash(encryptPassword(passwordPlainText));
	}



	@Override
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
		
	}



	@Override
	public void setEmail(String email) {
		this.email = email;
		
	}



	@Override
	public String getUsername() {
		return this.username;
	}



	@Override
	public String getPassword() {
		return this.password;
	}



	@Override
	public String getPasswordHash() {
		return this.passwordHash;
	}



	@Override
	public String getEmail() {
		return this.email;
	}



	@Override
	public String encryptPassword(String pw) {
		String retVal = "";
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			md.reset();
			byte[] buffer = pw.getBytes("UTF-8");
			md.update(buffer);
			byte[] digest = md.digest();

			String hexStr = "";
			for (int i = 0; i < digest.length; i++) {
				hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
			}
			
			retVal = hexStr;
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		System.err.println("HASH='"+retVal+"'");
		return retVal;
	}



	@Override
	public Set<Bookmark> getBookmarks() {
		return this.bookmarks;
	}



	@Override
	public void addBookmark(Bookmark bookmark) {
		if (getBookmarks() == null) {
//			System.out.println("getBookmarks() returned null. Recreating user bookmarks");
			setBookmarks(new HashSet<Bookmark>());
		}
		getBookmarks().add(bookmark);
		for(Bookmark b: getBookmarks()) {
			System.out.println("Bookmark: "+b.getAlias()+" :: "+b.getPath());
		}
	}



	@Override
	public void setBookmarks(Set<Bookmark> bookmarks) {
		if (bookmarks == null) bookmarks = new HashSet<Bookmark>();
		this.bookmarks = bookmarks;
	}



	@Override
	public Set<String> getGroups() {
		return this.groups;
	}



	@Override
	public void addGroup(String group) {
		if (getGroups() == null) setGroups(new HashSet<String>());
		getGroups().add(group);
	}
	
	
	@Override
	public void removeGroup(String group) {
		if (getGroups() == null) {
			setGroups(new HashSet<String>());
		} else {
			for (String groupname: getGroups()) {
				if (groupname.equals(group)) {
					getGroups().remove(groupname);
					break;
				}
			}
		}
	}



	@Override
	public void setGroups(Set<String> groups) {
		if (groups == null) groups = new HashSet<String>();
		this.groups = groups;
		
	}



	@Override
	public boolean login(String passwordPlainText) throws AuthenticationException {
		AuthenticationException authenticationException;
		
		if (passwordPlainText != null) {
			if (encryptPassword(passwordPlainText).equals(getPasswordHash())) {
				this.userSession = new UserSessionImpl();
				
				this.userSession.startNewSession(300); // 5 minutes session timeout
				this.userSession.renewSeed(128); // 128 chars for session seed
			} else {
				System.out.println("Password is incorrect: "+getPasswordHash()+" != "+encryptPassword(passwordPlainText));
				
				authenticationException = new AuthenticationException();
				authenticationException.setMessage1("Cannot login user");
				authenticationException.setMessage2("Password is incorrect");
				authenticationException.setProbableCause(getUsername());
				authenticationException.setStatusCode(HttpStatus.UNAUTHORIZED);
				throw authenticationException;
			}
		} else {
			authenticationException = new AuthenticationException();
			authenticationException.setMessage1("Cannot login user");
			authenticationException.setMessage2("Password is NULL");
			authenticationException.setProbableCause(getUsername());
			authenticationException.setStatusCode(HttpStatus.UNAUTHORIZED);
			throw authenticationException;
		}
		
		return isLoggedIn();
	}
	
	
	@Override
	public boolean login(String passwordPlainText, int session_timeout_sec) throws AuthenticationException {
		
		if (login(passwordPlainText)) {
			this.userSession.startNewSession(session_timeout_sec); 
		}
		
		return isLoggedIn();
	}


	@Override
	public void logout() {
		if (getSession() != null) {
			getSession().invalidate();
		}
		
	}



	@Override
	public UserSession getSession() {
		return this.userSession;
	}
	
	
	
	
	public String toString() {
		return "USER["+getUsername()+";"+getEmail()+";"+getPasswordHash()+"]";
	}
	
	
	
	
}
