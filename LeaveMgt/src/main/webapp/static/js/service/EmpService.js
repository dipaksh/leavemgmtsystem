'use strict';

App.service('EmpService', ['$http', function(http){

//	GET - gets Employee Details
	this.getEmpdetails = function(id){
		return http.get('restemp/'+id);						
	};
	
	
//	POST - Add new Employee details
	this.addNewEmpdetails = function(empdetails){
		return http.post('restaddnewemp/', empdetails);
	};

//	POST - Add new Leave
	this.addNewLeave = function(newLeave){
		return http.post('restaddnewleave/', newLeave);
	}
	
//	GET - gets Employee Approved Leave
	this.getEmpApprovedLeaves = function(id){
		return http.get('restempleave/'+id)
	}
	
//	GET - gets Employee Leave for manager to Approve
	this.getLeaves = function(){
		return http.get('restmgrleave/');						
	};
	
//	POST - gets Employee Leave details
	this.getEmpLeaveDetails = function(empId, startDate){
		var empLeave = {"empId":empId,"startDate":startDate};
		return http.post('empleavedetails/',empLeave );
	};
	
//	POST - Delete employee Leave
	this.deleteEmpLeave = function(id, startDate){
		var empLeave = {"empId":id,"startDate":startDate};
		
//		return http({
//		    method: 'POST',
//		    url: 'restempleavedelete/',
//		    headers: {'Content-Type': 'application/json'},
//		    data: {"empId":id,"startDate":startDate}
//		});
		
		return http.post('restempleavedelete/',empLeave );

	};
	
//	PUT - Approve Employee Leave
	this.approveLeave = function(leaveNum){
		return http.put('approveleave/'+leaveNum);
	}
	
//	PUT - Disapprove Employee Leave
	this.disapproveLeave = function(leaveNum){
		return http.put('disapproveleave/'+leaveNum);
	}
	
//	PUT - Change Password
	this.changePassword = function(empLogin){
		return http.put('changepassword/',empLogin);
	};

	
}]);