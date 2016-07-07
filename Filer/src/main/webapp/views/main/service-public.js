(function() {
	'use strict'
	
	var adminServiceFn = function($http, glbl, session){
		
		
		var sendFeedback = function(feedback) {
			
		};
		
		
		
		var requestNewUser = function(username, password, email) {
			
		};
		
		
		var requestGroupsMembership = function(username, groups) {
			
		};
		
		var searchUserName = function(partialName) {
			
			var url = 'http://'+window.location.host+'/filer/admin/'+session.getUser().name+"/user/"+partialName+"/find";
			
			var userDetails = {
					userReceived: null,
					userNames   : [],
					message     : "",
			};
			
			var promise = $http(
					{
						url: url,
						method: "GET",
						params: {
							username: partialName,
							token: session.getSessionData().token,
						}
					}
				);
			
			session.resetInactivity();
			
			//glbl.emptyArray(userDetails.userNames);
			
			promise.then(
					function(response) {
						var data = response.data;
						
						if (response.status == 200) {
							//glbl.copyArray(data, userDetails.userNames);
							userDetails.userNames    = data;
							userDetails.message      = "success";
							userDetails.userReceived = true;
							//session.addNotification("success", username, "User has been modified");
						} else {
							userDetails.message      = ""+data.message1+". "+data.message2;
							userDetails.userReceived = false;
							//session.addNotification("warning", "Cannot modify user "+username, data.message1+". "+data.message2);
						}
					},
					function(error) {
						userDetails.message      = error.statusText;
						userDetails.userReceived = false;
						session.addNotification("warning", "Cannot query username "+username, error.staus+". "+error.statusText);
					}
			);
			
			return userDetails;
		};
		
		return {
			sendFeedback           : sendFeedback,
			requestNewUser         : requestNewUser,
			requestGroupsMembership: requestGroupsMembership,
			searchUserName         : searchUserName,
		};
		
		
	};

	angular.module('app-filer').service('publics', ['$http', 'glbl', 'session', adminServiceFn]);

})()