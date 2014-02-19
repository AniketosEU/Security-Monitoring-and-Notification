package eu.aniketos.threatuploader;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import eu.aniketos.threatuploader.util.Base64;

/**
 * A simple GUI for the Threat Repository Client.
 * 
 * @author balazs
 * 
 */
public class ThreatUploader extends JFrame {

	protected Properties conf;
	private String payload;
	private static final long serialVersionUID = 1L;
	/** < Helper variable for serializable attribute. */
	private JPanel contentPane;
	/** < The main pane containing the client UI elements. */
	private JTextField uuidField;
	/** < A text field where the user can enter a UUID or a search query. */
	private ThreatRepositoryClient cli;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JTextField updateField;
	private JTextField nameField;
	private JTextField cmNameField;
	private JTextField cmUuidField;
	private JTextField fileField;
	final DefaultListModel tags = new DefaultListModel();
	final DefaultListModel cm_tags = new DefaultListModel();
	final JTabbedPane tabbedPane;
	final JList cmTagList;
	final JList tagList;
	final JTextArea outputArea;	
	private JTextField cvssField;
	
	/** < The client object. */

	/**
	 * Launch the application.
	 * 
	 * @param args
	 *            Command-line arguments (not used)
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ThreatUploader frame = new ThreatUploader(
							new ThreatRepositoryClient());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/** 
	 * Helper function for reading a binary payload and Base64-encoding it.
	 * 
	 * @param fi
	 */
	public void ReadPayload(File fi) {
		try {
			FileInputStream fis = new FileInputStream(fi);
			byte[] content = null;
			if (fi.length() > Integer.MAX_VALUE) {
				throw new IllegalArgumentException("File is too big.");
			}
			content = new byte[(int) fi.length()];
			while (fis.read(content) != -1) {
			}
			fis.close();
			payload = Base64.encodeBytes(content);
		} catch (Exception f) {
			// TODO: exception handling
		}
	}

	/** Helper function for fetching the current list of tags from the SVRS (or using a previously downloaded list).
	 * 
	 */
	protected List<TagData> fetchTags(){
		return cli.processTagListRequestEvent();
	}
	
	/** Helper function for reloading the settings from the config file.
	 * 
	 */
	protected void updateSettings() {
		conf = ConfigHandler.readConfigFile();
		cli.processConfigChangeEvent(conf);
	}
	
	/**
	 * Create the frame.
	 * 
	 * @param client
	 *            The ThreatRepositoryClient object to use
	 */
	public ThreatUploader(ThreatRepositoryClient client) {
		cli = client;
		final String[] CMList = { "policy", "pattern", "monitoringcontrol" };
		final String[] TList = { "threat", "vulnerabilityclass",
				"vulnerabilityinstance" };
		final String[] relations = { "prevent", "remove", "detect", "mitigate" };
		final DefaultListModel erm = new DefaultListModel();
		final DefaultListModel cm_erm = new DefaultListModel();
		final DefaultListModel cm_threat = new DefaultListModel();
		final ThreatUploader parent = this;
		
		updateSettings();
		setTitle("Threat Repository uploader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 770, 544);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		final JFileChooser ch = new JFileChooser();

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(0, 342, 759, 176);
		contentPane.add(scrollPane_2);

		outputArea = new JTextArea();
		scrollPane_2.setViewportView(outputArea);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 25, 762, 305);
		contentPane.add(tabbedPane);

		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		tabbedPane.addTab("Threat", null, panel, null);
		tabbedPane.addTab("Countermeasure", null, panel2, null);
		panel2.setLayout(null);

		JLabel label = new JLabel("Name:");
		label.setBounds(0, 4, 46, 14);
		panel2.add(label);

		cmNameField = new JTextField();
		cmNameField.setColumns(38);
		cmNameField.setBounds(82, 1, 324, 20);
		panel2.add(cmNameField);

		JLabel label_1 = new JLabel("UUID:");
		label_1.setBounds(417, 4, 31, 14);
		panel2.add(label_1);

		cmUuidField = new JTextField();
		cmUuidField.setFont(new Font("Courier New", Font.PLAIN, 10));
		cmUuidField.setColumns(36);
		cmUuidField.setBounds(454, 1, 218, 20);
		panel2.add(cmUuidField);

		JButton button = new JButton("Generate");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cmUuidField.setText(UUID.randomUUID().toString());
			}
		});
		button.setBounds(671, 0, 91, 23);
		panel2.add(button);

		JLabel lblThisIsA = new JLabel("This is a");
		lblThisIsA.setBounds(0, 26, 63, 14);
		panel2.add(lblThisIsA);

		final JComboBox comboBox = new JComboBox();
		comboBox.setBounds(82, 22, 155, 22);
		comboBox.setModel(new DefaultComboBoxModel(CMList));
		panel2.add(comboBox);

		final JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setBounds(266, 22, 140, 22);
		comboBox_1.setModel(new DefaultComboBoxModel(relations));
		panel2.add(comboBox_1);

		JLabel lblThreatWithThe = new JLabel("the threat(s):");
		lblThreatWithThe.setBounds(427, 27, 79, 14);
		panel2.add(lblThreatWithThe);

		JLabel label_4 = new JLabel("Description:");
		label_4.setBounds(0, 51, 79, 14);
		panel2.add(label_4);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(82, 48, 341, 76);
		panel2.add(scrollPane);

		final JTextArea cmDescArea = new JTextArea();
		scrollPane.setViewportView(cmDescArea);
		cmDescArea.setForeground(Color.BLACK);

		JLabel label_5 = new JLabel("External");
		label_5.setBounds(0, 155, 99, 14);
		panel2.add(label_5);

		final JList cmErefList = new JList();
		cmErefList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cmErefList.setBorder(new LineBorder(new Color(0, 0, 0)));
		cmErefList.setBounds(81, 155, 341, 112);
		cmErefList.setModel(cm_erm);
		panel2.add(cmErefList);

		JButton cmErefAddButton = new JButton("Add");
		cmErefAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String eref = JOptionPane
						.showInputDialog(cmErefList,
								"Please enter external reference (URL, publication data, etc)");
				if ((eref != null) && (!eref.isEmpty()))
					cm_erm.addElement(eref);
			}
		});
		cmErefAddButton.setBounds(10, 192, 62, 23);
		panel2.add(cmErefAddButton);

		JButton cmErefEditButton = new JButton("Edit");
		cmErefEditButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (cmErefList.getSelectedIndex() > -1) {
					String eref = JOptionPane
							.showInputDialog(
									cmErefList,
									"Please enter external reference (URL, publication data, etc)",
									cmErefList.getSelectedValues()[0]);
					if ((eref != null) && (!eref.isEmpty()))
						cm_erm.setElementAt(eref, cmErefList.getSelectedIndex());
				}
			}
		});
		cmErefEditButton.setBounds(10, 218, 62, 23);
		panel2.add(cmErefEditButton);

		JButton cmErefDelButton = new JButton("Del");
		cmErefDelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (cmErefList.getSelectedIndex() > -1) {
					cm_erm.removeElementAt(cmErefList.getSelectedIndex());
				}
			}
		});
		cmErefDelButton.setBounds(10, 244, 62, 23);
		panel2.add(cmErefDelButton);

		JLabel label_6 = new JLabel("references:");
		label_6.setBounds(0, 170, 66, 16);
		panel2.add(label_6);

		JLabel label_7 = new JLabel("Tags:");
		label_7.setBounds(440, 155, 46, 14);
		panel2.add(label_7);

		cmTagList = new JList();
		cmTagList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cmTagList.setBorder(new LineBorder(new Color(0, 0, 0)));
		cmTagList.setBounds(514, 155, 121, 113);
		cmTagList.setModel(cm_tags);
		panel2.add(cmTagList);

		JButton cmTagAddButton = new JButton("Add");
		cmTagAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*String tag = JOptionPane.showInputDialog(cmTagList,
						"Please enter tag");
				if ((tag != null) && (!tag.isEmpty()))
					cm_tags.addElement(tag);*/
				TagWindow tagw = new TagWindow(parent, 0);
				tagw.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				tagw.setVisible(true);
			}
		});
		cmTagAddButton.setBounds(444, 192, 62, 23);
		panel2.add(cmTagAddButton);

		JButton cmTagEditButton = new JButton("Edit");
		cmTagEditButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*if (cmTagList.getSelectedIndex() > -1) {
					String tag = JOptionPane
							.showInputDialog(
									cmTagList,
									"Please enter external reference (URL, publication data, etc)",
									cmTagList.getSelectedValues()[0]);
					if ((tag != null) && (!tag.isEmpty()))
						cm_tags.setElementAt(tag, cmTagList.getSelectedIndex());
				}*/
				TagWindow tagw = new TagWindow(parent, 1);
				tagw.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				tagw.setVisible(true);
			}
		});
		cmTagEditButton.setBounds(444, 218, 62, 23);
		panel2.add(cmTagEditButton);

		JButton cmTagDeleteButton = new JButton("Del");
		cmTagDeleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (cmTagList.getSelectedIndex() > -1) {
					cm_tags.removeElementAt(cmTagList.getSelectedIndex());
				}
			}
		});
		cmTagDeleteButton.setBounds(444, 244, 62, 23);
		panel2.add(cmTagDeleteButton);

		JLabel lblThat = new JLabel("that");
		lblThat.setBounds(239, 26, 31, 14);
		panel2.add(lblThat);

		JLabel lblFileName = new JLabel("Data:");
		lblFileName.setBounds(0, 130, 63, 14);
		panel2.add(lblFileName);

		fileField = new JTextField();
		fileField.setEditable(false);
		fileField.setColumns(38);
		fileField.setBounds(188, 129, 234, 20);
		panel2.add(fileField);

		JButton btnChooseFile = new JButton("Choose file");
		btnChooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int rv = fc.showDialog(contentPane,
						"Open file to upload to the repository");
				if (rv == JFileChooser.APPROVE_OPTION) {
					File fi = fc.getSelectedFile();
					ReadPayload(fi);
					fileField.setText(fi.getName());
				}
			}
		});
		btnChooseFile.setBounds(82, 127, 99, 23);
		panel2.add(btnChooseFile);
		panel.setLayout(null);

		final JLabel lblUpdateNote = new JLabel("Update note:");
		lblUpdateNote.setBounds(145, 7, 76, 14);
		lblUpdateNote.setVisible(false);
		contentPane.add(lblUpdateNote);

		JRadioButton rdbtnNew = new JRadioButton("New");
		rdbtnNew.setSelected(true);
		rdbtnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateField.setVisible(false);
				lblUpdateNote.setVisible(false);
			}
		});
		buttonGroup_1.add(rdbtnNew);
		rdbtnNew.setBounds(0, 3, 57, 23);
		contentPane.add(rdbtnNew);

		final JRadioButton rdbtnUpdate = new JRadioButton("Update");
		rdbtnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateField.setVisible(true);
				lblUpdateNote.setVisible(true);
			}
		});
		buttonGroup_1.add(rdbtnUpdate);
		rdbtnUpdate.setBounds(61, 3, 70, 23);
		contentPane.add(rdbtnUpdate);

		updateField = new JTextField();
		updateField.setVisible(false);
		updateField.setBounds(217, 8, 265, 20);
		contentPane.add(updateField);
		updateField.setColumns(10);

		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(0, 2, 46, 14);
		panel.add(lblName);

		nameField = new JTextField();
		nameField.setBounds(81, 2, 324, 20);
		nameField.setColumns(38);
		panel.add(nameField);

		JLabel lblUuid = new JLabel("UUID:");
		lblUuid.setBounds(417, 2, 31, 14);
		panel.add(lblUuid);

		uuidField = new JTextField();
		uuidField.setFont(new Font("Courier New", Font.PLAIN, 10));
		uuidField.setBounds(450, 2, 218, 20);
		panel.add(uuidField);
		uuidField.setColumns(36);

		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				uuidField.setText(UUID.randomUUID().toString());
			}
		});
		btnGenerate.setBounds(671, 1, 91, 23);
		panel.add(btnGenerate);

		JLabel lblType = new JLabel("Subtype:");
		lblType.setBounds(0, 27, 63, 14);
		panel.add(lblType);

		final JComboBox typeDropdown = new JComboBox();
		typeDropdown.setBounds(81, 23, 156, 22);
		typeDropdown.setModel(new DefaultComboBoxModel(TList));
		panel.add(typeDropdown);

		JLabel lblDescription = new JLabel("Description:");
		lblDescription.setBounds(0, 52, 79, 14);
		panel.add(lblDescription);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(81, 48, 681, 79);
		panel.add(scrollPane_1);

		final JTextArea descArea = new JTextArea();
		scrollPane_1.setViewportView(descArea);
		descArea.setForeground(new Color(0, 0, 0));

		JLabel lblTags = new JLabel("Tags:");
		lblTags.setBounds(450, 136, 46, 14);
		panel.add(lblTags);

		JLabel lblExternal = new JLabel("External");
		lblExternal.setBounds(0, 136, 99, 14);
		panel.add(lblExternal);

		final JList erefList = new JList();
		erefList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		erefList.setBorder(new LineBorder(new Color(0, 0, 0)));
		erefList.setBounds(81, 138, 341, 130);
		erefList.setModel(erm);
		panel.add(erefList);

		JButton erefAdd = new JButton("Add");
		erefAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String eref = JOptionPane
						.showInputDialog(erefList,
								"Please enter external reference (URL, publication data, etc)");
				if ((eref != null) && (!eref.isEmpty()))
					erm.addElement(eref);
			}
		});
		erefAdd.setBounds(10, 179, 62, 23);
		panel.add(erefAdd);

		JButton erefEdit = new JButton("Edit");
		erefEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (erefList.getSelectedIndex() > -1) {
					String eref = JOptionPane
							.showInputDialog(
									erefList,
									"Please enter external reference (URL, publication data, etc)",
									erefList.getSelectedValues()[0]);
					if ((eref != null) && (!eref.isEmpty()))
						erm.setElementAt(eref, erefList.getSelectedIndex());
				}
			}
		});
		erefEdit.setBounds(10, 210, 62, 23);
		panel.add(erefEdit);

		JButton erefDel = new JButton("Del");
		erefDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (erefList.getSelectedIndex() > -1) {
					erm.removeElementAt(erefList.getSelectedIndex());
				}
			}
		});
		erefDel.setBounds(10, 241, 62, 23);
		panel.add(erefDel);

		tagList = new JList();
		tagList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tagList.setBorder(new LineBorder(new Color(0, 0, 0)));
		tagList.setBounds(514, 138, 121, 130);
		tagList.setModel(tags);
		panel.add(tagList);

		JButton tagDel = new JButton("Del");
		tagDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (tagList.getSelectedIndex() > -1) {
					tags.removeElementAt(tagList.getSelectedIndex());
				}
			}
		});
		tagDel.setBounds(444, 241, 62, 23);
		panel.add(tagDel);

		JButton tagEdit = new JButton("Edit");
		tagEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*if (tagList.getSelectedIndex() > -1) {
					String tag = JOptionPane
							.showInputDialog(
									tagList,
									"Please enter external reference (URL, publication data, etc)",
									tagList.getSelectedValues()[0]);
					if ((tag != null) && (!tag.isEmpty()))
						tags.setElementAt(tag, tagList.getSelectedIndex());
				}*/
				TagWindow tagw = new TagWindow(parent, 1);
				tagw.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				tagw.setVisible(true);
			}
		});
		tagEdit.setBounds(444, 210, 62, 23);
		panel.add(tagEdit);

		JButton tagAdd = new JButton("Add");
		tagAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TagWindow tagw = new TagWindow(parent, 0); //JOptionPane.showInputDialog(tagList,						"Please enter tag");
				tagw.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				tagw.setVisible(true);
			}
		});
		tagAdd.setBounds(444, 179, 62, 23);
		panel.add(tagAdd);

		JLabel lblReferences = new JLabel("references:");
		lblReferences.setBounds(0, 151, 66, 16);
		panel.add(lblReferences);

		
		cvssField = new JTextField();
		cvssField.setBounds(450, 24, 114, 20);
		panel.add(cvssField);
		cvssField.setColumns(10);
		
		JLabel lblCvssValue = new JLabel("CVSS value:");
		lblCvssValue.setBounds(380, 26, 68, 16);
		panel.add(lblCvssValue);		
		
		
		final JList cmThreatUuidField = new JList();
		cmThreatUuidField.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cmThreatUuidField.setBorder(new LineBorder(new Color(0, 0, 0)));
		cmThreatUuidField.setBounds(514, 30, 243, 113);
		cmThreatUuidField.setModel(cm_threat);
		panel2.add(cmThreatUuidField);

		
		JButton btnSend = new JButton("Send!");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				// sanity check: are necessary fields filled in?
				boolean err = false;
				if (uuidField.getText().isEmpty())
				{
					outputArea.append("Threat UUID must be specified!\n");
					err = true;
				}
				if (nameField.getText().isEmpty())
				{
					outputArea.append("Threat name must be specified!\n");
					err = true;
				}
				
				if (err)
					return;				
				
				svrs.schema.upload.Resource r = new svrs.schema.upload.Resource();
				svrs.schema.upload.Resourcedata rdat = new svrs.schema.upload.Resourcedata();
				// Assemble Resource object based on GUI fields

				svrs.schema.upload.Coreelement c = new svrs.schema.upload.Coreelement();
				c.setResourcedata(rdat);
				r.setCoreelement(c);
				rdat.setResourcetype(typeDropdown.getSelectedItem().toString());
				// Common settings
				rdat.setAccesslevel(BigInteger.ZERO); // public
				GregorianCalendar cal = new GregorianCalendar();
				try {
					rdat.setCreationdate(DatatypeFactory.newInstance()
							.newXMLGregorianCalendar(cal));
				} catch (DatatypeConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				rdat.setDescription(descArea.getText());
				rdat.setName(nameField.getText());
				rdat.setUuid(uuidField.getText());
				try {
					Double d = Double.valueOf(cvssField.getText());
					if (d != null) c.setCvss(d);
				} catch (NumberFormatException e)
				{
				}

				// erefs
				if (!erm.isEmpty()) {
					svrs.schema.upload.Resourcedata.Externalrefs erefs = new svrs.schema.upload.Resourcedata.Externalrefs();
					for (int i = 0; i < erm.getSize(); i++) {
						erefs.getExternalref().add(
								erm.getElementAt(i).toString());
					}
					rdat.setExternalrefs(erefs);
				}

				// tags
				if (!tags.isEmpty()) {
					svrs.schema.upload.Resourcedata.Tags etags = new svrs.schema.upload.Resourcedata.Tags();
					for (int i = 0; i < tags.getSize(); i++) {
						etags.getTag().add(tags.getElementAt(i).toString());
					}
					rdat.setTags(etags);
				}

				// update info
				if (rdbtnUpdate.isSelected()) {
					svrs.schema.upload.Updateinfo uinfo = new svrs.schema.upload.Updateinfo();
					uinfo.setOrigversion(BigInteger.ONE);
					uinfo.setUpdatecomment(updateField.getText());
					r.setUpdateinfo(uinfo);
				}

				outputArea.append(cli.processAddResourceEvent(
						uuidField.getText(), r));
			}
		});
		btnSend.setBounds(660, 167, 79, 75);
		panel.add(btnSend);

		JButton cmSendButton = new JButton("Send!");
		cmSendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// send countermeasure
				
				// sanity check: are necessary fields filled in?
				boolean err = false;
				if (cmThreatUuidField.getModel().getSize() == 0)
				{
					outputArea.append("At least one threat ID must be specified!\n");
					err = true;
				}
				if (cmUuidField.getText().isEmpty())
				{
					outputArea.append("Countermeasure UUID must be specified!\n");
					err = true;
				}
				if (cmNameField.getText().isEmpty())
				{
					outputArea.append("Countermeasure name must be specified!\n");
					err = true;
				}
				if (fileField.getText().isEmpty())
				{
					outputArea.append("Countermeasure data must be provided!\n");
					err = true;
				}
				
				if (err)
					return;
				
				svrs.schema.upload.Resource r = new svrs.schema.upload.Resource();
				svrs.schema.upload.Resourcedata rdat = new svrs.schema.upload.Resourcedata();
				// Assemble Resource object based on GUI fields

				// Countermeasure-specific
				svrs.schema.upload.Model m = new svrs.schema.upload.Model();
				m.setFilename(fileField.getText());

				// set threat association
				svrs.schema.upload.Model.Coreassociations cassocs = new svrs.schema.upload.Model.Coreassociations();
				
				for (int j = 0; j < cm_threat.getSize(); j++) {
					svrs.schema.upload.Model.Coreassociations.Coreassociation cassoc = new svrs.schema.upload.Model.Coreassociations.Coreassociation();
					cassoc.setCoreuuid(cm_threat.get(j).toString());
					cassoc.setRelation(comboBox_1.getSelectedItem().toString());
					cassoc.setCoreversion(BigInteger.ONE);
					cassocs.getCoreassociation().add(cassoc);
				}
				m.setCoreassociations(cassocs);

				svrs.schema.upload.Countermeasure c = new svrs.schema.upload.Countermeasure();
				c.setCmtype(comboBox.getSelectedItem().toString());
				c.setContent(payload);
				c.setMimetype("application/x-aniketos");
				// Add these later if needed ...
				// c.setPreconditions
				// c.setResultdesc
				// c.setExecresponsible

				rdat.setResourcetype("countermeasure");
				svrs.schema.upload.Model.Modeldata md = new svrs.schema.upload.Model.Modeldata();
				md.setCountermeasure(c);
				m.setModeldata(md);
				m.setResourcedata(rdat);
				r.setModel(m);

				// Common settings
				rdat.setAccesslevel(BigInteger.ZERO); // public
				GregorianCalendar cal = new GregorianCalendar();
				try {
					rdat.setCreationdate(DatatypeFactory.newInstance()
							.newXMLGregorianCalendar(cal));
				} catch (DatatypeConfigurationException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				rdat.setDescription(cmDescArea.getText());
				rdat.setName(cmNameField.getText());
				rdat.setUuid(cmUuidField.getText());

				// erefs
				if (!cm_erm.isEmpty()) {
					svrs.schema.upload.Resourcedata.Externalrefs erefs = new svrs.schema.upload.Resourcedata.Externalrefs();
					for (int i = 0; i < cm_erm.getSize(); i++) {
						erefs.getExternalref().add(
								cm_erm.getElementAt(i).toString());
					}
					rdat.setExternalrefs(erefs);
				}

				// tags
				if (!cm_tags.isEmpty()) {
					svrs.schema.upload.Resourcedata.Tags etags = new svrs.schema.upload.Resourcedata.Tags();
					for (int i = 0; i < cm_tags.getSize(); i++) {
						etags.getTag().add(cm_tags.getElementAt(i).toString());
					}
					rdat.setTags(etags);
				}

				// update info
				if (rdbtnUpdate.isSelected()) {
					svrs.schema.upload.Updateinfo uinfo = new svrs.schema.upload.Updateinfo();
					uinfo.setOrigversion(BigInteger.ONE);
					uinfo.setUpdatecomment(updateField.getText());
					r.setUpdateinfo(uinfo);
				}

				outputArea.append(cli.processAddResourceEvent(
						cmUuidField.getText(), r));
			}
		});
		cmSendButton.setBounds(660, 170, 79, 75);
		panel2.add(cmSendButton);
		
		JButton cmThreatDeleteButton = new JButton("Del");
		cmThreatDeleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cmThreatUuidField.getSelectedIndex() > -1) {
					cm_threat.removeElementAt(cmThreatUuidField.getSelectedIndex());
				}
			}
		});
		cmThreatDeleteButton.setBounds(444, 119, 62, 23);
		panel2.add(cmThreatDeleteButton);
		
		JButton cmThreatEditButton = new JButton("Edit");
		cmThreatEditButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cmThreatUuidField.getSelectedIndex() > -1) {
					String threat = JOptionPane
							.showInputDialog(cmThreatUuidField,
									"Please enter threat UUID");
					if ((threat != null) && (!threat.isEmpty()))
						cm_threat.setElementAt(threat, cmThreatUuidField.getSelectedIndex());	
				}
			}
		});
		cmThreatEditButton.setBounds(444, 93, 62, 23);
		panel2.add(cmThreatEditButton);
		
		JButton cmThreatAddButton = new JButton("Add");
		cmThreatAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String threat = JOptionPane
						.showInputDialog(cmThreatUuidField,
								"Please enter threat UUID");
				if ((threat != null) && (!threat.isEmpty()))
					cm_threat.addElement(threat);				
			}
		});
		cmThreatAddButton.setBounds(444, 67, 62, 23);
		panel2.add(cmThreatAddButton);
		
		JButton btnNewButton = new JButton("Settings");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettingsWindow settings = new SettingsWindow(parent);
				settings.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				settings.setVisible(true);
			}
		});
		btnNewButton.setBounds(661, 5, 98, 26);
		contentPane.add(btnNewButton);

	}
}
