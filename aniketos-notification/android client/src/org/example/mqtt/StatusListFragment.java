package org.example.mqtt;

import java.util.ArrayList;

import org.example.mqtt.data.NotificationContentProvider;
import org.example.mqtt.data.NotificationCursorAdapter;
import org.example.mqtt.data.NotificationData;
import org.example.mqtt.model.NotifService;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class StatusListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	private final String TAG = "StatusListFragment";
	
	private NotificationCursorAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] uiBindFrom = { NotificationData.SERVICE_ID, NotificationData.ALERT_TYPE, NotificationData.SERVER_TIME, NotificationData.DESCRIPTION, NotificationData.VALUE };
	    int[] uiBindTo = { R.id.service, R.id.alert, R.id.timestamp, R.id.description, R.id.value }; // from all_status_row_layout.xml
	    getLoaderManager().initLoader(MqttApplication.STATUS_LIST_LOADER, null, this);
	    adapter = new NotificationCursorAdapter(
	            getActivity().getApplicationContext(), R.layout.all_status_row_layout,
	            null, uiBindFrom, uiBindTo,
	            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	    setListAdapter(adapter);
	    Log.d(TAG, "On Create");
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = { NotificationData._ID, NotificationData.ALERT_TYPE, NotificationData.DESCRIPTION,
				NotificationData.SERVER_TIME, NotificationData.SERVICE_ID, NotificationData.THREAT_ID,
				NotificationData.THRESHOLD, NotificationData.VALUE,NotificationData.SERVICE_FULL_URI};

		// creating a selection clause to filter the notifications
		// in order to show only the ones of active subscriptions
		Activity parent = getActivity();
		MqttApplication app = (MqttApplication) parent.getApplication();
		ArrayList<NotifService> servList = app.getServiceList();
		String selection = null;
		String args[] = null;
		if(servList.isEmpty() == false){
			selection = NotificationData.SERVICE_ID + " IN (";
			args = new String[servList.size()];
			int i=0;
			for(NotifService n : servList){
				args[i] = n.getServiceURI();
				i++;
				selection+="?,";
			}
			selection = selection.substring(0, selection.length()-1) + ")"; // replace last , with )
			Log.d(TAG, "selection clause " + selection);
		}

		
		
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
        		NotificationContentProvider.CONTENT_URI, projection, selection, args,NotificationData.SERVER_TIME + " DESC");
        return cursorLoader;

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
	  public void onListItemClick(ListView l, View v, int position, long id) {
	    // do something with the data
		  Cursor  cursor = (Cursor) getListAdapter().getItem(position);
		  String serviceUri = cursor.getString(cursor.getColumnIndex(NotificationData.SERVICE_ID));
		  Log.d(TAG, "selected service " + serviceUri);
		  MainActivity m = (MainActivity) getActivity();
		  m.showServiceSpecificNotifications(serviceUri,"");// TODO: fetch and send the service name!!, now is sending an empty string
	  }
	  
		// as the services are part of the loader selection clause (and not the projection, see my onCreateLoader)
	  // I need to restart the loader if there is a new service is added or removed.
	  
	  
		public void notifyServiceChanged() {
			getLoaderManager().restartLoader(0, null, this);	
		}
}
