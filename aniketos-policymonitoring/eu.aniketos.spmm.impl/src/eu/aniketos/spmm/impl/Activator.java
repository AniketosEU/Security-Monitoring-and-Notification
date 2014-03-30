package eu.aniketos.spmm.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import eu.aniketos.notification.IAlert;
import eu.aniketos.spmm.IPropertyMonitoringInterface;
import eu.aniketos.data.IAgreementTemplate;
import eu.aniketos.data.impl.AgreementTemplate;

/**
 * @author Muhammad Asim, LJMU
 * 
 */

public class Activator implements BundleActivator {

	private static BundleContext context;
	private ServiceRegistration registration;
	private static Activator plugin;
	private IAlert service;
	public String myProperty = null;
	ServiceTracker trackerNotification = null;
	ServiceReference notificationReference = null;
	private ContractMonitoring contractMonitor;
	private Timer timer;
	private static String contractPath;
	private static String ServiceID;


	

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		plugin = this;
		this.context = bundleContext;

		trackerNotification = new ServiceTracker(bundleContext,
				IAlert.class.getName(), null);
		trackerNotification.open();

		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				notificationReference = context
						.getServiceReference(IAlert.class.getName());
				if (notificationReference == null) {
					System.out
							.println("-------------------------- notification Reference is : "
									+ notificationReference);
				} else {
					timer.cancel();
					System.out.println(String
							.format("Received reference to IAlert service"));
				}

				try {
					 //initializeSPDM();
					 //initializePVM();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 0, 5000);

		Activator.context = bundleContext;
		// DOM - Create some properties to help identify this bundle
		Hashtable<String, String> props = new Hashtable<String, String>();

		props.put("service.exported.interfaces", "*");
		props.put("service.exported.configs", "org.apache.cxf.ws");
		props.put("org.apache.cxf.ws.address", "http://localhost:9091/spmm");

		// Create an instance of the module and register it as a service
		this.contractMonitor = new ContractMonitoring();
		registration = bundleContext.registerService(
				IPropertyMonitoringInterface.class.getName(),
				this.contractMonitor, props);

		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println("           SPMM Registered");
		System.out.println("----------------------------------------");

		setContract();

	}

	// test method for sending agreement template to SPMM
	public void setContract() throws IOException {

		String KARAF_HOME = "C:/aniketos-karaf-0.0.2";
		String rscFolderPath = System.getProperty("karaf.home", KARAF_HOME)
				+ "/rsc/";

		File afile = new File(rscFolderPath + "config.txt");
		FileReader fileread = new FileReader(afile);
		BufferedReader bufread = new BufferedReader(fileread);
		contractPath = bufread.readLine();
		//bufread.readLine();// This line is skipped because it contains the
							// policy path used in CMM
		ServiceID = bufread.readLine();
		bufread.close();

		IAgreementTemplate AT = new AgreementTemplate(ServiceID);

		if (contractPath.toLowerCase().endsWith(".xml")) {
			AT.setXmlContents(readFileContents(contractPath));
		} else if (contractPath.toLowerCase().endsWith(".zip")) {
			AT.setXmlContents(readZip(contractPath));
		}

		System.out.println("ServiceID=" + ServiceID);
		// System.out.println("Contract Specifications="+AT.getXmlContents().length);
		System.out.println("contract Path=" + contractPath);
		contractMonitor.monitorSecurityPolicy(AT, ServiceID);

	}

	private String[] readFileContents(String filename) {

		String[] xmlContents = new String[1];
		StringBuilder builder = new StringBuilder();
		try {
			File fileDir = new File(filename);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileDir), "UTF8"));

			String line;
			while ((line = br.readLine()) != null) {
				builder.append(line).append('\n');
			}
			xmlContents[0] = builder.toString();

			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlContents;
	}

	private String[] readZip(String zipfilename) {

		String[] xmlContents = null;
		try {
			ZipFile zf = new ZipFile(zipfilename);
			Enumeration<? extends ZipEntry> entries = zf.entries();
			xmlContents = new String[zf.size()];
			int i = 0;
			while (entries.hasMoreElements()) {
				StringBuilder builder = new StringBuilder();

				ZipEntry ze = (ZipEntry) entries.nextElement();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						zf.getInputStream(ze)));

				String line;
				while ((line = br.readLine()) != null) {
					builder.append(line).append('\n');
				}
				xmlContents[i] = builder.toString();
				i++;
				br.close();
			}

			zf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xmlContents;
	}

	public static String getContractPath() {
		return contractPath;
	}

	public static String getServiceID() {
		return ServiceID;
	}

	public IAlert getAlert() throws InterruptedException {
		// create a reference to the Alert service

		IAlert alert = (IAlert) context.getService(notificationReference);
		// IAlert alert = (IAlert)trackerNotification.waitForService(10);

		if (alert == null) {
			System.out.println("Cannot find the Notification service");
			System.out.println("----------------------------------------");
			return null;
		}

		return alert;
	}


	
	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		registration.unregister();
		Activator.context = null;
	}

	public static Activator getDefault() {
		return plugin;
	}

}
