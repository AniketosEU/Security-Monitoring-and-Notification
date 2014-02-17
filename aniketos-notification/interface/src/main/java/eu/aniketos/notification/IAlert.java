package eu.aniketos.notification;


/**
 * Receives an alert from some Aniketos component/service and allows any evaluation and reasoning to be done, 
 * before passing it on to the message broker where it is published to its subscribing entities.
 * 
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 */
public interface IAlert {
     
	/**
	 * Receives an alert from some Aniketos component/service, and passes it on to the 
	 * notification message broker for evaluation and publishing to subscribing entities.
	 *  
	 * @param serviceId The identification of composed service in the Marketplace.
	 * @param alertType Types of alert to be sent to the Notification module (class constants of <i>Notification</i>)
	 * @param value Contains the value corresponding to the type parameter concerned.
	 */
	public void alert(String serviceId, String alertType, String value);

	/**
	 * Receives an alert from some Aniketos component/service, and passes it on to the 
	 * notification message broker for evaluation and publishing to subscribing entities.
	 *  
	 * @param serviceId The identification of composed service in the Marketplace.
	 * @param alertType Types of alert to be sent to the Notification module (class constants of <i>Notification</i>)
	 * @param value Contains the value corresponding to the type parameter concerned.
	 * @param description An alert description. Can be one of the constant fields in <i>AlertDescription</i>, however free text descriptions are allowed.
	 */
	public void alert(String serviceId, String alertType, String value, String description);

	/**
	 * Receives an alert from some Aniketos component/service, and passes it on to the 
	 * notification message broker for evaluation and publishing to subscribing entities.
	 *  
	 * @param serviceId The identification of composed service in the Marketplace.
	 * @param alertType Types of alert to be sent to the Notification module (class constants of <i>Notification</i>)
	 * @param value Contains the value corresponding to the type parameter concerned.
	 * @param description An alert description. Can be one of the constant fields in <i>AlertDescription</i>, however free text descriptions are allowed.
	 * @param threatId The threat identification with which the notification is associated, as it is registered in the Marketplace.
	 */
	public void alert(String serviceId, String alertType, String value, String description, String threatId);
	
}