/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.aniketos.threatrepository.impl;

import eu.aniketos.threatrepository.impl.util.Base64;
import eu.aniketos.threatrepository.impl.util.ProxyAuthenticator;
import eu.aniketos.threatrepository.impl.util.TRUtils;
import eu.aniketos.threatrepository.schema.download.Countermeasure;
import eu.aniketos.threatrepository.schema.download.Threat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * This singleton class is a temporary connector for the first (monolithic)
 * intermediate version of the SVRS integration. It will be replaced by a more
 * robust solution (e.g. connection pool-based) in a later phase.
 * 
 * @author balazs
 */
public class SVRSConnectionHandler {
	
	private String uri = "https://svrs.shields-project.eu:443/ANIKETOS/MI";

	private SVRSConnectionHandler() {
	}

	/**
	 * Helper function for getting the singleton instance.
	 * 
	 * @return A SVRSConnectionHandlerHolder instance.
	 */
	public static SVRSConnectionHandler getInstance() {
		return SVRSConnectionHandlerHolder.INSTANCE;
	}

	/**
	 * This is the 'holder' class of the singleton instance.
	 * 
	 * @author balazs
	 * 
	 */
	private static class SVRSConnectionHandlerHolder {

		/** This is the singleton instance. */
		private static final SVRSConnectionHandler INSTANCE = new SVRSConnectionHandler();
	}

	private String proxyName = null;
	private Integer proxyPort = null;
	private String proxyUser = null;
	private String proxyPass = null;
	private String username = "aniketos";
	private String password = "aniketos";

	/**
	 * This function sets (or clears) SVRS credential data.
	 * 
	 * @param user
	 *            Username to use when connecting to the SVRS.
	 * @param pass
	 *            Password to use when connecting to the SVRS.
	 */
	public void setCredentials(String user, String pass) {
		username = user;
		password = pass;
	}	
	
	/**
	 * This function sets (or clears) proxy data.
	 * 
	 * @param name
	 *            Proxy host name
	 * @param port
	 *            Proxy port
	 * @param user
	 *            Proxy user name
	 * @param pass
	 *            Proxy password
	 */
	public void setProxyData(String name, Integer port, String user, String pass) {
		// Quick and (very) dirty solution. This may change depending on requirements for SSL tunneling through an auth-required proxy. 
		if (name == null) {
			proxyName = null;
			proxyPort = null;
			proxyUser = null;
			proxyPass = null;
			System.clearProperty("https.proxyHost");
			System.clearProperty("https.proxyPort");
			System.clearProperty("http.proxyUser");
			System.clearProperty("http.proxyPassword");
		} else {
			proxyName = name;
			proxyPort = port;
			proxyUser = user;
			proxyPass = pass;
			System.setProperty("https.proxyHost", proxyName);
			if (proxyPort!=null) System.setProperty("https.proxyPort", proxyPort.toString());
			if (proxyUser!=null) System.setProperty("http.proxyUser", proxyUser);
			if (proxyPass!=null) System.setProperty("http.proxyPassword", proxyPass);
		}
	}

	/**
	 * This function fetches a threat with the provided UUID from the
	 * repository.
	 * 
	 * @param resuuid
	 *            The UUID of the threat to retrieve.
	 * @return A Threat object corresponding to the provided UUID. Returns null
	 *         if no threat with the specified UUID exists.
	 * @throws MalformedURLException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws JAXBException
	 * @throws EncoderException
	 */
	Threat fetchThreat(String resuuid) throws MalformedURLException,
			NoSuchAlgorithmException, KeyManagementException, IOException,
			GeneralSecurityException, JAXBException {
		svrs.schema.download.Resource dlres = null; // download response from
													// the SVRS
		// Fetch resource from SVRS
		dlres = SVRSConnectionHandler.getInstance().downloadResource(resuuid);

		// Exit if no resource was found
		if (dlres == null)
			return null;

		// Parse result into a Threat object
		eu.aniketos.threatrepository.schema.download.Threat.Metadata m = new eu.aniketos.threatrepository.schema.download.Threat.Metadata();
		m.setCreationdate(dlres.getCoreelement().getResourcedata()
				.getCreationdate());
		m.setLastupdated(dlres.getCoreelement().getResourcedata()
				.getLastmodified());
		m.setName(dlres.getCoreelement().getResourcedata().getName());
		m.setDescription(dlres.getCoreelement().getResourcedata()
				.getDescription());
		// Set external references if available
		if (null != dlres.getCoreelement().getResourcedata().getExternalrefs()) {
			eu.aniketos.threatrepository.schema.download.Threat.Metadata.Externalrefs erefs = new eu.aniketos.threatrepository.schema.download.Threat.Metadata.Externalrefs();
			m.setExternalrefs(erefs);
			for (String eref : dlres.getCoreelement().getResourcedata()
					.getExternalrefs().getExternalref()) {
				erefs.getExternalref().add(eref);

			}
		}
		m.setType(dlres.getCoreelement().getResourcedata().getResourcetype());
		m.setThreatId(resuuid);
		// TODO: add these after Aniketos-specific metadata inside threat
		// objects is supported
		// m.setCauseId(id)
		// m.setThreatclass(name)
		eu.aniketos.threatrepository.schema.download.Threat t = new eu.aniketos.threatrepository.schema.download.Threat();
		t.setMetadata(m);
		eu.aniketos.threatrepository.schema.download.Threat.Countermeasures c = new eu.aniketos.threatrepository.schema.download.Threat.Countermeasures();

		// Check if this threat has any associated models
		if (null != dlres.getCoreelement().getModelassociations()) {
			for (svrs.schema.download.Coreelement.Modelassociations.Modelassociation assoc : dlres
					.getCoreelement().getModelassociations()
					.getModelassociation()) {
				// Verify that the relation is one of the 'valid' ones
				if (!TRUtils.isRelation(assoc.getRelation())) {
					continue;
				}
				// Fetch model from the repository
				svrs.schema.download.Resource model = SVRSConnectionHandler
						.getInstance().downloadResource(assoc.getModeluuid());

				// Is it a countermeasure?
				if (!model.getModel().getResourcedata().getResourcetype()
						.equalsIgnoreCase("countermeasure")) {
					continue;
				}

				// Does the MIME type match?
				if (!model.getModel().getModeldata().getCountermeasure()
						.getMimetype()
						.equalsIgnoreCase("application/x-aniketos")) {
					continue;
				}

				// OK, this is a valid countermeasure, let's add it
				eu.aniketos.threatrepository.schema.download.Threat.Countermeasures.Countermeasure a = new eu.aniketos.threatrepository.schema.download.Threat.Countermeasures.Countermeasure();
				a.setCounterId(assoc.getModeluuid());
				a.setRelation(assoc.getRelation());
				c.getCountermeasure().add(a);
			}
		}
		// Add countermeasures if there were any
		if (!c.getCountermeasure().isEmpty()) {
			t.setCountermeasures(c);
		}
		// Finally, return the threat
		return t;
	}

	/**
	 * This function fetches a countermeasure with the provided UUID from the
	 * repository.
	 * 
	 * @param resuuid
	 *            The UUID of the countermeasure to retrieve.
	 * @return A Countermeasure object corresponding to the provided UUID.
	 *         Returns null if no countermeasure with the specified UUID exists.
	 * @throws MalformedURLException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws JAXBException
	 * @throws EncoderException
	 */
	Countermeasure fetchCountermeasure(String resuuid)
			throws MalformedURLException, NoSuchAlgorithmException,
			KeyManagementException, IOException, GeneralSecurityException,
			JAXBException {
		svrs.schema.download.Resource dlres = null; // download response from
													// the SVRS
		// Fetch resource from SVRS
		dlres = SVRSConnectionHandler.getInstance().downloadResource(resuuid);

		// Exit if no resource was found
		if (dlres == null)
			return null;

		// Parse result into a Countermeasure object
		eu.aniketos.threatrepository.schema.download.Countermeasure.Metadata m = new eu.aniketos.threatrepository.schema.download.Countermeasure.Metadata();
		m.setCreator(dlres.getModel().getResourcedata().getAuthor());
		m.setCreationdate(dlres.getModel().getResourcedata().getCreationdate());
		m.setLastupdatedby(dlres.getModel().getResourcedata()
				.getLastmodifiedby());
		m.setLastupdated(dlres.getModel().getResourcedata().getLastmodified());

		// CM-specific metadata fields (some are optional)
		if (dlres.getModel().getModeldata().getCountermeasure() != null) {
			m.setType(dlres.getModel().getModeldata().getCountermeasure()
					.getCmtype());
			if (dlres.getModel().getModeldata().getCountermeasure()
					.getExecresponsible() != null) {
				m.setExecresponsible(dlres.getModel().getModeldata()
						.getCountermeasure().getExecresponsible());
			}
			if (dlres.getModel().getModeldata().getCountermeasure()
					.getResultdesc() != null) {
				m.setResultdesc(dlres.getModel().getModeldata()
						.getCountermeasure().getResultdesc());
			}
		}

		m.setName(dlres.getModel().getResourcedata().getName());
		m.setDescription(dlres.getModel().getResourcedata().getDescription());
		// Set external references if available
		if (null != dlres.getModel().getResourcedata().getExternalrefs()) {
			eu.aniketos.threatrepository.schema.download.Countermeasure.Metadata.Externalrefs erefs = new eu.aniketos.threatrepository.schema.download.Countermeasure.Metadata.Externalrefs();
			m.setExternalrefs(erefs);
			for (String eref : dlres.getModel().getResourcedata()
					.getExternalrefs().getExternalref()) {
				erefs.getExternalref().add(eref);

			}
		}
		m.setCounterId(resuuid);

		eu.aniketos.threatrepository.schema.download.Countermeasure c = new eu.aniketos.threatrepository.schema.download.Countermeasure();
		c.setMetadata(m);
		
		// set actual CM data
		c.setCountermeasuredata(dlres.getModel().getModeldata().getCountermeasure().getContent());
		
		eu.aniketos.threatrepository.schema.download.Countermeasure.Threats t = new eu.aniketos.threatrepository.schema.download.Countermeasure.Threats();

		// Check if this countermeasure has associated threats (and it should
		// have at least one!)
		if (null != dlres.getModel().getCoreassociations()) { // TODO: exception
																// if there are
																// none
			for (svrs.schema.download.Model.Coreassociations.Coreassociation assoc : dlres
					.getModel().getCoreassociations().getCoreassociation()) {
				// Verify that the relation is one of the 'valid' ones
				if (!TRUtils.isRelation(assoc.getRelation())) {
					continue;
				}
				// Fetch threat from the repository
				svrs.schema.download.Resource threat = SVRSConnectionHandler
						.getInstance().downloadResource(assoc.getCoreuuid());

				// Is it a threat?
				if (!threat.getCoreelement().getResourcedata()
						.getResourcetype().equalsIgnoreCase("threat")) {
					continue;
				}

				// OK, this is a valid threat, let's add it
				eu.aniketos.threatrepository.schema.download.Countermeasure.Threats.Threat a = new eu.aniketos.threatrepository.schema.download.Countermeasure.Threats.Threat();
				a.setThreatId(assoc.getCoreuuid());
				a.setRelation(assoc.getRelation());
				t.getThreat().add(a);
			}
		}
		// Add threats if there were any
		if (!t.getThreat().isEmpty()) {
			c.setThreats(t);
		}
		// Finally, return the countermeasure
		return c;
	}

	/**
	 * TEMPORARY function for handling TR <-> SVRS connections. To be replaced
	 * with something more robust soon!
	 * 
	 * @return A Live HttpsURLConnection object, to be used in further
	 *         operations.
	 * @param uri
	 *            An URL to open a connection to.
	 * @throws MalformedURLException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public HttpsURLConnection Connect(String uri) throws MalformedURLException,
			NoSuchAlgorithmException, KeyManagementException, IOException,
			GeneralSecurityException {
		HttpsURLConnection connection = null;
		URL url = new URL(uri);

		if (proxyName != null) {
			// TODO: Use this implementation as an alternate if needed.
		/*	// Set proxy object, if necessary.
			SSLSocketFactory sslfac = (SSLSocketFactory) SSLSocketFactory
					.getDefault();
			Socket tunnel = new Socket(proxyName, proxyPort);
			ProxyAuthenticator proxyAuth = new ProxyAuthenticator(proxyUser,
					proxyPass);
			doTunnelHandshake(tunnel, url.getHost(), url.getPort());
			SSLSocket ssls = (SSLSocket)sslfac.createSocket(tunnel, url.getHost(), url.getPort(), true);
			ssls.startHandshake();*/
		}

		// This is to accept all certificates, even untrusted or unverified ones
		javax.net.ssl.TrustManager[] trustAll = new javax.net.ssl.TrustManager[] { new javax.net.ssl.X509TrustManager() {

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };
		final javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext
				.getInstance("SSL");
		sslContext.init(null, trustAll, new java.security.SecureRandom());
		// java.security.Security.addProvider(new
		// com.sun.net.ssl.internal.ssl.Provider());
		HttpsURLConnection
				.setDefaultSSLSocketFactory((javax.net.ssl.SSLSocketFactory) sslContext
						.getSocketFactory());
		HttpsURLConnection
				.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {

					public boolean verify(String hostname,
							javax.net.ssl.SSLSession session) {
						return true;
					}
				});
		connection = (HttpsURLConnection) url.openConnection();
		if (connection == null) {
			throw new java.security.GeneralSecurityException();
		}
		connection.setConnectTimeout(20000);
		connection.setReadTimeout(5000);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Content-Type", "application/xml");

		return connection;
	}

	/**
	 * Function for handling HTTP responses.
	 * 
	 * @param con
	 *            A live HttpsURLConnection object.
	 * @return The HTTP response string.
	 * @throws IOException
	 */
	public String getResp(HttpsURLConnection con) throws IOException {
		String response = new String();
		System.out.println("Response: HTTP "
				+ Integer.toString(con.getResponseCode()) + " - "
				+ con.getResponseMessage());
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String responseLine;
		while ((responseLine = in.readLine()) != null) {
			response = response.concat(responseLine + "\n");
		}
		return response;
	}

	/**
	 * This function forwards search requests to the SVRS, and returns the
	 * results.
	 * 
	 * @param searchquery
	 *            The search query XML as a String.
	 * @return A svrs.schema.searchres.Searchresults object that contains the
	 *         search results, if any.
	 * @throws MalformedURLException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws JAXBException
	 * @throws GeneralSecurityException
	 */
	public svrs.schema.searchres.Searchresults searchResources(
			String searchquery) throws MalformedURLException,
			NoSuchAlgorithmException, KeyManagementException, IOException,
			JAXBException, GeneralSecurityException {
		svrs.schema.searchres.Searchresults searchres = null;

		HttpsURLConnection connection = Connect(uri + "/search");
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.connect();
		OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream());
		out.write(searchquery);
		out.flush();
		String response = SVRSConnectionHandler.getInstance().getResp(
				connection);
		connection.disconnect();
		// System.out.println(response);

		StringReader sr = new StringReader(response);
		ClassLoader cl = svrs.schema.search.ObjectFactory.class
				.getClassLoader();
		JAXBContext jc = JAXBContext.newInstance("svrs.schema.searchres", cl);
		// JAXBContext jc =
		// JAXBContext.newInstance(svrs.schema.searchres.Searchresults.class);
		Unmarshaller u = jc.createUnmarshaller();
		// We don't need to validate data coming from the SVRS...
		// u.setSchema(SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new
		// File(svrs.util.ConfigFileHandler.getInstance().getSchemaPath() +
		// "/SearchQuerySchema.xsd")));
		searchres = (svrs.schema.searchres.Searchresults) u.unmarshal(sr);

		return searchres;
	}

	/**
	 * This function fetches the latest version of a resource with the given
	 * UUID from the SVRS.
	 * 
	 * @param id
	 *            The UUID as a String.
	 * @return A svrs.schema.download.Resource object that contains the SVRS
	 *         resource.
	 * @throws MalformedURLException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws JAXBException
	 * @throws EncoderException
	 */
	public svrs.schema.download.Resource downloadResource(String id)
			throws MalformedURLException, NoSuchAlgorithmException,
			KeyManagementException, IOException, GeneralSecurityException,
			JAXBException {
		svrs.schema.download.Resource dlres = null;

		// Create download request
		UUID uuid = UUID.fromString(id);
		HttpsURLConnection connection = Connect(uri + "/resource/"
				+ uuid.toString());
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		// SVRS is in sandbox mode, so endorsement is disabled and
		// aniketos:aniketos will work.
		//String authdata = Base64.encodeBytes("aniketos:aniketos".getBytes());
		String cred = username + ":" + password;
		String authdata = Base64.encodeBytes(cred.getBytes());
		connection.setRequestProperty("Authorization", "Basic " + authdata);
		// Send request to SVRS
		connection.connect();

		String response = SVRSConnectionHandler.getInstance().getResp(
				connection);
		connection.disconnect();
		// System.out.println(response);

		// Unmarshal XML into Resource object
		dlres = new svrs.schema.download.Resource();
		StringReader dsr = new StringReader(response);
		ClassLoader cl = svrs.schema.download.ObjectFactory.class
				.getClassLoader();
		JAXBContext djc = JAXBContext.newInstance("svrs.schema.download", cl);
		Unmarshaller du = djc.createUnmarshaller();
		// We don't need to validate data coming from the SVRS...
		// u.setSchema(SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new
		// File(svrs.util.ConfigFileHandler.getInstance().getSchemaPath() +
		// "/SearchQuerySchema.xsd")));
		dlres = (svrs.schema.download.Resource) du.unmarshal(dsr);

		return dlres;
	}

	/**
	 * This function sends a new or updated resource (threat or countermeasure)
	 * to the SVRS and returns the result of the operation.
	 * 
	 * @param resource
	 *            The XML string (marshalled JAXB resource) to send to the SVRS.
	 * @param id
	 *            The UUID as a String.
	 * @return A String that contains the HTTP status code along with some text
	 *         describing the result of the operation.
	 * @throws MalformedURLException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws JAXBException
	 * @throws EncoderException
	 */
	public String uploadResource(String resource, String id)
			throws MalformedURLException, NoSuchAlgorithmException,
			KeyManagementException, IOException, GeneralSecurityException,
			JAXBException {
		String result = null;

		// Create upload request
		UUID uuid = UUID.fromString(id);
		HttpsURLConnection connection = Connect(uri + "/resource/"
				+ uuid.toString());
		connection.setRequestMethod("PUT");
		connection.setDoOutput(true);
		// SVRS is in sandbox mode, so endorsement is disabled and
		// aniketos:aniketos will work.
		//String authdata = Base64.encodeBytes("aniketos:aniketos".getBytes());
		String cred = username + ":" + password;
		String authdata = Base64.encodeBytes(cred.getBytes());
		connection.setRequestProperty("Authorization", "Basic " + authdata);
		connection.connect();
		// Send request to SVRS
		OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream());
		out.write(resource);
		out.flush();

		result = "Response: HTTP "
				+ Integer.toString(connection.getResponseCode()) + " - "
				+ connection.getResponseMessage();
		/*
		 * BufferedReader in = new BufferedReader(new
		 * InputStreamReader(connection.getInputStream())); String responseLine;
		 * while ((responseLine = in.readLine()) != null) { result =
		 * result.concat(responseLine + "\n"); }
		 */
		connection.disconnect();

		return result;
	}

	/**
	 * This function deletes a resource from the SVRS.
	 * 
	 * @param id
	 *            The UUID of the resource to delete.
	 * @return A String that contains the HTTP status code along with some text
	 *         describing the result of the operation.
	 * @throws MalformedURLException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws EncoderException
	 */
	public String deleteResource(String id) throws MalformedURLException,
			NoSuchAlgorithmException, KeyManagementException, IOException,
			GeneralSecurityException {
		String result = null;

		// Create deletion request for 'version 0' (-> most recent version)
		UUID uuid = UUID.fromString(id);
		HttpsURLConnection connection = Connect(uri + "/resource/"
				+ uuid.toString() + "/0");
		connection.setRequestMethod("DELETE");
		connection.setDoOutput(false);
		// SVRS is in sandbox mode, so endorsement is disabled and
		// aniketos:aniketos will work.
		//String authdata = Base64.encodeBytes("aniketos:aniketos".getBytes());
		String cred = username + ":" + password;
		String authdata = Base64.encodeBytes(cred.getBytes());
		connection.setRequestProperty("Authorization", "Basic " + authdata);
		connection.connect();
		// Send request to SVRS
		result = "Response: HTTP "
				+ Integer.toString(connection.getResponseCode()) + " - "
				+ connection.getResponseMessage();

		connection.disconnect();

		return result;
	}
	
	
	/**
	 * This function fetches the current list of tags from the SVRS.
	 * 
	 * @return A Taglist object containing the tag list.
	 * @throws MalformedURLException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws JAXBException
	 * @throws GeneralSecurityException
	 * @throws EncoderException
	 */
	public svrs.schema.taglist.Taglist downloadTaglist() throws MalformedURLException,
	NoSuchAlgorithmException, KeyManagementException, IOException,
	JAXBException, GeneralSecurityException {

		// Create deletion request for 'version 0' (-> most recent version)
		HttpsURLConnection connection = Connect(uri + "/tags");
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		// SVRS is in sandbox mode, so endorsement is disabled and
		// aniketos:aniketos will work.
		//String authdata = Base64.encodeBytes("aniketos:aniketos".getBytes());
		String cred = username + ":" + password;
		String authdata = Base64.encodeBytes(cred.getBytes());
		connection.setRequestProperty("Authorization", "Basic " + authdata);
		connection.connect();
		// Send request to SVRS
		String response = SVRSConnectionHandler.getInstance().getResp(connection);
		connection.disconnect();

		// Unmarshal XML into Taglist object
		svrs.schema.taglist.Taglist dlres = new svrs.schema.taglist.Taglist();
		StringReader dsr = new StringReader(response);
		ClassLoader cl = svrs.schema.taglist.ObjectFactory.class
				.getClassLoader();
		JAXBContext djc = JAXBContext.newInstance("svrs.schema.taglist", cl);
		Unmarshaller du = djc.createUnmarshaller();
		dlres = (svrs.schema.taglist.Taglist) du.unmarshal(dsr);
		
		connection.disconnect();

		return dlres;
	}
}
