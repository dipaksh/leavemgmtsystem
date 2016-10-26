'use strict';

App.controller('empdetailsCtrl', ['$scope', '$state', 'EmpService', function(scope, state, empService){
	
//	For Role based options
	if(role === 2 )
		scope.isManager = true;
	else
		scope.isManager = false;
	
//	Gets the Employee details
	var promise = empService.getEmpdetails(id);
	
	promise.then(
			function(response){
				scope.emp = response.data; //prints fetched data
				scope.empDetailsId = id; //prints empId
				console.log("Employee details retrived");
			},
			function(error){
				scope.errorMessage = error.statusText;
			}
		);
	//Button navigation
	scope.loadNewLeave = function(){
		$(".collapse").collapse('show');
		state.go('empdetails.newleave');
	}
	
	scope.loadApprovedLeaves = function(){
		$(".collapse").collapse('show');
		state.go('empdetails.approvedleave');
	}
	
	scope.loadNewEmp = function(){
		$(".collapse").collapse('show');
		state.go('empdetails.newemp');
	}
	
	scope.loadApproveLeaveEmp = function(){
		$(".collapse").collapse('show');
		state.go('empdetails.approveleaveemp');
	}
	
//	Change password
	scope.submit = function(){
		if(!(scope.newpass === scope.cpass))
			alert("Password don't match")
		else{
			var empLogin = {"empId": id, "password":scope.newpass}
			
			empService.changePassword(empLogin).then(
					function(response){
						alert("Password Changed Successfully..!!");
					},
					function(error){
						alert("Password Change Failed.. Please try after some time.")
					}
				);
		}
	}
	
}]);