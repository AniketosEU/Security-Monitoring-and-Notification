package org.example.mqtt.model;

import java.io.Serializable;

public class NotifService implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3605829973365018575L;
	
	String serviceURI;
	String serviceName;
	
	
	
	public NotifService(String serviceURI, String serviceName) {
		super();
		this.serviceURI = serviceURI;
		this.serviceName = serviceName;
	}
	
	public String getServiceURI() {
		return serviceURI;
	}
	public void setServiceURI(String serviceURI) {
		this.serviceURI = serviceURI;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	


}
