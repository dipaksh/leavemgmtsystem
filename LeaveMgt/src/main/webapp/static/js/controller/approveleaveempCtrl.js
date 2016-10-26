'use strict';

App.controller('approveleaveempCtrl', ['$scope', '$state', 'EmpService', function(scope, state, empService){
//	Cancel button
	$(".btn-warning").click(function(){
        $(".collapse").collapse('hide');
    });
	
	
	var promise = empService.getLeaves();
	
	promise.then(
			function(response){
				scope.showDetails = false; //Hide Employee details
				
				scope.mgrLeaves = response.data;
				console.log("Retrieved Employee Leaves to Manager");
			},
			function(error){
				alert("No New Employee Leave Request to approve or disapprove");
			}
		);

//	Approve Leave
	scope.approveLeave = function(leaveNum, empId){
		var yes = confirm("Approve Leave for Employee with id "+empId);
		if(yes){
			empService.approveLeave(leaveNum).then(
					function(response){
						scope.showDetails = false; //Hide Employee details
						
						scope.mgrLeaves = response.data;
						alert("Approved Leave for Employee with id "+empId)
						console.log("Retrieved Employee Leaves to Manager");
					},
					function(error){
						alert(error.statusText);
					}
				);
		}
	};
	
//	Disapprove Employee Leave
	scope.disapproveLeave = function(leaveNum, empId){
		var yes = confirm("Disapprove Leave for Employee with id "+empId);
		if(yes){
			empService.disapproveLeave(leaveNum).then(
					function(response){
						scope.showDetails = false; //Hide Employee details 
						scope.mgrLeaves = response.data;
						alert("Disapproved Leave for Employee with id "+empId)
						console.log("Retrieved Employee Leaves to Manager");
					},
					function(error){
						alert(error.statusText);
					}
				);
		}
	};

	
//	Employee leave details on click on emp id 
	scope.getEmpLeaveDetails = function(empId, startDate){
		
		//Gets Emp details and Name
		empService.getEmpdetails(empId)
			.then(
				function(response){
					scope.showDetails = true; //Show Employee details 
					scope.empdetails = response.data; //prints fetched data
					
				},
				function(error){
//					alert(error.statusText);
				}
			);
		
		//gets Employee leave details
		empService.getEmpLeaveDetails(empId, startDate).then(
				function(response){
					scope.showDetails = true; //Show Employee details 
					scope.empLeaveDetails = response.data;
					console.log("Retrieved Employee Leave Details");
				},
				function(error){
//					alert(error.statusText);
				}
			);
	}
}]);