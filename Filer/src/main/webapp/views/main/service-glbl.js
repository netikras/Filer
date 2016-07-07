(function(){
	
	'use strict'
		
		var servicefn = function($http){
			
			// contains user information
			var user = {
					name:      '',
					pswd_hash: '',
					email:     '',
					favs:      {}
			};
			
			// holds a reference to a watched listener. Once this value is changed watcher is interrupted and main view is refreshed
			var explorerViewProposed = {
					cwd: "",
					files: []
					};
			
			var explorerViewSuggested = {
					cwd: "",
					files: [],
					state: 'ok' // ok, fa
			}
			
			var statics = {
					separator: "/",
					rootDir  : "/",
			}
			
			
			
			var timestampToDateString = function(timestamp){
				var retVal = "-";
				var targetDate = null;
				var expectedLength = (""+Date.now()).length;
				
				
				function pad(n){return n<10 ? '0'+n : n};
				
				if (timestamp != undefined && timestamp != null && timestamp > 0) {
					
					while ((""+timestamp).length < expectedLength) {
						timestamp *= 10;
					};
					
					targetDate = new Date(timestamp);
					
					retVal =  ""+pad(targetDate.getFullYear() )
							+"-"+pad((targetDate.getMonth()+1))
							+"-"+pad(targetDate.getDate()     )
							+" "+pad(targetDate.getHours()    )
							+":"+pad(targetDate.getMinutes()  )
							+":"+pad(targetDate.getSeconds()  );
					
				}
				return retVal;
			};
			
			var secondsToString = function(seconds){
				var retVal = "0s";
				
				if (seconds != undefined && seconds != null) {
					retVal = "";
					
					var numyears   = Math.floor(seconds / 31536000);
					var numdays    = Math.floor((seconds % 31536000) / 86400); 
					var numhours   = Math.floor(((seconds % 31536000) % 86400) / 3600);
					var numminutes = Math.floor((((seconds % 31536000) % 86400) % 3600) / 60);
					var numseconds = (((seconds % 31536000) % 86400) % 3600) % 60;
					
					if (numyears   != 0) retVal = retVal +numyears  +"yrs ";
					if (numdays    != 0) retVal = retVal +numdays   +"days ";
					if (numhours   != 0) retVal = retVal +numhours  +"hrs ";
					if (numminutes != 0) retVal = retVal +numminutes+"min ";
					if (numseconds != 0) retVal = retVal +numseconds+"sec" ;
					
				}
				//console.log("Human readable "+seconds+" seconds look like that: "+retVal);
				return retVal;
			};
			
			
			var bytesToString = function(bytes){
				var measures = ["B", "KB", "MB", "GB", "TB"];
				var result = "? B";
				var level = 0;
				
				if (bytes != undefined && bytes != null) { 
				
					while (bytes > 1024) {
						bytes = bytes / 1024;
						level++;
					};
					
					result = ""+(bytes.toFixed(3))+" "+measures[level];
				}
				
				return result;
			};
			
			
			
			var sortByProperty = function(property, asc){
				
				var order = (asc == true ? 1 : -1);
				
				return function (a,b) {
					var result = (a[property] < b[property]) ? -1 : (a[property] > b[property]) ? 1 : 0;
					return result * order;
				}
				
			};
			
			var sortByType = function(left, right){
				var retVal = 0;
				console.log("sorting...");
				if (left != undefined && right != undefined){
					
					if (left.type < right.type) retVal = -1;
					if (left.type > right.type) retVal =  1;
					//console.log("COmparing: "+left.type+" vs "+right.type+"; retVal="+retVal);
				}
				
				return retVal;
			};
			
			
			var arrayContains = function(array, item){
				
				var contains = false;
				
				if (array != undefined && array != null && array.length > 0) {
					
					for (var i=0; i<array.length && contains == false; i++) {
						if (array[i] === item || array[i] == item) {
							contains = true;
						};
					};
				};
				
				
				return contains;
			};
			
			
			var emptyObject = function(obj){
				if (obj != undefined && obj != null) {
					for(var key in obj) {
						obj[key] = null;
					};
				};
				return obj;
			};
			
			
			var emptyArray = function(array){
				if (array != undefined && array != null && array.length > 0) {
					while(array.length > 0) {
						array.pop();
					};
				};
				return array;
			};
			
			
			var copyObject = function(src, dest) {
				if (dest == undefined || dest == null) {
					dest = {};
				};
				
				if (src != undefined && src != null) {
					for (var key in src) {
						dest[key] = src[key];
					};
				};
				
				return dest;
			};
			
			
			var copyArray = function(src, dest){
				if (dest == undefined || dest == null) {
					dest = [];
				};
				
				if (src != undefined && src != null) {
					for (var i=0; i<src.length; i++) {
						dest.push(src[i]);
					};
				};
				
				return dest;
			};
			
			
			
			
			
			

			
			
			var emptySuggestedFiles = function(){
				for(var i=0; i<explorerViewSuggested.files.length; i++){
					explorerViewSuggested.files[i].pop();
				}
			}
			
			var fillSuggestedFiles = function(newList){
				for(var i=0; i<newList.length; i++){
					explorerViewSuggested.files.push(newList[i]);
				}
			}
			
			
			
			
			var fileModeToString = function(mode){
				var result = "";
				var s = {r:"-", w:"-", x:"-"}; // sticky:false, suid:false, sgid:false
				var u = {r:"-", w:"-", x:"-"};
				var g = {r:"-", w:"-", x:"-"};
				var o = {r:"-", w:"-", x:"-"};
				
				var permissions = [s, u, g, o];
				
				var intVal = 0;
				var mode_str = ""+mode;
				if (mode_str.length == 3) mode_str = "0"+mode_str;
				
				
				for (var i=0; i<mode_str.length; i++) {
					intVal = parseInt(mode_str[i]);
					
					if (intVal % 2 == 1) {
						permissions[i].x = "x";
						//s.sticky = true;
						intVal -= 1;
					};
					
					if ((intVal / 2) % 2 == 1) {
						permissions[i].r = "r";
						//s.suid = true;
						intVal -= 2;
					};
					
					if (intVal == 4) {
						permissions[i].w = "w";
						//s.sgid = true;
					};
				};
				
				if (s.r == "r") u.x = (u.x == "x" ? "s" : "S");
				if (s.w == "w") g.x = (g.x == "x" ? "s" : "S");
				if (s.x == "x") o.x = (o.x == "x" ? "t" : "T");
				
				for (var i=1; i<permissions.length; i++) {
					for (var p in permissions[i]) {
						result += permissions[i][p];
					};
				};
				
				return result;
			};
			
			
			
			var performActionOnFiles = function(action, files){
				
				if (files == undefined || files == null){
					files = explorerViewProposed.files;
				}
				
				var url = 'http://'+window.location.host+'/filer/'+action;
				
				console.log("GET'ing url: "+url);
				console.log("GET params: "+"cwd="+explorerViewProposed.cwd+"; files:");
				console.log(files);
				
				var promise = $http(
					{
						method: 'GET',
						url   : url,
						params: {
							cwd : explorerViewProposed.cwd,
							file: files
						}
					}
				).then(
					function(response){
						// onSuccess
						
						//response.data.sort(sortByType);
						
						console.log("HTTPresponse");
						console.log(response);
						if (response.data.sort != undefined && response.data.sort != null)
							response.data.sort(sortByProperty("type", true));
						explorerViewSuggested.cwd   = explorerViewProposed.cwd;
						explorerViewSuggested.files = response.data;
						explorerViewSuggested.state = 'ok';
						console.log("Received "+explorerViewSuggested.files.length+" files.");
						
						explorerViewProposed.cwd = "";
						explorerViewProposed.files = []
					},
					function(error){
						// onError
						explorerViewSuggested.state = 'fa';
					}
				);
			}
			
			var setUser = function(newUser){
				user = newUser;
			}
			
			var getUser = function(){
				return user;
			}
			
			
			var setExplorerViewProposed = function(newVal){
				explorerViewProposed = newVal;
			}
			
			var getExplorerViewProposed = function(){
				return explorerViewProposed;
			}
			
			
			var setExplorerViewSuggested = function(sugg){
				explorerViewSuggested = sugg;
			}
			
			var getExplorerViewSuggested = function(){
				return explorerViewSuggested;
			}
			
			
			var getStatics = function(){
				return statics;
			}
			
			
			
			
			
			return {
				'getUser': getUser,
				'setUser': setUser,
				'performActionOnFiles': performActionOnFiles,
				'getExplorerViewProposed' : getExplorerViewProposed,
				'setExplorerViewProposed' : setExplorerViewProposed,
				'getExplorerViewSuggested': getExplorerViewSuggested,
				'setExplorerViewSuggested': setExplorerViewSuggested,
				'getStatics': getStatics,
				'getSorterByProperty' : sortByProperty,
				'timestampToDateString': timestampToDateString,
				'secondsToString': secondsToString,
				arrayContains : arrayContains,
				emptyObject : emptyObject,
				emptyArray : emptyArray,
				copyObject : copyObject,
				copyArray : copyArray,
				fileModeToString: fileModeToString,
				bytesToString: bytesToString,
				
			}
			
		}
		
		
		
		angular.module('app-filer').service('glbl', ['$http', servicefn]);
		
		
})();