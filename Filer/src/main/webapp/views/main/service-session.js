(function() {
	'use strict'
	
	var sessionServiceFn = function($http, $interval, glbl){
		
		var user = {
				name     : null, // string
//				pw_hash  : null, // string
				email    : null, // string
				groups   : null, // []
				bookmarks: null, // []
				isAdmin  : false,
		};
		
		var environment = {
				os_name      : null, // string
				os_arch      : null, // string
				os_ver       : null, // string
				user_home    : null, // string
				user_root    : null, // string
				separator    : null, // string("/")
				lineSeparator: null, // string("\n")
		};
		
		var sessionData = {
				token           : null, // string
				timeout         : null, // int
				openTime        : null, // int
				lastActivityTime: null, // int
				remainSec       : null, // int
				userInfoScope   : null,
				loggedIn        : false,
		};
		
		
		
		var notifications = [];
		
		var getEnvironment = function(){
			return environment;
		};
		
		var getToken = function(){
			return sessionData.token;
		};
		
		var getTimeout = function(){
			return sessionData.timeout;
		};
		
		
		var getLastActivityTime = function(){
			return sessionData.lastActivityTime;
		};
		
		var resetInactivity = function(){
			getSessionData().lastActivityTime = Date.now();
		};
		
		var getOpenTime = function(){
			return sessionData.openTime;
		};
		
		var getNotifications = function() {
			return notifications;
		};
		
		var clearNotifications = function() {
			glbl.emptyArray(getNotifications());
		};
		
		var addNotification = function(type, title, text) {
			getNotifications().push(
					{
						id: getNotifications().length,
						time: glbl.timestampToDateString(Date.now()),
						type: type, 
						title: title, 
						text: text,
					}
				);
		};
		
		var popNotification = function(id) {
			var notifs = getNotifications();
			var notifs_new = [];
			
			for(var i=0; i<notifs.length; i++) {
				if (notifs[i].id != id) {
					notifs[i].id = i; // rearranging IDs
					notifs_new.push(notifs[i]);
				};
			};
			
			
			clearNotifications();
			glbl.copyArray(notifs_new, notifs);
		};
		
		var getRemainingTime = function(){
			return (getLastActivityTime() + (getTimeout()) ) - Date.now() ;
		};
		
		var startExpiryTimer = function(){
			
			if (getLastActivityTime() > 0 && getTimeout() > 0) {
				
				//var timer = setInterval(
				var timer = $interval(
						function(){
							var remSec = getLastActivityTime() + (getTimeout() * 1000) - Date.now();
							if (getTimeout() > 0 && remSec > 0) {
								getSessionData().remainSec = remSec; // terrible overhead... this structure is used in controller ($scope) and updates like that cause UI to refresh each time
								//console.log("Remaining session time: "+getSessionData().remainSec);
								
							} else {
								getSessionData().loggedIn = false;
								getSessionData().remainSec = remSec;
								//clearInterval(timer);
								$interval.cancel(timer);
								console.log("session time has expired");
								if (getUser().name != null) {
									addNotification("danger", "Session expired", "User "+getUser().name+" session has expired");
								};
								if (getSessionData() != null && getSessionData().userInfoScope != null) {
									getSessionData().userInfoScope.$apply();
								};
							};
						},
						1000,
						0,
						false
				);
			} else {
				//console.log("getLastActivityTime() == "+getLastActivityTime()+", getTimeout() == "+getTimeout()+".");
			};
			
		};
		
		var isLoggedIn = function() {
			return getSessionData().loggedIn;
		};
		
		var getUser = function(){
			return user;
		}
		
		var getSessionData = function(){
			return sessionData;
		};
		
		var getUserInfo = function(username){
			
			var url = 'http://'+window.location.host+'/filer/user/'+username+"/info";
			
			var promise = $http(
				{
					method: 'GET',
					url   : url,
					params: {
						token : getToken()
					}
				});
			
			resetInactivity();
			
			promise.then(
					function(response){
						var data = response.data;
						if (response.status == 200) {
							
							console.log("getUserInfo(): "); console.log(data);
							getUser().name      = data.username;
							getUser().groups    = data.groups;
							getUser().bookmarks = data.bookmarks;
							getUser().email     = data.email;
							
							for (var i=0; i<data.groups.length; i++) {
								if (data.groups[i] == "admin") {
									getUser().isAdmin = true;
								};
							};
							
							console.log("Loaded user is: ");
							console.log(getUser())
						} else {
							console.log("getUserInfo(username) failed : code="+response.status+"; message="+data);
							
						}
					},
					function(error){
						console.log("getUserInfo() error: ");
						console.log(error);
					}
					
			);
		};
		
		var login = function(username, password, sessionTimeout){
			
			var url = 'http://'+window.location.host+'/filer/user/'+username+"/login";
			
			var promise = $http(
				{
					method: 'POST',
					url   : url,
					params: {
						password : password,
						timeout  : getTimeout(),
					}
				});
			promise.then(
					function(response){
						var data = response.data;
						if (response.status == 200) {
							getSessionData().token         = data.sessionSeed;
							getSessionData().timeout       = data.sessionTimeout_s;
							getSessionData().openTime      = Date.now();
//							getSessionData().lastActivityTime   = Date.now();
							resetInactivity();
							
							getEnvironment().os_name       = data.os_name;
							getEnvironment().os_ver        = data.os_ver;
							getEnvironment().os_arch       = data.os_arch;
//							getEnvironment().user_home     = data.homedir;
							getEnvironment().user_root     = data.rootdir;
							getEnvironment().separator     = data.separator;
							getEnvironment().lineSeparator = data.lineSeparator;
							
							getUserInfo(username);
							
							getSessionData().loggedIn = true;
							startExpiryTimer();
							
							addNotification("success", "Logged in", "User "+username + " logged in");
							
							console.log("logged in as "+username+", session token is "+getToken());
							console.log("System info: ");
							console.log(environment);
						} else {
							addNotification("warning", "Login failed", "User "+username+" login failed. "+response.data.message1+". "+response.data.message2);
							console.log("login(username) returned: code="+response.status+"; message="+data);
							
						}
					},
					function(error){
						console.log("getUserInfo() error: ");
						console.log(error);
						if (error.data != null && error.data.causedBy != undefined) {
							addNotification("warning", "Login failed", "User login failed. Caused by "+error.data.causedBy+": "+error.data.message1+". "+error.data.message2);
							console.log("known error");
						} else {
							addNotification("warning", "Login failed", "User "+username+" login failed. "+error.status+": "+error.statusText);
							console.log("unknown error. Should be reported");
							// report error to the server
						}
					}
					
			);
		};
		
		
		var logout = function(){
			
			var url = 'http://'+window.location.host+'/filer/user/'+user.name+"/logout";
			var tokenOld = getToken();
			
			glbl.emptyObject(user);
			glbl.emptyObject(environment);
			glbl.emptyObject(sessionData);
			
			var promise = $http(
				{
					method: 'POST',
					url   : url,
					params: {
						token: tokenOld
					}
				});
			promise.then(
					function(response){
						console.log("User has been logged out");
					},
					
					function(error){
						console.log("User could not be logged out:");
						console.log(error);
					}
			);
			addNotification("success", "Logged out", "User has logged out");
		};
		
		
		return {
			getToken           : getToken,
			getUser            : getUser,
			getEnvironment     : getEnvironment,
			getSessionData     : getSessionData,
			getOpenTime        : getOpenTime,
			getRemainingTime   : getRemainingTime,
			getTimeout         : getTimeout,
			getNotifications   : getNotifications,
			clearNotifications : clearNotifications,
			addNotification    : addNotification,
			popNotification    : popNotification,
			resetInactivity    : resetInactivity,
			getUserInfo        : getUserInfo,
			login              : login,
			logout             : logout,
			isLoggedIn         : isLoggedIn,
		};
	}
	
	
	angular.module('app-filer').service('session', ['$http', '$interval', 'glbl', sessionServiceFn]);
	
})()