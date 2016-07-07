(function() {
	'use strict'
	
	var userServiceFn = function($http, session, glbl){
		
		
		var manageBookmarks = function(action, alias, path, username) {
			
			var url = 'http://'+window.location.host+'/filer/user/'+username+"/bookmark/"+alias;
			
			var promise = $http(
					{
						method: action,
						url   : url,
						params: {
							path : path,
							token: session.getToken(),
						},
					}
			);
			
			promise.then(
					function(response) {
						var data = response.data;
						if (response.status == 200 || response.status == 201) {
							session.addNotification("success", "Bookmark updated", "User '"+username+"' bookmark '"+alias+"' has been updated.");
							session.getUserInfo(username);
						} else {
							session.addNotification("warning", "Bookmark update failed", "User '"+username+"' bookmark '"+alias+"' update failed. "+data.message1+". "+data.message2+".");
						};
					},
					function(error) {
						session.addNotification("danger", "Bookmark update failed", "User '"+username+"' bookmark '"+alias+"' update failed. "+error.status+": "+error.statusText+".");
					}
			);
			
			
		};
		
		
		
		var addBookmark = function(alias, path, username) {
			
			if (username == null) {
				username = session.getUser().name;
			};
			
			manageBookmarks("PUT", alias, path, username);
			
		};
		
		
		var delBookmark = function(alias, path, username) {
			
			if (username == null) {
				username = session.getUser().name;
			};
			
			manageBookmarks("DELETE", alias, path, username);
			
		};
		
		
		var updateBookmark = function(alias, path, username) {
			
			if (username == null) {
				username = session.getUser().name;
			};
			
			manageBookmarks("POST", alias, path, username);
			
		};
		
		
		var setPassword = function(newPass) {
			
		};
		
		
		
		return {
			addBookmark    : addBookmark,
			delBookmark    : delBookmark,
			updateBookmark : updateBookmark,
			setPassword    : setPassword,
			
		};
		
	}
	
	
	angular.module('app-filer').service('user', ['$http', 'session', 'glbl', userServiceFn]);
	
})()