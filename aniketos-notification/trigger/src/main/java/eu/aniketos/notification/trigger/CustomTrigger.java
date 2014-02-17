package eu.aniketos.notification.trigger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.InputMismatchException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.aniketos.notification.IAlert;
import eu.aniketos.notification.Notification;
import eu.aniketos.notification.descriptions.*;

public class CustomTrigger extends JFrame {

	/** Unique ID for serialization **/
	private static final long serialVersionUID = 1496046183331799050L;
	
	/** Reference to the IAlert service **/
	protected IAlert alertService;
	
	/** Is set when the service becomes available **/ 
	boolean serviceIsAvailable;
	
	boolean firstRun;

	/** The OSGI bundle context object **/
	private BundleContext context;
	
	/** Checks for service availability **/
	private Timer timer;

	private final static String SERVICE_ID_HISTORY_FILE = "serviceIds.dat";
	
	private final static String THREAT_ID_HISTORY_FILE = "threatIds.dat"; 

	
	private JPanel panel;
	
	private MemoryComboBox serviceId;
	
	private JComboBox alertType;
	
	private JComboBox description;

	private JTextField value;
		
	private MemoryComboBox threatId;

	/** A button for triggering alerts **/
	private JButton alertButton;
	
	
	/**
	 * Instantiates a window containing the IAlert console
	 */
	public CustomTrigger(BundleContext context) {
		super("Notification trigger");
		this.context = context;
		serviceIsAvailable = false;
		firstRun = true;
		
		setResizable(true);
		setLayout(new BorderLayout());

//		Dimension size = new Dimension(380, 460);
		alertButton = new JButton("Send notification");
//		alertButton.setPreferredSize(size);
//		alertButton.setMinimumSize(size);
		
		panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(20,20,20,20));
		getContentPane().add(panel);
		
		serviceId = new MemoryComboBox(SERVICE_ID_HISTORY_FILE);
		serviceId.addItem("");
		serviceId.addItem("https://www.chrispay.com/api/pay");
		serviceId.addItem("http://www.weblocation.com/iploc");
		
		threatId = new MemoryComboBox(THREAT_ID_HISTORY_FILE);
		threatId.addItem("");
		threatId.addItem("22b4ecd8-507d-4e1a-a5dd-3c9a362fd3b9");
				
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// Check availability with service
				registerWithAlertService();
			}
		}, 0, 5000);
				
		pack();
		setVisible(true);
//		requestFocus();	
	}
	

	/**
	 * Try to connect with the notification module implementation bundle 
	 */
	private void registerWithAlertService() {		
		ServiceReference reference = context.getServiceReference(IAlert.class.getName());
		boolean newServiceAvailability = (reference != null);

		// Get access to the service
		if (newServiceAvailability) {
			alertService = (IAlert) context.getService(reference);
		}
		else
			alertService = null;
		
		if (newServiceAvailability != serviceIsAvailable || firstRun) {
			serviceIsAvailable = newServiceAvailability;
			updateGUI(serviceIsAvailable);
		}		
		firstRun = false;
	}
	
	
	
	/**
	 * Initializes or refreshes all GUI components
	 */
	protected void updateGUI(boolean serviceIsAvailable) {
		panel.removeAll();
		
		if (!serviceIsAvailable) {
			panel.add(new JLabel("Notification service is currently not available! Please wait..."), BorderLayout.CENTER);
			pack();
			panel.validate();
			return;
		}
		
		
		alertType = new JComboBox();
		alertType.addItem(Notification.CONTEXT_CHANGE);
		alertType.addItem(Notification.CONTRACT_CHANGE);
		alertType.addItem(Notification.CONTRACT_VIOLATION);
		alertType.addItem(Notification.SECURITY_PROPERTY_CHANGE);
		alertType.addItem(Notification.SERVICE_CHANGE);
		alertType.addItem(Notification.THREAT_LEVEL_CHANGE);
		alertType.addItem(Notification.TRUST_LEVEL_CHANGE);
		alertType.setEnabled(true);
		alertType.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				// Update description list
				if (e.getItem().equals(Notification.THREAT_LEVEL_CHANGE)) {
					
//					if (alertDesc.getSelectedItem().equals(null)) {
						description.removeAllItems();
						description.addItem("");
						description.addItem(ThreatLevelChange.ADAPTION_IMPACTS_FUNCTIONALITY);
						description.addItem(ThreatLevelChange.CASCADE_FAILURES);
						description.addItem(ThreatLevelChange.CHANGED_SECURITY_INTERFACE);
						description.addItem(ThreatLevelChange.CORRUPT_LOAD_BALANCING);
						description.addItem(ThreatLevelChange.DDOS_ATTACK_ON_SERVICE_COMPONENT);
						description.addItem(ThreatLevelChange.DECREASING_REPUTATION);
						description.addItem(ThreatLevelChange.DEGRADE_POLICY_NEGOTIATION);
						description.addItem(ThreatLevelChange.DEGRADED_SECURITY_INTERFACE);
						description.addItem(ThreatLevelChange.DISSOLVED_REDUNDANCY);
						description.addItem(ThreatLevelChange.END_USER_ANNOYED_BY_CONFIRMATIONS);
						description.addItem(ThreatLevelChange.EXTRACTING_INFORMATION_FROM_LOGS);
						description.addItem(ThreatLevelChange.FALSE_PERCEPTION_OF_TRUST_FOR_END_USER);
						description.addItem(ThreatLevelChange.INCOMPATIBILITY_ISSUES);
						description.addItem(ThreatLevelChange.INCOMPATIBLE_ACCESS_CONTROL_MODELS);
						description.addItem(ThreatLevelChange.INCOMPATIBLE_LAWS);
						description.addItem(ThreatLevelChange.INFORMATION_AND_ACCOUNTABILITY_LOST);
						description.addItem(ThreatLevelChange.INSECURE_INTERFACES_AND_APIS);
						description.addItem(ThreatLevelChange.INSUFFICIENT_AUTOMATED_SECURITY_EVALUATION);
						description.addItem(ThreatLevelChange.INTERACTION_BASED_THREATS);
						description.addItem(ThreatLevelChange.LACK_OF_TRUST_BETWEEN_PROVIDERS);
						description.addItem(ThreatLevelChange.LACK_OF_USABILITY_IN_SECURE_COMPOSITON);
						description.addItem(ThreatLevelChange.MALICIOUS_SERVICE);
						description.addItem(ThreatLevelChange.MALICIOUS_SERVICE_PROVIDER);
						description.addItem(ThreatLevelChange.MANIPULATION_OF_TRUST_PROPERTIES);
						description.addItem(ThreatLevelChange.MISSING_END_USER_NOTIFICATION);
						description.addItem(ThreatLevelChange.NON_FUNCTIONAL_CONSTAINTS_VIOLATION_VIA_COMPOSITION);
						description.addItem(ThreatLevelChange.PRIVACY_VIOLATION_VIA_COMPOSITON);
						description.addItem(ThreatLevelChange.RECOMPOSITION_CORRUPTS_RESPONSE_TIME);
						description.addItem(ThreatLevelChange.SECURITY_GUIDELINES_COMPROMISED);
						description.addItem(ThreatLevelChange.SERVICE_INJECTION);
						description.addItem(ThreatLevelChange.SYNCHRONIZATION_THREATS);
						description.addItem(ThreatLevelChange.TRUST_POISONING);
						description.addItem(ThreatLevelChange.TRUSTWORTHINESS_MANAGEMENT_THREATS);
						description.addItem(ThreatLevelChange.UNTRUSTED_OUTSOURCING_OR_DELEGATION);
						description.addItem(ThreatLevelChange.UNTRUSTED_SERVICE_COMPOSITION);
						description.addItem(ThreatLevelChange.USER_INTERACTION_ISSUES);
					}
					
//					String selAlertDesc = (String) alertDesc.getSelectedItem();
//					String selThreatId = (String) threatId.getSelectedItem();
//					
//					if ((selAlertDesc == null || selAlertDesc.equals("")) && (selThreatId == null || selThreatId.equals(""))) {
//						alertDesc.setSelectedItem(AlertDescription.UNTRUSTED_SERVICE_COMPOSITION);
//						threatId.setSelectedItem("22b4ecd8-507d-4e1a-a5dd-3c9a362fd3b9");
//					}
//				}

				
				else if (e.getItem().equals(Notification.CONTEXT_CHANGE)) {
					description.removeAllItems();
					description.addItem("");
					description.addItem(ContextChange.SERVICE_SITE_CHANGED);
				}
				else
					description.removeAllItems();
				
			}
			
		});

		description = new JComboBox();
		description.setEditable(true);
		description.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				String d = (String) e.getItem();
				String selAlertType = (String) alertType.getSelectedItem();
				
				if (selAlertType.equals(Notification.THREAT_LEVEL_CHANGE) && d != null && !d.equals("")) {
					
					if (d.equals(ThreatLevelChange.INCOMPATIBILITY_ISSUES) || d.equals(ThreatLevelChange.INCOMPATIBLE_ACCESS_CONTROL_MODELS)
							|| d.equals(ThreatLevelChange.INCOMPATIBLE_LAWS))
						threatId.setSelectedItem("068d7290-c6be-4a57-a436-f63b56ce3525");

					else if (d.equals(ThreatLevelChange.TRUST_POISONING))
						threatId.setSelectedItem("a7b64361-d6ff-43ab-b2ff-10caf8e16b08");
					
					else if (d.equals(ThreatLevelChange.UNTRUSTED_SERVICE_COMPOSITION))
						threatId.setSelectedItem("22b4ecd8-507d-4e1a-a5dd-3c9a362fd3b9");
					
					else if (d.equals(ThreatLevelChange.MALICIOUS_SERVICE) || d.equals(ThreatLevelChange.MALICIOUS_SERVICE_PROVIDER) 
							|| d.equals(ThreatLevelChange.INSECURE_INTERFACES_AND_APIS) || d.equals(ThreatLevelChange.DEGRADED_SECURITY_INTERFACE))
						threatId.setSelectedItem("3dc34eea-f4bf-4056-b850-51d48ae7100a");
					
					else if (d.equals(ThreatLevelChange.PRIVACY_VIOLATION_VIA_COMPOSITON))
						threatId.setSelectedItem("4ca25fa2-500e-4efe-8ad5-034a4c14cdb8");
					
					else if (d.equals(ThreatLevelChange.ADAPTION_IMPACTS_FUNCTIONALITY))
						threatId.setSelectedItem("5dc0e1a2-d6b9-4d44-b890-3bdc87b05eb5");
					
					else if (d.equals(ThreatLevelChange.DDOS_ATTACK_ON_SERVICE_COMPONENT))
						threatId.setSelectedItem("f56fe85e-f673-4567-be02-5d63420b4fb4");

					else if (d.equals(ThreatLevelChange.INFORMATION_AND_ACCOUNTABILITY_LOST))
						threatId.setSelectedItem("f0f9d897-c2e3-41d0-9672-4683fd9f3be0");
					
					else if (d.equals(ThreatLevelChange.NON_FUNCTIONAL_CONSTAINTS_VIOLATION_VIA_COMPOSITION))
						threatId.setSelectedItem("2c06f975-3ba6-4c62-95d1-dbad107e1de3");
					
					else if (d.equals(ThreatLevelChange.INFORMATION_AND_ACCOUNTABILITY_LOST))
						threatId.setSelectedItem("f0f9d897-c2e3-41d0-9672-4683fd9f3be0");
					
					else if (d.equals(ThreatLevelChange.USER_INTERACTION_ISSUES) || d.equals(ThreatLevelChange.END_USER_ANNOYED_BY_CONFIRMATIONS))
						threatId.setSelectedItem("443c9ca4-d85a-40ac-b61a-eacc28d947d1");
					else
						threatId.setSelectedItem("");
				}
				else
					threatId.setSelectedItem("");
				
			}
		});

		value = new JTextField();
				
		JPanel centerPanel = new JPanel(new GridLayout(0,1));

		JLabel serviceIdLabel = new JLabel("Service ID");
		centerPanel.add(serviceIdLabel);
		centerPanel.add(serviceId);
		
		JLabel alertTypeLabel = new JLabel("Type");
		centerPanel.add(alertTypeLabel);
		centerPanel.add(alertType);
		
		JLabel alertDescLabel = new JLabel("Description");
		centerPanel.add(alertDescLabel);
		centerPanel.add(description);
		
		JLabel alertValueLabel = new JLabel("Value");
		centerPanel.add(alertValueLabel);
		centerPanel.add(value);
		
		JLabel threatIdLabel = new JLabel("Threat ID");
		centerPanel.add(threatIdLabel);
		centerPanel.add(threatId);
		
		centerPanel.add(new JLabel());

		Dimension size = new Dimension(340, 60);
		alertButton = new JButton("Send notification");
		alertButton.setPreferredSize(size);
		alertButton.setMinimumSize(size);
		alertButton.setFont(alertButton.getFont().deriveFont(Font.BOLD, 16f));
		alertButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {	
				try { 
					alertButtonClicked();
				} catch(Exception ex) {
					JOptionPane.showMessageDialog(panel.getParent(), "Service ID is a required field!");
				}
			}
		});
//		alertButton.setMaximumSize(size);
		
		panel.add(centerPanel, BorderLayout.CENTER);
		panel.add(alertButton, BorderLayout.PAGE_END);
				
		pack();
		panel.validate();
	}
	
	
	
	private void alertButtonClicked() throws InputMismatchException {

		String myServiceId = (String) serviceId.getSelectedItem();

		if (myServiceId == null || myServiceId.length() == 0)
			throw new InputMismatchException("Service ID is missing!");

		String myAlertType = (String) alertType.getSelectedItem();
		String myAlertValue = value.getText();
		String myAlertDesc = (String) description.getSelectedItem();
		String myThreatId = (String) threatId.getSelectedItem();

		if (alertService != null)
			alertService.alert(myServiceId, myAlertType, myAlertValue, myAlertDesc, myThreatId);
		else
			JOptionPane.showMessageDialog(this, "Could not access IAlert service.");
		
		serviceId.add(myServiceId);
//		serviceId.save();
		threatId.add(myThreatId);
//		threatId.save();
		
		updateGUI(serviceIsAvailable);
		
		threatId.setSelectedItem("");
		alertType.setSelectedItem(myAlertType);
		description.setSelectedItem(myAlertDesc);
		value.setText("");
		threatId.setSelectedItem(myThreatId);
	}
	
	
	public void close() {
		timer.cancel();
		this.dispose();	
	}
}