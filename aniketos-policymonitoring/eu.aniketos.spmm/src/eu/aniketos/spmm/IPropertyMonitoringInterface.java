package eu.aniketos.spmm;




import eu.aniketos.data.IAgreementTemplate;
import eu.aniketos.data.ICompositionPlan;
import eu.aniketos.spmm.event.ReceiveEvent;





/**
 * The interface that our Property Monitoring Module will implement, based on
 * the WP5 draft
 * 
 * @author M Asim, LJMU
 * 
 */
public interface IPropertyMonitoringInterface
{
	
	/**
	 * A function of the interface responsible for receiving an agreement template and a BPMN file from Service Composition Framework.
	 *
	 * @param SecuredComposition The secured composition plan selected for monitoring
	 * @param agreementTemplates Corresponding agreement template for the secured composition.
	 * @return 
	 * @return 
	 */
	public void monitorSecurityPolicy(IAgreementTemplate agreementTemplate, String serviceID);

	/** 
	 * A function of the interface responsible for receiving events from the Service Runtime Environment.
	 * 
	 * @param event 	The event to process.
	 */
	public void IMonitoringData(ReceiveEvent event);
	
	/**
	 * Stop monitoring a service for compliance violations and release any memory
	 * allocated by the monitoring service.
	 * 
	 * @param serviceID	The identifier of the service to stop monitoring.
	 */
	public void unmonitorSecurityPolicy(String serviceID); 
	
}
