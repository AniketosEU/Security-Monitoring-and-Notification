package eu.aniketos.notification.trigger;

import javax.swing.JComboBox;

/**
 * http://www.javafaq.nu/java-bookpage-20-4.html
 * 
 * @author erlendg
 * 
 */
public class MemoryComboBox extends JComboBox {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 3618203752317676168L;
	
	public static final int MAX_MEM_LEN = 30;
	
	private String filename;
	
	

	public MemoryComboBox(String filename) {

		super();
		this.filename = filename;

//		load();
		setEditable(true);

	}

	public void add(String item) {

		removeItem(item);

		insertItemAt(item, 0);

		setSelectedItem(item);

		if (getItemCount() > MAX_MEM_LEN)

			removeItemAt(getItemCount() - 1);

	}

	public void load() {
//		try {
//
//			if (getItemCount() > 0)
//				removeAllItems();
//
//			Bundle bundle = FrameworkUtil.getBundle(this.getClass());
//			File f = bundle.getBundleContext().getDataFile(filename);
//			
//			if (!f.exists()) {
//				System.out.println("Alert history file could not be loaded: " + f.getPath());
//				f.createNewFile();
//				if (f.exists())
//					System.out.println("File created!");
//				return;
//			}
//			
//			BufferedReader reader = new BufferedReader(new FileReader(f));
//			StringBuilder builder = new StringBuilder();
//			String aux = "";
//
//			while ((aux = reader.readLine()) != null) {
//			    builder.append(aux);
//			}
//
//			reader.close();
//		}
//
//		catch (Exception e) {
//			e.printStackTrace();
//			System.err.println("Serialization error: " + e.toString());
//		}
	}

	public void save() {

//		try {
//
//			Bundle bundle = FrameworkUtil.getBundle(this.getClass());
//			File f = bundle.getBundleContext().getDataFile(filename);
//						
//			if (!f.exists()) {
//				System.out.println("Alert history file does not exist");
//				return;
//			}
//			
//			FileOutputStream fStream = new FileOutputStream(f);
//			ObjectOutput stream = new ObjectOutputStream(fStream);
//			
//			stream.writeObject(getModel());
//			stream.flush();
//			stream.close();
//			fStream.close();
//
//		}
//
//		catch (Exception e) {
//			e.printStackTrace();
//			System.err.println("Serialization error: " + e.toString());
//		}
	}
}
