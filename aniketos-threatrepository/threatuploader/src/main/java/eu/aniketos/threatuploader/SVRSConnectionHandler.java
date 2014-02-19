/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.aniketos.threatuploader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/*import eu.aniketos.threatrepository.impl.SVRSConnectionHandler;
import eu.aniketos.threatrepository.impl.svrs;*/
import eu.aniketos.threatuploader.util.Base64;

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
	
	private String username = "aniketos";
	private String password = "aniketos";
	private String proxyName = null;
	private Integer proxyPort = null;
	private String proxyUser = null;
	private String proxyPass = null;

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
		String authdata = Base64.encodeBytes(new String(username + ":" + password).getBytes());
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

	/** This function retrieves the current list of tags (along with their usages) from the SVRS.
	 * 
	 * @return an svrs.schema.taglist.Taglist object representing all of the tags in the SVRS.
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws MalformedURLException 
	 * @throws KeyManagementException 
	 * @throws JAXBException 
	 */
	public svrs.schema.taglist.Taglist getTagList() throws KeyManagementException, MalformedURLException, NoSuchAlgorithmException, IOException, GeneralSecurityException, JAXBException {

		// Create upload request
		HttpsURLConnection connection = Connect(uri + "/tags");
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		// SVRS is in sandbox mode, so endorsement is disabled and
		// aniketos:aniketos will work.
		String authdata = Base64.encodeBytes(new String(username + ":" + password).getBytes());
		connection.setRequestProperty("Authorization", "Basic " + authdata);
		connection.connect();
		// Send request to SVRS
		String res = "Response: HTTP "
				+ Integer.toString(connection.getResponseCode()) + " - "
				+ connection.getResponseMessage();
		System.out.println(res);
		String response = getResp(connection);
		connection.disconnect();

		// Unmarshal XML into Taglist object
		svrs.schema.taglist.Taglist dlres = new svrs.schema.taglist.Taglist();
		StringReader dsr = new StringReader(response);
		ClassLoader cl = svrs.schema.taglist.ObjectFactory.class
				.getClassLoader();
		JAXBContext djc = JAXBContext.newInstance("svrs.schema.taglist", cl);
		Unmarshaller du = djc.createUnmarshaller();
		dlres = (svrs.schema.taglist.Taglist) du.unmarshal(dsr);
		
		/*
		 * BufferedReader in = new BufferedReader(new
		 * InputStreamReader(connection.getInputStream())); String responseLine;
		 * while ((responseLine = in.readLine()) != null) { result =
		 * result.concat(responseLine + "\n"); }
		 */

		return dlres;		
	}
}
