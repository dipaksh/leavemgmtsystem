package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bean.EmpDetails;
import com.bean.EmpLeave;
import com.bean.EmpLogin;
import com.bean.ManagerLeave;
import com.services.EmpDetailsService;

@RestController
public class EmpDetailsRestController {
	
	@Autowired
	EmpDetailsService empDetailsService;
	
//	GET - EmpDetails with empId
//	Gets employee details by id
	@RequestMapping(value="/restemp/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmpDetails> getEmpDetailsRest(@PathVariable("id")int empId){
		System.out.println("Retriving EmpDetails with id " + empId);
		
		EmpDetails emp = empDetailsService.getEmpDetails(empId); //Calls get Employee details service
		
		if(emp == null){
			System.out.println("EmpDetails Not Found");
			return new ResponseEntity<EmpDetails>(emp, HttpStatus.NOT_FOUND);
		}
		
		System.out.println("EmpDetails is Retrived");
		return new ResponseEntity<EmpDetails>(emp, HttpStatus.OK);
	}
	
//	GET - EmpLeaves with empId
//	Gets employee Leaves details by id
	@RequestMapping(value="/restempleave/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmpLeave>> getEmpLeavesRest(@PathVariable("id")int empId){
		System.out.println("Retriving Employee leaves with id " + empId);
		
		List<EmpLeave> emp = empDetailsService.getEmployeeLeaves(empId); //Calls Get Employee Leaves service
		
		if(emp == null){
			System.out.println("Employee leaves Not Found");
			return new ResponseEntity<List<EmpLeave>>(emp, HttpStatus.NOT_FOUND);
		}
		
		System.out.println("Employee leaves is Retrived");
		return new ResponseEntity<List<EmpLeave>>(emp, HttpStatus.OK);
	}
	
	
//	GET - Employee Leaves for  Manager to approve or disapprove
//	Gets employee Leaves for  Manager to approve or disapprove
	@RequestMapping(value="/restmgrleave/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ManagerLeave>> getManagerLeavesRest(){
		System.out.println("Retriving Employee leaves for  Manager to approve or disapprove");
		
		List<ManagerLeave> mgr = empDetailsService.getLeaves(); //Calls Get Employee Leaves for Manager service
		
		if(mgr == null){
			System.out.println("Employee leaves for  Manager to approve or disapprove Not Found");
			return new ResponseEntity<List<ManagerLeave>>(mgr, HttpStatus.NOT_FOUND);
		}
		
		System.out.println("Retrived Employee for  Manager to approve or disapprove");
		return new ResponseEntity<List<ManagerLeave>>(mgr, HttpStatus.OK);
	}
	
//	POST - EmpLeave details with empId and start date
//	Gets a Emp Leave request details with empId and start date
	@RequestMapping(value="/empleavedetails/", method = RequestMethod.POST)
	public ResponseEntity<EmpLeave> getEmpLeaveDetailsRest(@RequestBody EmpLeave el){
		
		int empId = el.getEmpId();
		String startDate = el.getStartDate();
		
		System.out.println("Retrieving Employee Leave with id " + empId + " and Start date "+startDate);
		EmpLeave empLeave = empDetailsService.getLeaveRequest(empId, startDate); //gets Employee Leave details
		System.out.println("Retrieved Employee Leave with id " + empId + " and Start date "+startDate);
		
		return new ResponseEntity<EmpLeave>(empLeave, HttpStatus.OK);
	}
	
	
//	POST - Delete EmpLeave with empId and start date
//	Deletes a Emp Leave record and Gets remaining employee Leaves details by id
	@RequestMapping(value="/restempleavedelete/", method = RequestMethod.POST)
	public ResponseEntity<List<EmpLeave>> deleteAndGetEmpLeavesRest(@RequestBody EmpLeave el){
		
		int empId = el.getEmpId();
		String startDate = el.getStartDate();
		
		System.out.println("Deleting Employee Leave with id " + empId + " and Start date "+startDate);
		empDetailsService.deleteLeaveRequest(empId, startDate); //Calls Delete Employee Leave service
		System.out.println("Deleting Employee Leave with id " + empId + " and Start date "+startDate);
		
		
		
		System.out.println("Retriving Employee leaves with id " + empId);
		List<EmpLeave> emp = empDetailsService.getEmployeeLeaves(empId);
		
		if(emp == null){
			System.out.println("Employee leaves Not Found");
			return new ResponseEntity<List<EmpLeave>>(emp, HttpStatus.NOT_FOUND);
		}
		
		System.out.println("Employee leaves is Retrived");
		return new ResponseEntity<List<EmpLeave>>(emp, HttpStatus.OK);
	}
	
	
//	POST - Add New Employee
//	Adds new Employee
	@RequestMapping(value="/restaddnewemp/", method = RequestMethod.POST)
	public ResponseEntity<Void> addNewEmployeeRest(@RequestBody EmpDetails emp){
		System.out.println("Adding Employee details");
		if(emp == null){
			System.out.println("Employee Details not provieded properly from frontend");
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		empDetailsService.addEmployee(emp); //Calls Add Employee service
		
		System.out.println("Added Employee details");
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

//	POST - Add new Leave
//	Adds new Leave
	@RequestMapping(value="/restaddnewleave/", method = RequestMethod.POST)
	public ResponseEntity<Void> addNewLeaveRest(@RequestBody EmpLeave empLeave){
		System.out.println("Adding new Leave request");
		if(empLeave == null){
			System.out.println("Leave Request Details not provieded properly from frontend");
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		
		empDetailsService.addLeave(empLeave);  //Calls Add Leave service
		
		System.out.println("Added Leave Request");
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	
//	PUT - Employee Leave status for  Manager to approve
//	updates employee Leave status for  Manager to approve
	@RequestMapping(value="/approveleave/{leaveNum}", method = RequestMethod.PUT)
	public ResponseEntity<List<ManagerLeave>> putApproveStatusRest(@PathVariable("leaveNum")int leaveNum){
		empDetailsService.setApproved(leaveNum); // Updates Employee leave status
		
		System.out.println("Retriving Remainng Employee leaves for  Manager to approve or disapprove");
		
		List<ManagerLeave> mgr = empDetailsService.getLeaves(); //Calls Get Employee Leaves for Manager service
		
		System.out.println("Retrived Employee for  Manager to approve or disapprove");
		return new ResponseEntity<List<ManagerLeave>>(mgr, HttpStatus.OK);
	}
	
//	PUT - Employee Leave status for  Manager to disapprove
//	updates employee Leave status for  Manager to disapprove
	@RequestMapping(value="/disapproveleave/{leaveNum}", method = RequestMethod.PUT)
	public ResponseEntity<List<ManagerLeave>> putDisApproveStatusRest(@PathVariable("leaveNum")int leaveNum){
		empDetailsService.setDisApproved(leaveNum); // Updates Employee leave status
		
		System.out.println("Retriving Remainng Employee leaves for  Manager to approve or disapprove");
		
		List<ManagerLeave> mgr = empDetailsService.getLeaves(); //Calls Get Employee Leaves for Manager service
		
		System.out.println("Retrived Employee for  Manager to approve or disapprove");
		return new ResponseEntity<List<ManagerLeave>>(mgr, HttpStatus.OK);
	}
	
//	PUT - Change Employee Password
//	updates employee Leave status for  Manager to approve
	@RequestMapping(value="/changepassword/", method = RequestMethod.PUT)
	public ResponseEntity<Void> changePasswordRest(@RequestBody EmpLogin empLogin){
		
		empDetailsService.changePassword(empLogin); //Calls Change Password Service
		
		System.out.println("Retrived Employee for  Manager to approve or disapprove");
		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	
}