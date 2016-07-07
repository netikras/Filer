package com.filer.objects.dao.user;

import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import com.filer.exceptions.AuthenticationException;
import com.filer.exceptions.ItemNotFoundException;
import com.filer.exceptions.UserUpdateException;


public interface UserDAO {
	
	
	public enum LOCK_MODE {
		L_INF    , L_NONE   ,
		L_1HOUR  , L_3HOURS , L_6HOURS,
		L_12HOURS, L_24HOURS, L_7DAYS,
	}
	
	public void setDataSource(DataSource ds);
	
	public User getUser(String username) throws ItemNotFoundException;
	
	public User create(User user);
	
	public User update(User user) throws ItemNotFoundException;
	
	public boolean delete(User user) throws ItemNotFoundException;
	
	public boolean delete(String username) throws ItemNotFoundException;
	
	public boolean deleteBookmark(User user, String bookmarkName) throws ItemNotFoundException, UserUpdateException;
	
	public boolean deleteGroup(User user, String group) throws ItemNotFoundException, UserUpdateException;
	
	public List<User> listUsers();
	
	public boolean login (User user, String password, int sessionTimeout) throws AuthenticationException;
	
	public boolean logout (User user);
	
	public boolean isLoggedIn (User user);

	public Set<String> getUsersBySubstring(String substring);

	public void setLock(User user, LOCK_MODE mode);
	
	public LOCK_MODE getLock(User user);

	
}
