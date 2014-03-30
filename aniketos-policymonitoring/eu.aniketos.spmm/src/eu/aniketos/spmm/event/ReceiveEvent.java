package eu.aniketos.spmm.event;


public class ReceiveEvent{
	

	// For correlating the events.
	
	private  String processInstanceId;
	
	private  String eventType;
	
	private  String serviceId;
	
	private  String eventName;

	private  String Assignee;
	
	private  String eventDate;
	
	private  String eventTime;
	
	private String serviceType;
	
	private String serviceInputType;
	
	private String serviceOutputType;
	
	private String resultVariable;
	
	private String serviceOperation;
	
	private String id;
	
	private String serviceProvider;
	
	private String serviceLocation;
	
      public ReceiveEvent() {

	}
	
	public ReceiveEvent(String processInstanceId, String eventType, String serviceId, String eventName,String Assignee, String eventDate,String eventTime,
			String serviceType, String serviceInputType, String serviceOutputType, String resultVariable, String serviceOperation, String id, String serviceProvider, String serviceLocation) {
		this.processInstanceId = processInstanceId;
		this.eventType = eventType;
		this.serviceId = serviceId;
		this.eventName = eventName;
		this.Assignee=Assignee;
		this.eventDate=eventDate;
		this.eventTime=eventTime;
		
		this.serviceType=serviceType;
		this.serviceInputType=serviceInputType;
		this.serviceOutputType=serviceOutputType;
		this.resultVariable=resultVariable;
		this.serviceOperation=serviceOperation;
		this.serviceProvider=serviceProvider;
		this.serviceLocation=serviceLocation;
	}
	
	//get methods

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	
	public String getEventType() {
		return eventType;
	}
	
	public String getServiceId() {
		return serviceId;
	}
	
	public String getEventName() {
		return eventName;
	}
	
	public String getAssignee() {
		return Assignee;
	}
	
	public String getEventDate() {
		return eventDate;
	}
	
	public String getEventTime() {
		return eventTime;
	}
	
	
	public String getServiceType() {
		return serviceType;
	}
	public String getServiceInputType() {
		return serviceInputType;
	}
	public String getServiceOutputType() {
		return serviceOutputType;
	}
	public String getResultVariable() {
		return resultVariable;
	}
	public String getServiceProvider() {
		return serviceProvider;
	}
	public String getServiceOperation() {
		return serviceOperation;
	}
	public String getId() {
		return id;
	}
	public String getServiceLocation() {
		return serviceLocation;
	}
	//set methods

		public void setProcessInstanceId(String processInstanceId) {
			this.processInstanceId = processInstanceId;
		}

		
		public void setEventType(String eventType) {
			this.eventType = eventType;
		}
		
		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}
		
		public void setEventName(String eventName) {
			this.eventName = eventName;
		}
		
		public void setAssignee(String Assignee) {
			this.Assignee=Assignee;
		}

		public void setEventDate(String eventDate) {
			this.eventDate=eventDate;
		}

		public void setEventTime(String eventTime) {
			this.eventTime=eventTime;
		}
		
		public void setServiceType(String serviceType) {
			this.serviceType=serviceType;
		}
		
		public void setServiceInputType(String serviceInputType) {
			this.serviceInputType=serviceInputType;
		}
		
		public void setServiceOutputType(String serviceOutputType) {
			this.serviceOutputType=serviceOutputType;
		}
		public void setResultVariable(String resultVariable) {
			this.resultVariable=resultVariable;
		}
		public void setServiceOperation(String serviceOperation) {
			this.serviceOperation=serviceOperation;
		}
		
		public void setServiceProvider(String serviceProvider) {
			this.serviceProvider=serviceProvider;
		}
		public void setServiceLocation(String serviceLocation) {
			this.serviceLocation=serviceLocation;
		}
		public void setId(String id) {
			this.id=id;
		}
		public String toString() {
			return "Event = = {processInstanceId="+processInstanceId+",EventType="+eventType+", serviceId="+serviceId+", eventName="+eventName+",Assignee="+Assignee+"," +
					"eventDate="+eventDate+",eventTime="+eventTime+", " +
					"serviceType="+serviceType+", serviceInputType="+serviceInputType+", serviceOutputType="+serviceInputType+", " +"resultVariable="+resultVariable+
							"serviceOutputType="+serviceOutputType+", serviceOperation="+serviceOperation+", serviceProvider="+serviceProvider+"id="+id+"}";
		}

		
	}

	

