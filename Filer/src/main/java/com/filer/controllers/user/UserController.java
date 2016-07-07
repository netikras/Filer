package com.filer.controllers.user;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.filer.exceptions.AuthenticationException;
import com.filer.exceptions.ItemNotFoundException;
import com.filer.exceptions.UserUpdateException;
import com.filer.objects.dao.user.User;
import com.filer.objects.dao.user.UserDAO;
import com.filer.objects.impl.user.Bookmark;
import com.filer.objects.meta.JSONResponse;
import com.filer.objects.meta.SystemInfo;
import com.filer.service.auth.AuthenticationService;
import com.filer.service.auth.AuthenticationService.ActionType;
import com.filer.service.user.UserService;
import com.filer.utils.GlobalFactory;


@RestController
@RequestMapping(value="/user/{user}")
public class UserController {
	
	
	
	
	
	@RequestMapping(value="/login", method=RequestMethod.POST, produces="application/json")
	@ResponseStatus(code=HttpStatus.OK)
	public SystemInfo loginUser(
			@PathVariable(value="user")                     String username,
			@RequestParam(value="password", required=true)  String password,
			HttpServletRequest request,
			HttpServletResponse response
			) throws AuthenticationException, ItemNotFoundException {
		SystemInfo retVal = null;
		
		
		UserService userService;
		User user;
		SystemInfo systemInfo;
		
		System.out.println("Requested SystemInfo");
		
		systemInfo = SystemInfo.getSystemInfo();
		
		userService = GlobalFactory.getUserManagementService_singlet();
		user = userService.getUser(username);
		
		
		
		if (userService.login(user, password, systemInfo.getSessionTimeout_s())){
			
			retVal = SystemInfo.getSystemInfo(user.getSession().getCurrentSeed());
			request.getSession().setAttribute("user", user);
			//request.getSession().setAttribute("token", retVal.getSessionSeed()); // requestor will have to pass the seed as a parameter 'token'
			System.err.println(username+" authenticated. Session seed: ["+retVal.getSessionSeed()+"]");
		} else {
			System.err.println(username+" NOT authenticated");
		}
		
		
		return retVal;
	}
	
	
	
	
	
	@RequestMapping(value="/logout", method=RequestMethod.POST, produces="application/json")
	@ResponseStatus(code=HttpStatus.OK, reason="User has been logged out")
	@ResponseBody User logoutUser(
			@PathVariable(value="user") String username,
			HttpServletRequest request
			) throws ItemNotFoundException, AuthenticationException {
		
		AuthenticationService authenticationService;
		UserService userService;
		User user;
		
		authenticationService = GlobalFactory.getAuthenticationService_singlet();
		userService = GlobalFactory.getUserManagementService_singlet();
		
		user = authenticationService.assumeIsAuthenticated(request);
		
		userService.logout(user);
		
		return user;
		
	}
	
	
	
	
	
	@RequestMapping(value="/info", method=RequestMethod.GET, produces="application/json")
	@ResponseStatus(code=HttpStatus.OK)
	@ResponseBody User getUserInfo(
			@PathVariable(value="user") String username,
			HttpServletRequest request
			) throws ItemNotFoundException, AuthenticationException {
		
		User user;
		UserService userService;
		AuthenticationService authenticationService;
		
		authenticationService = GlobalFactory.getAuthenticationService_singlet();
		authenticationService.assumeIsAuthenticated(request); // this was not enabled on purpose
		
		userService = GlobalFactory.getUserManagementService_singlet();
		user = userService.getUser(username);
		
		
		return user;
		
	}
	
	
	
	
	
	
	@RequestMapping(value="/bookmark/{name}", method=RequestMethod.PUT, produces="application/json")
	@ResponseStatus(code=HttpStatus.CREATED, reason="Bookmark has been created")
	@ResponseBody Bookmark createUserBookmarks(
			@PathVariable(value="user")                String username,
			@PathVariable(value="name")                String bookmarkName,
			@RequestParam(value="path", required=true) String bookmarkPath,
			HttpServletRequest request
			) throws ItemNotFoundException, AuthenticationException, UserUpdateException {
		
		Bookmark bookmark;
		AuthenticationService authenticationService;
		UserService userService;
		User user;
		
		AuthenticationException authenticationException;
		
		authenticationService = GlobalFactory.getAuthenticationService_singlet();
		
		user = authenticationService.assumeIsAuthenticated(request); // throws exception if not
		
		
		if (authenticationService.isAuthorized(user, user, null, ActionType.MODIFY_USER)) {
			
			userService = GlobalFactory.getUserManagementService_singlet();
			
			bookmark = new Bookmark();
			bookmark.setUsername(username);
			bookmark.setAlias(bookmarkName);
			bookmark.setPath(bookmarkPath);
			
			user.addBookmark(bookmark);
			
			userService.updateUser(user);
		} else {
			authenticationException = new AuthenticationException();
			authenticationException.setMessage1("User bookmark cannot be updated");
			authenticationException.setMessage2("User is not authorized");
			authenticationException.setProbableCause("User: "+username+"; bookmark: ["+bookmarkName+"; "+bookmarkPath+"]");
			authenticationException.setStatusCode(com.filer.utils.HttpStatus.UNAUTHORIZED);
			
			throw authenticationException;
		}
		
		
		return bookmark;
		
	}
	
	
	
	
	@RequestMapping(value="/bookmark", method=RequestMethod.GET, produces="application/json")
	@ResponseStatus(code=HttpStatus.OK)
	@ResponseBody Set<Bookmark> getUserBookmarks(
			@PathVariable(value="user") String username,
			HttpServletRequest request
			) throws ItemNotFoundException, AuthenticationException {
		
		Set<Bookmark> bookmarks;
		User user;
		AuthenticationService authenticationService;
		
		authenticationService = GlobalFactory.getAuthenticationService_singlet();
		
		user = authenticationService.assumeIsAuthenticated(request);
		
		bookmarks = user.getBookmarks();
		
		return bookmarks;
		
	}
	
	@RequestMapping(value="/bookmark/{name}", method=RequestMethod.POST, produces="application/json")
	@ResponseStatus(code=HttpStatus.OK, reason="Bookmark has been updated")
	@ResponseBody Bookmark updateUserBookmarks(
			@PathVariable(value="user") String username,
			@PathVariable(value="name") String bookmarkName,
			@RequestParam(value="path", required=true) String bookmarkPath,
			HttpServletRequest request
			) throws ItemNotFoundException, AuthenticationException, UserUpdateException{
		
		Bookmark bookmark = new Bookmark();
		
		AuthenticationService authenticationService;
		UserService userService;
		User user;
		
		userService = GlobalFactory.getUserManagementService_singlet();
		
		bookmark.setUsername(username);
		bookmark.setAlias(bookmarkName);
		bookmark.setPath(bookmarkPath);
		
		
		authenticationService = GlobalFactory.getAuthenticationService_singlet();
		user = authenticationService.assumeIsAuthenticated(request);
		
		user.addBookmark(bookmark);
		
		userService.updateUser(user);
		
		
		
		return bookmark;
		
	}
	
	
	
	@RequestMapping(value="/bookmark/{name}", method=RequestMethod.DELETE, produces="application/json")
	@ResponseStatus(code=HttpStatus.OK, reason="Bookmark has been deleted")
	@ResponseBody JSONResponse deleteUserBookmarks(
			@PathVariable(value="user") String username,
			@PathVariable(value="name") String bookmarkName,
			HttpServletRequest request
			) throws ItemNotFoundException, AuthenticationException, UserUpdateException {
		
		JSONResponse retVal = new JSONResponse();
		
		User user;
		AuthenticationService authenticationService;
		UserService userService;
		
		userService = GlobalFactory.getUserManagementService_singlet();
		
		authenticationService = GlobalFactory.getAuthenticationService_singlet();
		
		user = authenticationService.assumeIsAuthenticated(request);
		
		userService.deleteBookmark(user, bookmarkName);
		
		
		return retVal;
		
	}
	
	
	
	
	
}
