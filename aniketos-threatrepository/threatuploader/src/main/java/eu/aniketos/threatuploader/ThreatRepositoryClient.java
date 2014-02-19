package eu.aniketos.threatuploader;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import eu.aniketos.threatuploader.TagData;


/** The implementation of a simple client showing how to access the Threat Repository Module.
 * 
 * @author balazs
 *
 */
public class ThreatRepositoryClient {
	
	private ArrayList<TagData> tags = new ArrayList<TagData>();
	
	/** This function processes UI requests to fetch a tag list. It checks if a taglist
	 * has already been downloaded -- if not, it downloads it.
	 * @return An ArrayList of TagData objects containing all tags currently in the SVRS.
	 */
	public List<TagData> processTagListRequestEvent() {
		
		if (!tags.isEmpty())
			return tags;
		
		try {
			svrs.schema.taglist.Taglist tl = SVRSConnectionHandler.getInstance().getTagList();
            // Convert to list
            for (svrs.schema.taglist.Taglist.Tag t : tl.getTag()) {
            	TagData tag = new TagData();
            	tag.tag = t.getTagname();
            	tag.occurrences = t.getTagusage().intValue(); 
                tags.add(tag);
            }
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tags;
	}
	
	
	/** This function processes UI changes for changing the SVRS client configuration.
	 * 
	 * @param config A Properties object containing the new configuration of the client.
	 */
	public void processConfigChangeEvent(Properties config)
	{
		String svrsuser = config.getProperty("svrsusername");
		String svrspassword = config.getProperty("svrspassword");
		String proxyname = config.getProperty("proxyhost");
		Integer proxyport = null;
		if (config.getProperty("proxyport") != null)
			proxyport = Integer.valueOf(config.getProperty("proxyport"));
		String proxyuser = config.getProperty("proxyusername");
		String proxypass = config.getProperty("proxypassword");
		
		if (svrsuser != null) {
			// set svrs username, if needed
			SVRSConnectionHandler.getInstance().setCredentials(svrsuser, svrspassword);
		} else	{
			// back to default
			SVRSConnectionHandler.getInstance().setCredentials(null, null);
		}
		
		if (proxyname != null) {
			// set proxy data, if needed
			SVRSConnectionHandler.getInstance().setProxyData(proxyname, proxyport, proxyuser, proxypass);
		} else {
			// back to default
			SVRSConnectionHandler.getInstance().setProxyData(null, null, null, null);
		}
	}
	
	
	/** This function processes UI events for adding a new threat to the repository.
	 * 
	 * @param threatID The UUID of the threat to add
	 * @param infile An XML file containing the threat data to send to the repository.
	 * @return The result of the transaction: a HTML response code followed by a short description.
	 */
	public String processAddResourceEvent(String resID, svrs.schema.upload.Resource res) {
		String result = "";
		try {
			UUID.fromString(resID);
		}
		catch (IllegalArgumentException e)
		{
			return ("Invalid UUID format.\n");
		}
		if (res == null)
			return ("No content specified.\n");
	    result = addResource(resID, res);
	    return("Attempting to send resource to SVRS...\n" + result + "\n");
	}

	private String addResource(String resID, svrs.schema.upload.Resource res) {
		String ret = "";
		String resource = null;

		try {
			StringWriter sw = new StringWriter();
			JAXBContext jc;
			jc = JAXBContext
					.newInstance(svrs.schema.upload.Resource.class);
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://www.shields-project.eu/svrs UploadResourceSchema.xsd");
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.marshal(res, sw);
			resource = sw.toString();
		} catch (JAXBException e) {
			return("JAXBException while marshalling XML:" + e.getMessage());
		}
		
		try {
			ret += SVRSConnectionHandler.getInstance().uploadResource(resource, resID);
		} catch (KeyManagementException e1) {
			return("KeyManagementException while attempting to connect:" + e1.getMessage());
		} catch (MalformedURLException e1) {
			return("MalformedURLException while attempting to connect:" + e1.getMessage());
		} catch (NoSuchAlgorithmException e1) {
			return("NoSuchAlgorithmException while attempting to connect:" + e1.getMessage());
		} catch (IOException e1) {
			return("IOException while attempting to connect:" + e1.getMessage());
		} catch (GeneralSecurityException e1) {
			return("GeneralSecurityException while attempting to connect:" + e1.getMessage());
		} catch (JAXBException e1) {
			return("JAXBException while attempting to connect:" + e1.getMessage());
		}
		
		// After sending, Marshal resource into XML to be written on screen (with 'content' removed)
		if (res.getModel() != null)
		{
			// Replace content field with a short size field for printing
			int size = res.getModel().getModeldata().getCountermeasure().getContent().length();
			if (size>100)
				res.getModel().getModeldata().getCountermeasure().setContent("(" + size + " bytes of Base64-encoded data)");
		}
		try {
			StringWriter sw = new StringWriter();
			JAXBContext jc;
			jc = JAXBContext
					.newInstance(svrs.schema.upload.Resource.class);
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://www.shields-project.eu/svrs UploadResourceSchema.xsd");
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.marshal(res, sw);
			ret = "Sent message:\n" + sw.toString() + "\n" + ret;
		} catch (JAXBException e) {
			ret += "\nJAXBException while trying to print sent message:" + e.getMessage();
		}
		return ret;
	}
}
