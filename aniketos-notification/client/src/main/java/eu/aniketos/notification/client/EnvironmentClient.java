package eu.aniketos.notification.client;

import java.security.SecureRandom;
import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.activemq.ActiveMQSslConnectionFactory;

import eu.aniketos.notification.Notification;


/**
 * Reference implementation of client that can subscribe to and receive messages from
 * the Aniketos notification module. Extended version which deals with unavailability
 * of ActiveMQ instance.
 * 
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 */
public class EnvironmentClient implements Runnable {

	/** URL pointing to the message broker used **/
	private String brokerUrl;
	/** A client ID that identifies this client for the broker, in case connection goes down and messages lost during this should still be retrieved **/
	private String clientId;

	/** Indicates whether or not the message broker connection is OK **/
	private boolean connectionIsReady;
	/** JMS connection object **/
	private Connection connection;
	/** JMS session object **/
	private Session session;

	/** A list of subscriptions **/
	private ArrayList<Subscription> subscriptions;
	/** A list of pending subscriptions, not yet committed to the broker **/
	private ArrayList<Notification> pendingSubscriptions;

	
	
	/**
	 * Connects to the broker once, and then creates all subscription threads using the
	 * same connection session. 
	 * 62550784
	 * @param brokerUrl URL of the broker to connect with, prepended with transport protocol (default: failover://tcp://localhost:91919/)
	 * @param clientId An identifier for the client used by the subscription broker so that users can register with persistent subscriptions (if connection goes down, the broker will still try to deliver messages when the client comes back up). Use null if no client ID is to be set.
	 */
	public EnvironmentClient(String brokerUrl, String clientId) {
		this.brokerUrl = brokerUrl;
		this.clientId = clientId;
		subscriptions = new ArrayList<Subscription>();
		pendingSubscriptions = new ArrayList<Notification>();
		connectionIsReady = false;
	}


	/**
	 * Creates a new subscription object instance for subscribing to notifications.
	 * 
	 * @param serviceId Marketplace ID for the subscribed service
	 * @param alertType Type of alert (found in eu.aniketos.notification.Notification)
	 * @param description More specific on type (from notification.descriptions.*)
	 * @param importanceThreshold A threshold above which messages should be sent (default=0)
	 */
	public void registerForAlert(String serviceId, String alertType, String description, int importanceThreshold) {
		if (connectionIsReady) {
			Subscription sub = new Subscription(serviceId, alertType, description, importanceThreshold, session);
			subscriptions.add(sub);
		}
		else
			enqueueSubscription(serviceId, alertType, description, importanceThreshold);
	}


	/**
	 * Creates a new subscription object instance for subscribing to notifications.
	 * 
	 * @param serviceId Marketplace ID for the subscribed service
	 */
	public void registerForAlerts(String serviceId) {
		registerForAlert(serviceId, ">", ".");
	}

	/**
	 * Creates a new subscription object instance for subscribing to notifications.
	 * 
	 * @param serviceId Marketplace ID for the subscribed service
	 * @param alertType Type of alert (found in eu.aniketos.notification.Notification)
	 */
	public void registerForAlert(String serviceId, String alertType) {
		registerForAlert(serviceId, alertType, ">");
	}

	/**
	 * Creates a new subscription object instance for subscribing to notifications.
	 * 
	 * @param serviceId Marketplace ID for the subscribed service
	 * @param alertType Type of alert (found in eu.aniketos.notification.Notification)
	 * @param description More specific on type (from eu.aniketos.notification.descriptions.*)
	 */
	public void registerForAlert(String serviceId, String alertType, String description) {
		registerForAlert(serviceId, alertType, description, 0);
	}


	/**
	 * Remove a subscription
	 * @param serviceId The serviceID that should be unsubscribed to
	 */
	public void unRegisterForAlerts(String serviceId) {
		for (Subscription sub : subscriptions) {
			if (sub.getService().equals(serviceId)) {
				sub.unsubscribe();
				subscriptions.remove(sub);
			}
		}		
	}

	
	/**
	 * Enqueue a subscription for being registered with the message broker. Returns
	 * instantly after adding to the queue, i.e. not blocking the execution if message
	 * broker is unavailable or processing takes some time.
	 * 
	 * @param serviceId Marketplace ID for the subscribed service
	 * @param alertType Type of alert (found in eu.aniketos.notification.Notification)
	 * @param description More specific on type (from eu.aniketos.notification.descriptions.*)
	 * @param importanceThreshold A threshold above which messages should be sent (default=0)
	 */
	private void enqueueSubscription(String serviceId, String alertType, String description, int importanceThreshold) {
		Notification subscription = new Notification();
		subscription.setServiceId(serviceId);
		subscription.setAlertType(alertType);
		subscription.setDescription(description);
		subscription.setImportance(importanceThreshold);
		pendingSubscriptions.add(subscription);
	}
	
	
	
	/**
	 * Commits all enqueued subscriptions. Use only when message broker is available.
	 */
	private void commitSubscriptions() {
		if (!pendingSubscriptions.isEmpty()) {
			Notification[] pendings =  new Notification[pendingSubscriptions.size()];
			pendingSubscriptions.toArray(pendings);
			for (int i = 0; i < pendings.length; i++) {
				Notification sub = pendings[i];
				registerForAlert(sub.getServiceId(), sub.getAlertType(), sub.getDescription(), sub.getImportance());
			}
		}
	}
	
	
	/**
	 * Starts the execution of the client in a separate thread, to avoid blocking
	 * other program execution. 
	 */
	public void run() {
		// Creates a connection
		ActiveMQSslConnectionFactory connectionFactory = new ActiveMQSslConnectionFactory(brokerUrl);

		// Setting the trust manager to the connection factory
		connectionFactory.setKeyAndTrustManagers(null, trustedCerts, new SecureRandom());
    	connectionFactory.setWatchTopicAdvisories(false);
		
		try {
			connection = connectionFactory.createConnection();

			// Provide a unique clientId for durable connections (e.g. email address)
			if (clientId != null)
				connection.setClientID(clientId);

			// Creating session for sending messages
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();
			connectionIsReady = true;
			commitSubscriptions();
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	/**
	 * Closes the connection with the message broker.
	 */
	public void close() {
		try {
			connection.close();
			connectionIsReady = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
	// Creating the Trust manager for accepting SSL certificates
	TrustManager[] trustedCerts = new TrustManager[] { new X509TrustManager() {
		
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certificates,
				String authType) {
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certificates,
				String authType) {
		}
	} };
}