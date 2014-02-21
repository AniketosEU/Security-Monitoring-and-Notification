package org.example.mqtt;

import java.util.ArrayList;

import org.example.mqtt.model.NotifService;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class AddServiceDialogFragment extends DialogFragment implements OnClickListener{

	EditText servUriEditText;
	EditText nameEditText; 
	
	Button saveButton;
	Button cancelButton;
	
	IServiceChangeListener activityCallback;
	
	
    
    // override the regular onAttach to set the callback to the activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
        	activityCallback = (IServiceChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_service_dialog_frag, container);
        
        nameEditText = (EditText) view.findViewById(R.id.txt_name);
        servUriEditText = (EditText) view.findViewById(R.id.txt_service_uri);
        getDialog().setTitle("Add Service");
        
        saveButton = (Button) view.findViewById(R.id.buttonSave);
        cancelButton = (Button) view.findViewById(R.id.buttonCancel);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);


        
        return view;
    }

	@Override
	public void onClick(View v) {
		if(v == saveButton) {

			// Try to insert
        	// sanity test
        	String dest = servUriEditText.getText().toString().trim();
        	String name = nameEditText.getText().toString().trim();
        	
        	if(null != dest && null != name && !(name.isEmpty()) && !(dest.isEmpty())){
        		
        		Activity parent = getActivity();
        		MqttApplication app = (MqttApplication) parent.getApplication();
        		int addServResult = app.addService(name, dest);
        		switch (addServResult) {
	                case MqttApplication.ADD_SERVICE_ERR_EXISTING_SERVICE_NAME:  
	                	toast("Error: service name already registered");
	                    break;
	                case MqttApplication.ADD_SERVICE_ERR_EXISTING_SERVICE_URI:  
	                	toast("Error: service URI already registered");
	                    break;
	                case MqttApplication.ADD_SERVICE_OK:
	                	activityCallback.notifyServiceListChanged(true,dest);
	                    break;
        		}

        	}else{
        		toast("sanity check failed");
        	}

        }
		if(v == cancelButton) {
			toast("service not added");
		}
		this.dismiss();
	}
	
	private void toast(String message)
	{
		Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
	}

	
}
