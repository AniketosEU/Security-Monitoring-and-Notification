package eu.aniketos.threatrepository.impl;

import eu.aniketos.threatrepository.impl.util.TRUtils;
import eu.aniketos.threatrepository.schema.download.Threat;
import eu.aniketos.threatrepository.TagData;
import eu.aniketos.threatrepository.ThreatRepositoryService;
import eu.aniketos.threatrepository.ThreatType;
//import eu.aniketos.threatrepository.impl.util.NamespacePrefixMapperImpl;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.rmi.UnmarshalException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import svrs.schema.search.Res;

/** This is the implementation class of the ThreatRepositoryService interface.
 * 
 * @author balazs
 *
 */
public class ThreatRepositoryServiceImpl implements ThreatRepositoryService {
	
	/** The tcache and ccache class variables are used to store a bundle-side cache of generic search queries. 
	 * 
	 * In both cases, domain and name queries are concatenated in the following way:
	 * key = name + "\n#" + domain
	 * */
	public HashMap<String, List<eu.aniketos.threatrepository.schema.download.Threat>> tcache = new HashMap<String, List<eu.aniketos.threatrepository.schema.download.Threat>>();
	public HashMap<String, List<eu.aniketos.threatrepository.schema.download.Countermeasure>> ccache = new HashMap<String, List<eu.aniketos.threatrepository.schema.download.Countermeasure>>();
	
    /** This function sets the credentials to use when communicating with the SVRS. 
    * Each SVRS operation is stateless and atomic, therefore this data can be changed at any time
    * without problems. 
    * If this function is not called (or it is called with a "null" userName parameter), the default 
    * 'Aniketos tester' credentials will be used. 
    *
    * @param userName The username to use for authenticating to the threat repository, or null to use the default account.
    * @param userPass The password to use for authenticating to the threat repository, or null to use the default account.
    */
    public void setCredentials(String userName, String userPass) {
    	SVRSConnectionHandler.getInstance().setCredentials(userName, userPass);
    }	
	
    /** This function sets the parameters of the proxy server to use when contacting the SVRS; 
    * all subsequent connections will use this server until the bundle is restarted. 
    * To stop using a proxy server, call this method with a "null" proxyName parameter. 
    *
    * @param proxyName The host name of the proxy server, or null to stop using the current proxy.
    * @param proxyPort The port number of the proxy server.
    * @param proxyUser The username to use for authenticating to the proxy server, or null if the proxy is anonymous.
    * @param proxyPass The password to use for authenticating to the proxy server, or null if the proxy is anonymous.
    */
    public void setProxy(String proxyName, Integer proxyPort, String proxyUser, String proxyPass) {
    	SVRSConnectionHandler.getInstance().setProxyData(proxyName, proxyPort, proxyUser, proxyPass);
    }
	
    /** This function adds a new threat to the SVRS. If updating an existing threat, use updateThreat instead.
     *
     * @param threatID The UUID of the threat to add.
     * @param threatData A eu.aniketos.threatrepository.schema.upload.Threat object containing threat data to be uploaded to the SVRS.
     * @return A string containing the HTTP response code and result of the upload attempt.
     */
    public String addThreat(String threatID, eu.aniketos.threatrepository.schema.upload.Threat threatData) {
        UUID uuid = null;
        svrs.schema.upload.Resource res = null; // top-level resource object
        svrs.schema.upload.Coreelement resc = null; // core element (threat) object
        svrs.schema.upload.Resourcedata rdat = null; // metadata object
        svrs.schema.upload.Coreelement.Coreassociations assoc = null; // TODO: make it possible to add associations through the TR (later)
        String uploadquery = ""; // XML query to send to the SVRS
        String result = null; // response from the SVRS

        // Initialise the resource object and its sub-objects
        res = new svrs.schema.upload.Resource();
        resc = new svrs.schema.upload.Coreelement();
        rdat = new svrs.schema.upload.Resourcedata();

        // Parse the fields from the threat object into the resource object.
        // We don't need to do extensive input validation here, as the SVRS will
        // do schema-based, syntactic and semantic checking anyway.
        uuid = UUID.fromString(threatID);

        // Check that the input Threat object is sane
        // This will be replaced with 'standard' XSD schema-based validation
        if (threatData.getMetadata() == null) {
            return "Invalid Threat object -- must include metadata";
        }

        // Set metadata. If some of these are NULL in the input Threat object,
        // that is fine -- it will translate into an omitted tag, which will
        // result in schema validation failure after reaching the SVRS.
        rdat.setAccesslevel(BigInteger.ZERO);
        rdat.setCreationdate(threatData.getMetadata().getCreationdate());
        rdat.setDescription(threatData.getMetadata().getDescription());
        if (null != threatData.getMetadata().getExternalrefs()) {
            svrs.schema.upload.Resourcedata.Externalrefs erefs = new svrs.schema.upload.Resourcedata.Externalrefs();
            rdat.setExternalrefs(erefs);
            for (String eref : threatData.getMetadata().getExternalrefs().getExternalref()) {
                erefs.getExternalref().add(eref);
            }
        }
        if (null != threatData.getMetadata().getTags()) {
            svrs.schema.upload.Resourcedata.Tags tags = new svrs.schema.upload.Resourcedata.Tags();
            rdat.setTags(tags);
            for (String tag : threatData.getMetadata().getTags().getTag()) {
                tags.getTag().add(tag);
            }
        }        
        rdat.setName(threatData.getMetadata().getName());
        if (null != threatData.getMetadata().getType()) {
            if (TRUtils.isThreatType(threatData.getMetadata().getType())) {
                rdat.setResourcetype(threatData.getMetadata().getType());
            } else {
                rdat.setResourcetype("vulnerability");
            }
        }

        rdat.setUuid(uuid.toString());


        // Link to the appropriate threat class (should also be in the SVRS)
        if (null != threatData.getMetadata().getThreatclass()) {
            assoc = new svrs.schema.upload.Coreelement.Coreassociations();
            svrs.schema.upload.Coreelement.Coreassociations.Coreassociation e = new svrs.schema.upload.Coreelement.Coreassociations.Coreassociation();
            e.setCoreuuid(threatData.getMetadata().getThreatclass());
            e.setCoreversion(BigInteger.ONE);
            svrs.schema.upload.ObjectFactory fact = new svrs.schema.upload.ObjectFactory();
            e.getForwardrelationOrBackwardrelation().add(fact.createCoreelementCoreassociationsCoreassociationForwardrelation("childof"));
            e.getForwardrelationOrBackwardrelation().add(fact.createCoreelementCoreassociationsCoreassociationBackwardrelation("parentof"));
            assoc.getCoreassociation().add(e);
        }

        // If a Cause is specified, link it
        if (null != threatData.getMetadata().getCauseId()) {
            if (assoc == null) {
                assoc = new svrs.schema.upload.Coreelement.Coreassociations();
            }
            svrs.schema.upload.Coreelement.Coreassociations.Coreassociation e = new svrs.schema.upload.Coreelement.Coreassociations.Coreassociation();
            e.setCoreuuid(threatData.getMetadata().getCauseId());
            e.setCoreversion(BigInteger.ONE);
            svrs.schema.upload.ObjectFactory fact = new svrs.schema.upload.ObjectFactory();
            e.getForwardrelationOrBackwardrelation().add(fact.createCoreelementCoreassociationsCoreassociationBackwardrelation("causes"));
            assoc.getCoreassociation().add(e);
        }


        // Finalise the resource object
        if (assoc != null) {
            resc.setCoreassociations(assoc);
        }
        resc.setResourcedata(rdat);
        res.setCoreelement(resc);

        // Marshal the resource object into an XML and send it to the SVRS (@localhost)
        try {
            StringWriter sw = new StringWriter();
            ClassLoader cl = svrs.schema.upload.ObjectFactory.class.getClassLoader();
            JAXBContext jc = JAXBContext.newInstance("svrs.schema.upload", cl);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.shields-project.eu/svrs UploadResourceSchema.xsd");
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            // TODO: re-enable prefix mapping (JAXB / jdk6 incompatibility issue)
            // m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl());
            m.marshal(res, sw);

            System.out.print(sw.toString());

            uploadquery = sw.toString();
        } catch (javax.xml.bind.JAXBException ex) {
            System.out.print("Marshalling error");
        }

        if (!uploadquery.isEmpty()) {
            try {
                // Send to SVRS
                result = SVRSConnectionHandler.getInstance().uploadResource(uploadquery, threatID);
            } catch (UnmarshalException ex) {
                System.out.println("Unmarshal failed");
            } catch (JAXBException ex) {
                System.out.println("JAXB error");
            } catch (java.security.GeneralSecurityException e) {
                System.out.println("A security exception occurred while setting up the SSL context");
                return result;
            } catch (MalformedURLException e) {
                Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                return result;
            } catch (IOException e) {
                Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                return result;
            }
        }
        return result;
    }

    /** This function updates an existing threat in the SVRS. If adding a new threat, use addThreat instead.
     *
     * @param threatID The UUID of the threat to update.
     * @param threatData A eu.aniketos.threatrepository.schema.upload.Threat object containing threat data to be uploaded to the SVRS.
     * @return A string containing the HTTP response code and result of the update attempt.
     */
    public String updateThreat(String threatID, eu.aniketos.threatrepository.schema.upload.Threat threatData) {
        UUID uuid = null;
        svrs.schema.upload.Resource res = null; // top-level resource object
        svrs.schema.upload.Updateinfo upd = null; // update object
        svrs.schema.upload.Coreelement resc = null; // core element (threat) object
        svrs.schema.upload.Resourcedata rdat = null; // metadata object
        svrs.schema.upload.Coreelement.Coreassociations assoc = null; // TODO: make it possible to add associations through the TR (later)
        String uploadquery = ""; // XML query to send to the SVRS
        String result = null; // response from the SVRS

        // Initialise the resource object and its sub-objects
        res = new svrs.schema.upload.Resource();
        resc = new svrs.schema.upload.Coreelement();
        rdat = new svrs.schema.upload.Resourcedata();

        // Parse the fields from the threat object into the resource object.
        // We don't need to do extensive input validation here, as the SVRS will
        // do schema-based, syntactic and semantic checking anyway.
        uuid = UUID.fromString(threatID);

        // Check that the input Threat object is sane
        // This will be replaced with 'standard' XSD schema-based validation
        if (threatData.getMetadata() == null) {
            return "Invalid Threat object -- must include metadata";
        }

        // Set metadata. If some of these are NULL in the input Threat object,
        // that is fine -- it will translate into an omitted tag, which will
        // result in schema validation failure after reaching the SVRS.
        rdat.setAccesslevel(BigInteger.ZERO);
        rdat.setCreationdate(threatData.getMetadata().getCreationdate());
        rdat.setDescription(threatData.getMetadata().getDescription());
        if (null != threatData.getMetadata().getExternalrefs()) {
            svrs.schema.upload.Resourcedata.Externalrefs erefs = new svrs.schema.upload.Resourcedata.Externalrefs();
            rdat.setExternalrefs(erefs);
            for (String eref : threatData.getMetadata().getExternalrefs().getExternalref()) {
                erefs.getExternalref().add(eref);
            }
        }
        if (null != threatData.getMetadata().getTags()) {
            svrs.schema.upload.Resourcedata.Tags tags = new svrs.schema.upload.Resourcedata.Tags();
            rdat.setTags(tags);
            for (String tag : threatData.getMetadata().getTags().getTag()) {
                tags.getTag().add(tag);
            }
        }             
        rdat.setName(threatData.getMetadata().getName());
        if (null != threatData.getMetadata().getType()) {
            if (TRUtils.isThreatType(threatData.getMetadata().getType())) {
                rdat.setResourcetype(threatData.getMetadata().getType());
            } else {
                rdat.setResourcetype("vulnerability");
            }
        }
        rdat.setUuid(uuid.toString());

        // Set the update note
        upd = new svrs.schema.upload.Updateinfo();
        if (null != threatData.getMetadata().getUpdatecomment()) {
            upd.setUpdatecomment(threatData.getMetadata().getUpdatecomment());
        } else {
            upd.setUpdatecomment("No update note specified");
        }
        res.setUpdateinfo(upd);

        // Link to the appropriate threat class (should also be in the SVRS)
        if (null != threatData.getMetadata().getThreatclass()) {
            assoc = new svrs.schema.upload.Coreelement.Coreassociations();
            svrs.schema.upload.Coreelement.Coreassociations.Coreassociation e = new svrs.schema.upload.Coreelement.Coreassociations.Coreassociation();
            e.setCoreuuid(threatData.getMetadata().getThreatclass());
            e.setCoreversion(BigInteger.ONE);
            svrs.schema.upload.ObjectFactory fact = new svrs.schema.upload.ObjectFactory();
            e.getForwardrelationOrBackwardrelation().add(fact.createCoreelementCoreassociationsCoreassociationForwardrelation("childof"));
            e.getForwardrelationOrBackwardrelation().add(fact.createCoreelementCoreassociationsCoreassociationBackwardrelation("parentof"));
            assoc.getCoreassociation().add(e);
        }

        // If a Cause is specified, link it
        if (null != threatData.getMetadata().getCauseId()) {
            if (assoc == null) {
                assoc = new svrs.schema.upload.Coreelement.Coreassociations();
            }
            svrs.schema.upload.Coreelement.Coreassociations.Coreassociation e = new svrs.schema.upload.Coreelement.Coreassociations.Coreassociation();
            e.setCoreuuid(threatData.getMetadata().getCauseId());
            e.setCoreversion(BigInteger.ONE);
            svrs.schema.upload.ObjectFactory fact = new svrs.schema.upload.ObjectFactory();
            e.getForwardrelationOrBackwardrelation().add(fact.createCoreelementCoreassociationsCoreassociationBackwardrelation("causes"));
            assoc.getCoreassociation().add(e);
        }


        // Finalise the resource object
        if (assoc != null) {
            resc.setCoreassociations(assoc);
        }
        resc.setResourcedata(rdat);
        res.setCoreelement(resc);

        // Marshal the resource object into an XML and send it to the SVRS (@localhost)
        try {
            StringWriter sw = new StringWriter();
            ClassLoader cl = svrs.schema.upload.ObjectFactory.class.getClassLoader();
            JAXBContext jc = JAXBContext.newInstance("svrs.schema.upload", cl);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.shields-project.eu/svrs UploadResourceSchema.xsd");
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            // TODO: re-enable prefix mapping (JAXB / jdk6 incompatibility issue)
            // m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl());
            m.marshal(res, sw);

            System.out.print(sw.toString());

            uploadquery = sw.toString();
        } catch (javax.xml.bind.JAXBException ex) {
            System.out.print("Marshalling error");
        }

        if (!uploadquery.isEmpty()) {
            try {
                // Send to SVRS
                result = SVRSConnectionHandler.getInstance().uploadResource(uploadquery, threatID);
            } catch (UnmarshalException ex) {
                System.out.println("Unmarshal failed");
            } catch (JAXBException ex) {
                System.out.println("JAXB error");
            } catch (java.security.GeneralSecurityException e) {
                System.out.println("A security exception occurred while setting up the SSL context");
                return result;
            } catch (MalformedURLException e) {
                Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                return result;
            } catch (IOException e) {
                Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                return result;
            }
        }
        return result;
    }

    /** This function removes a threat from the SVRS. Please note that the threat is not physically erased from the database -- its entry is merely flagged as 'deleted'.
     *
     * @param threatID The UUID of the threat to delete.
     * @return A string containing the HTTP response code and result of the deletion attempt.
     */
    public String deleteThreat(String threatID) {
        String res = null;
        try {
            res = SVRSConnectionHandler.getInstance().deleteResource(threatID);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    /** This function adds a new countermeasure to the SVRS. To update an existing countermeasure, use updateCountermeasure instead.
     *
     * @param counterMID The UUID of the countermeasure to add.
     * @param counterData A eu.aniketos.threatrepository.schema.upload.Countermeasure object containing countermeasure data to be uploaded to the SVRS.
     * @return A string containing the HTTP response code and result of the upload attempt.
     */
    public String addCountermeasure(String counterMID, eu.aniketos.threatrepository.schema.upload.Countermeasure counterData) {
        UUID uuid = null;
        svrs.schema.upload.Resource res = null; // top-level resource object
        svrs.schema.upload.Model resm = null; // SVRS model object (countermeasures are stored as artefact models)
        svrs.schema.upload.Model.Modeldata resmd = null; // SVRS model data object
        svrs.schema.upload.Countermeasure resc = null; // SVRS artefact object
        svrs.schema.upload.Resourcedata rdat = null; // metadata object
        //svrs.schema.upload.Model.Representation repr = null; // NYI: representation object
        String uploadquery = ""; // XML query to send to the SVRS
        String result = null; // response from the SVRS

        // Initialise the resource object and its sub-objects
        res = new svrs.schema.upload.Resource();
        resm = new svrs.schema.upload.Model();
        resmd = new svrs.schema.upload.Model.Modeldata();
        resc = new svrs.schema.upload.Countermeasure();
        //repr = new svrs.schema.upload.Model.Representation();
        rdat = new svrs.schema.upload.Resourcedata();

        // Parse the fields from the countermeasure object into the resource
        // object.
        // We don't need to do extensive input validation here, as the SVRS will
        // do schema-based, syntactic and semantic checking anyway.
        uuid = UUID.fromString(counterMID);

        // Check that the input Countermeasure object is sane
        // This will be replaced with 'standard' XSD schema-based validation
        if (counterData.getMetadata() == null) {
            return "Invalid Countermeasure object -- must include metadata";
        }
        if (counterData.getCountermeasuredata() == null) {
            return "Invalid Countermeasure object -- must include countermeasure data";
        }
        if (counterData.getThreats() == null) {
            return "Invalid Countermeasure object -- Countermeasure must be related to a Threat!";
        }

        // Set metadata. If some of these are NULL in the input Countermeasure
        // object, that is fine -- it will translate into an omitted tag, which
        // will result in schema validation failure after reaching the SVRS.
        rdat.setAccesslevel(BigInteger.ZERO);
        rdat.setCreationdate(counterData.getMetadata().getCreationdate());
        rdat.setDescription(counterData.getMetadata().getDescription());
        if (null != counterData.getMetadata().getExternalrefs()) {
            svrs.schema.upload.Resourcedata.Externalrefs erefs = new svrs.schema.upload.Resourcedata.Externalrefs();
            rdat.setExternalrefs(erefs);
            for (String eref : counterData.getMetadata().getExternalrefs().getExternalref()) {
                erefs.getExternalref().add(eref);
            }
        }
        if (null != counterData.getMetadata().getTags()) {
            svrs.schema.upload.Resourcedata.Tags tags = new svrs.schema.upload.Resourcedata.Tags();
            rdat.setTags(tags);
            for (String tag : counterData.getMetadata().getTags().getTag()) {
                tags.getTag().add(tag);
            }
        }
        rdat.setName(counterData.getMetadata().getName());
        //counterData.getMetadata().getType()
        rdat.setResourcetype("countermeasure");
        rdat.setUuid(uuid.toString());

        // Link to the appropriate threats (should also be in the SVRS)
        if (null != counterData.getThreats().getThreat()) {
            svrs.schema.upload.Model.Coreassociations assoc = new svrs.schema.upload.Model.Coreassociations();
            resm.setCoreassociations(assoc);
            for (eu.aniketos.threatrepository.schema.upload.Countermeasure.Threats.Threat t : counterData.getThreats().getThreat()) {
                svrs.schema.upload.Model.Coreassociations.Coreassociation c = new svrs.schema.upload.Model.Coreassociations.Coreassociation();
                c.setCoreuuid(t.getThreatId());
                c.setCoreversion(BigInteger.ONE);
                c.setRelation(t.getRelation());
                resm.getCoreassociations().getCoreassociation().add(c);
            }
        }
        resc.setContent(counterData.getCountermeasuredata());
        resc.setMimetype("application/x-aniketos");
        resc.setCmtype(counterData.getMetadata().getType());
        if (counterData.getMetadata().getExecresponsible() != null) {
            resc.setExecresponsible(counterData.getMetadata().getExecresponsible());
        }
        if (counterData.getMetadata().getResultdesc() != null) {
            resc.setResultdesc(counterData.getMetadata().getResultdesc());
        }

        if (null != counterData.getPreconditions()) {
            svrs.schema.upload.Countermeasure.Preconditions prec = new svrs.schema.upload.Countermeasure.Preconditions();
            resc.setPreconditions(prec);
            for (String pre : counterData.getPreconditions().getPrecondition()) {
                prec.getPrecondition().add(pre);
            }
        }

        // Finalise the resource object
        resmd.setCountermeasure(resc);
        resm.setModeldata(resmd);
        resm.setResourcedata(rdat);
        res.setModel(resm);

        // Marshal the resource object into an XML and send it to the SVRS (@localhost)
        try {
            StringWriter sw = new StringWriter();
            ClassLoader cl = svrs.schema.upload.ObjectFactory.class.getClassLoader();
            JAXBContext jc = JAXBContext.newInstance("svrs.schema.upload", cl);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.shields-project.eu/svrs UploadResourceSchema.xsd");
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            // TODO: re-enable prefix mapping (JAXB / jdk6 incompatibility issue)
            // m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl());
            m.marshal(res, sw);

            System.out.print(sw.toString());

            uploadquery = sw.toString();
        } catch (javax.xml.bind.JAXBException ex) {
            System.out.print("Marshalling error");
        }

        if (!uploadquery.isEmpty()) {
            try {
                // Send to SVRS
                result = SVRSConnectionHandler.getInstance().uploadResource(uploadquery, counterMID);
            } catch (UnmarshalException ex) {
                System.out.println("Unmarshal failed");
            } catch (JAXBException ex) {
                System.out.println("JAXB error");
            } catch (java.security.GeneralSecurityException e) {
                System.out.println("A security exception occurred while setting up the SSL context");
                return result;
            } catch (MalformedURLException e) {
                Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                return result;
            } catch (IOException e) {
                Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                return result;
            }
        }
        return result;
    }

    /** This function updates an existing countermeasure in the SVRS. To add a new countermeasure, use addCountermeasure instead.
     *
     * @param counterMID The UUID of the countermeasure to update.
     * @param counterData A eu.aniketos.threatrepository.schema.upload.Countermeasure object containing countermeasure data to be uploaded to the SVRS.
     * @return A string containing the HTTP response code and result of the update attempt.
     */
    public String updateCountermeasure(String counterMID, eu.aniketos.threatrepository.schema.upload.Countermeasure counterData) {
        UUID uuid = null;
        svrs.schema.upload.Resource res = null; // top-level resource object
        svrs.schema.upload.Model resm = null; // SVRS model object (countermeasures are stored as artefact models)
        svrs.schema.upload.Model.Modeldata resmd = null; // SVRS model data object
        svrs.schema.upload.Artefact resa = null; // SVRS artefact object
        svrs.schema.upload.Resourcedata rdat = null; // metadata object
        svrs.schema.upload.Updateinfo upd = null; // update object
        //svrs.schema.upload.Model.Representation repr = null; // NYI: representation object
        String uploadquery = ""; // XML query to send to the SVRS
        String result = null; // response from the SVRS

        // Initialise the resource object and its sub-objects
        res = new svrs.schema.upload.Resource();
        resm = new svrs.schema.upload.Model();
        resmd = new svrs.schema.upload.Model.Modeldata();
        resa = new svrs.schema.upload.Artefact();
        //repr = new svrs.schema.upload.Model.Representation();
        rdat = new svrs.schema.upload.Resourcedata();

        // Parse the fields from the countermeasure object into the resource
        // object.
        // We don't need to do extensive input validation here, as the SVRS will
        // do schema-based, syntactic and semantic checking anyway.
        uuid = UUID.fromString(counterMID);

        // Check that the input Countermeasure object is sane
        // This will be replaced with 'standard' XSD schema-based validation
        if (counterData.getMetadata() == null) {
            return "Invalid Countermeasure object -- must include metadata";
        }
        if (counterData.getCountermeasuredata() == null) {
            return "Invalid Countermeasure object -- must include countermeasure data";
        }
        if (counterData.getThreats() == null) {
            return "Invalid Countermeasure object -- Countermeasure must be related to a Threat!";
        }

        // Set metadata. If some of these are NULL in the input Countermeasure
        // object, that is fine -- it will translate into an omitted tag, which
        // will result in schema validation failure after reaching the SVRS.
        rdat.setAccesslevel(BigInteger.ZERO);
        rdat.setCreationdate(counterData.getMetadata().getCreationdate());
        rdat.setDescription(counterData.getMetadata().getDescription());
        if (null != counterData.getMetadata().getExternalrefs()) {
            svrs.schema.upload.Resourcedata.Externalrefs erefs = new svrs.schema.upload.Resourcedata.Externalrefs();
            rdat.setExternalrefs(erefs);
            for (String eref : counterData.getMetadata().getExternalrefs().getExternalref()) {
                erefs.getExternalref().add(eref);
            }
        }
        if (null != counterData.getMetadata().getTags()) {
            svrs.schema.upload.Resourcedata.Tags tags = new svrs.schema.upload.Resourcedata.Tags();
            rdat.setTags(tags);
            for (String tag : counterData.getMetadata().getTags().getTag()) {
                tags.getTag().add(tag);
            }
        }        
        rdat.setName(counterData.getMetadata().getName());
        //counterData.getMetadata().getType()
        rdat.setResourcetype("artefact");
        rdat.setUuid(uuid.toString());

        // Set the update note
        upd = new svrs.schema.upload.Updateinfo();
        if (null != counterData.getMetadata().getUpdatecomment()) {
            upd.setUpdatecomment(counterData.getMetadata().getUpdatecomment());
        } else {
            upd.setUpdatecomment("No update note specified");
        }
        res.setUpdateinfo(upd);

        // Link to the appropriate threats (should also be in the SVRS)
        if (null != counterData.getThreats().getThreat()) {
            svrs.schema.upload.Model.Coreassociations assoc = new svrs.schema.upload.Model.Coreassociations();
            resm.setCoreassociations(assoc);
            for (eu.aniketos.threatrepository.schema.upload.Countermeasure.Threats.Threat t : counterData.getThreats().getThreat()) {
                svrs.schema.upload.Model.Coreassociations.Coreassociation c = new svrs.schema.upload.Model.Coreassociations.Coreassociation();
                c.setCoreuuid(t.getThreatId());
                c.setCoreversion(BigInteger.ONE);
                c.setRelation(t.getRelation());
                resm.getCoreassociations().getCoreassociation().add(c);
            }
        }
        resa.setContent(counterData.getCountermeasuredata());
        resa.setMimetype("application/x-aniketos");

        // Finalise the resource object
        resmd.setArtefact(resa);
        resm.setModeldata(resmd);
        resm.setResourcedata(rdat);
        res.setModel(resm);

        // Marshal the resource object into an XML and send it to the SVRS (@localhost)
        try {
            StringWriter sw = new StringWriter();
            ClassLoader cl = svrs.schema.upload.ObjectFactory.class.getClassLoader();
            JAXBContext jc = JAXBContext.newInstance("svrs.schema.upload", cl);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.shields-project.eu/svrs UploadResourceSchema.xsd");
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            // TODO: re-enable prefix mapping (JAXB / jdk6 incompatibility issue)
            // m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl());
            m.marshal(res, sw);

            System.out.print(sw.toString());

            uploadquery = sw.toString();
        } catch (javax.xml.bind.JAXBException ex) {
            System.out.print("Marshalling error");
        }

        if (!uploadquery.isEmpty()) {
            try {
                // Send to SVRS
                result = SVRSConnectionHandler.getInstance().uploadResource(uploadquery, counterMID);
            } catch (UnmarshalException ex) {
                System.out.println("Unmarshal failed");
            } catch (JAXBException ex) {
                System.out.println("JAXB error");
            } catch (java.security.GeneralSecurityException e) {
                System.out.println("A security exception occurred while setting up the SSL context");
                return result;
            } catch (MalformedURLException e) {
                Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                return result;
            } catch (IOException e) {
                Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                return result;
            }
        }
        return result;
    }

    /** This function removes a countermeasure from the SVRS. Please note that the countermeasure is not physically erased from the database -- its entry is merely flagged as 'deleted'.
     *
     * @param counterMID The UUID of the countermeasure to delete.
     * @return A string containing the HTTP response code and result of the deletion attempt.
     */
    public String deleteCountermeasure(String counterMID) {
        String res = null;
        try {
            res = SVRSConnectionHandler.getInstance().deleteResource(counterMID);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    /** THIS FUNCTION IS DEPRECATED, USE getThreats(name, threatID, type, domain) instead!
    * This function fetches a list of countermeasures from the SVRS that match the provided requirements: UUID or name. Optional parameters should be null if not used.
    *
    * @param name The countermeasure name to search for. Optional.
    * @param id The UUID of the countermeasure, if available. Optional.
    * @return An ArrayList of Countermeasure objects that correspond to countermeasures in the repository. If there were no hits, it returns an empty list.
    */
    public List<eu.aniketos.threatrepository.schema.download.Countermeasure> getCountermeasures(String name, String id) {
    	return getCountermeasures(name, id, null);
    }


    /** This function fetches a list of countermeasures from the SVRS that match the provided requirements: UUID or name. Optional parameters should be null if not used.
     *
     * @param name The countermeasure name to search for. Optional.
     * @param id The UUID of the countermeasure, if available. Optional.
     * @param domain The domain of the countermeasure, if available. Optional.
     * @return An ArrayList of Countermeasure objects that correspond to countermeasures in the repository. If there were no hits, it returns an empty list.
     */
    public List<eu.aniketos.threatrepository.schema.download.Countermeasure> getCountermeasures(String name, String id, String domain) {
        UUID uuid = null;
        svrs.schema.search.Search req = null; // top-level search object
        svrs.schema.search.Search.Source reqs = null; // search object
        svrs.schema.search.Search.Source.Sourceresidset.Resid reqsid = null; // UUID
        svrs.schema.search.Search.Source.Sourcesearchtext reqstext = null; // search text object (+ search options)
        String searchquery = "";
        svrs.schema.searchres.Searchresults searchres = null; // search response from the SVRS
        ArrayList<eu.aniketos.threatrepository.schema.download.Countermeasure> res = new ArrayList<eu.aniketos.threatrepository.schema.download.Countermeasure>();

        // Check cache.
        String n = (name == null ? "null" : name);
        String d = (domain == null ? "null" : domain);
        String key = n + "\n#" + d;
        if (ccache.containsKey(key))
        {
        	return ccache.get(key);
        }
        
        // Initialise the search object
        req = new svrs.schema.search.Search();
        reqs = new svrs.schema.search.Search.Source();

        // Get countermeasure with a given ID (if specified)
        if (id != null) {
            try {
                uuid = UUID.fromString(id);
                reqs.setSourceresidset(new svrs.schema.search.Search.Source.Sourceresidset());
                reqsid = new svrs.schema.search.Search.Source.Sourceresidset.Resid();
                reqsid.setResuuid(id);
                reqs.getSourceresidset().getResid().add(reqsid);
            } catch (IllegalArgumentException e) {
                // TODO: add exception; for now we just treat this as if UUID was not specified
                uuid = null;
            }
        }

        // Set type
        reqs.setSourcetype(Res.COUNTERMEASURE);

        // Free-text search on the given text
        if (name != null) {
            // Empty search strings aren't allowed
            // TODO: add exception
            if (name.isEmpty()) {
                return res;
            }

            reqstext = new svrs.schema.search.Search.Source.Sourcesearchtext();
            // Parameters: search in name only, not case sensitive
            // TODO: maybe these parameters could also be specified?
            // TODO: add support for ontological search and time-based search (later)
            reqstext.setCasesensitive(BigInteger.ZERO);
            reqstext.setScope("name");
            reqstext.setValue(name);
            reqs.setSourcesearchtext(reqstext);
        } 
        /*else if (uuid == null) {
            return res; // Search text is only optional if an UUID was specified
        }*/

        // Domain search on the given text. For now, this overrides free-text search.
        // Thus, if a domain is specified, it will search for that string in tags ONLY, and ignore the 'name' field.
        // TODO: add support for 'name'+'domain' search, if requested
        if (domain != null) {
            // Empty search strings aren't allowed
            // TODO: add exception
            if (domain.isEmpty()) {
                return res;
            }

            reqstext = new svrs.schema.search.Search.Source.Sourcesearchtext();
            // Parameters: search in domain only, not case sensitive
            // TODO: maybe these parameters could also be specified?
            // TODO: add support for ontological search and time-based search (later)
            reqstext.setCasesensitive(BigInteger.ZERO);
            reqstext.setScope("tagname");
            reqstext.setValue(domain);
            reqs.setSourcesearchtext(reqstext);
        }
        
        // Finalise the search object
        req.setSource(reqs);

        try {
            if (uuid != null) {
                // If we have a UUID, just download the resource directly
                res.add(SVRSConnectionHandler.getInstance().fetchCountermeasure(uuid.toString()));
            } else {
                // Marshal the search object into an XML and send it to the SVRS (@localhost)
                StringWriter sw = new StringWriter();
                ClassLoader cl = svrs.schema.search.ObjectFactory.class.getClassLoader();
                JAXBContext jc = JAXBContext.newInstance("svrs.schema.search", cl);
                Marshaller m = jc.createMarshaller();
                m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.shields-project.eu/svrs SearchQuerySchema.xsd");
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
                // TODO: re-enable prefix mapping (JAXB / jdk6 incompatibility issue)
                // m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl());
                m.marshal(req, sw);

                System.out.print(sw.toString());

                searchquery = sw.toString();

                if (!searchquery.isEmpty()) {
                    // Search SVRS
                    searchres = SVRSConnectionHandler.getInstance().searchResources(searchquery);
                    // If we have any search results, iterate through them and download the metadata for each
                    if (searchres != null) {
                    	if (searchres.getResultset() != null)
                        for (svrs.schema.searchres.Searchresults.Resultset.Searchresult sres : searchres.getResultset().getSearchresult()) {

                            // Have we fetched this UUID already?
                            boolean found = false;
                            for (eu.aniketos.threatrepository.schema.download.Countermeasure t : res) {
                                if (sres.getResuuid().equalsIgnoreCase(t.getMetadata().getCounterId())) {
                                    found = true;
                                }
                            }
                            if (found) {
                                continue;
                            }

                            // Finally, add the countermeasure to the list of results
                            res.add(SVRSConnectionHandler.getInstance().fetchCountermeasure(sres.getResuuid()));
                        }
                    }
                }
            }
        } catch (UnmarshalException ex) {
            System.out.println("Unmarshal failed");
        } catch (JAXBException ex) {
            System.out.println("JAXB error");
        } catch (java.security.GeneralSecurityException e) {
            System.out.println("A security exception occurred while setting up the SSL context");
            return res;
        } catch (MalformedURLException e) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            return res;
        } catch (IOException e) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            return res;
        }
        
        // fill cache for non-specific queries
        if (uuid == null) {
        	ccache.put(key, res);
        }
        return res;
    }

    /** This function fetches a list of countermeasures from the SVRS that deal with a particular threat.
     *
     * @param threatID The UUID of the threat.
     * @return An ArrayList of Countermeasure objects that correspond to countermeasures in the repository. If there were no matches, it returns an empty list.
     */
    public List<eu.aniketos.threatrepository.schema.download.Countermeasure> getCountermeasuresForThreat(String threatID) {
        UUID uuid = null;
        ArrayList<eu.aniketos.threatrepository.schema.download.Countermeasure> res = new ArrayList<eu.aniketos.threatrepository.schema.download.Countermeasure>();
        svrs.schema.download.Resource dlres = null;

        try {
            uuid = UUID.fromString(threatID);
            dlres = SVRSConnectionHandler.getInstance().downloadResource(uuid.toString());

            // Check if this threat has any associated models
            if (null != dlres.getCoreelement().getModelassociations()) {
                for (svrs.schema.download.Coreelement.Modelassociations.Modelassociation assoc : dlres.getCoreelement().getModelassociations().getModelassociation()) {
                    // Verify that the relation is one of the 'valid' ones
                    if (!TRUtils.isRelation(assoc.getRelation())) {
                        continue;
                    }
                    svrs.schema.download.Resource model = SVRSConnectionHandler.getInstance().downloadResource(assoc.getModeluuid());
                    // Is it a countermeasure?
                    if (!model.getModel().getResourcedata().getResourcetype().equalsIgnoreCase("countermeasure")) {
                        continue;
                    }
                    if (!model.getModel().getModeldata().getCountermeasure().getMimetype().equalsIgnoreCase("application/x-aniketos")) {
                        continue;
                    }

                    // OK, fill in the countermeasure fields
                    eu.aniketos.threatrepository.schema.download.Countermeasure c = new eu.aniketos.threatrepository.schema.download.Countermeasure();
                    eu.aniketos.threatrepository.schema.download.Countermeasure.Metadata m = new eu.aniketos.threatrepository.schema.download.Countermeasure.Metadata();
                    eu.aniketos.threatrepository.schema.download.Countermeasure.Threats t = new eu.aniketos.threatrepository.schema.download.Countermeasure.Threats();

                    eu.aniketos.threatrepository.schema.download.Countermeasure.Threats.Threat th = new eu.aniketos.threatrepository.schema.download.Countermeasure.Threats.Threat();
                    th.setRelation(assoc.getRelation());
                    th.setThreatId(threatID);
                    t.getThreat().add(th); // TODO: Trivial for now, will change with multi-threat support

                    m.setCounterId(model.getModel().getResourcedata().getUuid());
                    m.setCreationdate(model.getModel().getResourcedata().getCreationdate());
                    m.setLastupdated(model.getModel().getResourcedata().getLastmodified());
                    m.setName(model.getModel().getResourcedata().getName());
                    m.setDescription(model.getModel().getResourcedata().getDescription());
                    // Set external references, if any
                    if (null != model.getModel().getResourcedata().getExternalrefs()) {
                        eu.aniketos.threatrepository.schema.download.Countermeasure.Metadata.Externalrefs erefs = new eu.aniketos.threatrepository.schema.download.Countermeasure.Metadata.Externalrefs();
                        m.setExternalrefs(erefs);
                        for (String eref : model.getModel().getResourcedata().getExternalrefs().getExternalref()) {
                            erefs.getExternalref().add(eref);
                        }
                    }
                    m.setType(model.getModel().getModeldata().getCountermeasure().getCmtype());
                    if (null != model.getModel().getModeldata().getCountermeasure().getExecresponsible()) {
                        m.setExecresponsible(model.getModel().getModeldata().getCountermeasure().getExecresponsible());
                    }
                    if (null != model.getModel().getModeldata().getCountermeasure().getResultdesc()) {
                        m.setResultdesc(model.getModel().getModeldata().getCountermeasure().getResultdesc());
                    }
                    if (null != model.getModel().getModeldata().getCountermeasure().getPreconditions()) {
                        eu.aniketos.threatrepository.schema.download.Countermeasure.Preconditions precs = new eu.aniketos.threatrepository.schema.download.Countermeasure.Preconditions();
                        c.setPreconditions(precs);
                        for (String precondition : model.getModel().getModeldata().getCountermeasure().getPreconditions().getPrecondition()) {
                            precs.getPrecondition().add(precondition);
                        }
                    }

                    // m.setType(ThreatID) //TODO: add Aniketos-specific fields once threat specification is finalised

                    c.setMetadata(m);
                    c.setCountermeasuredata(model.getModel().getModeldata().getCountermeasure().getContent());
                    c.setThreats(t);
                    res.add(c);
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    /** THIS FUNCTION IS DEPRECATED, USE getThreats(name, threatID, type, domain) instead!
    * This function fetches a list of threats from the SVRS that match the provided requirements: UUID, name, or type. Optional parameters should be null if not used.
    *
    * @param name The threat name to search for. Optional, but either name or UUID need to be specified.
    * @param threatID The UUID of the threat, if available. Optional, but either name or UUID need to be specified.
    * @param type Defines what kind of threat to search for: <i>threat</i>, <i>vulnerability</i>, or <i>category</i>. Optional.
    * @return An ArrayList of Threat objects that correspond to threats in the repository. If there were no hits, it returns an empty list.
    */
    public List<eu.aniketos.threatrepository.schema.download.Threat> getThreats(String name, String id, ThreatType type) {
    	return getThreats(name, id, type, null);
    }
    
    /** This function fetches a list of threats from the SVRS that match the provided requirements: UUID, name, or type. Optional parameters should be null if not used.
    *
    * @param name The threat name to search for. Optional, but either name, UUID, or domain need to be specified.
    * @param threatID The UUID of the threat, if available. Optional, but either name, UUID, or domain need to be specified.
    * @param type Defines what kind of threat to search for: <i>threat</i>, <i>vulnerability</i>, or <i>category</i>. Optional.
    * @param domain The threat domain to search for. Optional, but either name, UUID, or domain need to be specified.
    * @return An ArrayList of Threat objects that correspond to threats in the repository. If there were no hits, it returns an empty list.
    */
    public List<eu.aniketos.threatrepository.schema.download.Threat> getThreats(String name, String id, ThreatType type, String domain) {
        UUID uuid = null;
        svrs.schema.search.Search req = null; // top-level search object
        svrs.schema.search.Search.Source reqs = null; // search object
        svrs.schema.search.Search.Source.Sourceresidset.Resid reqsid = null; // UUID
        svrs.schema.search.Search.Source.Sourcesearchtext reqstext = null; // search text object (+ search options)
        String searchquery = "";
        svrs.schema.searchres.Searchresults searchres = null; // search response from the SVRS
        ArrayList<eu.aniketos.threatrepository.schema.download.Threat> res = new ArrayList<eu.aniketos.threatrepository.schema.download.Threat>();
        
        // Check cache.
        String n = (name == null ? "null" : name);
        String d = (domain == null ? "null" : domain);
        String key = n + "\n#" + d;
        if (tcache.containsKey(key))
        {
        	return tcache.get(key);
        }

        // Initialise the search object
        req = new svrs.schema.search.Search();
        reqs = new svrs.schema.search.Search.Source();

        // Get threat with a given ID (if specified)
        if (id != null) {
            try {
                uuid = UUID.fromString(id);
                reqs.setSourceresidset(new svrs.schema.search.Search.Source.Sourceresidset());
                reqsid = new svrs.schema.search.Search.Source.Sourceresidset.Resid();
                reqsid.setResuuid(id);
                reqs.getSourceresidset().getResid().add(reqsid);
            } catch (IllegalArgumentException e) {
                // TODO: add exception; for now we just treat this as if UUID was not specified
                uuid = null;
            }
        }
        else 
        {
        	// Set search-and-fetch operation (significantly faster)
        	req.setFetchsearch(BigInteger.ONE);
        }

        // Restrict threat to subtype (if specified)
        if (type != null) {
            svrs.schema.search.Res t = null;
            // type must be a valid enum member, so we can just convert to the appropriate SVRS equivalent
            switch (type) {
                case threat:
                    t = svrs.schema.search.Res.THREAT;
                    break;
                case vulnerability:
                    t = svrs.schema.search.Res.VULNERABILITY;
                    break;
                case category:
                    t = svrs.schema.search.Res.CATEGORY;
            }
            if (t != null) {
                reqs.setSourcetype(t);
            }
        }

        // Free-text search on the given text
        if (name != null) {
            // Empty search strings aren't allowed
            // TODO: add exception
            if (name.isEmpty()) {
                return res;
            }

            reqstext = new svrs.schema.search.Search.Source.Sourcesearchtext();
            // Parameters: search in name only, not case sensitive
            // TODO: maybe these parameters could also be specified?
            // TODO: add support for ontological search and time-based search (later)
            reqstext.setCasesensitive(BigInteger.ZERO);
            reqstext.setScope("name");
            reqstext.setValue(name);
            reqs.setSourcesearchtext(reqstext);
        } else if ((uuid == null) && (type == null) && (domain == null) ){
            return res; // Search text is only optional if an UUID, a threat subtype, or a domain was specified
        }
        
        // Domain search on the given text. For now, this overrides free-text search.
        // Thus, if a domain is specified, it will search for that string in tags ONLY, and ignore the 'name' field.
        // TODO: add support for 'name'+'domain' search, if requested
        if (domain != null) {
            // Empty search strings aren't allowed
            // TODO: add exception
            if (domain.isEmpty()) {
                return res;
            }

            reqstext = new svrs.schema.search.Search.Source.Sourcesearchtext();
            // Parameters: search in domain only, not case sensitive
            // TODO: maybe these parameters could also be specified?
            // TODO: add support for ontological search and time-based search (later)
            reqstext.setCasesensitive(BigInteger.ZERO);
            reqstext.setScope("tagname");
            reqstext.setValue(domain);
            reqs.setSourcesearchtext(reqstext);
        }

        // Finalise the search object
        req.setSource(reqs);

        try {
            if (uuid != null) {
                // If we have a UUID, just download the resource directly
                res.add(SVRSConnectionHandler.getInstance().fetchThreat(uuid.toString()));
            } else {
                // Marshal the search object into an XML and send it to the SVRS (@localhost)
                StringWriter sw = new StringWriter();
                ClassLoader cl = svrs.schema.search.ObjectFactory.class.getClassLoader();
                JAXBContext jc = JAXBContext.newInstance("svrs.schema.search", cl);
                Marshaller m = jc.createMarshaller();
                m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.shields-project.eu/svrs SearchQuerySchema.xsd");
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
                // TODO: re-enable prefix mapping (JAXB / jdk6 incompatibility issue)
                // m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl());
                m.marshal(req, sw);

                System.out.print(sw.toString());

                searchquery = sw.toString();

                if (!searchquery.isEmpty()) {

                    // Search SVRS
                    searchres = SVRSConnectionHandler.getInstance().searchResources(searchquery);
                    // If we have any search results, iterate through them and copy the received data directly
                    if (searchres != null) {
                    	if (searchres.getResultset() != null)
                        for (svrs.schema.searchres.Searchresults.Resultset.Searchresult sres : searchres.getResultset().getSearchresult()) {

                            // Have we added this UUID already?
                            boolean found = false;
                            for (eu.aniketos.threatrepository.schema.download.Threat t : res) {
                                if (sres.getResuuid().equalsIgnoreCase(t.getMetadata().getThreatId())) {
                                    found = true;
                                }
                            }
                            if (found) {
                                continue;
                            }
                            
                            Threat t = new Threat();
                            eu.aniketos.threatrepository.schema.download.Threat.Metadata me = new eu.aniketos.threatrepository.schema.download.Threat.Metadata();
                            
                    		me.setCreationdate(sres.getResmodified()); // TODO: should this be changed?
                    		me.setLastupdated(sres.getResmodified());
                    		me.setName(sres.getResname());
                    		me.setDescription(sres.getResdesc());
                    		
                    		// Set CVSS if available
                    		if (null != sres.getCvss())
                    		{
                    			me.setCvss(null);
                    		}

                    		// Set external references if available
                    		if (null != sres.getExternalrefs()) {
                    			eu.aniketos.threatrepository.schema.download.Threat.Metadata.Externalrefs erefs = new eu.aniketos.threatrepository.schema.download.Threat.Metadata.Externalrefs();
                    			me.setExternalrefs(erefs);
                    			for (String eref : sres.getExternalrefs().getExternalref()) {
                    				erefs.getExternalref().add(eref);
                    			}
                    		}
                    		// Set tags if available
                    		if (null != sres.getTags()) {
                    			eu.aniketos.threatrepository.schema.download.Threat.Metadata.Tags tags = new eu.aniketos.threatrepository.schema.download.Threat.Metadata.Tags();
                    			me.setTags(tags);
                    			for (String tag : sres.getTags().getTag()) {
                    				tags.getTag().add(tag);
                    			}
                    		}
                    		me.setType(sres.getRestype());
                    		me.setThreatId(sres.getResuuid());

                    		t.setMetadata(me);
                            res.add(t);
                        }
                    }
                }
            }
        } catch (UnmarshalException ex) {
            System.out.println("Unmarshal failed");
            return res;
        } catch (JAXBException ex) {
            System.out.println("JAXB error: " + ex.fillInStackTrace());
            return res;
        } catch (java.security.GeneralSecurityException e) {
            System.out.println("A security exception occurred while setting up the SSL context");
            return res;
        } catch (MalformedURLException e) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            return res;
        } catch (IOException e) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            return res;
        }
        
        // fill cache for non-specific queries
        if (uuid == null) {
        	tcache.put(key, res);
        }
        
        return res;
    }

    /** This wrapper function fetches a list of threats from the SVRS that are dealt with by a particular countermeasure.
     *
     * @param CounterMID The UUID of the countermeasure.
     * @return An ArrayList of Threat objects that correspond to the countermeasure in the repository. If there were no matches, it returns an empty list.
     */
    public List<eu.aniketos.threatrepository.schema.download.Threat> getThreatsForCountermeasure(String CounterMID) {
        UUID uuid = null;
        ArrayList<eu.aniketos.threatrepository.schema.download.Threat> res = new ArrayList<eu.aniketos.threatrepository.schema.download.Threat>();
        svrs.schema.download.Resource dlres = null;

        try {
            uuid = UUID.fromString(CounterMID);
            dlres = SVRSConnectionHandler.getInstance().downloadResource(uuid.toString());
            if (dlres == null) {
                return res;
            }
            // This countermeasure is supposed to have at least one threat linked to it
            for (svrs.schema.download.Model.Coreassociations.Coreassociation a : dlres.getModel().getCoreassociations().getCoreassociation()) {
                res.add(SVRSConnectionHandler.getInstance().fetchThreat(a.getCoreuuid()));
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    /** This function fetches the current list of content tags in the SVRS, along with their usage. The list is ordered by tag name.
     * 
     * @return A List of TagData objects; each object corresponds to a single tag, and contains the number of times the tag is used. 
     */
	public List<TagData> getTagList() {
		ArrayList<TagData> taglist = new ArrayList<TagData>();
		
        try {
            svrs.schema.taglist.Taglist res = SVRSConnectionHandler.getInstance().downloadTaglist();

            // Convert to list
            for (svrs.schema.taglist.Taglist.Tag t : res.getTag()) {
            	TagData tag = new TagData();
            	tag.tag = t.getTagname();
            	tag.occurrences = t.getTagusage().intValue(); 
                taglist.add(tag);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(ThreatRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
		

		return taglist;
	}
}
