/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.aniketos.threatrepository.impl;

import eu.aniketos.threatrepository.impl.ThreatRepositoryServiceImpl;
import eu.aniketos.threatrepository.ThreatType;
import java.io.StringReader;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.extensions.TestSetup;

/**
 * 
 * @author balazs
 */
public class ThreatRepositoryServiceImplTest extends TestCase {

	/**
	 * This method allows the execution of specific test cases.
	 * 
	 * @param testName
	 *            The test to execute
	 */
	public ThreatRepositoryServiceImplTest(String testName) {
		super(testName);
	}

	/**
	 * This class is used to handle one-time test setup and teardown tasks (adding and deleting SVRS resources)
	 * 
	 * @return A Test object.
	 *
	 */
	public static Test suite() {
		TestSetup setup = new TestSetup(new TestSuite(
				ThreatRepositoryServiceImplTest.class)) {
			
			/**
			 * This method is used to set up the test environment.
			 * 
			 */
			protected void setUp() throws Exception {
				System.out.print("addThreat");
				String threatID = "ffffeeee-dddd-cccc-bbbb-aaaa99998888";
				eu.aniketos.threatrepository.schema.upload.Threat threatData = null;
				try {
					threatData = new eu.aniketos.threatrepository.schema.upload.Threat();
					StringReader dsr = new StringReader(
							"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<a:threat  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n   xmlns:a='http://www.aniketos.eu'\n   xsi:schemaLocation='http://www.aniketos.eu Threat.xsd'>\n\n    <a:metadata>\n        <a:threat_id>ffffeeee-dddd-cccc-bbbb-aaaa99998888</a:threat_id>\n        <a:name>Example threat</a:name>\n        <a:description>Description of the threat goes here (2-3 lines).</a:description>\n        <a:type>vulnerabilityclass</a:type>\n        <a:creationdate>2012-01-01T12:13:14Z</a:creationdate>\n        <a:lastupdated>2012-01-01T12:13:14Z</a:lastupdated>\n        <!--a:threatclass>ffff6666-5555-4444-3333-22221111ffff</a:threatclass-->\n        <!--a:cause_id>77776666-5555-4444-3333-222211110000</a:cause_id-->\n        <a:externalrefs>\n            <a:externalref>http://www.example.org/threatinfo.pdf</a:externalref>\n            <a:externalref>CVE-2012-9999</a:externalref>\n        </a:externalrefs>\n    </a:metadata>\n</a:threat>");
		            ClassLoader cl = eu.aniketos.threatrepository.schema.upload.ObjectFactory.class.getClassLoader();
		            JAXBContext djc = JAXBContext.newInstance("eu.aniketos.threatrepository.schema.upload", cl);
					Unmarshaller du = djc.createUnmarshaller();
					threatData = (eu.aniketos.threatrepository.schema.upload.Threat) du
							.unmarshal(dsr);
				} catch (javax.xml.bind.JAXBException ex) {
					System.out.print("Marshalling error during test");
				}
				ThreatRepositoryServiceImpl instance = new ThreatRepositoryServiceImpl();
				String result = instance.addThreat(threatID, threatData);
				System.out.println(" result: " + result);
				//assertEquals("Response: HTTP 201 - Created", result);

				System.out.print("addCountermeasure");
				String counterMID = "01234567-89ab-cdef-0123-456789abcdef";
				eu.aniketos.threatrepository.schema.upload.Countermeasure counterData = null;
				try {
					counterData = new eu.aniketos.threatrepository.schema.upload.Countermeasure();
					StringReader dsr = new StringReader(
							"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<a:countermeasure  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n   xmlns:a='http://www.aniketos.eu'\n   xsi:schemaLocation='http://www.aniketos.eu Countermeasure.xsd'>\n\n    <a:metadata>\n        <a:counter_id>01234567-89ab-cdef-0123-456789abcdef</a:counter_id>\n        <a:name>Example countermeasure</a:name>\n        <a:description>Description of the countermeasure goes here.</a:description>\n        <a:type>pattern</a:type>\n        <a:creationdate>2012-01-01T13:14:15Z</a:creationdate>\n        <a:execresponsible>(execresponsibletext)</a:execresponsible>\n        <a:resultdesc>(resultdesctext)</a:resultdesc>\n        <a:lastupdated>2012-01-02T17:18:19Z</a:lastupdated>\n        <a:externalrefs>\n            <a:externalref>http://www.example.org/countermeasureinfo.html</a:externalref>\n        </a:externalrefs>\n    </a:metadata>\n    <a:countermeasuredata>PG9wZXJhdGlvbmFsUGFydD4NCiAgICA8dHJ1c3RNZWNoYW5pc21zPmRpZ2l0YWwgc2lnbmF0dXJlPC90cnVzdE1lY2hhbmlzbXM+DQogICAgPHZhbGlkaXR5Pg0KICAgICAgPHZhbGlkRnJvbT4wMTExMjAxMTwvdmFsaWRGcm9tPg0KICAgICAgPHZhbGlkVW50aWw+MDExMTIwMTI8L3ZhbGlkVW50aWw+DQogICAgPC92YWxpZGl0eT4NCiAgICA8bW9uaXRvcnM+DQogICAgICA8bW9uaXRvcj4NCiAgICAgICAgPGlkPlNQTTwvaWQ+DQogICAgICAgIDxsb2NhbGl6YXRpb24+aHR0cDovLzEyNy4wLjAuMTo4MDgwL1NlY3VyaXR5UG9saWN5TW9uaXRvcmluZzwvbG9jYWxpemF0aW9uPg0KICAgICAgICA8dHlwZT5wb2xpY2llczwvdHlwZT4NCiAgICAgICAgPGluaXRpYWxpemF0aW9uPjwvaW5pdGlhbGl6YXRpb24+DQogICAgICA8L21vbml0b3I+DQogICAgPC9tb25pdG9ycz4NCiAgICA8cm9sZXM+DQogICAgIDxyb2xlPg0KICAgICAgICA8cm9sZU5hbWU+UnVudGltZTwvcm9sZU5hbWU+DQoJICA8cm9sZVNjb3BlPlNlcnZpY2U8L3JvbGVTY29wZT4NCiAgICAgICAgPHBhcmFtZXRlcnM+S2V5U2l6ZTwvcGFyYW1ldGVycz4NCgkgIDxwYXJhbWV0ZXJzPkVuY3J5cHRBbGc8L3BhcmFtZXRlcnM+DQogICAgICAgIDxwcmVjb25kaXRpb25zPjwvcHJlY29uZGl0aW9ucz4NCiAgICAgICAgPHN5c3RlbUNvbmZpZ3VyYXRpb24+DQogICAgICAgICAgPHN5c3RlbUNvbmZpZ3VyYXRpb24+DQogICAgICAgICAgICA8ZGVzY3JpcHRpb24+PC9kZXNjcmlwdGlvbj4NCiAgICAgICAgICAgIDxmaWVsZD48L2ZpZWxkPg0KICAgICAgICAgICAgPHZhbHVlPjwvdmFsdWU+DQogICAgICAgICAgPC9zeXN0ZW1Db25maWd1cmF0aW9uPiAgICAgICAgICAgICAgICAgDQogICAgICAgIDxtb25pdG9yaW5nPg0KICAgPG1vbml0b3JpbmdSdWxlPiBFbmNyeXB0QWxnPUFFUyA8L21vbml0b3JpbmdSdWxlPg0KICA8L21vbml0b3Jpbmc+ICAgICAgICAgICAgICAgICANCiAgICAgIDwvcm9sZT4JICANCiAgICA8L3JvbGVzPg0KPC9vcGVyYXRpb25hbFBhcnQ+</a:countermeasuredata>\n    <a:threats>\n        <a:threat>\n            <a:threat_id>ffffeeee-dddd-cccc-bbbb-aaaa99998888</a:threat_id>\n            <a:relation>prevent</a:relation>\n        </a:threat>\n    </a:threats>\n\n    <a:preconditions>\n        <a:precondition>(precondition1text)</a:precondition>\n        <a:precondition>(precondition2text)</a:precondition>\n    </a:preconditions>\n</a:countermeasure>");
		            ClassLoader cl = eu.aniketos.threatrepository.schema.upload.ObjectFactory.class.getClassLoader();
		            JAXBContext djc = JAXBContext.newInstance("eu.aniketos.threatrepository.schema.upload", cl);
					Unmarshaller du = djc.createUnmarshaller();
					counterData = (eu.aniketos.threatrepository.schema.upload.Countermeasure) du
							.unmarshal(dsr);
				} catch (javax.xml.bind.JAXBException ex) {
					System.out.print("Marshalling error during test");
				}
				result = instance.addCountermeasure(counterMID, counterData);
				System.out.println(" result: " + result);
				
				//assertEquals("Response: HTTP 201 - Created", result);
				// TODO review the generated test code and remove the default
				// call to fail.
			}

			/**
			 * This method is used to tear down the test environment.
			 * 
			 */
			protected void tearDown() throws Exception {
				System.out.print("deleteCountermeasure");
				String counterMID = "01234567-89ab-cdef-0123-456789abcdef";
				ThreatRepositoryServiceImpl instance = new ThreatRepositoryServiceImpl();
				String result = instance.deleteCountermeasure(counterMID);
				//assertEquals("Response: HTTP 202 - Accepted", result);
				System.out.println(" result: " + result);
				System.out.print("deleteThreat");
				String threatID = "ffffeeee-dddd-cccc-bbbb-aaaa99998888";
				result = instance.deleteThreat(threatID);
				System.out.println(" result: " + result);
				//assertEquals("Response: HTTP 202 - Accepted", result);
			}
		};
		return setup;
	}

	/**
	 * Test of updateThreat method, of class ThreatRepositoryServiceImpl.
	 */
	public void _testUpdateThreat() {
		System.out.println("updateThreat");
		String threatID = "";
		eu.aniketos.threatrepository.schema.upload.Threat threatData = null;
		ThreatRepositoryServiceImpl instance = new ThreatRepositoryServiceImpl();
		instance.updateThreat(threatID, threatData);
		// TODO review the generated test code and remove the default call to
		// fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of updateCountermeasure method, of class
	 * ThreatRepositoryServiceImpl.
	 */
	public void _testUpdateCountermeasure() {
		System.out.println("updateCountermeasure");
		String counterMID = "";
		eu.aniketos.threatrepository.schema.upload.Countermeasure counterData = null;
		ThreatRepositoryServiceImpl instance = new ThreatRepositoryServiceImpl();
		instance.updateCountermeasure(counterMID, counterData);
		// TODO review the generated test code and remove the default call to
		// fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getCountermeasures method, of class ThreatRepositoryServiceImpl.
	 */
	public void testGetCountermeasures() {
		System.out.println("getCountermeasures");
		String counterMID = "01234567-89ab-cdef-0123-456789abcdef";
		ThreatRepositoryServiceImpl instance = new ThreatRepositoryServiceImpl();
		List result = instance.getCountermeasures(null, counterMID);
		assertEquals(1, result.size());
	}

	/**
	 * Test of getCountermeasuresForThreat method, of class
	 * ThreatRepositoryServiceImpl.
	 */
	public void testGetCountermeasuresForThreat() {
		System.out.println("getCountermeasuresForThreat");
		String ThreatID = "ffffeeee-dddd-cccc-bbbb-aaaa99998888";
		ThreatRepositoryServiceImpl instance = new ThreatRepositoryServiceImpl();
		List result = instance.getCountermeasuresForThreat(ThreatID);
		assertEquals(1, result.size());
	}

	/**
	 * Test of getThreats method, of class ThreatRepositoryServiceImpl.
	 */
	public void testGetThreats() {
		System.out.println("getThreats");
		String name = null;
		String id = null;
		ThreatType type = ThreatType.threat;
		String domain = "rfid";
		ThreatRepositoryServiceImpl instance = new ThreatRepositoryServiceImpl();
		List result = instance.getThreats(name, id, type, domain);
		result = instance.getThreats(name, id, type, domain); // cache test: read 10 more times
		result = instance.getThreats(name, id, type, domain); 
		result = instance.getThreats(name, id, type, domain); 
		result = instance.getThreats(name, id, type, domain); 
		result = instance.getThreats(name, id, type, domain); 
		result = instance.getThreats(name, id, type, domain); 
		result = instance.getThreats(name, id, type, domain); 
		result = instance.getThreats(name, id, type, domain); 
		result = instance.getThreats(name, id, type, domain); 
		result = instance.getThreats(name, id, type, domain); 
		assertEquals(2, result.size());
	}

	/**
	 * Test of getThreatsForCountermeasure method, of class
	 * ThreatRepositoryServiceImpl.
	 */
	public void testGetThreatsForCountermeasure() {
		System.out.println("getThreatsForCountermeasure");
		String counterMID = "01234567-89ab-cdef-0123-456789abcdef";
		ThreatRepositoryServiceImpl instance = new ThreatRepositoryServiceImpl();
		List result = instance.getThreatsForCountermeasure(counterMID);
		assertEquals(1, result.size());
	}

}
