package eu.aniketos.notification.client;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import eu.aniketos.notification.Notification;

/**
 * Receives an alert from some Aniketos component/service and allows any evaluation and reasoning to be done, 
 * before passing it on to the message broker where it is published to its subscribing entities.
 * 
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 */
public class Subscription implements MessageListener, ExceptionListener {
	
	/** A name of the topic this subscription represents **/
	private String topicName;
	/** Alert type filter for the subscription (optional) **/
	private String alertType;
	/** Importance threshold for messages (optional) **/
	private int threshold;
	
	/** A JMS message consumer instance **/
	private MessageConsumer consumer;
	/** Indicates whether or not the subscription is active **/
	private boolean isActive;
	
	/** The JMS session instance **/
	private Session session;

	
	/**
	 * Subscribe to only a specific notification type for a specific topic.
	 * 
	 * @param serviceId Marketplace ID for the subscribed service
	 * @param alertType Choose from eu.aniketos.notification.Notification
	 * @param importanceThreshold Only subscribe to alerts with importance above given threshold
	 * @param jmsSession An already created JMS session
	 */
	public Subscription(String serviceId, String alertType, int importanceThreshold, Session jmsSession) {
		this(serviceId, alertType, ">", importanceThreshold, jmsSession);
	}
	
	
	/**
	 * Subscribe to only a specific notification type and description (e.g. a specific threat) for a specific topic.
	 * 
	 * @param serviceId Service ID (URL) for the subscribed service
	 * @param alertType Choose from eu.aniketos.notification.Notification
	 * @param description Choose from eu.aniketos.notification.descriptions.*
	 * @param importanceThreshold Only subscribe to alerts with importance above given threshold
	 * @param jmsSession An already created JMS session
	 */
	public Subscription(String serviceId, String alertType, String description, int importanceThreshold, Session jmsSession) {
		this.topicName = serviceId;
		this.alertType = alertType;
		this.threshold = importanceThreshold;
		
		this.session = jmsSession;
		
		try {

			String topicId = "pub." + serviceId + "." + alertType;
			if (!alertType.equals(">"))
				 topicId += "." + description;
			// Setting up the given topic - and the given types of alerts
			Topic topic = session.createTopic(topicId);
			
			// MessageConsumer is used for receiving (consuming) messages from the topic (above given threshold)
			consumer = session.createConsumer(topic, "threshold>" + this.threshold);
			
			// Durable subscribers will survive in the broker if the connection is closed
//			consumer = session.createDurableSubscriber(topic, clientId, "alertThreshold>" + threshold, true);
			
			consumer.setMessageListener(this);
			isActive = true;
			
		} catch (Exception e) {
			isActive = false;
		}
	}
	
	
	/**
	 * Implementation of the MessageListener interface
	 * @param message The message object received
	 */
	public void onMessage(Message message) {
		//System.out.println("THIS IS WORKING RIGHT!");
		
		try {
			// Check if the Aniketos "proprietory" message property "alertType" is present in the message
			if (message.propertyExists("alertType")) {
				
//				Notification notif = new Notification(message.getJMSMessageID());
//				notif.setServiceId(message.getStringProperty("serviceId"));
//				notif.setAlertType(message.getStringProperty("alertType"));
//				notif.setValue(message.getStringProperty("value"));
//				notif.setDescription(message.getStringProperty("description"));
//				notif.setThreatId(message.getStringProperty("threatId"));
//				notif.setImportance(message.getIntProperty("importance"));
//				notif.setServerTime(message.getStringProperty("serverTime"));
				
				
				/**
				 * This is where the message is used for whatever it needs to be used for
				 */
				// For now, just print it to the console..
				System.out.println("[Client : Message received] " + message.getStringProperty("serviceId") + " "
						+ ": alertType=" + message.getStringProperty("alertType") 
						+ "; value=" + message.getStringProperty("value")
						+ "; description='" + message.getStringProperty("description") + "'" 
						+ "; threatId=" + message.getStringProperty("threatId")
						+ "; importance=" + message.getIntProperty("importance")
						+ "; serverTime=" + message.getStringProperty("serverTime")
						+ "; messageId=" + message.getJMSMessageID());
			}
			else
				throw new JMSException("[Client : Unknown message] JMSMessageID=" + message.getJMSMessageID());
			
		} catch (JMSException e) {
			System.out.println(e.getMessage());
		}
	}

	
	
	/**
	 * Unsubscribe from the current topic
	 */
	public void unsubscribe() {
		try {
			isActive = false;
			session.unsubscribe(topicName + "." + alertType);
			session.close();
			System.out.println("[Client : Unsubscribe] Removed subscription: " + topicName + "." + alertType);
		} catch(Exception e) { 
			System.out.println("[Client : Unsubscribe] No durable subscription exists for: " + topicName + "." + alertType);
		} finally {
			try {
				consumer.close();
                session.close();
			} catch (Exception ignore) { }
		}
	}
	
	
	/**
	 * Implementation of the ExceptionListener interface.
	 */
	public void onException(JMSException exception) {
		System.out.println("[JMS Exception] " + exception.getMessage());
	}
	

	/**
	 * Check if the subscription is active and running.
	 * @return True if active, false if otherwise
	 */
	public boolean isActive() {
		return isActive;
	}
	
	
	/**
	 * Get the name of the subscribed service.
	 * @return The JMS subscription string
	 */
	public String getService() {
		return this.topicName;
	}

	
	/**
	 * Get the alert type.
	 * @return The alert type string constant set for the subscription
	 */
	public String getAlertType() {
		return this.alertType;
	}
}