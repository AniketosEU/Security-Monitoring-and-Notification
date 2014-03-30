package eu.aniketos.spmm.impl;



import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import security.iit.pdp.PDP;
import security.iit.pdp.PDP.WHEN;
import security.iit.pdp.pdpResult;
import eu.aniketos.data.*;
import eu.aniketos.data.impl.*;
import eu.aniketos.notification.IAlert;
import eu.aniketos.notification.Notification;
import eu.aniketos.notification.descriptions.ContractViolation;
import eu.aniketos.pdplib.InvocationPDP;
import eu.aniketos.pdplib.SpecificationPDP;
import eu.aniketos.spmm.IPropertyMonitoringInterface;
import eu.aniketos.spmm.event.ReceiveEvent;



/**
 *  This class is responsible for getting contract, extraction of monitoring rules, getting real data, check of the real data against the monitoring rules, 
 *  and notifying Notificaiton module about contract (policy) violations.
 *  
 *   @author Muhammad Asim, LJMU
 */

public class ContractMonitoring implements IPropertyMonitoringInterface{
	
	
	private static final String String = null;
	private IAlert alertService;
	//private final List<SpecificationPDP> specContract = new LinkedList<SpecificationPDP>();
	//private final List<PDP> pdp_instance = new LinkedList<PDP>();
	//private String serviceId;
	//private IAgreementTemplate AT;
	
	Map<String, ServiceMonitor> serviceMap = new Hashtable<String, ServiceMonitor>();
	
    //private String eventServiceId;

	public void monitorSecurityPolicy(IAgreementTemplate agTemp, String serviceID) {
	
		if(serviceMap.containsKey(serviceID)){
			System.out.println("Service already exists!");
			serviceMap.remove(serviceID);
			ServiceMonitor sm = new ServiceMonitor(agTemp, serviceID);
			serviceMap.put(serviceID, sm);
			System.out.println("Overridden!");
			
			
		}else{
			ServiceMonitor sm = new ServiceMonitor(agTemp, serviceID);
			serviceMap.put(serviceID, sm);
			System.out.println("Service added to the list!");
			

			
	
		}
	
	}

	
	public void IMonitoringData(ReceiveEvent event) {
		
	
		
		System.out.println("\n\n\n\n-----------------------Event recieved---------------"+event.getEventType()+"-------------------");
		
		String eventServiceId = event.getProcessInstanceId();
		
		//Process Event
		if(event.getEventType().equals("processEvent"))
		{
			System.out.println("Event recieved: processInstanceID="+event.getProcessInstanceId()+", eventType="+event.getEventType()+", eventName="+event.getEventName()+",eventTime="+event.getEventTime());
		}
		
		//UserTask event
		if(event.getEventType().equals("userTaskEvent"))
		{
			System.out.println("Event recieved: processInstanceID="+event.getProcessInstanceId()+", eventType="+event.getEventType()+",serviceId="+event.getServiceId()+", eventName="+event.getEventName()+", eventDate="+event.getEventDate()+
					", eventTime="+event.getEventTime()+", Assignee="+event.getAssignee());
		}
		//ServiceTask event
		if(event.getEventType().equals("serviceTaskEvent"))
		{
			System.out.println("Event recieved: processID="+event.getProcessInstanceId()+", eventType="+event.getEventType()+", eventName="+event.getEventName()+	
					", eventTime="+event.getEventTime()+", serviceType="+event.getServiceType()+", service Id="+event.getServiceId()+", serviceOperation="+event.getServiceOperation()+", serviceProvider="+event.getServiceProvider());
		}
		
		System.out.println(" ");
		
		ServiceMonitor monitor = serviceMap.get(eventServiceId);
		if(monitor != null){
		  System.out.println("event passed to the monitor policy method!");
			monitor.monitorPolicy(event);
		}else{
			
			 System.out.println("event NULL!");
		}
	  
	}
	
	


	public void sendContractViolationNotification(String serviceID,String ruleid) throws InterruptedException{
		
		String property;
		property=ruleid.toLowerCase();
		
		alertService = (IAlert) Activator.getDefault().getAlert();
		if (alertService == null) {
            System.out.println("Alert Service is null");
            
        }
		
		 int intIndexSOD = property.indexOf("sod");
         if(intIndexSOD == - 1){
          
       }else{
    	   System.out.println("seperation-of-duty property violated");
    	   alertService.alert("https://www.chrispay.com/api/pay", Notification.CONTRACT_VIOLATION, ContractViolation.SEPARATION_OF_DUTY);
       }
		
         
         int intIndexBOD = property.indexOf("bod");
         if(intIndexBOD == - 1){
          
       }else{
    	   System.out.println("binding-of-duty property violated");
    	   alertService.alert("https://www.chrispay.com/api/pay", Notification.CONTRACT_VIOLATION, ContractViolation.BINDING_OF_DUTY);
       }
		
         int intIndexConf = property.indexOf("onf");
         if(intIndexConf == - 1){
          
       }else{
    	   System.out.println("\n--------------------------------------------------------");
    	   System.out.println(" Confidentiality Property Violated. Notification generated");
    	   System.out.println("Service ID: "+serviceID);
    	   System.out.println("Rule ID: "+ruleid);
    	   System.out.println("--------------------------------------------------------\n");
    	   
    	   alertService.alert("https://www.chrispay.com/api/pay", Notification.CONTRACT_VIOLATION, ContractViolation.CONFIDENTIALITY);
       }
		
		
	}


	@Override
	public void unmonitorSecurityPolicy(java.lang.String serviceID) {
		serviceMap.remove(serviceID);
	}
	
	
}


