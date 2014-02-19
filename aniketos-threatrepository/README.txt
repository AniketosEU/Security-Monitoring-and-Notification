Threat Repository bundles and tools
-client		(MAVEN): Demonstrates the usage of the most commonly-used functions of the TRM API (CRUD, search, browse). NB: only works if the impl bundle is running in the same OSGi container!
-feature	(MAVEN): Contains the Karaf feature for deployment.
-impl		(MAVEN): Threat Repository Module implementation bundle. Provides the TRM API as an OSGi service and as a SOAP web service, if CXF DOSGi is present.
-interface	(MAVEN): Threat Repository Module interfaces.
-threatuploader	(MAVEN): A standalone Threat Repository client that can be used to add new Aniketos content (threats, countermeasures) to the repository.