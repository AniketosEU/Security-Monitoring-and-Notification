Aniketos Notification Android Client
=================


Short description
-----------------
The Aniketos Notification Android Client is an android client which is capable of subscribing to Aniketos Notifications and receive those as soon as they are published.


Requirements
------------
The main technical requirement behind the Aniketos Notification Android Client was to offer and Android solution to subscribe to the Aniketos Notifications. The Aniketos Notifications are currently stored in ActiveMQ topics an Apache ActiveMQ server, making it necessary for the Android Client to be able to exchange messages with it.

Features
--------
This android client lets one connect to an Aniketos Notification server, (un)subscribe to Notification topics, receive and store locally the notifications of each topic.


Installation
------------
In order to install the client, you just need to build the project, generating its APK, and then install the APK on your Android. 

It is worth noting that the connections to the ActiveMQ server are identified by the id of the Android Device. That means that real android devices will be uniquely identified, but android emulators will have all the same identification. The practical effect of that is that android emulators will not retrieve "old notifications" when they connect, if another android emulator has connected in between its offline time.

Usage manual
------------
(15 lines max; if more is needed, split up in sections of the same max. size)

Example usage
-------------
(15 lines max; if more is needed, split up in sections of the same max. size)

Credits
-------
This client started from the an adaptation of the original android-mqtt-demo (https://github.com/jsherman1/android-mqtt-demo) as it served as basis for the mqtt communication to the ActiveMQ server hosting the Aniketos Notifications.

