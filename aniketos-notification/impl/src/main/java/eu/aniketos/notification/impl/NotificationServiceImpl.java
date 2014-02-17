package eu.aniketos.notification.impl;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.aniketos.notification.IAlert;
import eu.aniketos.notification.Notification;


/**
 * Implementation of the IAlert interface for the Notification module.
 * Basically, this class creates and starts an Apache ActiveMQ instance,
 * and keeps a reference to it so that it can publish messages to it directly.
 * 
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 *
 */
public class NotificationServiceImpl implements IAlert {

	/** Instance of logger **/
	private static Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
	
	/** URL of the ActiveMQ broker, prepended by transport protocol **/
    private static String messageBrokerUrl;
    
    /** Name of the ActiveMQ broker **/
    private static String messageBrokerName;

    /** Setting for using transacted messaging **/
    private static boolean transacted;

    /** Setting for JMS acknowledgment mode **/
    private static int ackMode;

    /** Setting for broker username **/
    private static String username;

    /** Setting for broker password (matching username) **/
    private static String password;


    /** The JMS Connection for creating sessions **/
	private Connection connection;
	/** The JMS Session object to connect with **/
    private Session session;

	/** Storage of topics **/
    private HashMap<String,TopicWrapper> topics;
    
    /** Separate channel for notifications to the Trustworthiness component **/
    private MessageProducer trustworthinessProducer;
    
    /** Separate channel for notifications to the Service threat monitoring module **/
    private MessageProducer servicethreatProducer;

//    /** A debug tool for looking at incoming messages **/
//    private IAlertConsole console = null;

	
    
    /** Static configuration initialization for connection **/
    static {
//        messageBrokerUrl = "tcp://notification.sintef9013.com:61616";//tcp://localhost:61616 "tcp://129.241.253.113:61616"; //notification.sintef9013.com
        messageBrokerUrl = "ssl://notification.aniketos.eu:61617"; //83.212.116.137
        messageBrokerName = "eu.aniketos.notification";
        transacted = false;
        ackMode = Session.AUTO_ACKNOWLEDGE;
        username = "topicadmin";
        password = "xvAFhQtg";
    }
    
    
	/**
	 * Constructor that invokes the necessary components.
	 * 
	 * @throws Exception If invocation fails
	 * @see http://activemq.apache.org/how-should-i-implement-request-response-with-jms.html
	 */
	public NotificationServiceImpl() throws Exception {
//		System.out.println("[Notification module]: Starting the IAlert service");
	    topics = new HashMap<String,TopicWrapper>();

	    // Open the console window (un-comment this line to remove!)
//	    console = new IAlertConsole();
	    
	    try {
	        // Start the session
	        startSession();
	    } catch (Exception ex) {
            // Handle the exception appropriately
        	logger.error(ex.getMessage());
//        	ex.printStackTrace();
        }
	}


	
	/**
	 * http://www.mostly-useless.com/blog/2007/12/27/playing-with-activemq/
	 * @throws JMSException 
	 * @throws NamingException 
	 */
    private void startSession() throws JMSException {
    	ActiveMQSslConnectionFactory connectionFactory = new ActiveMQSslConnectionFactory(messageBrokerUrl);
    	connectionFactory.setUseAsyncSend(true);
    
    	// Setting the trust manager to the connection factory
    	connectionFactory.setKeyAndTrustManagers(null, trustedCerts, new SecureRandom());
    	connectionFactory.setWatchTopicAdvisories(false);

    	System.out.println("[Notification module] Initializing broker connection: " + messageBrokerUrl);
        connection = connectionFactory.createConnection(username, password);
    	System.out.println("[Notification module] Connected with IAlert message broker!");
		    
		connection.start();
		session = connection.createSession(transacted, ackMode);

		
		// Create a new topic for the Trustworthiness component (TC)
		Topic trustworthinessTopic = session.createTopic("TrustworthinessComponent");
		trustworthinessProducer = session.createProducer(trustworthinessTopic);
        trustworthinessProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        

		// Create a new topic for the Service threat monitoring module (STMM)
	 	Topic servicethreatTopic = session.createTopic("ServiceThreatMonitoringModule");
		servicethreatProducer = session.createProducer(servicethreatTopic);
		servicethreatProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    
    
    /**
     * Publish an alert to a particular topic
     * @param alert The alert to publish
     */
	private void publishAlert(Notification alert) {
        String alertTopic = alert.getTopicId();
        TopicWrapper topicWrap;
        
		try {
			// Check if we provide alerts for this serviceId + alertType..
	        if (topics.containsKey(alertTopic))
	        	topicWrap = topics.get(alertTopic);
			
	        else {
	        	
	            if (session == null)
	            	throw new JMSException("[Notification module] No session!");
	            
				// Create a new topic
			 	Topic topic = session.createTopic("pub." + alertTopic);
				MessageProducer producer = session.createProducer(topic);
//	            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT); // .PERSISTENT is default
//	            producer.setTimeToLive(360000);
	            
	            topicWrap = new TopicWrapper(topic, producer);
				topics.put(alertTopic, topicWrap);        	
	        }

			Message msg = session.createMessage();
			// @deprecated                     
			msg.setIntProperty("importance", alert.getAlertThreshold());
			msg.setIntProperty("threshold", alert.getImportance());
			// @deprecated
			msg.setStringProperty("service", alert.getService());
			msg.setStringProperty("serviceId", alert.getServiceId());
			msg.setStringProperty("alertType", alert.getAlertType());  
			// @deprecated
			msg.setStringProperty("alertValue", alert.getAlertValue());
			msg.setStringProperty("value", alert.getValue());
			// @deprecated
			msg.setStringProperty("alertDesc", alert.getAlertDesc());
			msg.setStringProperty("description", alert.getDescription());
			// @deprecated
			msg.setStringProperty("alertThreatId", alert.getAlertThreatId());
			msg.setStringProperty("threatId", alert.getThreatId());
			msg.setStringProperty("serverTime", alert.getServerTime()); // new Date().toString()
//			System.out.println("Server time: " + getDateTime());
			
			
			// Actually send the message to topic subscribers
			topicWrap.getProducer().send(msg);
			
			String alertType = alert.getAlertType();
//			msg.setJMSMessageID(msg.getJMSMessageID());
			
			// Publish also to the individual TC channel
	        if (!alertType.equals(Notification.TRUST_LEVEL_CHANGE)) {
	        	trustworthinessProducer.send(msg);
	        }
	        
	        // Publish also to the individual STMM channel
	        if (!alertType.equals(Notification.THREAT_LEVEL_CHANGE)) {
	        	servicethreatProducer.send(msg);
	        }
	        	
	        // Print to internal logger(s)
	        logMessage(msg, alert);
	        
	        	// Invoke ISecurityVerfication (CMM)
//	        	ISecurityVerfication verificationModule = context.getServiceReference(ISecurityVerification);
//	        	SecurityVeficationResult result = analyseSecureComposition(SecurityVerificationRequest);
	        	
//	        	Invoke Trustworthiness prediction module? No, just invoke INotification in the Service Runtime Environment
	        	
//	        	https://project.sintef.no/eRoom/ikt/Aniketos/0_67f85
//	        	Q6: I think we need to change a few things in figure 46. When there is a SecurityPropertyChange, the NotificationModule should notify the service runtime environment 
//	        	of the affected services. Then the service runtime environment should be responsible of calling analyseSecureComposition. Similarly with alert(ContractViolation). 
//	        	The notification module should send a message to the service runtime environment about this, and that's it. And again, with there is a trustlevelchange, 
//	        	the message is forwarded to the environment, the notification module should not call analyseSecureComposition, this should be done from the environment. 
	        	
			
		} catch (JMSException e) {
			logger.error(e.getMessage());
			System.out.println(e.getMessage());
		}
	}



	public void alert(String serviceId, String alertType, String value, String description, String threatId) {
		Notification alert = new Notification();
		alert.setServiceId(serviceId);
		alert.setAlertType(alertType);
		alert.setValue(value);
		alert.setDescription(description);
		alert.setThreatId(threatId);
		alert.setServerTime(getServerTime());
		
		// Send alert to handling
        logger.debug("[IAlert : Message received] " + alert.getTopicId());		
        
        // Publish the alert
        publishAlert(alert);
        
        Thread t = new Thread(new RemoteLogger(alert));
        t.start();
	}
	


	public void alert(String serviceId, String alertType, String value) {
		alert(serviceId, alertType, value, null, null);
	}
	
	
	public void alert(String serviceId, String alertType, String value, String description) {
		alert(serviceId, alertType, value, description, null);
	}

	

	/**
	 * Creates a string which represents the current server time, including milliseconds and timezone.
	 * @return Server time in a string format
	 */
	private String getServerTime() {
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH) + 1;

		int d = c.get(Calendar.DAY_OF_MONTH);
		int h = c.get(Calendar.HOUR_OF_DAY);

		int m = c.get(Calendar.MINUTE);
		int s = c.get(Calendar.SECOND);
		int y = c.get(Calendar.YEAR);

		String dd = d < 10 ? "0" + d : "" + d;
		String hh = h < 10 ? "0" + h : "" + h;
		String mm = m < 10 ? "0" + m : "" + m;
		String ss = s < 10 ? "0" + s : "" + s;
		String sm = month < 10 ? "0" + month : "" + month;

		String ms = c.get(Calendar.MILLISECOND) + "";
		return y + "-" + sm + "-" + dd + " " + hh + ":" + mm + ":" + ss + "." + ms + " " + c.getTimeZone().getDisplayName(true, TimeZone.SHORT);
	}


	
	/**
	 * Logs the message to the logger service
	 * @param msg The JMS message to log
	 * @param alert The Notification object that was sent
	 * @throws JMSException In case of JMS error
	 */
	private void logMessage(Message msg, Notification alert) throws JMSException {
		String logMessage = "[IAlert : Message published] " + alert.getTopicId() + "; alertValue='" + alert.getValue() + "'; ";
		if (alert.getThreatId() != null)
			logMessage += "threatId=" + alert.getThreatId() + "; ";
		
		logger.info(logMessage);

		logMessage = alert.getServiceId() + "\n           " + alert.getAlertType() + " - VALUE: " + alert.getValue();
				
		if (alert.getDescription() != null)		
			logMessage += "\n           '" + alert.getDescription() + "'";
		if (alert.getThreatId() != null)
			logMessage += "\n           threatId: " + alert.getThreatId();
		
		logMessage += "\n           importance: " + alert.getImportance() 
				+ "\n           serverTime: " + alert.getServerTime()
				+ "\n           messageId: " + msg.getJMSMessageID() + "\n";

		alert.setMessageId(msg.getJMSMessageID());
//		if (console != null)
//			console.append(logMessage);
//		else
        logger.debug("[IAlert : Message published] " + msg.getJMSMessageID());		
	}
	
	

	/**
	 * Shuts down the broker and stops the thread. 
	 * @throws Exception
	 */
	public void deactivate() throws Exception {
//		console.dispose();
	}

	
    
    /**
     * A trust manager object which deals with broker certificates.
     */
	TrustManager[] trustedCerts = new TrustManager[] { new X509TrustManager() {
		
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certificates,
				String authType) {
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certificates,
				String authType) {
		}
	} };

}