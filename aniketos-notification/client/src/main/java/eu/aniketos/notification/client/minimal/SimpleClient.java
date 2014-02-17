package eu.aniketos.notification.client.minimal;

import java.security.SecureRandom;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.activemq.ActiveMQSslConnectionFactory;

import eu.aniketos.notification.Notification;
import eu.aniketos.notification.descriptions.ThreatLevelChange;

/**
 * A minimal reference example of what a client must implement to receive notifications from Aniketos. 
 * 
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 *
 */
public class SimpleClient {

	/** URL of the ActiveMQ broker, prepended by transport protocol **/
	private static String brokerUrl = "ssl://notification.aniketos.eu:61617"; 

	/** The JMS session object we re-use to create subscriptions **/
	private Session session;
    
	
	/**
	 * Creates an instance of the simple subscription client
	 */
	public SimpleClient() {
		// Creates a connection
		ActiveMQSslConnectionFactory connectionFactory = new ActiveMQSslConnectionFactory(brokerUrl);

		// Setting the trust manager to the connection factory
		connectionFactory.setKeyAndTrustManagers(null, trustedCerts, new SecureRandom());
    	connectionFactory.setWatchTopicAdvisories(false);

		try {
			// Connect to the broker
			Connection connection = connectionFactory.createConnection();

			// Creating session for sending messages
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();

		} catch (JMSException e) {
			session = null;
			e.printStackTrace();
		}
	}

	
	/**
	 * Creates a new subscription object instance for subscribing to notifications.
	 * 
	 * @param serviceId Marketplace ID for the subscribed service
	 * @param importanceThreshold An importance threshold above which messages should be sent
	 * @throws JMSException
	 */
	public void registerForAlert(String serviceId, int importanceThreshold)
			throws JMSException {

		new SimpleSubscription(serviceId, importanceThreshold, session);
	}

	/**
	 * Creates a new subscription object instance for subscribing to notifications.
	 * 
	 * @param serviceId Marketplace ID for the subscribed service
	 * @param alertType Type of alert (found in notification.Notification)
	 * @param importanceThreshold An importance threshold above which messages should be sent
	 * @throws JMSException
	 */
	public void registerForAlert(String serviceId, String alertType,
			int importanceThreshold) throws JMSException {

		new SimpleSubscription(serviceId, alertType, importanceThreshold, session);
	}

	
	/**
	 * Creates a new subscription object instance for subscribing to notifications.
	 * 
	 * @param serviceId Marketplace ID for the subscribed service
	 * @param alertType Type of alert (found in notification.Notification)
	 * @param description More specific on type (from notification.descriptions.*)
	 * @param importanceThreshold A threshold above which messages should be sent (default=0)
	 * @throws JMSException
	 */
	public void registerForAlert(String serviceId, String alertType,
			String description, int importanceThreshold) throws JMSException {

		new SimpleSubscription(serviceId, alertType, description, importanceThreshold,
				session);
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

	
	/**
	 * Create subscriptions for scenario S14A-1, as the example in D4.2 section 2.5 shows
	 */
	public static void main(String[] args) {
		SimpleClient client = new SimpleClient();

		try {
			// Subscribe to alerts of type TrustLevelChange only
			client.registerForAlert("https://www.chrispay.com/api/pay", Notification.TRUST_LEVEL_CHANGE, 0);

			// Subscribe to alerts, limiting both on alert type and description (threat)
			client.registerForAlert("https://www.chrispay.com/api/pay", Notification.THREAT_LEVEL_CHANGE, ThreatLevelChange.DDOS_ATTACK_ON_SERVICE_COMPONENT, 0);

			// Subscribe to alerts, limiting both on alert type and description (threat)
			client.registerForAlert("https://www.chrispay.com/api/pay", Notification.THREAT_LEVEL_CHANGE, ThreatLevelChange.DDOS_ATTACK_ON_SERVICE_COMPONENT, 0);
			
			// Subscribe to all alerts, regardless of type
			client.registerForAlert("http://www.weblocation.com/iploc", 0);

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
