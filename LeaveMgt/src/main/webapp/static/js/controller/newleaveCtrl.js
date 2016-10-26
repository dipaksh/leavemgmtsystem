'use strict';
//Cancel Button	
$(".btn-warning").click(function(){
    $(".collapse").collapse('hide');
});

App.controller('newleaveCtrl', ['$scope', '$state', 'EmpService', function(scope, state, empService){
	
//	Cancel Button	
	$(".btn-warning").click(function(){
        $(".collapse").collapse('hide');
    });
	
	scope.submit = function(){
		var newLeave = {"empId": id,"startDate": scope.sd,"typeOfLeave": scope.typeofleave ,"endDate":scope.d2,"status":"pending","reason":scope.res, "phone":scope.cn};
		
		var promise = empService.addNewLeave(newLeave);
		
		promise.then(
				function(response){
					scope.emp = response.data;
					alert("Added new Leave");
//					Close the dialog
					(function(){
				        $(".collapse").collapse('hide');
				    }());
					state.transitionTo(state.current, null, {
					    reload: true,
					    inherit: false,
					    notify: true
					});

				},
				function(error){
					alert(error.statusText);
				}
			);
	};
	
	
}]);