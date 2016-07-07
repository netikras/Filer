
(function(){

	'use strict'
	
	var route_mainView  = function ($stateProvider, $urlRouterProvider) {
		$urlRouterProvider.otherwise("/");
		$stateProvider
			.state('home',
				{
					url: '/',
					//abstract: true,
					views: {
						'left': {
							templateUrl: 'views/left_panel/view/left_panel.html',
							controller : 'ctrl_leftPane_favs'
						},
						'middle': {
							templateUrl: 'views/explorer/view/explorer.html',
							controller : 'ctrl_explorerPane'
						}/*,
						'messages': {
							templateUrl: 'views/explorer/view/explorer.html',
							controller : 'ctrl_explorerPane'
						},*/
						
					}
				}
			);
		
	};
	
	
	
	
	//angular.module('app-filer',[]).service('glbl', function(){});
	angular.module('app-filer').config(['$stateProvider', '$urlRouterProvider', route_mainView]);
	
	
})();