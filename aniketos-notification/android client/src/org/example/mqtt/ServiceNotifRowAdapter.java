package org.example.mqtt;

import java.util.List;

import org.example.mqtt.model.NotifService;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ServiceNotifRowAdapter extends ArrayAdapter<NotifService> {

	private final Context context;
	private final List<NotifService> values;
	  
	public ServiceNotifRowAdapter(Context context, 	List<NotifService> objects) {
		super(context,  R.layout.serv_list_fragment, objects);
		this.context = context;
		this.values = objects;
	}
	
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.service_row_layout, parent, false);
	    
	    TextView textViewServName = (TextView) rowView.findViewById(R.id.service_name);
	    TextView textViewServId = (TextView) rowView.findViewById(R.id.service_id);;
	    
	    textViewServName.setText(values.get(position).getServiceName());
	    textViewServId.setText(values.get(position).getServiceURI());

	    return rowView;
	  }

}
