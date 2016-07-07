(function() {
	'use strict'
	
	
	var directive_fn = function ($parse) {
	    return {
	        restrict: 'A',
	        link: function (scope, element, attrs) {
	            var model = $parse(attrs.fileModel);
	            var isMultiple = attrs.multiple;
	            var modelSetter = model.assign;
	            element.bind('change', function () {
	                scope.$apply(function () {
	                    if (isMultiple) {
	                    	modelSetter(scope, element[0].files)
	                    } else {
	                    	modelSetter(scope, element[0].files[0])
	                    }
	                });
	            });
	        }
	    };
	};
	
	
	var fileInput_fn = function ($parse) {
	    return {
	        restrict: "EA",
	        /*template: "<input type='file' />",*/
	        template: function(element, attrs) {
	        	var multiple = attrs.hasOwnProperty('multiple')?"multiple":"";
	        	var hidden = attrs.hasOwnProperty('hidden')?"style='display:none'":"";
	        	var inputHTML = "<input type='file' " + multiple + " " + hidden + " />";
	        	return inputHTML;
	        },
	        replace: true,
	        link: function (scope, element, attrs) {

	            var modelGet = $parse(attrs.fileInput);
	            var modelSet = modelGet.assign;
	            var onChange = $parse(attrs.onChange);
	            var isMultiple = attrs.multiple;

	            var updateModel = function () {
	                scope.$apply(function () {
	                	if (isMultiple) {
	                		modelSet(scope, element[0].files);
	                	} else {
	                		modelSet(scope, element[0].files[0]);
	                	}
	                    onChange(scope);
	                });
	            };
	            
	            element.bind('change', updateModel);
	        }
	    };
	};
	
	angular.module('app-filer').directive('fileModel', directive_fn);
	
	angular.module('app-filer').directive('fileInput', fileInput_fn);
	
})();

