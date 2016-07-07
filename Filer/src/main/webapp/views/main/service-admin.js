(function() {
	'use strict'
	
	var adminServiceFn = function($http, glbl, session){
		
		
		
		
		
		var changeUserPassword = function(username, newPassword) {
			modifyUser(username, "password", newPassword);
		};
		
		
		
		
		var changeUserEmail = function(username, newEmail) {
			modifyUser(username, "email", newEmail);
		};
		
		
		
		/**
		 * groupNames must be a Set
		 */
		var addUserToGroup = function(username, groupNames) {
			modifyUser(username, "groups", groupNames, "add");
		};
		
		
		
		/**
		 * groupNames must be a Set
		 */
		var removeUserFromGroup = function(username, groupNames) {
			modifyUser(username, "groups", groupNames, "remove");
		};
		
		
		
		
		var lockUser = function(username) {
			
		};
		
		
		
		
		var unlockUser = function(username) {
			
		};
		
		
		
		
		
		
		
		
		
		var modifyUser = function(username, property, value, mode) {
			
			var url = 'http://'+window.location.host+'/filer/admin/'+session.getUser().name+"/user/"+username; 
			
			var userDetails = {
					userReceived: null,
					userName    : "",
					userEmail   : "",
					message     : "",
			};
			
			var promise = $http(
					{
						method: 'POST',
						url   : url,
						params: {
							token    : session.getSessionData().token,
							property : value,
							mode     : mode,
						}
					}
				);
			
			session.resetInactivity();
			
			promise.then(
					function(response) {
						var data = response.data;
						
						if (response.status == 200) {
							userDetails.userName     = data.username;
							userDetails.userEmail    = data.email;
							userDetails.message      = "success";
							userDetails.userReceived = true;
							session.addNotification("success", username, "User has been modified");
						} else {
							userDetails.message      = ""+data.message1+". "+data.message2;
							userDetails.userReceived = false;
							session.addNotification("warning", "Cannot modify user "+username, data.message1+". "+data.message2);
						}
					},
					function(error) {
						userDetails.message      = error.stausText;
						userDetails.userReceived = false;
						session.addNotification("warning", "Cannot modify user "+username, error.staus+". "+error.statusText);
					}
			);
			
			return userDetails;
			
			
		};
		
		
		
		
		
		
		
		var deleteUser = function(username) {
			
			var url = 'http://'+window.location.host+'/filer/admin/'+session.getUser().name+"/user/"+username; 
			
			var userDetails = {
					userReceived: null,
					userName    : "",
					userEmail   : "",
					message     : "",
			};
			
			var promise = $http(
					{
						method: 'DELETE',
						url   : url,
						params: {
							token    : session.getSessionData().token,
						}
					}
				);
			
			session.resetInactivity();
			
			promise.then(
					function(response) {
						var data = response.data;
						
						if (response.status == 200) {
							userDetails.userName     = data.username;
							userDetails.userEmail    = data.email;
							userDetails.message      = "success";
							userDetails.userReceived = true;
							session.addNotification("success", username, "User has been created");
						} else {
							userDetails.message      = ""+data.message1+". "+data.message2;
							userDetails.userReceived = false;
							session.addNotification("warning", "Cannot delete user "+username, data.message1+". "+data.message2);
						}
					},
					function(error) {
						userDetails.message      = error.stausText;
						userDetails.userReceived = false;
						session.addNotification("warning", "Cannot delete user "+username, error.staus+". "+error.statusText);
					}
			);
			
			return userDetails;
			
		};
		
		
		
		
		
		var createUser = function(username, password, email, groups) {
			
			var url = 'http://'+window.location.host+'/filer/admin/'+session.getUser().name+"/user/"+username; 
			
			var userDetails = {
					userReceived: null,
					userName    : "",
					userEmail   : "",
					message     : "",
			};
			
			var promise = $http(
					{
						method: 'PUT',
						url   : url,
						params: {
							token    : session.getSessionData().token,
							password : password,
							email    : email,
							groups   : groups,
						}
					}
				);
			
			session.resetInactivity();
			
			promise.then(
					function(response) {
						if (response.status == 201) {
							userDetails.userName     = data.username;
							userDetails.userEmail    = data.email;
							userDetails.message      = "success";
							userDetails.userReceived = true;
							session.addNotification("success", username, "User has been created");
						} else {
							userDetails.message      = ""+data.message1+". "+data.message2;
							userDetails.userReceived = false;
							session.addNotification("warning", "Cannot create user "+username, response.data.message1+". "+response.data.message2);
						}
					},
					function(error) {
						userDetails.message      = error.stausText;
						userDetails.userReceived = false;
						session.addNotification("warning", "Cannot create user "+username, error.staus+". "+error.statusText);
					}
			);
			
			
		};
		
		
		
		
		var getUsetDetails = function(username) {
			
			var url = 'http://'+window.location.host+'/filer/admin/'+session.getUser().name+"/user/"+username; 
			
			var userDetails = {
					userReceived: null,
					userName    : "",
					userEmail   : "",
					message     : "",
			};
			
			var promise = $http(
					{
						method: 'GET',
						url   : url,
						params: {
							token    : session.getSessionData().token,
						}
					}
				);
			
			session.resetInactivity();
			
			promise.then(
					function(response) {
						var data = response.data;
						
						if (response.status == 200) {
							userDetails.userName     = data.username;
							userDetails.userEmail    = data.email;
							userDetails.message      = "success";
							userDetails.userReceived = true;
							//session.addNotification("success", username, "User has been created");
						} else {
							userDetails.message      = ""+data.message1+". "+data.message2;
							userDetails.userReceived = false;
							session.addNotification("warning", "Cannot get user "+username, data.message1+". "+data.message2);
						}
					},
					function(error) {
						userDetails.message      = error.stausText;
						userDetails.userReceived = false;
						session.addNotification("warning", "Cannot get user "+username, error.staus+". "+error.statusText);
					}
			);
			
			return userDetails;
			
		};
		
		
		
		
		var findUser = function(partialUserName) {
			
		};
		
		
		
		
		
		
		
		
		var getActiveUserSessions = function() {
			
		};
		
		
		
		
		var terminateUserSession = function(userName) {
			
		};
		
		
		
//		var createGroup = function(groupName) {
//			
//		};
//		
//		var deleteGroup = function(groupName) {
//			
//		};
		
		
		
		
		return {
			
			createUser           : createUser,
			lockUser             : lockUser,
			unlockUser           : unlockUser,
			deleteUser           : deleteUser,
			getUsetDetails       : getUsetDetails,
			changeUserPassword   : changeUserPassword,
			changeUserEmail      : changeUserEmail,
			addUserToGroup       : addUserToGroup,
			removeUserFromGroup  : removeUserFromGroup,
			
			getActiveUserSessions: getActiveUserSessions,
			terminateUserSession : terminateUserSession,
			
//			createGroup          : createGroup,
//			deleteGroup          : deleteGroup,
		};
	}
	
	
	angular.module('app-filer').service('admin', ['$http', 'glbl', 'session', adminServiceFn]);
	
})()