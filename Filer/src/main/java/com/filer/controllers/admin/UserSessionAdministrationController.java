package com.filer.controllers.admin;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value="/admin/{adminName}/session")
public class UserSessionAdministrationController {
	
	@RequestMapping(method=RequestMethod.GET, produces="application/json")
	public Set<String> getActiveSessions(
			HttpServletRequest request
			) {
		Set<String> userSessions = new HashSet<String>();
		
		
		
		
		
		return userSessions;
	}
	
	
}
