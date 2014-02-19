package eu.aniketos.threatrepository.client;

import java.io.File;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.osgi.service.component.ComponentContext;

import eu.aniketos.threatrepository.ThreatRepositoryService;
import eu.aniketos.threatrepository.ThreatType;
import eu.aniketos.threatrepository.schema.download.Countermeasure;
import eu.aniketos.threatrepository.schema.download.Threat;

/** The implementation of a simple client showing how to access the Threat Repository Module.
 * 
 * @author balazs
 *
 */
public class ThreatRepositoryClient {

	private ThreatRepositoryService rep; /**< The Threat Repository service instance. */
	private ThreatRepositoryClientWindow threatClient; /**< The UI window. */

	/** Declarative Services constructor.
	 * 
	 */
	public ThreatRepositoryClient() {
	}

	/** OSGi activation function.
	 * 
	 * @param context The context of the OSGi component.
	 */
	protected void activate(ComponentContext context) {
		System.out.println("Component " + this.getClass().getName()
				+ " is activated!");
		showUI();
	}

	/** OSGi deactivation function.
	 * 
	 * @param context The context of the OSGi component.
	 */
	protected void deactivate(ComponentContext context) {
		System.out.println("Component " + this.getClass().getName()
				+ " is deactivated!");
		threatClient.setVisible(false);
		threatClient.dispose();
	}

	/** The function for setting the class variable storing the TRM instance.
	 * 
	 * @param service The TRM instance.
	 */
	protected synchronized void setThreatRepositoryService(
			ThreatRepositoryService service) {
		System.out
				.println("[Service Threat Monitoring Client] Service was set!");
		this.rep = service;
	}

	/** The function for unsetting the class variable storing the TRM instance.
	 * 
	 * @param service The TRM instance.
	 */
	protected synchronized void unsetThreatRepositoryService(
			ThreatRepositoryService service) {
		System.out.println("[Threat Repository Client] Service was unset!");
		if (this.rep == service) {
			this.rep = null;
		}
	}

	/** This function shows the UI of the client.
	 * 
	 */
	private void showUI() {
		threatClient = new ThreatRepositoryClientWindow(this);
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				System.out.println("Starting SVRS client");
				threatClient.setVisible(true);
			}
		});
	}

	/** This function processes UI events for downloading a threat by UUID.
	 * 
	 * @param uuid The UUID of the threat to download
	 * @return The result of the transaction: either an error message, or the XML contents of the threat.
	 */
	public String processDownloadThreatEvent(String uuid) {
		Threat t = null;
		List<Threat> res = rep.getThreats(null, uuid, null);
		if (null != res)
			if (res.size()>0)
				t = res.get(0); // Only one hit since we are downloading via UUID
			else 
				return ("Resource doesn't exist.\n");
		else
			return ("Resource doesn't exist.\n");
		try {
			StringWriter sw = new StringWriter();
			JAXBContext jc = JAXBContext
					.newInstance("eu.aniketos.threatrepository.schema.download", eu.aniketos.threatrepository.schema.download.ObjectFactory.class.getClassLoader());
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://www.aniketos.eu DownloadThreat.xsd");
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.marshal(t, sw);
			return 	"Download result:\n-------------------------------------------\n" + sw.toString();
		} catch (javax.xml.bind.JAXBException ex) {
			return ("JAXB Marshalling error\n");
		}
	}

	/** This function processes UI events for downloading a countermeasure by UUID.
	 * 
	 * @param uuid The UUID of the countermeasure to download
	 * @return The result of the transaction: either an error message, or the XML contents of the countermeasure.
	 */
	public String processDownloadCountermeasureEvent(String uuid) {
		Countermeasure c = null;
		List<Countermeasure> res = rep.getCountermeasures(null, uuid);
		if (null != res)
			if (res.size()>0)
				c = res.get(0); // Only one hit since we are downloading via UUID
			else 
				return ("Resource doesn't exist.\n");
		else
			return ("Resource doesn't exist.\n");
		try {
			StringWriter sw = new StringWriter();
			JAXBContext jc = JAXBContext
					.newInstance(eu.aniketos.threatrepository.schema.download.Countermeasure.class);
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://www.aniketos.eu DownloadCountermeasure.xsd");
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.marshal(c, sw);
			return 	"Download result:\n-------------------------------------------\n" + sw.toString();
		} catch (javax.xml.bind.JAXBException ex) {
			return ("JAXB Marshalling error\n");
		}
	}

	/** This function processes UI events for searching for threats by name.
	 * 
	 * @param searchtext The search string the threat's name needs to contain
	 * @return The result of the transaction: either an error message, or the XML contents of the threats that contain the search text.
	 */
	public String processSearchThreatEvent(String searchtext) {
		String result = "";
		//rep.setProxy("localhost", 8888, null, null);
		List<Threat> res = rep.getThreats(searchtext, null, ThreatType.threat);
		if (null == res)
			return ("Resource doesn't exist.");
		try {
			for (Threat t : res) {
				result += "Search result:\n";
				StringWriter sw = new StringWriter();
				JAXBContext jc = JAXBContext
						.newInstance("eu.aniketos.threatrepository.schema.download", eu.aniketos.threatrepository.schema.download.ObjectFactory.class.getClassLoader());
				Marshaller m = jc.createMarshaller();
				m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
						"http://www.aniketos.eu DownloadThreat.xsd");
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
				m.marshal(t, sw);
				result += sw.toString();
				result += "-------------------------------------------\n";
			}
		} catch (javax.xml.bind.JAXBException ex) {
			return ("JAXB Marshalling error");
		}
		if (result.isEmpty())
			return "No results\n";
		else
			return result;
	}

	/** This function processes UI events for searching for countermeasures by name.
	 * 
	 * @param searchtext The search string the countermeasure's name needs to contain
	 * @return The result of the transaction: either an error message, or the XML contents of the countermeasures that contain the search text.
	 */
	public String processSearchCountermeasureEvent(String searchtext) {
		String result = "";
		List<Countermeasure> res = rep.getCountermeasures(searchtext, null);
		if (null == res)
			return ("Resource doesn't exist.");
		try {
			int i = 0;
			for (Countermeasure c : res) {
				i++;
				result += "Search result #" + i + ":\n";
				StringWriter sw = new StringWriter();
				JAXBContext jc = JAXBContext
						.newInstance(eu.aniketos.threatrepository.schema.download.Countermeasure.class);
				Marshaller m = jc.createMarshaller();
				m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
						"http://www.aniketos.eu DownloadCountermeasure.xsd");
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
				m.marshal(c, sw);
				result += sw.toString();
				result += "-------------------------------------------\n";
			}
		} catch (javax.xml.bind.JAXBException ex) {
			return ("JAXB Marshalling error");
		}
		if (result.isEmpty())
			return "No results\n";
		else
			return result;
	}

	/** This function processes UI events for adding a new threat to the repository.
	 * 
	 * @param threatID The UUID of the threat to add
	 * @param infile An XML file containing the threat data to send to the repository.
	 * @return The result of the transaction: a HTML response code followed by a short description.
	 */
	public String processAddThreatEvent(String threatID, File infile) {
		String result = "";
		eu.aniketos.threatrepository.schema.upload.Threat threatData = null;
		if (!infile.exists())
			return ("File doesn't exist.\n");
        try {
            threatData = new eu.aniketos.threatrepository.schema.upload.Threat();
            //StringReader dsr = new StringReader();
            JAXBContext djc = JAXBContext.newInstance(eu.aniketos.threatrepository.schema.upload.Threat.class);
            Unmarshaller du = djc.createUnmarshaller(); 
            threatData = (eu.aniketos.threatrepository.schema.upload.Threat) du.unmarshal(infile);
        } catch (javax.xml.bind.JAXBException ex) {
            return("JAXB marshalling error.\n");
        }
	    result = rep.addThreat(threatID, threatData);
	    return("Response from Threat Repository: " + result + "\n");
	}

	/** This function processes UI events for adding a new countermeasure to the repository.
	 * 
	 * @param counterID The UUID of the countermeasure to add
	 * @param infile An XML file containing the countermeasure data to send to the repository.
	 * @return The result of the transaction: a HTML response code followed by a short description.
	 */
	public String processAddCountermeasureEvent(String counterID, File infile) {
		String result = "";
		eu.aniketos.threatrepository.schema.upload.Countermeasure counterData = null;
		if (!infile.exists())
			return ("File doesn't exist.\n");
        try {
            counterData = new eu.aniketos.threatrepository.schema.upload.Countermeasure();
            //StringReader dsr = new StringReader();
            JAXBContext djc = JAXBContext.newInstance(eu.aniketos.threatrepository.schema.upload.Countermeasure.class);
            Unmarshaller du = djc.createUnmarshaller(); 
            counterData = (eu.aniketos.threatrepository.schema.upload.Countermeasure) du.unmarshal(infile);
        } catch (javax.xml.bind.JAXBException ex) {
        	ex.printStackTrace();
            return("JAXB marshalling error.\n" + ex.getMessage());
        }
	    result = rep.addCountermeasure(counterID, counterData);
	    return("Response from Threat Repository: " + result + "\n");
		}
}
