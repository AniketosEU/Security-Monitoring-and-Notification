package eu.aniketos.threatuploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigHandler {

	private static final String configfile = "svrsclient.cfg";

	public static Properties readConfigFile() {
		Properties cfprops = new Properties();
		String appdir = System.getProperty("user.home")
				+ System.getProperty("file.separator") + ".svrsclient";
		String conf = appdir + System.getProperty("file.separator")
				+ configfile;
		File appd = new File(appdir);
		appd.mkdirs(); // create dir if not yet available
		File c = new File(conf);
		if (c.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(c);
				cfprops.load(fis);
			} catch (FileNotFoundException e) {
				//
			} catch (IOException e) {
				//
			} finally {
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		return cfprops;
	}

	public static void writeConfigFile(Properties cfprops) {
		String appdir = System.getProperty("user.home")
				+ System.getProperty("file.separator") + ".svrsclient";
		String conf = appdir + System.getProperty("file.separator")
				+ configfile;
		File appd = new File(appdir);
		appd.mkdirs(); // create dir if not yet available
		File c = new File(conf);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(c);
			cfprops.store(fos, null);
		} catch (FileNotFoundException e) {
			//
		} catch (IOException e) {
			//
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

}
