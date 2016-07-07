(function(){

	
	
	var fn_rightClick = function($parse) {
		return function(scope, element, attrs) {
			var fn = $parse(attrs.ngRightClick);
			element.bind('contextmenu', function(event) {
				scope.$apply(function() {
					event.preventDefault();
					fn(scope, {$event:event});
				})
			});
		};
	};
	
	
	angular.module('app-filer').directive('ngRightClick', fn_rightClick);
	console.log("Loaded ngRightClick directive");

})()