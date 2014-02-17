package eu.aniketos.notification.trigger;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.aniketos.notification.IAlert;



/**
 * Activates the OSGI instance of the Aniketos notification module reference client.
 * 
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 */
public class Activator implements BundleActivator {


	/** The trigger window **/
	private JFrame triggerWindow;

	/** The OSGI bundle context object **/
	private BundleContext context;
	
	/** Checks for service availability **/
	private Timer timer;
	
	
	/**
	 * Starts the OSGI bundle.
	 */
	public void start(final BundleContext context) throws Exception {
		this.context = context;
		
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				// Check availability with service
				if (checkForAlertService()) {
					
					// Creates the GUI interface for triggering alerts
//					triggerWindow = new SimpleTrigger(context);
					
					triggerWindow = new CustomTrigger(context);
					
					// Cancels the timer
					timer.cancel();
				}
			}
		}, 0, 5000);
		
	}

	

	/**
	 * Try to connect with the notification module service implementation bundle 
	 */
	private boolean checkForAlertService() {		
		ServiceReference reference = context.getServiceReference(IAlert.class.getName());
		return (reference != null);
	}
	
	
	/**
	 * Stops the OSGI bundle.
	 */
	public void stop(BundleContext context) throws Exception {		
		timer.cancel();
		
		if (triggerWindow != null && triggerWindow instanceof CustomTrigger)
			((CustomTrigger) triggerWindow).close();
	}

}