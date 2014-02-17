package eu.aniketos.notification.client.platform;

import java.security.SecureRandom;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.activemq.ActiveMQSslConnectionFactory;

/**
 * A simple client example that shows how an Aniketos platform component can receive (all) notifications passing through Aniketos. 
 * 
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 *
 */
public class PlatformClient implements MessageListener, ExceptionListener {

	/** URL of the ActiveMQ broker, prepended by transport protocol **/
	private static String brokerUrl = "ssl://notification.aniketos.eu:61617"; 
	
	/** Topic to subscribe to for receiving access to all notifications **/
	private static String topicName = "TrustworthinessComponent"; // or ServiceThreatMonitoringModule
	
	/** Username for authenticating with the ActiveMQ broker **/
	private static String userName = "tw"; // or threat (for STMM)
	
	/** The password corresponding to the username **/
	private static String password = ""; // Find in eRoom: https://project.sintef.no/eRoom/ikt/Aniketos/0_738a1
	
	/** The JMS session object we re-use to create subscriptions **/
	private Session session;
    
	
	/**
	 * Creates an instance of the simple subscription client
	 */
	public PlatformClient() {
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
			
			// Creating the topic (i.e. creating a link to the already existing one on the broker)
			Topic topic = session.createTopic(topicName);
			
			// MessageConsumer is used for receiving (consuming) messages from the topic (above given threshold)
			MessageConsumer consumer = session.createConsumer(topic);  
			consumer.setMessageListener(this);
			
			
		} catch (JMSException e) {
			session = null;
			e.printStackTrace();
		}
	}


	@Override
	public void onException(JMSException exception) {
		System.out.println("[JMS Exception] " + exception.getMessage());		
	}



	@Override
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
