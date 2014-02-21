package org.example.mqtt;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


import org.example.mqtt.data.NotificationContentProvider;
import org.example.mqtt.data.NotificationData;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Application;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MQTTSubscriberService extends Service {

	private final String TAG = "MQTTSubscriberService";
	
	private final String MQTT_ALL_WILDCARD_SUFFIX = ".>";

    /** For showing and hiding our notification. */
	Handler threadHandler;
	MQTT mqtt = null;
	
	CallbackConnection  connection = null;
	
	public static final int MSG_BIND = 0;
	public static final int MSG_CONNECT = 1;
	public static final int MSG_DISCONNECT = 2;
	public static final int MSG_RECONNECT = 3;
	public static final int MSG_SUBSCRIBE = 4;
	public static final int MSG_UNSUBSCRIBE = 5;
	
    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    static final int MSG_UNREGISTER_CLIENT = 2;
	
	
	// handler for communication with main activity
	
    Messenger mClient;
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mqttServiceMessenger = new Messenger(new IncomingHandler());
    /**
     * Handler of incoming messages from clients
     * As I am expecting to communicate only with the Main activity thread
     * I will not get the replyTo from the message all the time to know who
     * to answer to
     */
    
    
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(TAG, "message received by service");
            switch (msg.what) {
	            case MSG_BIND:
	            	mClient = msg.replyTo;
	                break;            
                case MSG_CONNECT:
                	connect();
                    break;
                case MSG_DISCONNECT:
                	disconnect();
                    break;
                case MSG_SUBSCRIBE:
                   String topic = msg.getData().getString("topic");
                   if(null != topic) subscribeTopic(topic);
                    break;
                case MSG_UNSUBSCRIBE:
                    String t = msg.getData().getString("topic");
                    if(null != t) unsubscribeTopic(t);                   
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
	
	
	
	@Override
    public void onCreate() {
        // Handler will get associated with the current thread, 
        // which is the main thread.
	   threadHandler = new Handler();
	   Log.d(TAG, "service created");
        super.onCreate();
    }

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "service bound");
		return mqttServiceMessenger.getBinder();
	}
	

	public void connect()
	{
		MqttApplication appHandler = (MqttApplication) getApplication();
		mqtt = new MQTT();
		mqtt.setKeepAlive((short)2);

		try
		{
			mqtt.setHost(appHandler.getAddress());
			Log.d(TAG, "Address set: " + appHandler.getAddress());
		}
		catch(URISyntaxException urise)
		{
			Log.e(TAG, "URISyntaxException connecting to - " + urise);
		}
	    TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
	    mqtt.setClientId(tm.getDeviceId());
		mqtt.setCleanSession(false);
		connection = mqtt.callbackConnection();
		connection.connect(onui(new Callback<Void>(){
			public void onSuccess(Void value) {
				//connectButton.setEnabled(false);
				Log.d(TAG, "on success connection");
				connection.listener(new MessageListener());
				//toast("Connected");
				
        		MqttApplication appHandler = (MqttApplication) getApplication();
        		appHandler.setConnection(true);
        		
                Message msg = Message.obtain(null,
                		MainActivity.MSG_CONNECTED);
                msg.replyTo = mqttServiceMessenger;
                try {
					mClient.send(msg);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				// get topics from shared preferences to subscribe
				
		    	SharedPreferences keyValues = getSharedPreferences(MqttApplication.sharedPrefName, Context.MODE_PRIVATE);
		    	Map<String, String> initialSubs = (Map<String, String>) keyValues.getAll();
		    	Topic[] topics = new Topic[initialSubs.size()]; 

		    	int i= 0;
		    	for (Map.Entry<String, String> entry : initialSubs.entrySet()){
		    		topics[i] = new Topic(UTF8Buffer.utf8("pub." + entry.getKey() + MQTT_ALL_WILDCARD_SUFFIX), QoS.EXACTLY_ONCE);
		    		Log.d(TAG, "Added topic " + topics[i].toString());
		    		i++;
		    	}
							
				// now trying to subscribe
				connection.subscribe(topics,onui (new OnsubscribeCallback()));
				
			}
			public void onFailure(Throwable e) {
				Log.d(TAG, "on failure connection ");
				Log.e(TAG, "Exception connecting "  + e);
                Message msg = Message.obtain(null,
                		MainActivity.RECONNECT_TIMEOUT_ON_SERVER);
                msg.replyTo = mqttServiceMessenger;
                try {
					mClient.send(msg);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}));

	}
	
	private void subscribeTopic (String topic){
    	Topic[] topics = new Topic[1]; 
    	topics[0] = new Topic(UTF8Buffer.utf8("pub." + topic + MQTT_ALL_WILDCARD_SUFFIX), QoS.EXACTLY_ONCE);
					
		// now trying to subscribe
    	Log.d(TAG, "calling subscribe to " + topics[0].toString());
		connection.subscribe(topics,onui (new OnsubscribeCallback()));
		
	}
	
	private void unsubscribeTopic (String topic){
    	UTF8Buffer[] topics = new UTF8Buffer[1]; 
    	topics[0] = new UTF8Buffer("pub." + topic + MQTT_ALL_WILDCARD_SUFFIX);
    	Log.d(TAG, "calling unsubscribe" + topics[0].toString());
		connection.unsubscribe(topics, onui(new UnsubscribeCallback()));
	}

	
	
	private void disconnect()
	{
		//connectButton.setEnabled(true);
		try
		{
			if(connection != null )
			{
				
				
				// get topics from shared preferences to subscribe
				// notice that we append a "pub." before the topic URI
				// and a ".>" after the topic
				
		    	SharedPreferences keyValues = getSharedPreferences(MqttApplication.sharedPrefName, Context.MODE_PRIVATE);
		    	Map<String, String> initialSubs = (Map<String, String>) keyValues.getAll();
		    	UTF8Buffer[] topics = new UTF8Buffer[initialSubs.size()]; 

		    	int i= 0;
		    	for (Map.Entry<String, String> entry : initialSubs.entrySet()){
		    		topics[i] = new UTF8Buffer("pub." + entry.getKey() + MQTT_ALL_WILDCARD_SUFFIX);
		    		Log.d(TAG, "Unsubscribed topic" + entry.getKey());
		    		i++;
		    	}
					
				
				
				connection.unsubscribe(topics, onui(new UnsubscribeCallback()));
				connection.disconnect(onui(new Callback<Void>(){
					public void onSuccess(Void value) {
						// no need to flag to the activity as
						// the listener disconnect will do it
			           Log.e(TAG, "disconnected gracefully" );

					}
					public void onFailure(Throwable e) {
						Log.e(TAG, "Exception disconnecting " + e);
					}
				}));
			}

		}
		catch(Exception e)
		{
			Log.e(TAG, "Exception " + e);
		}
	}
	
	
	
	// subscribed callback
	private class OnsubscribeCallback  implements Callback <byte[]> {
		public void onSuccess(byte[] subscription) {
			
			Log.d(TAG, "subscribed");//TODO complement this log
            Message msg = Message.obtain(null,
            		MainActivity.SUBSCRIPTION_DONE);
            msg.replyTo = mqttServiceMessenger;
            try {
				mClient.send(msg);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}	
			
				
		public void onFailure(Throwable e) {
			connection.suspend();// perhaps change for disconnect
    		MqttApplication appHandler = (MqttApplication) getApplication();
    		appHandler.setConnection(false);
			//connectButton.setEnabled(true);
			Log.e(TAG, "Exception when subscribing, disconnecting: " + e);
		}

	}
	
	// unsubscribed callback
	private class UnsubscribeCallback  implements Callback <Void> {
		public void onSuccess(Void subscription) {
			
			Log.d(TAG, "Unsubscription worked");
            Message msg = Message.obtain(null,
            		MainActivity.UNSUBSCRIPTION_DONE);
            msg.replyTo = mqttServiceMessenger;
            try {
				mClient.send(msg);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}	
			
				
		public void onFailure(Throwable e) {
			Log.d(TAG, "Unsubscription failed");
		}

	}
	
	

	
	// listener
	
	private class MessageListener implements Listener {

		// apparently this is the function that sense a disconnection first
	    public void onDisconnected() {
	    	Log.d(TAG, "got disconnected");
	    	//connectButton.setEnabled(true);
			connection.suspend();// perhaps change for disconnect
    		MqttApplication appHandler = (MqttApplication) getApplication();
    		appHandler.setConnection(false);
            Message msg = Message.obtain(null,
            		MainActivity.MSG_DISCONNECTED);
            msg.replyTo = mqttServiceMessenger;
            try {
				mClient.send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    	
	    }
	    public void onConnected() {
	    	Log.d(TAG, "got connected on message listener");
	    }

	    public void onPublish(UTF8Buffer topic, Buffer payload, Runnable ack) {
	    	Log.d(TAG, "on publish called");
	    	
	    	
	    	
	        // You can now process a received message from a topic
			String fullPayLoad = new String(payload.data); // I did not find documentation on this, 
			Log.d(TAG, "String size is " + fullPayLoad.length() + " , and data size is " + payload.length);
			
			// but the payload seems to in fact consists of 0x32 0xlen (maybe more than a byte) 0x(topic) 0x(message number - in 2 bytes) 0x(message)   
			String receivedMesageTopic =  topic.toString();
            String[] fullPayLoadParts = fullPayLoad.split(receivedMesageTopic);// TODO: I should probably check if there are characters that needs to be scaped
                        
            Log.d(TAG, "fullpayload = " + fullPayLoad);
            if(fullPayLoadParts.length == 2){
            	// sometimes the payload includes the message ID (2 bytes), sometimes it doesnt....
            	// if the first character is a "{" then it didnt
            	String messagePayLoad;
            	if(fullPayLoadParts[1].charAt(0) == '{')
            		messagePayLoad = fullPayLoadParts[1];
            	else
            		messagePayLoad = fullPayLoadParts[1].substring(2);
                String val = this.insertMessage(messagePayLoad,receivedMesageTopic);
                // TODO: SEND AN UPDATE MESSAGE TO ACTIVITY
            }
			
	        // Once process execute the ack runnable.
	        ack.run();
	    }
	    public void onFailure(Throwable value) {
			connection.suspend();// perhaps change for disconnect
			Log.d(TAG, "On failure in the listener...");
			// TODO: check if the disconnect is called of if I need to send
			// a disconect message to the Activity
			
	    }

	    // parse the json message, instert it on the db and return the value
	    private String insertMessage(String messagePayLoad, String receivedMesageTopic){
	    	String value = null;
	    	try {
				JSONObject jObject = new JSONObject(messagePayLoad);
				
				
				// time convertion test
				  String timeInString = jObject.getString("serverTime");
				  try {
					    SimpleDateFormat format =
					        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
					    Date parsed = format.parse(timeInString);
					    Calendar c = Calendar.getInstance();
					    c.setTime(parsed);
					    // TODO: remove the 3 lines of debuging below
					    Log.d(TAG, "Date time in millis " + c.getTime().getTime());
					    String formatted = format.format(c.getTime());
					    Log.d(TAG, "String date " + formatted);
					    
					    
					    // real inserting
						  ContentValues values = new ContentValues();
						  values.put(NotificationData.SERVICE_ID, jObject.getString("serviceId"));
						  values.put(NotificationData.ALERT_TYPE, jObject.getString("alertType"));
						  values.put(NotificationData.DESCRIPTION, jObject.getString("description"));
						  values.put(NotificationData.SERVER_TIME, c.getTime().getTime());		  
						  values.put(NotificationData.VALUE, jObject.getString("value"));
						  values.put(NotificationData.THREAT_ID, jObject.getString("threatId"));
						  values.put(NotificationData.THRESHOLD, jObject.getInt("threshold"));
						  values.put(NotificationData.SERVICE_FULL_URI, receivedMesageTopic);
						  

						  
						
						Uri instertedUri = getContentResolver().insert(NotificationContentProvider.CONTENT_URI, values);
						// TODO: handle failure on content provider and on main code  

						
						value = jObject.getString("value");
					    
					    
					}
					catch(ParseException pe) {
					    throw new IllegalArgumentException();
					}
				
				

				
			} catch (JSONException e) {
				Log.d(TAG, "Failure parsing json message + " + messagePayLoad);
				e.printStackTrace();
			}
	    	
	    	return value;
	    }
	
	}

	// callback used for Future
	<T> Callback<T> onui(final Callback<T> original) {
		return new Callback<T>() {
			public void onSuccess(final T value) {
				threadHandler.post(new Runnable(){
					public void run() {
						original.onSuccess(value);
					}
				});
			}
			public void onFailure(final Throwable error) {
				threadHandler.post(new Runnable(){
					public void run() {
						original.onFailure(error);
					}
				});
			}
		};
	}


	
}
