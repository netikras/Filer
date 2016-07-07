(function(){
	
	var fn_filter = function() {
		
		  return function(files, showAll) {
			  
			  var retVal = [];
			  
		      if (showAll == false && files != undefined){
		    	  
		    	  for (var i=0; i<files.length; i++){
		    		  
		    		  if (files[i] != null && files[i].name != null 
		    			&& files[i].name.charAt(0) != '.'){
		    			  
		    			  retVal.push(files[i]);
		    		  }
		    		  
		    	  }
		      } else {
		    	  retVal = files;
		      }
		      
		    return retVal;
		  };
		}
	
	
	
	angular.module('app-filer').filter('showHidden', fn_filter);
	
})()