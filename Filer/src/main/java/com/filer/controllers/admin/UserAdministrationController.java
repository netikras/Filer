package com.filer.controllers.admin;


import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.filer.exceptions.AuthenticationException;
import com.filer.exceptions.AuthorizationException;
import com.filer.exceptions.ItemNotFoundException;
import com.filer.exceptions.UserCreationException;
import com.filer.exceptions.UserUpdateException;
import com.filer.objects.dao.user.User;
import com.filer.service.auth.AuthenticationService;
import com.filer.service.auth.AuthenticationService.ActionType;
import com.filer.service.user.UserService;
import com.filer.utils.GlobalFactory;


@RestController
@RequestMapping(value="/admin/{adminName}/user/{username}")
public class UserAdministrationController {
	
	
	private AuthenticationException adminNameMismatchException = new AuthenticationException();
	
	
	
	public UserAdministrationController() {
		// TODO Auto-generated constructor stub
		adminNameMismatchException.setMessage1("Admin action not authorized");
		adminNameMismatchException.setMessage2("Admin name mismatch");
		adminNameMismatchException.setStatusCode(com.filer.utils.HttpStatus.UNAUTHORIZED);
	}
	
	
	
	@ResponseStatus(code=HttpStatus.OK)
	@RequestMapping(value="/find", method=RequestMethod.GET, produces="application/json")
	public Set<String> findUsernames(
			@PathVariable(value="username")  String usernameSubstr,
			@PathVariable(value="adminName") String adminName,
			HttpServletRequest  request
			) throws AuthorizationException, AuthenticationException, ItemNotFoundException {
		
		
		Set<String> retVal = null;
		Set<String> users;
		User adminUser;
		User user;
		UserService userService;
		AuthenticationService authenticationService;
		
		userService = GlobalFactory.getUserManagementService_singlet();
		users = userService.findUsernames(usernameSubstr);
		
		authenticationService = GlobalFactory.getAuthenticationService_singlet();
		adminUser = authenticationService.assumeIsAuthenticated(request);
		
		if (users.size() > 0) {
			user = userService.getUser(users.iterator().next().toString()); // take a first element from the list
			
			adminUser = authenticationService.assumeIsAuthorized(adminUser, user, null, ActionType.VIEW_USER);
			
			if (adminUser.getUsername().equals(adminName)) {
				retVal = users;
			}
		} else {
			retVal = new HashSet<String>();
		}
		
		return retVal;
	}
	
	
	
	@ResponseStatus(code=HttpStatus.OK)
	@RequestMapping(method=RequestMethod.GET, produces="application/json")
	public User getUserDetails(
			@PathVariable(value="username")  String username,
			@PathVariable(value="adminName") String adminName,
			HttpServletRequest  request
			) throws AuthorizationException, AuthenticationException, ItemNotFoundException {
		
		
		User retVal = null;
		User user;
		User adminUser;
		UserService userService;
		AuthenticationService authenticationService;
		
		userService = GlobalFactory.getUserManagementService_singlet();
		user = userService.getUser(username);
		
		authenticationService = GlobalFactory.getAuthenticationService_singlet();
		adminUser = authenticationService.assumeIsAuthenticated(request);
		adminUser = authenticationService.assumeIsAuthorized(adminUser, user, null, ActionType.VIEW_USER);
		
		if (adminUser.getUsername().equals(adminName)) {
			retVal = user;
		} else {
			throw adminNameMismatchException;
		}
		
		return retVal;
	}
	
	
	@ResponseStatus(code=HttpStatus.CREATED)
	@RequestMapping(method=RequestMethod.PUT, produces="application/json")
	public User createUser(
			@PathVariable(value="username" )               String username,
			@PathVariable(value="adminName")               String adminName,
			@RequestParam(name="password", required=true ) String password,
			@RequestParam(name="email"   , required=true ) String email,
			@RequestParam(name="groups"  , required=false) Set<String> groups,
			HttpServletRequest  request
			) throws UserCreationException, AuthorizationException, AuthenticationException {
		
		
		User retVal = null;
		User user;
		User adminUser;
		UserService userService;
		
		AuthenticationService authenticationService;
		
		userService = GlobalFactory.getUserManagementService_singlet();
		authenticationService = GlobalFactory.getAuthenticationService_singlet();
		
		user = userService.makeUser(username, password, email, groups);
		
		adminUser = authenticationService.assumeIsAuthenticated(request);
		adminUser = authenticationService.assumeIsAuthorized(adminUser, user, null, ActionType.MODIFY_USER);
		
		if (adminUser.getUsername().equals(adminName)) {
			userService.createUser(user);
			retVal = user;
		} else {
			throw adminNameMismatchException;
		}
		
		return retVal;
	}
	
	
	@ResponseStatus(code=HttpStatus.OK)
	@RequestMapping(method=RequestMethod.DELETE, produces="application/json")
	public User deleteUser(
			@PathVariable(value="username" ) String username,
			@PathVariable(value="adminName") String adminName,
			HttpServletRequest  request
			) throws AuthorizationException, AuthenticationException, ItemNotFoundException {
		
		
		User retVal = null;
		User user;
		User adminUser;
		UserService userService;
		
		AuthenticationService authenticationService;
		
		userService = GlobalFactory.getUserManagementService_singlet();
		authenticationService = GlobalFactory.getAuthenticationService_singlet();
		
		user = userService.getUser(username);
		
		adminUser = authenticationService.assumeIsAuthenticated(request);
		adminUser = authenticationService.assumeIsAuthorized(adminUser, user, null, ActionType.MODIFY_USER);
		
		if (adminUser.getUsername().equals(adminName)) {
			userService.deleteUser(user);
			retVal = user;
		} else {
			throw adminNameMismatchException;
		}
		
		return retVal;
	}
	
	
	@ResponseStatus(code=HttpStatus.OK)
	@RequestMapping(method=RequestMethod.POST, produces="application/json")
	public User updateUser(
			@PathVariable(value="username" )               String username,
			@PathVariable(value="adminName")               String adminName,
			@RequestParam(name="password", required=false) String password,
			@RequestParam(name="email"   , required=false) String email,
			@RequestParam(name="groups"  , required=false) Set<String> groups,
			
			@RequestParam(name="mode"    , required=false) String mode, // optional. Possible values: "add" and "remove"
			HttpServletRequest  request
			) throws AuthorizationException, AuthenticationException, ItemNotFoundException, UserUpdateException {
		
		
		User retVal = null;
		User user;
		User adminUser;
		UserService userService;
		
		AuthenticationService authenticationService;
		
		userService = GlobalFactory.getUserManagementService_singlet();
		authenticationService = GlobalFactory.getAuthenticationService_singlet();
		
		user = userService.getUser(username);
		
		adminUser = authenticationService.assumeIsAuthenticated(request);
		adminUser = authenticationService.assumeIsAuthorized(adminUser, user, null, ActionType.MODIFY_USER);
		
		if (adminUser.getUsername().equals(adminName)) {
			
			userService.updateUserObject(user, request.getParameterMap());
			userService.updateUser(user);
			retVal = user;
		} else {
			throw adminNameMismatchException;
		}
		
		return retVal;
	}
	
	
	
	
	
}
