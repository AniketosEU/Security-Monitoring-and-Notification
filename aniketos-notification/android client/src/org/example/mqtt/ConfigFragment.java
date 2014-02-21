package org.example.mqtt;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ConfigFragment extends Fragment implements OnClickListener{

	
	Button connectButton = null;
	Button disconnectButton = null;
	private final String TAG = "Config_fragment";

	EditText address = null;
	
	static final int DISCONNECT = 0;
	static final int CONNECT = 1;
	
    // input is true if connected and false if disconnected
    public void setConnectButtons(boolean connect){
		connectButton.setEnabled(!connect);
		disconnectButton.setEnabled(connect);
		address.setEnabled(!connect);
    }

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.conf, container, false);
        setupView(rootView);
        Log.d(TAG, "On Create View");
        return rootView;
    }
	
	   public void setupView(View rootView)
	    {
	    	
	    	
	    	Activity parent = getActivity();
			MqttApplication appHandler = (MqttApplication) parent.getApplication();
	    	
	    	connectButton = (Button) rootView.findViewById(R.id.connectButton);
	    	connectButton.setOnClickListener(this);
	    	
	    	disconnectButton = (Button) rootView.findViewById(R.id.disconnectButton);
	    	disconnectButton.setOnClickListener(this);
	    	
	    	address = (EditText) rootView.findViewById(R.id.url_value);
	    	address.setText(appHandler.getAddress());
	    	

			this.setConnectButtons(appHandler.isConnection());
	    	
	    }
	   
		public void onClick(View v) {
			if(v == connectButton)
			{	
				
				// TODO: move those checks to the connect function!!
				// check if there is internet connectivity
				MainActivity activeHandler = (MainActivity) getActivity();
				if(activeHandler.isOnline()== false){
			    	Context context = getActivity();
			    	int duration = Toast.LENGTH_LONG;
	
			    	Toast toast = Toast.makeText(context, "please ensure you are connected to the internet", duration);
			    	toast.show();
			    	return;
				}
				
				
				// check if app has been configured, in other words, if some subscriptions have been added
	        	SharedPreferences keyValues = getActivity().getSharedPreferences(MqttApplication.sharedPrefName, Context.MODE_PRIVATE);
	        	if (keyValues.getAll().isEmpty()){
	        		toast("you should first add some subscriptions on the config tab");
	        	}else{
					connect();
	        	}
			}
			
			if(v == disconnectButton)
			{
				disconnect();
			}
			

		}
		

		
		private void connect()
		{
			MainActivity activeHandler = (MainActivity) getActivity();
			MqttApplication appHandler = (MqttApplication) activeHandler.getApplication();
			appHandler.setAddress(address.getText().toString());
			activeHandler.sendMessageToMQTTservice(MQTTSubscriberService.MSG_CONNECT);
		}
		private void toast(String message)
		{
			Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
		}
		
		
		private void disconnect()
		{
			MainActivity activeHandler = (MainActivity) getActivity();
			activeHandler.sendMessageToMQTTservice(MQTTSubscriberService.MSG_DISCONNECT);
		}
	

	
}
