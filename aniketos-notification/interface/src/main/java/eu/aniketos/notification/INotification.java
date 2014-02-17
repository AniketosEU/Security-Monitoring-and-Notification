package eu.aniketos.notification;

/**
 * The Notification module defines the INotification interface to be used by components that wants to receive notifications 
 * regardless of which service the notifications concern. The Service runtime environment (SRE) should always implement this interface.
 * 
 * @version 1.0
 * @author Erlend Andreas Gjære (SINTEF), erlendandreas.gjare@sintef.no
 * 
 */
public interface INotification {

	/**
	 * Receives a notification outside the regular subscription mechanism, for cases where message targeting 
	 * is done better from the Notification module's perspective rather than from the subscriber's.
	 * 
	 * @param alert Message object which contains the data about the notification, i.e. serviceId, alertType and value (required), and optionally description and threatId.
	 */
	public void alert(Notification alert);
	
}
