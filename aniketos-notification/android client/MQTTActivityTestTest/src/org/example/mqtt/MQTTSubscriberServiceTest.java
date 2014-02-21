package org.example.mqtt;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.example.mqtt.MQTTSubscriberService;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

public class MQTTSubscriberServiceTest extends
		ServiceTestCase<MQTTSubscriberService> {

	


    CountDownLatch latch;
/*
 
    final Messenger clientMessenger = new Messenger(new MainActMsgHandler());
    static final int MSG_CONNECTED = 1;
    static final int MSG_DISCONNECTED = 2;
    static final int MSG_NEW_MESSAGE = 3;
    
    
    
    class MainActMsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CONNECTED:
                	latch.countDown();
                	Log.i(TAG, "connected");
                    break;
                case MSG_DISCONNECTED:
                    break;
                case MSG_NEW_MESSAGE:
                    
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    
	
	Messenger mServer = null;*/
    MQTTSubscriberService mService;
	
    private static final String TAG = "MQTTSubscriberServiceTest";
    
	
	public MQTTSubscriberServiceTest() {
		super(MQTTSubscriberService.class);
		// TODO Auto-generated constructor stub
	}
	   @Override
	    public void setUp() {
	        try {
	            Log.i(TAG, "setUp()");
	            super.setUp();


	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	   @Override
	    public void tearDown() {
	        try {
	            super.tearDown();
	            Log.i(TAG, "tearDown()");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    @LargeTest
	    public void testConnection() {
	        Log.i(TAG, "testAHello");

	        // binding
        	Intent bindingIntent = new  Intent();
        	bindingIntent.setClass(getContext(), MQTTSubscriberService.class);
        	IBinder binder = this.bindService(bindingIntent);
            assertNotNull(binder);
            mService = this.getService();
            
            mService.connect();
            try {
				TimeUnit.SECONDS.sleep(360);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            assertNotNull(mService.mqtt);
            Log.i(TAG, "passed the connection assert");
/*            mServer = new Messenger(binder);
	        assertNotNull(mServer);

	        try {
	        // send the binding callback
		        Message msg = Message.obtain(null,
	            		MQTTSubscriberService.MSG_BIND);
	            msg.replyTo = clientMessenger;
	            mServer.send(msg);
	            Log.i(TAG, "sent the binding");
	            
		        // connect
	            msg = Message.obtain(null,
	            		MQTTSubscriberService.MSG_CONNECT);
		        msg.replyTo = clientMessenger;
		        mServer.send(msg);
		        Log.i(TAG, "sent the binding");
		        
		        latch = new CountDownLatch(1);
		        boolean await = latch.await(120, TimeUnit.SECONDS);

		        assertTrue(await);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }*/

	    }

}
