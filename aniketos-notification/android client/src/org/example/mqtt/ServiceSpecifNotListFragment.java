package org.example.mqtt;

import org.example.mqtt.data.NotificationContentProvider;
import org.example.mqtt.data.NotificationCursorAdapter;
import org.example.mqtt.data.NotificationData;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ServiceSpecifNotListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	private final String TAG = "ServiceSpecifNotListFragment";
	Button deleteServiceButton = null;
	String serviceUri;
	String servName;
	TextView serviceName = null;
	AlertDialog alertDeleteService;
	AlertDialog alertClearDb;
	
	private NotificationCursorAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
			String[] uiBindFrom = { NotificationData.ALERT_TYPE, NotificationData.DESCRIPTION, NotificationData.VALUE,NotificationData.SERVER_TIME };
		    int[] uiBindTo = { R.id.serv_type, R.id.serv_desc,R.id.notif_val ,R.id.notif_date  }; // from service_specific_notif.row

		    getLoaderManager().initLoader(MqttApplication.SERVICE_SPECIFIC_LIST_LOADER, null, this);
		    adapter = new NotificationCursorAdapter(
		            getActivity().getApplicationContext(), R.layout.service_specifc_notif_row,
		            null, uiBindFrom, uiBindTo,
		            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		    setListAdapter(adapter);
		    
		    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   Log.d(TAG, "deleting service ");
	                    dialog.dismiss();
	                    alertClearDb.show();
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	               }
	           });
		    alertDeleteService = builder.create();
		    
		    AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
		    builder2.setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   Log.d(TAG, "deleting notifications ");
	                    dialog.dismiss();
	                    MainActivity m = (MainActivity)getActivity();
	                    MqttApplication app = (MqttApplication) m.getApplication();
	                    app.deleteService(servName,true);
	                    m.notifyServiceListChanged(false,serviceUri); // TODO: change those by callbacks to the activity (not done yet because I need 
	                    // to think a nice way to do the back)
	                    m.onBackPressed();
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                    dialog.dismiss();
	                    MainActivity m = (MainActivity)getActivity();
	                    MqttApplication app = (MqttApplication) m.getApplication();
	                    app.deleteService(servName,false);
	                    m.notifyServiceListChanged(false,serviceUri); // TODO: change those by callbacks to the activity (not done yet because I need 
	                    // to think a nice way to do the back)
	                    m.onBackPressed();
	               }
	           });
		    
		    alertClearDb = builder2.create();
		    alertClearDb.setMessage("Do you want to delete the stored notifications?");
	/*	    Bundle arg = this.getArguments();
		    serviceUri = arg.getString(MqttApplication.SERVICE_URI_BUNDLE_TAG);
		    // TODO move the serviceURI null check here*/
		    Log.d(TAG, "On Create ");

	}
	
	  @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
	        View rootView = inflater.inflate(R.layout.service_notif_list_fragment, container, false);
	        serviceName = (TextView) rootView.findViewById(R.id.serviceName);
		    serviceName.setText(servName); // the name is set in the onCreateLoader
	    	

		    alertDeleteService.setMessage("Delete " + servName + " ?");
		   deleteServiceButton = (Button) rootView.findViewById(R.id.delServiceButton);
		   deleteServiceButton.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	 alertDeleteService.show();
	             }
	         });

		   
		    Log.d(TAG, "On CreateView");
	    	
	    	
	    	
	        return rootView;
	    }
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle bundleArg) {
		String[] projection = { NotificationData.SERVICE_ID, NotificationData._ID, NotificationData.ALERT_TYPE, NotificationData.DESCRIPTION,
				NotificationData.SERVER_TIME, NotificationData.SERVICE_FULL_URI, 	NotificationData.THRESHOLD, NotificationData.VALUE};

	    Bundle arg = this.getArguments();
	    serviceUri = arg.getString(MqttApplication.SERVICE_URI_BUNDLE_TAG);
	    servName = arg.getString(MqttApplication.SERVICE_NAME_BUNDLE_TAG);
	    // TODO move the serviceURI null check here
	    Log.d(TAG, "On createLoader with serviceuri = " + serviceUri);

		
		if(null != serviceUri){
			String selection = NotificationData.SERVICE_ID + " = ?";
			String [] selectionArgs =  {serviceUri};
	        CursorLoader cursorLoader = new CursorLoader(getActivity(),
	        		NotificationContentProvider.CONTENT_URI, projection, selection, selectionArgs, NotificationData.SERVER_TIME + " DESC");
	        return cursorLoader;
		}
		else{
			Log.d(TAG, "failed to retrieve service name");
			return null;
			//TODO: find a destroy self
		}
		

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
		
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
