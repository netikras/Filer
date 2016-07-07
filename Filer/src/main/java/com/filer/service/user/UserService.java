package com.filer.service.user;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.filer.exceptions.AuthenticationException;
import com.filer.exceptions.ItemNotFoundException;
import com.filer.exceptions.UserCreationException;
import com.filer.exceptions.UserUpdateException;
import com.filer.objects.dao.user.User;



//@Service(value="UserManagementService")
public interface UserService {
	
	
	public User createUser(String name, Map<String, String[]> properties) throws UserCreationException;
	
	public User createUser(User user) throws UserCreationException;
	
	public User updateUser(String name, Map<String, String[]> properties) throws UserUpdateException, ItemNotFoundException;
	
	public User updateUser(User user) throws UserUpdateException, ItemNotFoundException;
	
	public User getUser(String name) throws ItemNotFoundException;
	
	public List<User> getUsers(Map<String, String[]> properties);
	
	public Set<String> findUsernames(String nameSubString);
	
	public boolean deleteUser(String name) throws ItemNotFoundException;
	
	boolean deleteBookmark(User user, String bookmarkName) throws ItemNotFoundException, UserUpdateException;
	
	public boolean deleteGroup(User user, String group) throws ItemNotFoundException, UserUpdateException;
	
	public boolean deleteUser(User user) throws ItemNotFoundException;
	
	public boolean login (User user, String password, int sessionTimeout) throws AuthenticationException;
	
	public boolean logout (User user);
	
	public boolean isLoggedIn (User user);
	
	/**
	 * Creates a User object filled with given information w/o accessing database
	 * @param username - user name
	 * @param password_plaintext - password in plain text
	 * @param email - user email
	 * @param groups - a Set<String> of groups the user is member of
	 * @return a User object
	 */
	public User makeUser(String username, String password_plaintext, String email, Set<String> groups);

	public User updateUserObject(User user, Map<String, String[]> properties);

	
	
}
