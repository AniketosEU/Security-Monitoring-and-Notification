package org.example.mqtt;

import java.util.ArrayList;
import java.util.Map;

import org.example.mqtt.data.NotificationContentProvider;
import org.example.mqtt.data.NotificationData;
import org.example.mqtt.model.NotifService;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


// TODO: investigate the possibility to use loaders for the list
public class ServicesListFragment extends ListFragment  {

	private final String TAG = "ServicesListFragment";
	Button addButton = null;
	
	ServiceNotifRowAdapter adapter;

    boolean is_phone;
	
	// to be called if one wants to change the dataset
	// basically to be used by the main activity after the Service Add dialog
	public void notifyServiceListChange(){
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		is_phone=  getResources().getBoolean(R.bool.is_phone);
		
		Activity parent = getActivity();
		MqttApplication app = (MqttApplication) parent.getApplication();

    	adapter = new ServiceNotifRowAdapter(getActivity().getApplicationContext(),app.getServiceList());
	    setListAdapter(adapter);
	    Log.d(TAG, "On Create");
	}
	
	  @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
	        View rootView = inflater.inflate(R.layout.serv_list_fragment, container, false);

	       	addButton = (Button) rootView.findViewById(R.id.addServiceButton);
	       	OnClickListener m = (OnClickListener) getActivity(); // the main acitivy will handle the adition of a service as it will
	       	// display a fragment for that
	    	addButton.setOnClickListener(m);
	    	Log.d(TAG, "On CreateView");
	        return rootView;
	    }
	  
	  @Override
	  public void onListItemClick(ListView l, View v, int position, long id) {
	    // do something with the data
		  NotifService  item = (NotifService) getListAdapter().getItem(position);
		  Log.d(TAG, "selected service " + item.getServiceURI());
		//  MainActivity m = (MainActivity) getActivity();
		//  m.showServiceSpecificNotifications(item.getServiceURI());
		  
		if (is_phone){
			Fragment newFragment = new ServiceSpecifNotListFragment();
			Bundle bundle = new Bundle();
			bundle.putString(MqttApplication.SERVICE_URI_BUNDLE_TAG, item.getServiceURI());
			bundle.putString(MqttApplication.SERVICE_NAME_BUNDLE_TAG, item.getServiceName());
			newFragment.setArguments(bundle);
	        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
	        transaction.addToBackStack(null);
	        transaction.replace(R.id.servicelist_fragment, newFragment).commit();
		}
		else{
			MainActivity parent = (MainActivity) getActivity();
	        parent.showServiceSpecificNotifications(item.getServiceURI(), item.getServiceName());// TODO: replace for
	        // callback so the fragment code is not dependent on MainActivity
			
		}
        
        /*
         * 		  MainActivity m = (MainActivity) getActivity();
		  m.replaceFragmentToServiveListNot(item.getServiceURI());
         * 
         */
		  
	  }

		private void toast(String message)
		{
			Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
		}


		@Override
		public void onDestroyView(){
			super.onDestroyView();
			Log.d(TAG, "on destroy view");
		}
		
		@Override
		public void onDestroy(){
			super.onDestroy();
			Log.d(TAG, "on destroy");
		}	
		
}
