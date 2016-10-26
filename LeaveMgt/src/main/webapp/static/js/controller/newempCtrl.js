'use strict';

App.controller('newempCtrl', ['$scope', '$state', 'EmpService', function(scope, state, empService){



	
//	Cancel Button	
	$(".btn-warning").click(function(){
        $(".collapse").collapse('hide');
    });
	
	
	scope.submit = function(){
		var newEmp = {"empMail": scope.email, "empname":scope.name, "empId": scope.eid, "dateOfJoining": scope.doj, "bloodGrp": scope.bgroup, "gender": scope.gender};
		
		var promise = empService.addNewEmpdetails(newEmp);
		
		promise.then(
				function(response){
					scope.emp = response.data;
					alert("New Employee Added");
					(function(){
				        $(".collapse").collapse('hide');
				    }());
				},
				function(error){
					alert(scope.errorMessage = error.statusText);
				}
			); 
	}
		
	
}]);