package org.example.mqtt.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.example.mqtt.MqttApplication;
import org.example.mqtt.R;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// cursor adaptor that translates the date from long to string
public class NotificationCursorAdapter extends SimpleCursorAdapter {

	private final String TAG = "NotificationCursorAdapter";
	
	private Context context;
	
	public NotificationCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		this.context = context;
	}
	
	@Override
	public void bindView(View v, Context context, Cursor c) {
	    super.bindView(v, context, c);
	    // the value is empty, we clear its linear layout
	    String value = c.getString(c.getColumnIndex(NotificationData.VALUE));
	    Log.d(TAG, "value is " + value);
    	LinearLayout valueLayout = (LinearLayout) v.findViewById(R.id.valueLayout);
	    if(null == value || value.isEmpty()){	    	
	    	valueLayout.setVisibility(View.GONE);
	    }else{
	    	valueLayout.setVisibility(View.VISIBLE); // if I do not set it explicitly to visible
	    	//, Android seems to get confused and set some of them to gone
	    }
	    
	    String type = c.getString(c.getColumnIndex(NotificationData.ALERT_TYPE));
	    String imgName = MqttApplication.iconList.get(type);
	    if(null != imgName){
	    	int id = context.getResources().getIdentifier("org.example.mqtt:drawable/" + imgName, null, null);
	    	ImageView img = (ImageView) v.findViewById(R.id.list_image);
	    	Drawable drawable = context.getResources().getDrawable( id );
	    	img.setImageDrawable(drawable);
	    }
	}
	

	
    @Override
    public void setViewText(TextView v, String text) {
    	
    	int view = v.getId();
    	
    	switch (view){
    		// time column from service_specifc_notif_row.xml or all_status_row_layout.xml
    		case R.id.notif_date:
    		case R.id.timestamp:
      		  long l =  Long.valueOf(text).longValue();
			  Date d = new Date(l);
			    SimpleDateFormat format =
				        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    try{
			    	text = format.format(d.getTime());
			    }
			    catch(IllegalArgumentException e){
			    	Log.e(TAG, "long is " + l + " and exception is " + e.getMessage());
			    }
			    break;
			// replace service URI by name
    		case R.id.service:
        		MqttApplication app = (MqttApplication) context.getApplicationContext();
        		String servName = app.getServiceNameFromURI(text);
        		text= servName;
        		break;

    	}
    	

        super.setViewText(v,text);
        
    }



}
