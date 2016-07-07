(function(){
	
	'use strict'
	
	
	var controller = function($scope, glbl, session, explorer, user, publics){
		
		$scope.favs = [];
		console.log("Getting favs data");
		
		$scope.sessionData = session.getSessionData();
		$scope.user = session.getUser();
		$scope.notifications = session.getNotifications();
		
		$scope.sessionData.userInfoScope = $scope;
		
		
		$scope.login = function(username, password){
			session.login(username, password, 60);
		};
		
		
		$scope.logout = function(){
			session.logout();
		};
		
		$scope.removeNotification = function(id) {
			session.popNotification(id);
		};
		
		$scope.getTimeout = function(){
			var retVal = "-";
			
			if ($scope.sessionData.timeout != undefined && $scope.sessionData.timeout != null) {
				retVal = glbl.secondsToString($scope.sessionData.timeout);
			} else {
				console.log("session timeout is invalid: "+$scope.sessionData.timeout);
			}
			return retVal;
		};
		
		$scope.sessionOpenTime = function(){
			var retVal = "-";
			var expireTime = 0;
			
			if ($scope.sessionData.openTime > 0 && $scope.sessionData.openTime < Date.now()) {
				retVal = glbl.timestampToDateString($scope.sessionData.openTime);
			}
			return retVal;
		};
		
		$scope.sessionExpireTime = function(){
			var retVal = "-";
			var expireTime = 0;

			if ($scope.sessionData.openTime > 0 && $scope.sessionData.openTime < Date.now()) {
				expireTime = $scope.sessionData.openTime + ($scope.sessionData.timeout*1000);
				
				retVal = glbl.timestampToDateString(expireTime);
			}
			
			return retVal;
		};
		
		$scope.getRemainingTime = function(){
			var retVal = "";
			
			retVal = glbl.secondsToString(Math.floor($scope.sessionData.remainSec/1000));
			
			if (retVal == "") {
				retVal = "expired";
			}
			
			return retVal;
		};
		
		$scope.isLoggedIn = function(){
			var loggedIn = false;
			
			if ($scope.user.name != null) {
				loggedIn = true;
			};
			
			if ( ! session.isLoggedIn()) {
				loggedIn = false;
			};
			
			return loggedIn;
		};
		
		$scope.showFav = function(path) {
			explorer.openFile(path);
		};
		
		$scope.toggleSettingsWindow = function() {
			console.log("__________________________________________");
		};
		
		
		
		
		$scope.delBookmark = function(alias) {
			user.delBookmark(alias);
		};
		
		
		
		$scope.hasSessionExpired = function() {
			var expired = false;
			if ( ! $scope.isLoggedIn() && $scope.sessionData.remainSec != null && $scope.sessionData.remainSec < 1) {
				expired = true;
			};
			return expired;
			
		};
		
		$scope.searchUserName = function (substring) {
			var userDetails = publics.searchUserName(substring);
			console.log(userDetails.userNames);
			console.log(userDetails);
			return userDetails.userNames;
		}
		
		
		
	};
	
	
	
	
	angular.module('app-filer').controller('ctrl_leftPane_favs', ['$scope', 'glbl', 'session', 'explorer', 'user', 'publics', controller]);
	
})()