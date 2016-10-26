package com.services;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bean.EmpDetails;
import com.bean.EmpLeave;
import com.bean.EmpLogin;
import com.bean.ManagerLeave;
import com.bean.User_Roles;
import com.codeEncryption.PasswordEncoderGenerator;
import com.services.calculation.CalLeaves;
import com.services.calculation.CalPlannedLeaves;



@Service("empDetailsService")
@Transactional
public class EmpDetailsServiceImpl implements EmpDetailsService {

	@Autowired
	SessionFactory sf;
	Session s;
	Transaction t;

	public EmpDetailsServiceImpl() {
	}

	@Override
	public EmpDetails getEmpDetails(int empId) {
		s = sf.openSession();
		t = s.beginTransaction();

		System.out.println("Current id code is " + empId);
		Query q = s.createQuery("FROM EmpDetails  where empid=?");

		q.setInteger(0, empId);

		@SuppressWarnings("unchecked")
		List<EmpDetails> e = q.list();
		EmpDetails empdetails = null;

		for (EmpDetails emp : e) {

			empdetails = emp;
		}

		s.close();
		return empdetails;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setApproved(int leaveNum) 
	{
		System.out.println("entering in set approve");
		s = sf.openSession();
		t = s.beginTransaction();
		Query q=s.createQuery("update ManagerLeave m set m.status=:n where m.leaveNum=:i");  
		q.setParameter("n","approved");  
		q.setParameter("i",leaveNum);  
		int status=q.executeUpdate();  
		
		System.out.println("fetching data from manaerleave");
		Query q1 = s.createQuery("FROM ManagerLeave m where m.leaveNum=:i");
		q1.setParameter("i",leaveNum);
		List<ManagerLeave> e = q1.list();
		String start=null;
		int id=0;
		System.out.println("*********************************************************************");
		for (ManagerLeave managerLeave : e) 
		{
			System.out.println("in for loop");
			start=managerLeave.getStartdate();
			id=managerLeave.getEmpId();
		}			
		System.out.println("*******************value of startdate and empid is + "+start+" and "+id);		
		
		Query q2=s.createQuery("update EmpLeave e set e.status=:n where e.empId=:i and e.startDate=:x");  
		q2.setParameter("n","approved");  
		q2.setParameter("i",id);
		q2.setParameter("x",start);
		 status=q2.executeUpdate(); 
		
		
		System.out.println("employee status has been changed to approved"+status);  
		t.commit();  
		
		s.close();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setDisApproved(int leaveNum) 
	{
		s = sf.openSession();
		t = s.beginTransaction();
		Query q=s.createQuery("update ManagerLeave set status=:n where leavenum=:i");  
		q.setParameter("n","dis-approved");  
		q.setParameter("i",leaveNum);  
		int status=q.executeUpdate();  
		
		Query q1 = s.createQuery("FROM ManagerLeave where leavenum=:i");
		q1.setParameter("i",leaveNum);
		List<ManagerLeave> e = q1.list();
		String start=null;
		int id=0;
		for (ManagerLeave managerLeave : e) 
		{
			start=managerLeave.getStartdate();
			id=managerLeave.getEmpId();
		}			
		System.out.println(start+" and "+id);		
		
		Query q2=s.createQuery("update EmpLeave e set e.status=:n where e.empId=:i and e.startDate=:x");  
		q2.setParameter("n","dis-approved");  
		q2.setParameter("i",id);
		q2.setParameter("x",start);
		 status=q2.executeUpdate(); 
		
		
		System.out.println("employee status has been changed to dis-approved"+status);  
		t.commit();
		
		s.close();
	}
	
	@Override
	public List<EmpLeave> getEmployeeLeaves(int empId) 
	{
		s = sf.openSession();
		t = s.beginTransaction();

		System.out.println("fetching the leaves of employee" + empId);
		Query q = s.createQuery("FROM EmpLeave where empid=?");

		q.setInteger(0, empId);

		@SuppressWarnings("unchecked")
		List<EmpLeave> e = q.list();
		System.out.println("successfully return the list of employees leave");
		s.close();
		return e;
	}
	
	
	@Override
	public void addEmployee(EmpDetails newObj) 
	{
		s = sf.openSession();
		t = s.beginTransaction();
		
		EmpLogin elogin=new EmpLogin();
		
		elogin.setEmpId(newObj.getEmpId());
		elogin.setPassword("$2a$10$WjaAeuZOndUM1G3Av6Lvkec29dgocnHvL..IjkSCCCIqrX2qPvnqK");		
		elogin.setEnabled(true);		
		s.save(elogin);
		System.out.println("addedd login");
		User_Roles roles = new User_Roles();
		roles.setEmpId(newObj.getEmpId());
		roles.setRole("ROLE_USER");
		s.save(roles);   //here it may give prob just cz of role_id which is auto increment in database
		System.out.println("addedd role user");
		
		EmpDetails ed=new EmpDetails();
		
		ed.setEmpId(newObj.getEmpId());
		ed.setEmpMail(newObj.getEmpMail());
		ed.setEmpname(newObj.getEmpMail());
		ed.setDateOfJoining(newObj.getDateOfJoining());
		String startDate=newObj.getDateOfJoining();
		
		System.out.println("code called of calculating planned leaves for date "+startDate);
		double leaves=new CalPlannedLeaves().calPlannedLeaves(startDate);
		ed.setPlannedLeaves(leaves);
		System.out.println("***************************planned leaves are "+leaves);
		
		ed.setUnPlannedLeaves(4.0);
		ed.setGender(newObj.getGender());
		ed.setBloodGrp(newObj.getBloodGrp());
		s.save(ed);
		System.out.println("adding new employee with empid " +newObj.getEmpId());	
		System.out.println("employee successfully addedd");
		t.commit();
		s.close();	
	}
	@SuppressWarnings("unchecked")
	@Override    // adding a leave for employee in database by checking his type of leave
 	public void addLeave(EmpLeave leave)
	{
		s = sf.openSession();
		t = s.beginTransaction();
		System.out.println("type of leave is "+leave.getTypeOfLeave());
		EmpLeave eleave=new EmpLeave();
		eleave.setEmpId(leave.getEmpId());
		eleave.setPhone(leave.getPhone());
		eleave.setStatus("pending");
		s = sf.openSession();
		t = s.beginTransaction();

		//fetching the planned leaves of current employeees
		System.out.println("Current id code is " + leave.getEmpId());
		Query q = s.createQuery("FROM EmpDetails  where empid=?");
		q.setInteger(0, leave.getEmpId());
		double l=0.00;
		double u=0.00;
		List<EmpDetails> e = q.list();	
		for (EmpDetails emp : e) 
		{
			l=emp.getPlannedLeaves();
			u=emp.getUnPlannedLeaves();
			System.out.println("*******************plananed leaves are "+l);
			System.out.println("************un plananed leaves are "+u);
			
		}	
		
		CalLeaves cl=new CalLeaves();
		int leaves = cl.LeavedaysCounter(leave.getStartDate(),leave.getEndDate());
		String typeOfLeave=leave.getTypeOfLeave();
		
		
		
		if(typeOfLeave.equalsIgnoreCase("pl"))
		{
			if(l==leaves || l>=leaves)
			{
				l=l-leaves;	
				eleave.setFirstHalf(leaves);
				eleave.setSecondHalf(leaves);
				eleave.setEndDate(leave.getEndDate());
				eleave.setStartDate(leave.getStartDate());
				eleave.setTypeOfLeave("Planned Leaves");
				 q = s.createQuery("update EmpDetails e set e.plannedLeaves=:x where e.empId=:y");
				q.setParameter("x",l);
				q.setParameter("y",leave.getEmpId());
				q.executeUpdate();
				System.out.println("planned leaves successfully updated");
				System.out.println("in planned leaves");
			}
			else
			{
				System.out.println("applied leaves are more than your saving leaves");
			}
			
			eleave.setReason(leave.getReason());
		}
		else if(typeOfLeave.equalsIgnoreCase("upl"))
		{
			if(u==leaves || u>=leaves)
			{
				u=u-leaves;	
				eleave.setFirstHalf(leaves);
				eleave.setSecondHalf(leaves);
				eleave.setEndDate(leave.getEndDate());
				eleave.setStartDate(leave.getStartDate());
				eleave.setTypeOfLeave("Un-Planned Leaves");
				 q = s.createQuery("update EmpDetails e set e.unPlannedLeaves=:x where e.empId=:y");
					q.setParameter("x",u);
					q.setParameter("y",leave.getEmpId());
					q.executeUpdate();
					System.out.println("planned leaves successfully updated");
					
				System.out.println("in unplanned leaves");
			}
			else
			{
				System.out.println("applied leaves are more than your saving leaves");
			}
			eleave.setReason(leave.getReason());
		}
		
		else if(typeOfLeave.equalsIgnoreCase("lwp"))
		{
			eleave.setFirstHalf(leaves);
			eleave.setSecondHalf(leaves);
			eleave.setReason(leave.getReason());
			eleave.setStartDate(leave.getStartDate());
			eleave.setEndDate(leave.getEndDate());
			eleave.setReason(leave.getReason());
			eleave.setTypeOfLeave("Leave Without Pay");
		}
		System.out.println("new leave details for employee" +eleave );
		ManagerLeave ml=new ManagerLeave();
		ml.setEmpId(eleave.getEmpId());
		ml.setStartdate(eleave.getStartDate());
		ml.setStatus("pending");
		
		s.save(ml);
		System.out.println("leaves has been updated to manager table for employees");
		s.save(eleave);
		t.commit();
		System.out.println("employee leave has been addedd successfully");
		s.close();
		
			
	}

	@Override  //returning the leaves calculation between two dates
	public int calLeavesBetweenGivenDates(String start, String end) 
	{
		CalLeaves cl=new CalLeaves();
		int leaves = cl.LeavedaysCounter(start, end);
		
		return leaves;
	}
	
	 @Override   //only returning the plannedleaves which is going to show to manager when he add an employee
	public double calPlanedLeaves(String date) 
	 {
		 CalPlannedLeaves cl=new CalPlannedLeaves();
		 double plannedLeaves=cl.calPlannedLeaves(date);				 
		return plannedLeaves;
	}
	 @SuppressWarnings("unchecked")
	@Override
		public void deleteLeaveRequest(int empId,String start) 
		{
			s = sf.openSession();
			t = s.beginTransaction();
			
			System.out.println("fetching first half");
			Query query1 = s.createQuery("From EmpLeave e where e.empId = :x and e.startDate = :y");
			query1.setParameter("x",empId);
			query1.setParameter("y",start);
			List<EmpLeave> list=query1.list();
			double l=0.00;
			String status=null;
			String type=null;
			for (EmpLeave empLeave : list) 
			{
				l=empLeave.getFirstHalf();		
				status=empLeave.getStatus();
				type=empLeave.getTypeOfLeave();
			}		
			System.out.println("deleting from empleave table");
			Query query = s.createQuery("Delete from EmpLeave e where e.empId = :x and e.startDate = :y");
			query.setParameter("x",empId);
			query.setParameter("y",start);
			query.executeUpdate();
			System.out.println("successfully deleted");
			
			Query q = s.createQuery("From EmpDetails e where e.empId=:x");
			q.setParameter("x",empId);
			List<EmpDetails> l1=q.list();
			double orignalLeaves=0.00;
			double orignalUnPlanned=0.00;
			for (EmpDetails empDetails : l1) 
			{
				orignalLeaves=empDetails.getPlannedLeaves();
				orignalUnPlanned=empDetails.getUnPlannedLeaves();
			}
			System.out.println("********current status is "+status);
			
			if(type.equals("Planned Leaves") && status.equals("pending") || status.equals("dis-approved"))
			{
				Query q1 = s.createQuery("update EmpDetails e set e.plannedLeaves=:x where e.empId=:y");
				q1.setParameter("x",(orignalLeaves+l));
				q1.setParameter("y",empId);
				q1.executeUpdate();
				System.out.println("planned leaves successfully updated");
			}
			else if(type.equals("Un-Planned Leaves") && status.equals("pending") || status.equals("dis-approved"))
			{
				Query q1 = s.createQuery("update EmpDetails e set e.unPlannedLeaves=:x where e.empId=:y");
				q1.setParameter("x",(orignalUnPlanned+l));
				q1.setParameter("y",empId);
				q1.executeUpdate();
				System.out.println("un-planned leaves successfully updated");
				
			}
			t.commit();
			System.out.println("***************employee leave has been deleted successfully");
			s.close();
		}
	@SuppressWarnings("unchecked")
	@Override
	public List<ManagerLeave> getLeavesNotPending() 
	{
	s = sf.openSession();
	t = s.beginTransaction();

	System.out.println("fetching the leaves of employee to manager not pending");
	Query q = s.createQuery("FROM ManagerLeave m where m.status != :x");
	q.setParameter("x","pending");
	

	List<ManagerLeave> e = q.list();
	System.out.println("successfully retrieved the list of employees leave for manager non pendng");
	
	s.close();
	return e;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EmpLeave getLeaveRequest(int empId, String start) 
	{
		s = sf.openSession();
		t = s.beginTransaction();		
		EmpLeave el=null;
		Query q = s.createQuery("From EmpLeave e where e.empId = :x and e.startDate = :y");
		q.setParameter("x",empId);
		q.setParameter("y",start);
		List<EmpLeave> e = q.list();
		for (EmpLeave empLeave : e)
		{
			el=empLeave;
		}
		
		//t.commit();
		System.out.println("employee leave has been fetched successfully");
		
		s.close();
		return el;
	}

	@Override
	public void changePassword(EmpLogin elogin) 
	{
			String orignalPass=elogin.getPassword();
			PasswordEncoderGenerator p=new PasswordEncoderGenerator();
			String ep=p.getEncryptPass(orignalPass);
			System.out.println("encrtypted pass is "+ep);
			
			s = sf.openSession();
			t = s.beginTransaction();
			Query q=s.createQuery("update EmpLogin e set e.password=:n where e.empId=:i");  
			q.setParameter("n",ep);  
			q.setParameter("i",elogin.getEmpId());  
			int status=q.executeUpdate();  
			System.out.println("employee pass has been updated"+status);  
			
			t.commit(); 
			s.close();
			
	}

	@Override
	public List<ManagerLeave> getLeaves() {
		s = sf.openSession();
		t = s.beginTransaction();

		System.out.println("fetching the leaves of employee to manager");
		Query q = s.createQuery("FROM ManagerLeave m where m.status = :x");
		q.setParameter("x","pending");
		

		@SuppressWarnings("unchecked")
		List<ManagerLeave> e = q.list();
		System.out.println("successfully retrieved the list of employees leave for manager");
		
		s.close();
		return e;
	}
	
}