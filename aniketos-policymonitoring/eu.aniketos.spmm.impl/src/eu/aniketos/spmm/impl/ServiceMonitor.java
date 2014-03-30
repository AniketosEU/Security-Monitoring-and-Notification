package eu.aniketos.spmm.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import security.iit.pdp.PDP;
import security.iit.pdp.pdpResult;
import security.iit.pdp.PDP.WHEN;
import eu.aniketos.data.IAgreementTemplate;
import eu.aniketos.notification.IAlert;
import eu.aniketos.notification.Notification;
import eu.aniketos.pdplib.SpecificationPDP;
import eu.aniketos.spmm.event.ReceiveEvent;

public class ServiceMonitor {
	
	
	private IAlert alertService;
	private final List<SpecificationPDP> specContract = new LinkedList<SpecificationPDP>();
	private final List<PDP> pdp_instance = new LinkedList<PDP>();
	private String serviceId;
	private IAgreementTemplate AT;

	
	public ServiceMonitor(IAgreementTemplate AT, String ServiceID){
		
		
		serviceId=ServiceID;
		this.AT = AT;
		System.out.println("AT.getXmlContents().length:"+AT.getXmlContents().length);
		
	}
	
	
	void monitorPolicy(ReceiveEvent event){
		
		System.out.println(" ");
		
		if(event.getEventType().equals("processEvent") && event.getEventName().equals("start"))
		{
			
			System.out.println("                                 ----------------------------------------------------------- ");
			System.out.println("                                 ----------------------------------------------------------- ");
			System.out.println("                                 ----------------------------------------------------------- ");
		    System.out.println("                                   "+event.getEventName()+" of Process name =  "+event.getProcessInstanceId());
		    System.out.println("                                 ----------------------------------------------------------- ");
		    System.out.println("                                 ----------------------------------------------------------- ");
		    System.out.println("                                 ----------------------------------------------------------- ");
		   
		    
		    specContract.clear();
			pdp_instance.clear();
			
			
			for (int j = 0; j < AT.getXmlContents().length; j++) {	
				
				try {
					SpecificationPDP contract = new SpecificationPDP();
					
					// These two lines may raise exceptions due to null pointer errors or
					// invalid contract definitions.  If this occurs, no contract specification
					// or PDP instance will be added to the vectors.
					contract.load(AT.getXmlContents()[j].getBytes());
					PDP instance = new PDP(contract);
					
					// If we reach this line, we know that both the contract and PDP instance
					// were constructed successfully.
					specContract.add(contract);
					pdp_instance.add(instance);
				} catch (Exception e) {
					System.out.println("Could not load contract or create PDP instance, because:");
					e.printStackTrace();
				}
			}
			System.out.println("\nLoaded " + specContract.size() + " contract specifications");
		    
		    
		}
		
		if(event.getEventType().equals("processEvent") && event.getEventName().equals("end"))
		{
			
			System.out.println("----------------------------------------------------------- ");
		    System.out.println("  "+event.getEventName()+" of Process name =  "+event.getProcessInstanceId());
		    System.out.println("----------------------------------------------------------- ");
		
		}
		
		if(!event.getEventType().equals("processEvent"))
		{
			System.out.println("Params assigned");
		    String id=event.getServiceType();
		    String type=event.getEventType();
		    String time=event.getEventTime();
		    String date=event.getEventDate();
		    String provider=event.getServiceProvider();
		    String output="test";
		    
		    //wrap parameters in Object[] to pass them to PDP
		    Object[] params = new Object[]{id, type,time,date, provider,output};
		    pdpCheck(params);
		}

}		
	
	private void pdpCheck (Object[] params){
		try {
			//parameters of function OutputStream.write
			System.out.println("----------------------------------------------------------- ");
		    System.out.println("                           PDP Decision  ");
		    System.out.println("----------------------------------------------------------- ");
		   
		    System.out.println("Applying " + specContract.size() + " contract specifications");
		    if (specContract == null) {
		    	System.out.println("specContract has been set to null, or was never set");
		    }
		    for (int i = 0; i < specContract.size(); i++) {
		    	
		    	System.out.println("\n\nApplying contract specification #" + i);
		    	
		    	SpecificationPDP contract = specContract.get(i);
		    	PDP instance = pdp_instance.get(i);
		    	pdpResult.setServiceId(serviceId);
		    	pdpResult.setRuleId(contract.getAttribute("id"));
		    	
		    
		    // Event handlers
		        if (instance.PDP_allow("process.start", WHEN.BEFORE, params, null)) {
		    		System.out.println("Rule \"" + contract.getAttribute("id") + "\" Permitted");
		    		// This action is permitted.
		    	} else {
		    		// This action is not permitted, do something?
		    		System.out.println("Rule \"" + contract.getAttribute("id") + "\" Denied.");
		    	}
		    	

		        if (instance.PDP_allow("process.end", WHEN.BEFORE, params, null)) {
		    		System.out.println("Rule \"" + contract.getAttribute("id") + "\" Permitted");
		    		// This action is permitted.
		    	} else {
		    		// This action is not permitted, do something?
		    		System.out.println("Rule \"" + contract.getAttribute("id") + "\" Denied.");
		    	}
		    	
		        if (instance.PDP_allow("activity.start", WHEN.BEFORE, params, null)) {
		    		System.out.println("Rule \"" + contract.getAttribute("id") + "\" Permitted");
		    		// This action is permitted.
		    	} else {
		    		// This action is not permitted, do something?
		    		System.out.println("Rule \"" + contract.getAttribute("id") + "\" Denied.");
		    	}
		        
		    	 if (instance.PDP_allow("activity.end", WHEN.BEFORE, params, null)) {
			    		System.out.println("Rule \"" + contract.getAttribute("id") + "\" Permitted");
			    		// This action is permitted.
			    	} else {
			    		// This action is not permitted, do something?
			    		System.out.println("Rule \"" + contract.getAttribute("id") + "\" Denied.");
			    	}
		    	
		    }
		    
		    System.out.println(" ");
		  
		} catch (Exception e) {
		e.printStackTrace();
		}
	}
	

}
