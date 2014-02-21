package org.example.mqtt;


// interface to receive a notification when a service is added or removed
// it was created to let fragments communicate to the Main Acitivity in
// a more Activity independent format

public interface IServiceChangeListener {
	
	//added is true when the topic has been added, and false when it has been removed
	// topic is the topic that has been changed
	void notifyServiceListChanged(boolean added, String topic);
}
