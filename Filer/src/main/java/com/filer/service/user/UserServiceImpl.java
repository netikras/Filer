package com.filer.service.user;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.filer.exceptions.AuthenticationException;
import com.filer.exceptions.ItemNotFoundException;
import com.filer.exceptions.UserCreationException;
import com.filer.exceptions.UserUpdateException;
import com.filer.objects.impl.user.UserImpl;
import com.filer.objects.dao.user.User;
import com.filer.objects.dao.user.UserDAO;
import com.filer.utils.GlobalFactory;
import com.filer.utils.HttpStatus;

public class UserServiceImpl implements UserService{

	
	private static UserService userManagementService = null;
	
	
	
	public static UserService getInstance() {
		if (userManagementService == null) {
			userManagementService = new UserServiceImpl();
		}
		return userManagementService;
	}
	
	
	
	@Override
	public User makeUser(String username, String password_plaintext, String email, Set<String> groups) {
		User user;
		
		user = new UserImpl();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password_plaintext);
		user.setGroups(groups);
		
		return user;
	}
	
	
	
	@Override
	public User createUser(String name, Map<String, String[]> properties) throws UserCreationException {
		User user;
		String[] values;
		
		user = new UserImpl();
		
		user.setUsername(name);
		
		if ((values = properties.get("email")) != null && values.length > 0) {
			user.setEmail(values[0]);
		}
		if ((values = properties.get("password")) != null && values.length > 0) {
			user.setPassword(values[0]);
		}
		
		if ((values = properties.get("groups")) != null && values.length > 0) {
			for (String groupValue : values) {
				user.addGroup(groupValue);
			}
		}
		
		
		createUser(user);
		
		
		return user;
	}

	
	@Override
	public User createUser(User user) throws UserCreationException {
		
		UserCreationException userCreationException;
		UserDAO userDAO;
		
		
		if (user != null) {
			
			try {
				getUser(user.getUsername());
				
				userCreationException = new UserCreationException();
				userCreationException.setStatusCode(HttpStatus.CONFLICT);
				userCreationException.setMessage1("Cannot create a new user");
				userCreationException.setMessage2("User with such name already exists");
				userCreationException.setProbableCause(user.getUsername());
				
				throw userCreationException;
				
			} catch (ItemNotFoundException e){
				
				try {
//					saveToDatabase(user);
					userDAO = GlobalFactory.getUserDao_singlet();
					userDAO.create(user);
				} catch (Exception e1) {
					userCreationException = new UserCreationException();
					userCreationException.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
					userCreationException.setMessage1("Cannot create a new user");
					userCreationException.setMessage2(e1.getLocalizedMessage());
					userCreationException.setProbableCause(user.getUsername());
					
					throw userCreationException;
				}
			} catch (Exception e) {
				System.err.println("Unexpected error in createUser(): "+e.getLocalizedMessage());
				
				userCreationException = new UserCreationException();
				userCreationException.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
				userCreationException.setMessage1("Cannot create a new user");
				userCreationException.setMessage2(e.getLocalizedMessage());
				userCreationException.setDeveloperMessage("Cannot query for possibly already existing user with that name. Unknown exception: "+e);
				userCreationException.setProbableCause(user.getUsername());
				
				throw userCreationException;
			}
			
			
		}
		
		return user;
	}
	
	
	
	
	
	@Override
	public User updateUser(String name, Map<String, String[]> properties) throws ItemNotFoundException, UserUpdateException {
		
		User user;
		String[] values;
		
		
		user = getUser(name);
		
		updateUserObject(user, properties);
		updateUser(user);
		
		
		return user;
	}
	
	
	@Override
	public User updateUserObject(User user, Map<String, String[]> properties) {
		
		String[] values;
		
		if (user != null && properties != null) {
			if ((values = properties.get("email")) != null && values.length > 0) {
				user.setEmail(values[0]);
			}
			if ((values = properties.get("password")) != null && values.length > 0) {
				user.setPassword(values[0]);
			}
			
			if ((values = properties.get("groups")) != null && values.length > 0) {
				if (properties.containsKey("mode") && properties.get("mode").equals("remove")) {
				
					for (String groupValue : values) {
						user.removeGroup(groupValue);
					}
				} else {
					for (String groupValue : values) {
						user.addGroup(groupValue);
					}
				}
			}
		}
		
		return user;
		
	}
	
	
	
	@Override
	public User updateUser(User user) throws UserUpdateException, ItemNotFoundException {
		
		UserUpdateException userUpdateException;
		UserDAO userDAO;
		
		if (user != null) {
			
			try {
				userDAO = GlobalFactory.getUserDao_singlet();
				userDAO.update(user);
			} catch (Exception e) {
				System.out.println("Cannot update the user: "+e.getLocalizedMessage());
				
				userUpdateException = new UserUpdateException();
				userUpdateException.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
				userUpdateException.setMessage1("Cannot update the user");
				userUpdateException.setMessage2(e.getLocalizedMessage());
				userUpdateException.setProbableCause(user.getUsername());
				
				throw userUpdateException;
			}
			
		}
		
		
		return user;
	}
	
	
	
	
	
	@Override
	public User getUser(String name) throws ItemNotFoundException {
		
		User user;
		UserDAO userDAO;
		ItemNotFoundException itemNotFoundException;
		
		//user = loadFromDatabase(name);
//		user = loadFromDatabaseManually(name);
		userDAO = GlobalFactory.getUserDao_singlet();
		user = userDAO.getUser(name);
		
		if (user != null) {
			System.out.println(user);
		} else {
			itemNotFoundException = new ItemNotFoundException();
			
			itemNotFoundException.setStatusCode(HttpStatus.NOT_FOUND);
			itemNotFoundException.setMessage1("User cannot be found");
			itemNotFoundException.setMessage2("Cannot find user in the database");
			itemNotFoundException.setProbableCause(name);
			
			throw itemNotFoundException;
		}
		
		
		return user;
	}

	
	
	
	
	
	
	
	@Override
	@Deprecated
	public List<User> getUsers(Map<String, String[]> properties) {
		
		return null;
	}

	
	
	
	
	
	@Override
	public Set<String> findUsernames(String nameSubString) {
		Set<String> usernames = null;
		
		UserDAO userDAO = GlobalFactory.getUserDao_singlet();
		
		usernames = userDAO.getUsersBySubstring(nameSubString);
		
		return usernames;
	}
	
	
	
	
	
	
	@Override
	public boolean deleteUser(String name) throws ItemNotFoundException {
		boolean deleted = false;
		
		User user;
		
		user = getUser(name);
		
		deleted = deleteUser(user);
		
		
		return deleted;
	}
	
	
	@Override
	public boolean deleteUser(User user) throws ItemNotFoundException {
		
		boolean deleted = false;
		String name;
		UserDAO userDAO;
		
		
		if (user != null) {
			
			name = user.getUsername();
			
//			deleteFromDatabase(user);
			
			userDAO = GlobalFactory.getUserDao_singlet();
			deleted = userDAO.delete(user);
			
			try {
				user = getUser(name);
				if (user != null) {
					deleted = false;
				} else {
					deleted = true;
				}
			} catch(ItemNotFoundException e) {
				deleted = true;
			} catch (Exception e) {
				System.err.println("Unexpected error in deleteUser(): "+e+" :: "+e.getLocalizedMessage());
				deleted = false;
			}
		}
			
		return deleted;
	}
	
	
	
	@Override
	public boolean deleteBookmark(User user, String bookmarkName) throws ItemNotFoundException, UserUpdateException {
		boolean bookmarkDeleted = false;
		
		UserDAO userDAO;
		
		userDAO = GlobalFactory.getUserDao_singlet();
		
		bookmarkDeleted = userDAO.deleteBookmark(user, bookmarkName);
		
		
		return bookmarkDeleted;
	}
	
	
	
	@Override
	public boolean deleteGroup(User user, String group) throws ItemNotFoundException, UserUpdateException {
		boolean groupDeleted = false;
		
		UserDAO userDAO;
		
		userDAO = GlobalFactory.getUserDao_singlet();
		
		groupDeleted = userDAO.deleteGroup(user, group);
		
		
		return groupDeleted;
	}
	
	
	
	
	
	@Override
	public boolean login(User user, String passwd, int sessionTimeout) throws AuthenticationException {
		
		AuthenticationException authenticationException;
		
		if (user != null) {
			
			user.login(passwd, sessionTimeout);
			
			//user.addGroup("root");
		} else {
			System.out.println("E: login() :: User is null");
			
			authenticationException = new AuthenticationException();
			authenticationException.setMessage1("Cannot login a user");
			authenticationException.setMessage1("User is NULL");
			authenticationException.setStatusCode(HttpStatus.UNAUTHORIZED);
			throw authenticationException;
			
			//return false;
		}
		
		return user.isLoggedIn();
	}





	@Override
	public boolean logout(User user) {
		if (user != null) {
			user.logout();
		}
		return isLoggedIn(user);
	}





	@Override
	public boolean isLoggedIn(User user) {
		boolean loggedIn = false;
		
		if (user != null) {
			loggedIn = user.isLoggedIn();
		} else {
			return false;
		}
		return loggedIn;
	}
	
	
	
	
	
	
}
