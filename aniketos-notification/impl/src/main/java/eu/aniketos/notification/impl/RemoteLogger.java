package eu.aniketos.notification.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import eu.aniketos.notification.Notification;


public class RemoteLogger implements Runnable {

	
	public static final String LOGGER_URL = "http://aniketos.sintef9013.com/notification/"; //infosec.sintef.no/notifications/
	
	private Notification alert;

	
	public RemoteLogger(Notification alert) {
		this.alert = alert;
	}


	private String urlEncode(String input) {
		if (input == null)
			return "";
		try {
			return URLEncoder.encode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	
	@Override
	public void run() {
			
		try {
	        URL url = new URL(LOGGER_URL);
	        String query = "importance=" + urlEncode(alert.getImportance()+"");
			query += "&serviceId=" + urlEncode(alert.getServiceId());
			query += "&type=" + urlEncode(alert.getAlertType());
			query += "&value=" + urlEncode(alert.getValue());
			query += "&description=" + urlEncode(alert.getDescription());
			query += "&threatId=" + urlEncode(alert.getThreatId());
			query += "&serverTime=" + urlEncode(alert.getServerTime());
			query += "&messageId=" + urlEncode(alert.getMessageId());
	        // Create connection
	        URLConnection urlc = url.openConnection();
	        // Use POST mode
	        urlc.setAllowUserInteraction(false);
	        urlc.setDoOutput(true);
	
	        // Send query
	        PrintStream ps = new PrintStream(urlc.getOutputStream());
	        ps.print(query);
	        ps.close();
//	        System.out.println(LOGGER_URL + "/?" + query);
	
//		        // Get result
	        BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
//		        String l = null;
//		        while ((l=br.readLine())!=null) {
//		            System.out.println(l);
//		        }
//		        br.close();
		} catch (Exception ex) {
//			ex.printStackTrace();
			// SIMPLY DO NOTHING - logging is not of high priority, at least for now.
		}
	}
}
