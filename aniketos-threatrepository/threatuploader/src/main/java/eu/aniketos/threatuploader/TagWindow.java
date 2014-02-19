package eu.aniketos.threatuploader;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Dialog.ModalityType;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.BoxLayout;
import javax.swing.SwingWorker;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;

public class TagWindow extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private final JComboBox comboBox;
	private int action;

	/** This helper method fetches a list of tags from the SVRS.
	 * 
	 */
	private void loadTags(final ThreatUploader parent, int act) {
		List<TagData> tags = parent.fetchTags();
		comboBox.setEnabled(true);
		comboBox.setEditable(true);
		comboBox.removeAllItems();
		for (TagData t: tags)
		{
			comboBox.addItem(t.tag);
		}
		
		if (act == 1)
		{
			Object curval; 
			if (parent.tabbedPane.getSelectedIndex() == 1) {
				// cm
				curval = parent.cmTagList.getSelectedValue();
			}
			else {
				// threat
				curval = parent.tagList.getSelectedValue();
			}
			
			// when editing, set focus to currently selected tag
			comboBox.setSelectedItem(curval);
			if (!comboBox.getSelectedItem().equals(curval))
			{
				// add if needed
				comboBox.addItem(curval);
				comboBox.setSelectedItem(curval);
			}

		}
		
		
		
		comboBox.validate();
	}
	
	/**
	 * Create the dialog.
	 */
	public TagWindow(final ThreatUploader parent, int act) {
		action = act;
		if (action == 1) 
		{
			// edit
			setTitle("Edit tag");
		} else
		{
			//add
			setTitle("Add tag");
		}
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(100, 100, 450, 150);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 100, 165, 86, 0 };
		gbl_contentPanel.rowHeights = new int[] { 30, 30, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0, 1.0,
				Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0,
				Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblUsername = new JLabel("Tag name");
			GridBagConstraints gbc_lblUsername = new GridBagConstraints();
			gbc_lblUsername.anchor = GridBagConstraints.EAST;
			gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
			gbc_lblUsername.gridx = 0;
			gbc_lblUsername.gridy = 0;
			contentPanel.add(lblUsername, gbc_lblUsername);
		}
		{
			comboBox = new JComboBox();
			comboBox.setEnabled(false);
			comboBox.setModel(new DefaultComboBoxModel(new String[] {"Fetching tag list..."}));
			comboBox.setToolTipText("A list of tags currently in the SVRS. Select an existing one or add your own.");
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.gridwidth = 2;
			gbc_comboBox.insets = new Insets(0, 0, 5, 5);
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = 1;
			gbc_comboBox.gridy = 0;
			contentPanel.add(comboBox, gbc_comboBox);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new GridLayout(0, 1, 0, 0));
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (action == 1) {
							// edit
							if (parent.tabbedPane.getSelectedIndex() == 1) {
								// cm
								parent.cm_tags.setElementAt(
										comboBox.getSelectedItem(),
										parent.cmTagList.getSelectedIndex());
							} else {
								// threat
								parent.tags.setElementAt(
										comboBox.getSelectedItem(),
										parent.tagList.getSelectedIndex());
							}
						} else {

							// add
							if (parent.tabbedPane.getSelectedIndex() == 1) {
								// cm
								parent.cm_tags.add(parent.cm_tags.size(),
										comboBox.getSelectedItem());
							} else {
								// threat
								parent.tags.add(parent.tags.size(),
										comboBox.getSelectedItem());
							}
						}
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		SwingWorker w = new SwingWorker<Integer, Void>() {

			@Override
			protected Integer doInBackground() throws Exception {
				loadTags(parent, action);
				return 0;
			}
		};
		w.execute();
	}


}
