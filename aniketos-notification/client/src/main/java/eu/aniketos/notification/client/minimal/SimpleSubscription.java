package eu.aniketos.notification.client.minimal;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

/**
 * A minimal helper class that handles subscriptions and listens to incoming messages. 
 * 
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 *
 */
public class SimpleSubscription implements MessageListener {


	/**
	 * Subscribe to only a specific notification type and description (e.g. a specific threat) for a specific topic.
	 * 
	 * @param serviceId Marketplace ID for the subscribed service
	 * @param importanceThreshold Only subscribe to alerts with importance above given threshold
	 * @param jmsSession An already created JMS session
	 * @throws JMSException
	 */
	public SimpleSubscription(String serviceId, int importanceThreshold, Session jmsSession) throws JMSException {

		// This constructor does not use subfiltering on alertDesc below
		// alertType
		this(serviceId, ">", importanceThreshold, jmsSession);
	}

	
	/**
	 * Subscribe to only a specific notification type and description (e.g. a specific threat) for a specific topic.
	 * 
	 * @param serviceId Marketplace ID for the subscribed service
	 * @param alertType Choose from eu.aniketos.notification.Notification
	 * @param importanceThreshold Only subscribe to alerts with importance above given threshold
	 * @param jmsSession An already created JMS session
	 * @throws JMSException
	 */
	public SimpleSubscription(String serviceId, String alertType, int importanceThreshold,
			Session jmsSession) throws JMSException {

		// This constructor does not use sub-filtering on alertDesc below alertType
		this(serviceId, alertType, ">", importanceThreshold, jmsSession);
	}
	

	/**
	 * Creates a new JMS subscription using a consumer for the topic. 
	 * 
	 * @param serviceId Marketplace ID for the subscribed service
	 * @param alertType Type of alert (found in notification.Notification)
	 * @param description More specific on type (from notification.descriptions.*)
	 * @param importanceThreshold A threshold above which messages should be sent (default=0)
	 * @param jmsSession
	 * @throws JMSException
	 */
	public SimpleSubscription(String serviceId, String alertType,
			String description, int importanceThreshold, Session jmsSession) throws JMSException {

		// Setting up the given topic (for subscription) - and the supplied
		// type(s) of alerts

		String topicId = "pub." + serviceId + "." + alertType;
		if (!alertType.equals(">"))
			 topicId += "." + description;
		Topic topic = jmsSession.createTopic(topicId);

		// Object that receives messages from the topic (above given importance
		// threshold)
		MessageConsumer consumer = jmsSession.createConsumer(topic, "threshold>" + importanceThreshold);

		/*
		 * ALTERNATIVELY: A durable subscriber that can be restored if the
		 * connection is closed (receive lost messages):
		 */
		// consumer = session.createDurableSubscriber(topic, uniqueClientId,
		// "threshold>" + threshold, true);

		// Set this class for listening for and handling incoming messages
		consumer.setMessageListener(this);
	}

	
	/**
	 * Implementation of MessageListener. Receives any incoming messages.
	 * @param message The message object received
	 */
	public void onMessage(Message message) {

		try {
			String messageId = message.getJMSMessageID();
			String serviceId = message.getStringProperty("serviceId");
			String alertType = message.getStringProperty("alertType");
			String value = message.getStringProperty("value");
			String threatId = message.getStringProperty("threatId");
			String description = message.getStringProperty("description");
			String serverTime = message.getStringProperty("serverTime");
			int importance = message.getIntProperty("importance");

			// Create Notification object from components.notification.interface if needed
			/**
			 * This is where the message is used for whatever it needs to be used for
			 */
			// For now, just print it to the console..
			System.out.println("[Client : Message received] " + serviceId + " "
					+ ": alertType=" + alertType 
					+ "; value=" + value
					+ "; threatId=" + threatId
					+ "; description='" + description + "'" 
					+ "; importance=" + importance
					+ "; serverTime=" + serverTime
					+ "; messageId=" + messageId);
			// Handle the message..

		} catch (JMSException e) {
			System.out.println(e.getMessage());
		}
	}
}