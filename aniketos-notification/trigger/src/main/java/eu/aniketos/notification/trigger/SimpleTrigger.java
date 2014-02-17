package eu.aniketos.notification.trigger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.aniketos.notification.IAlert;
import eu.aniketos.notification.Notification;
import eu.aniketos.notification.descriptions.ContextChange;
import eu.aniketos.notification.descriptions.ThreatLevelChange;
/**
 * Simple graphical interface for sending some example alerts to the notification module's IAlert interface.
 *  
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 *
 */
public class SimpleTrigger extends JFrame {

	/** Unique ID for serialization **/
	private static final long serialVersionUID = -7654038266564722502L;
	
	/** The OSGI bundle context object **/
	private BundleContext context;
	/** The IAlert service that shall be invoked from this class **/
	private IAlert service;
	
	/** A button for triggering alerts **/
	private JButton alertButton;
	/** A simple step counter for progressing in the alert "story" **/
	private int alertStage;
	
	
	/**
	 * Initiates the dummy alert triggering mechanism.
	 * @param context The OSGI bundle context object
	 */
	public SimpleTrigger(BundleContext context) {
		super("Dummy alert trigger");
		this.context = context;
		
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		setLayout(new BorderLayout());
		alertStage = 1;
				
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// Check availability with service
				registerWithAlertService();
			}
		}, 0, 5000);

		setVisible(true);
//		requestFocus();	
	}
	
	
	/**
	 * Try to connect with the notification module implementation bundle 
	 */
	private void registerWithAlertService() {		
		ServiceReference reference = context.getServiceReference(IAlert.class.getName());
		boolean serviceIsAvailable = (reference != null);
		
		// Get access to the service
		if (serviceIsAvailable)
			service = (IAlert) context.getService(reference);
		else
			service = null;
			
		updateGUI(serviceIsAvailable);		
	}
	
	
	/**
	 * Update the GUI contents.
	 * @param serviceIsAvailable Whether or not the message broker is available
	 */
	private void updateGUI(boolean serviceIsAvailable) {
		getContentPane().removeAll();

		Dimension size = new Dimension(200, 85);
		alertButton = new JButton("Alert!");
		alertButton.setPreferredSize(size);
		alertButton.setMinimumSize(size);
		alertButton.setMaximumSize(size);
		
		if (serviceIsAvailable) {
			
			if (alertStage <= 5) {
				alertButton.setFont(alertButton.getFont().deriveFont(36f));
		
				alertButton.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						
						if (alertStage == 1)
							service.alert("https://www.chrispay.com/api/pay", Notification.TRUST_LEVEL_CHANGE, "3", ThreatLevelChange.DEGRADED_SECURITY_INTERFACE, "81b8ea61-71e7-4d58-9f70-a08a38a0047b");
						else if (alertStage == 2)
							service.alert("https://www.chrispay.com/api/pay", Notification.THREAT_LEVEL_CHANGE, "2", ThreatLevelChange.DDOS_ATTACK_ON_SERVICE_COMPONENT, "dbb9b1e4-46d7-44c7-8151-90239c5548b");
						else if (alertStage == 3)
							service.alert("http://www.weblocation.com/iploc", Notification.THREAT_LEVEL_CHANGE, "5", ThreatLevelChange.SERVICE_INJECTION, "dbb9b1e4-46d7-44c7-8151-90239c5548b");
						else if (alertStage == 4)
							service.alert("https://www.chrispay.com/api/pay", Notification.THREAT_LEVEL_CHANGE, "3", ThreatLevelChange.DEGRADED_SECURITY_INTERFACE);
						else if (alertStage == 5)
							service.alert("https://www.chrispay.com/api/pay", Notification.SECURITY_PROPERTY_CHANGE, ContextChange.SERVICE_SITE_CHANGED, "EU/Ireland");
						else {
							service.alert("https://www.chrispay.com/api/pay", Notification.TRUST_LEVEL_CHANGE, "4");
							alertButton.setEnabled(false);
							alertButton.setText("Stop");
						}
						
						alertStage++;
					}
				});
			}
			
			else {
				alertButton.setEnabled(false);
				alertButton.setText("Nothing left to trigger");
			}
		}
		
		else {
			alertButton.setEnabled(false);
			alertButton.setText("Service not available");
		}
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(40,40,40,40));
		panel.add(alertButton, BorderLayout.CENTER);
				
		getContentPane().add(panel);
		
		pack();
		validate();
	}
}
