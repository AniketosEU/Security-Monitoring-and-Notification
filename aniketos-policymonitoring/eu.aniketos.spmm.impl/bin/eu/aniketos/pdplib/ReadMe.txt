CONSPEC PDP FOR ANIKETOS
The project is an eclipse java project, you can import it from eclipse and simply run as a java application

There are 3 packages:
	-eu.aniketos.spec: classes that models a conspec policy through java object. the global policy is modelled by Specification.java, that has methods for load a policy written
		inside an xml file according to the XMLSchema in rsc/xsd_aniketos.xsd.
		Some example of conspec policies are available inside the rsc folder.
		At load time, the policy are mapped in java classes contained in this package, to make the PDP able to make operations on it
	-security.iit.pdp: contains the core classes of the PDP: PDP.java contains the logic of the PDP, and it has the PDP_Allow method that is the entry for the external requests
		The EnvironmentManager.java instead create the environment in the PDP, that contains all the variables used by the PDP for its operation. These variables are the variables
		declared in the conspec policy and the parameters passed in the PDP_Allow method
	-iit.main: the main class of the project, it is an example of how to invoke the PDP from an external component
	
	
Current Project status:
The PDP is completed and tested (it works with the policies non-delegation.xml, spec_ret.xml, specification.xml in "rsc" folder), but some extension could be needed for aniketos:
for instance, currently the PDP allow reasoning only on boolean, integer and string types, this means for instance that we cannot do operations on double values and moreover we cannot
invoke every java operations through the <invoke> tag: we can invoke only methods that have boolean, int, or string parameters; and if the method belongs to a generic class (not
Integer, Boolean, String) we can invoke it only if it is a static method, because the PDP is not able to manage the instance of this class.