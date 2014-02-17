package eu.aniketos.notification;

/**
 * Only used on the client side for implementing JMS listener to the ActiveMQ message broker.
 * This interface however only represents a guideline for such implementation, and will not be
 * invoked as an API.
 * 
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 */
public interface INotificationClient {


	/**
	 * Registers a service to receive alerts of specified type above specified threshold 
	 * in publish/subscribe mode
	 * 
	 * @param serviceId Unique URL identifying the service
	 * @param alertType
	 * @param alertThreshold
	 */
	public void registerForAlert(String serviceId, String alertType, int alertThreshold);
	
	
	/**
	 * Registers a service to receive all alerts in publish/subscribe mode
	 * 
	 * @param serviceId Unique URL identifying the service
	 */
	public void registerForAlerts(String serviceId);
	
	
	/**
	 * Unregisters a service from the publish/subscribe service for an alert
	 *  
	 * @param serviceId Unique URL identifying the service
	 * @param alertType
	 */
	public void unRegisterForAlert(String serviceId, String alertType);
	

	/**
	 * Unregisters a service from the publish/subscribe service for an alert
	 *  
	 * @param serviceId Unique URL identifying the service
	 */
	public void unRegisterForAlerts(String serviceId);
}
