package eu.aniketos.notification.client;

import javax.jms.JMSException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.aniketos.notification.Notification;
import eu.aniketos.notification.client.minimal.SimpleClient;
import eu.aniketos.notification.client.platform.PlatformClient;
import eu.aniketos.notification.descriptions.ContextChange;
import eu.aniketos.notification.descriptions.ThreatLevelChange;



/**
 * Activates the OSGI instance of the Aniketos notification module reference client.
 * 
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 */
public class Activator implements BundleActivator {

	/** URL of the ActiveMQ broker, prepended by transport protocol **/
	private static String brokerUrl = "ssl://notification.aniketos.eu:61617";
	
	/** A reference to the client that is activated by this class **/
	private EnvironmentClient client;
	
	
	/**
	 * Starts the OSGI bundle.
	 */
	public void start(final BundleContext context) throws Exception {

		// Starts a simple version of the client
//		startSimple();
		
		// Starts a platform internal version of the client (for STMM and Trustworthiness Component)
//		startPlatform();
		
		// Starts the more robust version
		startThreaded();
				
	}

	

	/**
	 * Threaded client example - stacks up failed subscription requests and resends them when broker connection is ready. 
	 */
	public void startThreaded() {

		// The second parameter is currently not in use, but for future development, 
		// a unique (and perhaps portable) client ID could be used for durable subscriptions.
		// Any email address would probably be appropriate here.
		client = new EnvironmentClient(brokerUrl, null);
		
		// Dispatch separate thread to avoid blocking while connecting
		new Thread(client).start();

		// Subscribe to all alerts, regardless of type
		client.registerForAlerts("https://www.chrispay.com/api/pay");

		// Subscribe to alerts of type TrustLevelChange only
		client.registerForAlert("http://www.weblocation.com/iploc", Notification.TRUST_LEVEL_CHANGE);
		// Subscribe to alerts, limiting both on alert type and description (threat)
		client.registerForAlert("http://www.weblocation.com/iploc", Notification.THREAT_LEVEL_CHANGE, ThreatLevelChange.DDOS_ATTACK_ON_SERVICE_COMPONENT, 0);
		client.registerForAlert("http://www.weblocation.com/iploc", Notification.THREAT_LEVEL_CHANGE, ThreatLevelChange.UNTRUSTED_SERVICE_COMPOSITION, 0);
		client.registerForAlert("http://www.weblocation.com/iploc", Notification.CONTEXT_CHANGE, ContextChange.SERVICE_SITE_CHANGED, 0);

	}

	
	/**
	 * Simple example, minimal code - subscription requests are lost if message broker is not available.
	 */
	public void startSimple() {
		SimpleClient client = new SimpleClient();
		this.client = null;

		try {
			// Subscribe to all alerts, regardless of type
			client.registerForAlert("https://www.chrispay.com/api/pay", 0);

			// Subscribe to alerts of type TrustLevelChange only
			client.registerForAlert("http://www.weblocation.com/iploc", Notification.TRUST_LEVEL_CHANGE, 0);
			// Subscribe to alerts, limiting both on alert type and description (threat)
			client.registerForAlert("http://www.weblocation.com/iploc", Notification.THREAT_LEVEL_CHANGE, ThreatLevelChange.DDOS_ATTACK_ON_SERVICE_COMPONENT, 0);
			client.registerForAlert("http://www.weblocation.com/iploc", Notification.THREAT_LEVEL_CHANGE, ThreatLevelChange.UNTRUSTED_SERVICE_COMPOSITION, 0);
			client.registerForAlert("http://www.weblocation.com/iploc", Notification.CONTEXT_CHANGE, ContextChange.SERVICE_SITE_CHANGED, 0);

		} catch (JMSException e) {
			e.printStackTrace();
		}	
	}
	

	
	/**
	 * Simple example client for STMM/TC which subscribes to all notifications with their respective credentials
	 */
	public void startPlatform() {
		new PlatformClient();
		this.client = null;
	}
	
	
	
	
	/**
	 * Stops the OSGI bundle.
	 */
	public void stop(BundleContext context) throws Exception {
		
		if (client != null)
			client.close();
	}

}