package org.example.mqtt;

import java.util.ArrayList;
import java.util.Map;

import org.example.mqtt.data.NotificationContentProvider;
import org.example.mqtt.data.NotificationData;
import org.example.mqtt.model.NotifService;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MqttApplication extends Application {
	
	private static final String TAG = "MQTTApplication";

	public static final int STATUS_LIST_LOADER = 0x01;
	public static final int SERVICE_SPECIFIC_LIST_LOADER = 0x02;
	public static String SERVICE_NAME_BUNDLE_TAG = "service";
	public static String SERVICE_URI_BUNDLE_TAG = "uri";
	
	public static String sharedPrefName = "subscriptionList"; 
	private String address = "tcp://83.212.116.137:1883"; // TODO: possibly add an input for this
	
	private ArrayList<NotifService> serviceList = new ArrayList<NotifService>();
	
	//public static String StatusListFragTag = "status_list_frag_tag";
	//public static String ServicesListFragTag = "services_list_frag_tag";
	//public static String ConfigFragTag = "config_frag_tag";
	
	
	// during the creation of the application, we retrieve the service list from the shared preferences
	 @Override
	public void onCreate (){
		 super.onCreate();
		 
	   	// populating adapter with list of stored subscriptions
    	SharedPreferences keyValues = getSharedPreferences(MqttApplication.sharedPrefName, Context.MODE_PRIVATE);
    	Map<String, String> initialSubs = (Map<String, String>) keyValues.getAll();
    	
    	for (Map.Entry<String, String> entry : initialSubs.entrySet()){
    		serviceList.add(new NotifService(entry.getKey(),entry.getValue()));
    	}
	    	
	
	}
	
	public static final int ADD_SERVICE_OK = 0x00;
	public static final int ADD_SERVICE_ERR_EXISTING_SERVICE_URI = 0x01;
	public static final int ADD_SERVICE_ERR_EXISTING_SERVICE_NAME = 0x02;
	 
	public int addService(String serviceName, String serviceURI){
		
		for(NotifService serv : serviceList) {
		    if(serv.getServiceName() != null && serv.getServiceName().equals(serviceName)) {
		    	return ADD_SERVICE_ERR_EXISTING_SERVICE_NAME;
		     }
		    if(serv.getServiceURI() != null && serv.getServiceURI().equals(serviceURI)) {
		    	return ADD_SERVICE_ERR_EXISTING_SERVICE_URI;
		     }
		}
		
    	SharedPreferences keyValues = getSharedPreferences(MqttApplication.sharedPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor keyValuesEditor = keyValues.edit();
    	keyValuesEditor.putString(serviceURI,serviceName);
    	keyValuesEditor.commit();
    	serviceList.add(new NotifService(serviceURI,serviceName));

    	return ADD_SERVICE_OK;
	}

	public String getServiceNameFromURI(String serviceURI){
		
		for(NotifService serv : serviceList) {
		    if(serv.getServiceURI() != null && serv.getServiceURI().equals(serviceURI)) {
		    	return serv.getServiceName();
		     }
		}
    	return null;
	}
	
	public ArrayList<NotifService> getServiceList() {
		return serviceList;
	}


	public boolean deleteService(String serviceName, boolean clearNotif){
		
		for(NotifService serv : serviceList) {
		    if(serv.getServiceName() != null && serv.getServiceName().equals(serviceName)) {
		    	
		    	if(clearNotif){
			    	// clear the notifications
			    	String mSelectionClause = NotificationData.SERVICE_ID + " LIKE ?";
			    	String[] mSelectionArgs = {serv.getServiceURI()};
			    	int mRowsDeleted = getContentResolver().delete(NotificationContentProvider.CONTENT_URI, 
			    	    mSelectionClause, mSelectionArgs);
			    	Log.d(TAG, mRowsDeleted + " notifications from " + serviceName + " deleted from DB");
		    	}
		    	
		    	// remove the service from the shared preferences
		    	SharedPreferences keyValues = getSharedPreferences(MqttApplication.sharedPrefName, Context.MODE_PRIVATE);
		        SharedPreferences.Editor keyValuesEditor = keyValues.edit();
		        keyValuesEditor.remove(serv.getServiceURI());
		        keyValuesEditor.commit();
		        return serviceList.remove(serv);
		     }
		}
		return false;
	}

	
	

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean connection = false;
	
	
	
    public boolean isConnection() {
		return connection;
	}



	public void setConnection(boolean connection) {
		this.connection = connection;
	}

	public static final String statusTab = "Status";
	public static final String servicesTab = "Services";
	public static final String settingsTab = "Settings" ;

	// Tab titles
    public static String[] tabs = { statusTab, servicesTab,settingsTab };

	
}
