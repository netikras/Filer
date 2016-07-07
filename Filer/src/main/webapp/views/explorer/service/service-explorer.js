(function(){
	
	'use strict'
	
	
	var explorerServiceFn = function($http, glbl, session){
		
		var currentFile = {
				cwd     : "",
				filename: "",
				addrBar : "",
				files   : [],
		};
		
		var selectedFiles = [];
		
		
		var getCurrentFile = function(){
			return currentFile;
		};
		
		
		
		
		
		
		var getSelectedFiles = function(){
			return selectedFiles;
		};
		
		var emptySelectedFiles = function(){
			glbl.emptyArray(getSelectedFiles());
		};
		
		var addSelectedFile = function(file){
			if (file != undefined && file != null) {
				getSelectedFiles().push(file);
			};
		};
		
		var popSelectedFile = function(file){
			if (file != undefined && file != null) {
				var files = getSelectedFiles();
				var files_new = [];
				
				for(var i=0; i<files.length; i++) {
					if (files[i].absolutePath == file.absolutePath && files[i].cannonicalPath == file.cannonicalPath) {
						console.log("Found the file! Excluding it from selected items now");
					} else {
						files_new.push(files[i]);
					};
				};
				
				console.log("Deselected files: "+(files.length - files_new.length));
				
				emptySelectedFiles();
				glbl.copyArray(files_new, files);
			};
		};
		
		var isFileSelected = function(file) {
			var selected = false;
			
			
			if (file != undefined && file != null) {
				var files = getSelectedFiles();
				
				for(var i=0; i<files.length && ! selected; i++) {
					if (files[i].absolutePath == file.absolutePath && files[i].cannonicalPath == file.cannonicalPath) {
						selected = true;
					};
				};
			};
			
			return selected;
		};
		
		
		
		
		
		
		
		var splitFullPath = function(fullPath){
			var result = [];
			var strippedPath = fullPath;
			var strippedPathSplit;
			var separator = session.getEnvironment().separator;
			
			// Snip off the trailing slash
			if (fullPath.length > 1 && 
				fullPath.charAt(fullPath.length - 1) == separator) {
				strippedPath = fullPath.substr(0, fullPath.length - 1);
			};
			
			//console.log("fullPath="+fullPath+"; user_root="+session.getEnvironment().user_root);
			
			if (fullPath == session.getEnvironment().user_root) {
				result = ["", session.getEnvironment().user_root];
			} else {
				strippedPathSplit = strippedPath.split(separator);
				
				//fullPath.substr(0, fullPath.length - (fullPath.charAt(fullPath.length - 1) == separator ? 1 : 0)).splitOnLast(separator)
				
				// filename
				result[1] = strippedPathSplit.pop();
				// cwd
				result[0] = strippedPathSplit.join(separator);
			};
			
			
			
			console.log("splitFullPath("+fullPath+") returns:");
			console.log(result);
			return result;
		};
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/**
		 * Calls web-method to open a file and show its contents
		 */
		var openFile = function(cwd, filename){
			
			
			if (cwd == undefined || cwd == null) {
				console.log("openFile() with no parameters (cwd = "+cwd+")");
				return;
			}
			
			if (filename == undefined || filename == null) {
				var fullPathSplit = splitFullPath(cwd);
				cwd      = fullPathSplit[0];
				filename = fullPathSplit[1];
			}
			
			
			var url = 'http://'+window.location.host+'/filer/explorer/open';
			
			var promise = $http(
					{
						method: "GET",
						url: url,
						params: {
							token    : session.getToken(),
							'cwd'      : cwd,
							'file' : filename,
						}
					}
			);
			
			session.resetInactivity();
			
			promise.then(
					function(response) {
						if (response.status == 200) {
							console.log("File "+filename+" has been opened");
							//console.log(response.data);
							currentFile.cwd      = cwd;
							currentFile.filename = filename;
							currentFile.files = response.data;
							
							currentFile.addrBar = cwd+session.getEnvironment().separator+filename;
							
							getCurrentFile().files.sort(glbl.getSorterByProperty("directory", false));
							//getCurrentFile().files.sort(glbl.getSorterByProperty("name"     , true));
							
						} else {
							session.addNotification("warning", "Cannot open file", "Caused by "+response.data.causedBy+": "+response.data.message1+". "+response.data.message2+"");
							console.log("Error occurred: ");
							console.log(response.data);
						};
					},
					function(error) {
						if (error.data != undefined && error.data.causedBy != undefined) {
							session.addNotification("warning", "Cannot open file", "Caused by "+error.data.causedBy+": "+error.data.message1+". "+error.data.message2+"");
						} else {
							session.addNotification("warning", "Cannot open file: "+filename+". "+error.status+": "+error.statusText);
						};
						
						console.log("Error occurred: ");
						console.log(error);
					}
			);
			
		};
		
		
		
		
		/**
		 * Calls web-method to delete a file
		 */
		var deleteFile = function(cwd, filenames){
			
			
		};
		
		
		/**
		 * Calls web-method to upload a new file and save it in ${cwd} 
		 * called ${filename}
		 */
		var uploadFiles = function(cwd, files){
			
			var url = 'http://'+window.location.host+'/filer/explorer/upload';
			var promise = null;
			var formData = new FormData();
			var filenames = "";
			
			var result = {
					success: "",
					data   : "",
			};
			
			if (files != null) {
				for (var i=0; i<files.length; i++) {
					console.log("Appending file for upload");
					console.log(files[i]);
					
					formData.append('file', files[i]);
					filenames = filenames + (i==0?"":"; ") + files[i].name;
				};
				
				//formData.append('cwd', cwd);
				
				promise = $http(
						{
							method: "POST",
							url: url,
							data: formData,
							params : {
								cwd: cwd,
							},
							headers: {'Content-Type': undefined},
							transformRequest: angular.identity,
						}
				);
				
				
				promise.then(
						function(response) {
							if (response.status == 201) { // TODO show filenames in notification
								session.addNotification("success", "Files uploaded", "Files have been successfully uploaded: "+filenames);
								result.data = response.data;
								result.success = true;
								
							} else {
								session.addNotification("warning", "Files' upload failed", "Failed to upload files: " + filenames + ". "+response.data.message1+". "+response.data.message2);
								result.data = response.data;
								result.success = false;
							};
						},
						function(error) {
							session.addNotification("warning", "Files' upload failed", "Failed to upload files: " + filenames + ". "+error.status+": "+error.statusText);
							result.data = error.data;
							result.success = false;
						}
				);
				
			};
			
			return result;
			
		};
		
		
		/**
		 * Calls web-method to download a file named under ${filename} and 
		 * located in ${cwd}
		 */
		var downloadFile = function(cwd, filename){
			
			console.log("cwd:"+cwd+"; filename:"+filename);
			
			if (cwd == undefined || cwd == null) {
				console.log("downloadFile() with no parameters (cwd = "+cwd+")");
				return;
			}
			
			if (filename == undefined || filename == null) {
				var fullPathSplit = splitFullPath(cwd);
				cwd      = fullPathSplit[0];
				filename = fullPathSplit[1];
			}
			
			
			var url = 'http://'+window.location.host+'/filer/explorer/download';
			
			var promise = $http(
					{
						method: "GET",
						url: url,
						params: {
							token    : session.getToken(),
							'cwd'      : cwd,
							'file' : filename,
						}
					}
			);
			
			session.resetInactivity();
			
			var downloadAssistane = {
					click : function(node) {
						var ev = document.createEvent("MouseEvents");
						ev.initMouseEvent("click", true, false, self, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
						return node.dispatchEvent(ev);
					},
					encode : function(data) {
						return 'data:application/octet-stream;base64,' + btoa( data );
					},
					link : function(data, name){
						var a = document.createElement('a');
						a.download = name || self.location.pathname.slice(self.location.pathname.lastIndexOf('/')+1);
						a.href = data || self.location.href;
						return a;
					}
			};
			downloadAssistane.save = function(data, name) {
				this.click(
						this.link(
								this.encode( data ),
								name
						)
				);
			};
			
			
			
			
			
			
			promise.then(
					function(response) {
						if (response.status == 200) {
							console.log("File "+filename+" has been downloaded");
							//console.log(response);
							downloadAssistane.save(response.data, filename);
						} else {
							session.addNotification("warning", "Cannot download file", "Caused by "+response.data.causedBy+": "+response.data.message1+". "+response.data.message2+"");
							console.log("Error occurred: ");
							console.log(response.data);
						};
					},
					function(error) {
						if (error.data != undefined && error.data.causedBy != undefined) {
							session.addNotification("warning", "Cannot download file", "Caused by "+error.data.causedBy+": "+error.data.message1+". "+error.data.message2+"");
						} else {
							session.addNotification("warning", "Cannot download file: "+filename+". "+error.status+": "+error.statusText);
						};
						
						console.log("Error occurred: ");
						console.log(error);
					}
			);
			
		};
		
		
		/**
		 * Calls web-method to compress one or more files. There are a few 
		 * use scenarios:
		 *   1) ${filename} is null -- each file will be compressed to a 
		 *     separate archive, called ${original_filename}.${exension};
		 *   2) ${filename} is provided -- all ${files} will be compressed
		 *     to a single archive (if possible), called ${filename}.${extension}.
		 * ${format} tells webmethod what compression algorithm can be used.
		 * Currently the following algorithms are available:
		 *   - "tar" -- creates a tar archive w/o compression
		 *   - "gz" or "gzip" -- creates a gzip archive with default compression
		 *       ratio. NOTE: gzip algorithm does not allow multiple files in
		 *       a single archive.
		 *   - "zip" - creates a ZIP archive with default compression ratio.
		 *   - "tgz" or "targz" or "tar.gz" or "tar_gz" -- creates a tarball 
		 *       and compresses it using GZIP with default compression ratio.
		 */
		var compressFile = function(files, filename, format){
			
			
			
		};
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		var performActionOnFiles = function(action, file) {
			switch (action) {
				case "download":
					if (file != undefined && file != null) {
						downloadFile(file.parent, file.name);
					} else {
						var files = getSelectedFiles();
						for(var i=0; i<files.length; files++) {
							downloadFile(files[i].parent, files[i].filename);
						};
					};
					break;
					
				case "delete":
					if (file != undefined && file != null) {
						deleteFile(file.parent, file.name);
					} else {
						var files = getSelectedFiles();
						for(var i=0; i<files.length; files++) {
							deleteFile(files[i].parent, files[i].filename);
						};
					};
					break;
					
				default:
					break;
			}
		};
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return {
			getCurrentFile       : getCurrentFile,
			getSelectedFiles     : getSelectedFiles,
			emptySelectedFiles   : emptySelectedFiles,
			addSelectedFile      : addSelectedFile,
			popSelectedFile      : popSelectedFile,
			isFileSelected       : isFileSelected,
			openFile             : openFile,
			deleteFile           : deleteFile,
			uploadFiles          : uploadFiles,
			downloadFile         : downloadFile,
			compressFile         : compressFile,
			performActionOnFiles : performActionOnFiles,
		};
	};
	
	
	angular.module('app-filer').service('explorer', ['$http', 'glbl', 'session', explorerServiceFn]);
	
})()