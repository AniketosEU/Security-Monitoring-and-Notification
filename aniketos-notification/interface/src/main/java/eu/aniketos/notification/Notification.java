package eu.aniketos.notification;

/**
 * A message object that is used for Aniketos notifications. 
 * Usually created by subscribers that receive a notification.
 * 
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 *
 */
public class Notification {
	

	/** Represents a service change **/
	public static final String SERVICE_CHANGE = "ServiceChange";
	
	/** Represents a context change **/
	public static final String CONTEXT_CHANGE = "ContextChange";
	
	/** Represents a threat level change **/
	public static final String THREAT_LEVEL_CHANGE = "ThreatLevelChange";
	
	/** Represents a security property change **/
	public static final String SECURITY_PROPERTY_CHANGE = "SecurityPropertyChange";
	
	/** Represents a trust level change **/
	public static final String TRUST_LEVEL_CHANGE = "TrustLevelChange";
	
	/** Represents a contract change **/
	public static final String CONTRACT_CHANGE = "ContractChange";

	/** Represents a contract violation **/
	public static final String CONTRACT_VIOLATION = "ContractViolation";
	
	
	/**
	 *  A value of monitored parameter
	 */
	private String alertValue; 
	
	/**
	 *  A description of the alert
	 */
	private String description;
	
	/**
	 * Types of alert to be sent to the Notification module:
	 * - ServiceChange
	 * - ContextChange
	 * - ThreatLevelChange
	 * - SecurityPropertyChange
	 * - TrustLevelChange
	 * - ContractChange
	 * - ContractViolation
	 */
	private String alertType;
	
	/**
	 * The threat id with which the notification is associated
	 */
	private String threatId;
	
	/**
	 *  Service that the alert concerns
	 */
	private String serviceId;
	
	/**
	 * The threshold above which the alert should be sent
	 */
	private int importance;
	
	/**
	 * The server time for receiving the alert
	 */
	private String serverTime;
	
	/**
	 * The threshold above which the alert should be sent
	 */
	private String messageId;
	
	
	/**
	 * Simple constructor for messages that do not have a unique message ID
	 */
	public Notification() {
		initObj();
	}
	
	
	/**
	 * Constructor used by the publisher and subscriber to store the unique message ID 
	 * already assigned to the JMS message created by the ActiveMQ message broker.
	 * 
	 * @param messageId
	 */
	public Notification(String messageId) {
		initObj();
		this.messageId = messageId;
	}

	
	/**
	 * Get the identifier of the service ID that the alert concerns
	 * @return A service ID
	 */
    public String getServiceId() {
		return serviceId;
	}
	
	/**
	 * Get the identifier of the service ID that the alert concerns
	 * @return A service ID
	 * @deprecated Replace with getServiceId()
	 */
    public String getService() {
		return getServiceId();
	}

    /**
     * Set the ID of the service which the notification relates to
     * @param serviceId The service URL
	 * @deprecated Replace with getServiceId()
     */
	public void setService(String serviceId) {
		setServiceId(serviceId);
	}


    /**
     * Set the ID of the service which the notification relates to
     * @param serviceId The service URL
     */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}


	/**
	 * Set the type for this notification
	 * @return One of this class' class constants
	 */
	public String getAlertType() {
		return alertType;
	}

	/**
	 * Set the type for this notification
	 * @param alertType Use one of this class' class constants
	 */
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	/**
	 * Get the value of the alert
	 * @return The alert value (string contents, not a standardized format)
	 */
	public String getValue() {
		return alertValue;
	}
	
	/**
	 * Get the value of the alert
	 * @return The alert value (string contents, not a standardized format)
	 * @deprecated Replace with getValue()
	 */
	public String getAlertValue() {
		return getValue();
	}

	/**
	 * Set the value of the alert
	 * @param alertValue A value that represents the alert's content beyond type and standardized description 
	 * @deprecated Replace with setValue()
	 */
	public void setAlertValue(String alertValue) {
		setValue(alertValue);
	}

	/**
	 * Set the value of the alert
	 * @param alertValue A value that represents the alert's content beyond type and standardized description 
	 */
	public void setValue(String alertValue) {
		this.alertValue = alertValue;
	}

	/**
	 * Get the description of the notification contents
	 * @param description A class constant from the appropriate class of the taxonomy package, corresponding to the alertType
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get the description of the notification contents
	 * @param description A class constant from the appropriate class of the taxonomy package, corresponding to the alertType
	 * @deprecated Replace with getDescription()
	 */
	public String getAlertDesc() {
		return getDescription();
	}

	/**
	 * Set the description of the notification contents
	 * @param description Use a class constant from the appropriate class of the taxonomy package, corresponding to the alertType
	 * @deprecated Replace with setDescription()
	 */
	public void setAlertDesc(String description) {
		setDescription(description);
	}

	/**
	 * Set the description of the notification contents
	 * @param description Use a class constant from the appropriate class of the taxonomy package, corresponding to the alertType
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the threatID associated with the message, as it is found in the SVRS.
	 * @return The threatID (UUID format), or null if not set
	 */
	public String getThreatId() {
        return threatId;
    }

	/**
	 * Get the threatID associated with the message, as it is found in the SVRS.
	 * @return The threatID (UUID format), or null if not set
	 * @deprecated Replace with getThreatId()
	 */
	public String getAlertThreatId() {
        return getThreatId();
    }

	/**
	 * Set a threat ID for the object
	 * @param alertThreatId A threat ID as it is found in the SVRS
	 * @deprecated Replace with setThreatId()
	 */
    public void setAlertThreatId(String alertThreatId) {
    	setThreatId(alertThreatId);
    }

	/**
	 * Set a threat ID for the object
	 * @param alertThreatId A threat ID as it is found in the SVRS
	 */
    public void setThreatId(String alertThreatId) {
        this.threatId = alertThreatId;
    }

    /**
     * Returns a value that indicates the importance of this notification.
     * @return An integer on an integer range where 1 is least important
     */
	public int getImportance() {
		return importance;
	}

    /**
     * Returns a value that indicates the importance of this notification.
     * @return An integer on an integer range where 1 is least important
     * @deprecated Replace with getImportance()
     */
	public int getAlertThreshold() {
		return getImportance();
	}

	/**
	 * Set the importance value of this notification.
	 * @param alertThreshold An integer >0 which represents the relative importance
     * @deprecated Replace with setImportance()
	 */
	public void setAlertThreshold(int alertThreshold) {
		setImportance(alertThreshold);
	}

	/**
	 * Set the importance value of this notification.
	 * @param importance An integer >0 which represents the relative importance
	 */
	public void setImportance(int importance) {
		this.importance = importance;
	}
	
	/**
	 * When used with the ActiveMQ message broker, a notification represents a specific topic.
	 * This method returns the topic ID in which the notification will be found 
	 * @return The topic ID which can be for subscribing to messages of this same category
	 */
	public String getTopicId() {
		String alertId = serviceId;
		
		if (alertType != null) {
			alertId += "." + alertType;
			
			if (description != null && !alertType.equals(">"))
				alertId += "." + description;
		}
		else {
			alertId = "pub";
		}
		return alertId;
	}

	
	/**
	 * Returns the time from the Notification server when it was received
	 * @return String containing the time (incl. milliseconds)
	 */
	public String getServerTime() {
		return serverTime;
	}

	/**
	 * Set the time from the Notification server when it was received
	 * @param The server time
	 */
	public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }
	
	/**
	 * Returns the unique JMS message ID assigned by the ActiveMQ message broker.
	 * @return The JMS message ID if set, null if empty
	 */
	public String getMessageId() {
		return messageId;
	}
	
	/**
	 * Set a unique JMS message ID as assigned by the ActiveMQ message broker.
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	
	/**
	 * Initialize the object fields.
	 */
	private void initObj() {
		alertValue = null;
		description = null;
		alertType = null;
		threatId = null;
		serviceId = null;
		importance = 1;
		messageId = null;		
	}
}