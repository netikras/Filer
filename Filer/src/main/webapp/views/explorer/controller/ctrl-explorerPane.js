(function(){
	
	'use strict'
	
	var controller = function($scope, glbl, session, explorer, user){
		
		
		var targetItems = [];
		
		$scope.path_input = explorer.getCurrentFile().cwd;
		
		$scope.currentFile = explorer.getCurrentFile();
		
		
		$scope.explorerViewSuggested = glbl.getExplorerViewSuggested();
		$scope.explorerViewSuggested = explorer.getCurrentFile();
		
		
		$scope.selectedFile = {};
		
		$scope.setSelectedFile = function(file) {
			$scope.selectedFile = file;
		};
		
		$scope.compressionMode = "individually"; /// "single_arch"
		
		
		
		
		$scope.img_height = 64;
		$scope.img_width  = 64;
		
		$scope.toolbar_img_height = 32;
		$scope.toolbar_img_width  = 32;
		
		$scope.style_wrap_breakWords = {
				'word-wrap' : "break-word",
				'word-break': "break-word",
			};
		
		
		$scope.style_wrap_breakSpace = "white-space: normal";
		
		$scope.setTargetItems = function(items) {
			targetItems = items;
		};
		
		$scope.getTargetItems = function() {
			return targetItems;
		};
		
		$scope.submitPath = function(){
			explorer.openFile($scope.currentFile.addrBar);
		}
		
		$scope.openFile = function(cwd, name){
			explorer.openFile(cwd, name);
		}
		
		$scope.showFileInfo = function(file){
			$scope.targetFile = file;
		};
		
		$scope.getPermissionsString = glbl.fileModeToString;
		$scope.timestampToString = glbl.timestampToDateString;
		$scope.bytesToString = glbl.bytesToString;
		
		$scope.performAction = function(file, action){
			console.log("performActionOnFiles("+file+", "+action+")");
			console.log(file);
			explorer.performActionOnFiles(action, file);
			//glbl.performActionOnFiles(action, [file]);
		}
		
		$scope.getDownloadUrl = function(file) {
			 var url = "";
			 
			 url = 'http://'+window.location.host+'/filer/explorer/download?'
			 	+ "cwd="+file.parent
			 	+ "&file="+file.name
			 	+"&token="+session.getToken();
			 
			 //console.log("file url="+url);
			 
			 return url;
		};
		
		$scope.addToFavorites = function(alias, path){
			user.addBookmark(alias, path);
		};
		
		
		$scope.context = function(filename){
			console.log("right-click on a file: "+filename);
		}
		
		
		
		$scope.show_hidden    = false;
		$scope.sort_ascending = true;
		$scope.sort_property  = "type";
		
		
		$scope.sortByProperty = function(property){
			$scope.sort_property = property;
			$scope.explorerViewSuggested.files.sort(glbl.getSorterByProperty(property, $scope.sort_ascending));
			
		};
		
		$scope.goUp = function(){
			explorer.openFile(explorer.getCurrentFile().cwd);
			$scope.path_input = explorer.getCurrentFile().cwd;
		};
		
		$scope.path_input = glbl.getStatics().rootDir;
		
		
		
		// Files' selection
		
		$scope.selectedFiles = explorer.getSelectedFiles();
		
		$scope.selectFile = function(file) {
			if ( ! explorer.isFileSelected(file)) {
				explorer.addSelectedFile(file);
			} else {
				console.log("file is already selected");
			};
		};
		
		$scope.unselectFile = function(file) {
			explorer.popSelectedFile(file);
		};
		
		$scope.unselectAllFiles = function() {
			explorer.emptySelectedFiles();
		};
		
		
		
		$scope.compress = function(method, filesList) {
			
			var filename = null;
			
			explorer.compressFile(filesList, filename, method);
			
		};
		
		$scope.upload = function(files, cwd) {
			if (cwd == undefined || cwd == null) {
				cwd = explorer.getCurrentFile().cwd
						+ session.getEnvironment().separator
						+ explorer.getCurrentFile().filename;
			}
			
			if (files != null) {
				explorer.uploadFiles(cwd, files);
			}
		};
		
		
		
		
		
	}
	
	angular.module('app-filer').controller('ctrl_explorerPane', ['$scope', 'glbl', 'session', 'explorer', 'user', controller]);
	
})()