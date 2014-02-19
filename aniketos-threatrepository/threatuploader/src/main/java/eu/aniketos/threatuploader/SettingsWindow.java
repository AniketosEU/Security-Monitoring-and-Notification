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

import java.awt.Desktop;
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
import java.net.URI;
import java.util.Properties;

public class SettingsWindow extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JPasswordField svrspassField;
	private JTextField svrsuserField;
	private JTextField proxyhostField;
	private JTextField proxyportField;
	private JTextField proxyuserField;
	private JPasswordField proxypassField;
	private String webappuri = "https://svrs.shields-project.eu/ANIKETOS/";

	/**
	 * Launch the application.
	 */
	/*
	 * public static void main(String[] args) { try { SettingsWindow dialog =
	 * new SettingsWindow();
	 * dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	 * dialog.setVisible(true); } catch (Exception e) { e.printStackTrace(); } }
	 */

	/**
	 * Load configuration file.
	 * 
	 */
	private void loadConfig() {
		Properties cfprops = ConfigHandler.readConfigFile();
		if (cfprops.getProperty("svrsusername") != null) {
			svrsuserField.setText(cfprops.getProperty("svrsusername"));
		}
		if (cfprops.getProperty("svrspassword") != null) {
			svrspassField.setText(cfprops.getProperty("svrspassword"));
		}
		if (cfprops.getProperty("proxyusername") != null) {
			proxyuserField.setText(cfprops.getProperty("proxyusername"));
		}
		if (cfprops.getProperty("proxypassword") != null) {
			proxypassField.setText(cfprops.getProperty("proxypassword"));
		}
		if (cfprops.getProperty("proxyhost") != null) {
			proxyhostField.setText(cfprops.getProperty("proxyhost"));
		}
		if (cfprops.getProperty("proxyport") != null) {
			proxyportField.setText(cfprops.getProperty("proxyport"));
		}		
	}

	/**
	 * Save configuration file.
	 * 
	 */
	private void saveConfig() {
		Properties cfprops = new Properties();
		cfprops.setProperty("svrsusername", svrsuserField.getText());
		cfprops.setProperty("svrspassword", new String(svrspassField.getPassword()));
		cfprops.setProperty("proxyusername", proxyuserField.getText());
		cfprops.setProperty("proxypassword", new String(proxypassField.getPassword()));
		cfprops.setProperty("proxyhost", proxyhostField.getText());
		try {
			cfprops.setProperty("proxyport", Integer.toString(Integer.parseInt(proxyportField.getText())));
		} catch (NumberFormatException e)
		{
			// do nothing
		}
		ConfigHandler.writeConfigFile(cfprops);
	}

	/**
	 * Create the dialog.
	 */
	public SettingsWindow(final ThreatUploader parent) {
		setTitle("Settings");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 100, 165, 30, 125, 0 };
		gbl_contentPanel.rowHeights = new int[] { 30, 30, 30, 30, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0,
				Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblUsername = new JLabel("SVRS username");
			GridBagConstraints gbc_lblUsername = new GridBagConstraints();
			gbc_lblUsername.anchor = GridBagConstraints.EAST;
			gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
			gbc_lblUsername.gridx = 0;
			gbc_lblUsername.gridy = 0;
			contentPanel.add(lblUsername, gbc_lblUsername);
		}
		{
			svrsuserField = new JTextField();
			GridBagConstraints gbc_svrsuserField = new GridBagConstraints();
			gbc_svrsuserField.gridwidth = 2;
			gbc_svrsuserField.insets = new Insets(0, 0, 5, 5);
			gbc_svrsuserField.fill = GridBagConstraints.HORIZONTAL;
			gbc_svrsuserField.gridx = 1;
			gbc_svrsuserField.gridy = 0;
			contentPanel.add(svrsuserField, gbc_svrsuserField);
			svrsuserField.setColumns(10);
		}
		{
			JButton btnCreateAccount = new JButton("Create account");
			btnCreateAccount.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
					URI reg = new URI(webappuri + "registration.jsp");
					Desktop.getDesktop().browse(reg);
					} catch (Exception e)
					{
						// TODO: handle
					}
				}
			});
			GridBagConstraints gbc_btnCreateAccount = new GridBagConstraints();
			gbc_btnCreateAccount.insets = new Insets(0, 0, 5, 0);
			gbc_btnCreateAccount.gridx = 3;
			gbc_btnCreateAccount.gridy = 0;
			contentPanel.add(btnCreateAccount, gbc_btnCreateAccount);
		}
		{
			JLabel lblPassword = new JLabel("SVRS password");
			GridBagConstraints gbc_lblPassword = new GridBagConstraints();
			gbc_lblPassword.anchor = GridBagConstraints.EAST;
			gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
			gbc_lblPassword.gridx = 0;
			gbc_lblPassword.gridy = 1;
			contentPanel.add(lblPassword, gbc_lblPassword);
		}
		{
			svrspassField = new JPasswordField();
			GridBagConstraints gbc_svrspassField = new GridBagConstraints();
			gbc_svrspassField.gridwidth = 2;
			gbc_svrspassField.insets = new Insets(0, 0, 5, 5);
			gbc_svrspassField.fill = GridBagConstraints.HORIZONTAL;
			gbc_svrspassField.gridx = 1;
			gbc_svrspassField.gridy = 1;
			contentPanel.add(svrspassField, gbc_svrspassField);
		}
		{
			JButton btnForgotPassword = new JButton("Forgot password?");
			btnForgotPassword.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
					URI reg = new URI(webappuri + "recoverLogin.jsp");
					Desktop.getDesktop().browse(reg);
					} catch (Exception e)
					{
						// TODO: handle
					}
				}
			});
			GridBagConstraints gbc_btnForgotPassword = new GridBagConstraints();
			gbc_btnForgotPassword.insets = new Insets(0, 0, 5, 0);
			gbc_btnForgotPassword.gridx = 3;
			gbc_btnForgotPassword.gridy = 1;
			contentPanel.add(btnForgotPassword, gbc_btnForgotPassword);
		}
		{
			JLabel lblProxyHostName = new JLabel("Proxy host name");
			GridBagConstraints gbc_lblProxyHostName = new GridBagConstraints();
			gbc_lblProxyHostName.anchor = GridBagConstraints.EAST;
			gbc_lblProxyHostName.insets = new Insets(0, 0, 5, 5);
			gbc_lblProxyHostName.gridx = 0;
			gbc_lblProxyHostName.gridy = 3;
			contentPanel.add(lblProxyHostName, gbc_lblProxyHostName);
		}
		{
			proxyhostField = new JTextField();
			GridBagConstraints gbc_proxyhostField = new GridBagConstraints();
			gbc_proxyhostField.insets = new Insets(0, 0, 5, 5);
			gbc_proxyhostField.fill = GridBagConstraints.HORIZONTAL;
			gbc_proxyhostField.gridx = 1;
			gbc_proxyhostField.gridy = 3;
			contentPanel.add(proxyhostField, gbc_proxyhostField);
			proxyhostField.setColumns(10);
		}
		{
			JLabel lblPort = new JLabel("port");
			GridBagConstraints gbc_lblPort = new GridBagConstraints();
			gbc_lblPort.anchor = GridBagConstraints.EAST;
			gbc_lblPort.insets = new Insets(0, 0, 5, 5);
			gbc_lblPort.gridx = 2;
			gbc_lblPort.gridy = 3;
			contentPanel.add(lblPort, gbc_lblPort);
		}
		{
			proxyportField = new JTextField();
			GridBagConstraints gbc_proxyportField = new GridBagConstraints();
			gbc_proxyportField.insets = new Insets(0, 0, 5, 0);
			gbc_proxyportField.fill = GridBagConstraints.HORIZONTAL;
			gbc_proxyportField.gridx = 3;
			gbc_proxyportField.gridy = 3;
			contentPanel.add(proxyportField, gbc_proxyportField);
			proxyportField.setColumns(10);
		}
		{
			JLabel lblProxyUsername = new JLabel("Proxy username");
			GridBagConstraints gbc_lblProxyUsername = new GridBagConstraints();
			gbc_lblProxyUsername.anchor = GridBagConstraints.EAST;
			gbc_lblProxyUsername.insets = new Insets(0, 0, 0, 5);
			gbc_lblProxyUsername.gridx = 0;
			gbc_lblProxyUsername.gridy = 4;
			contentPanel.add(lblProxyUsername, gbc_lblProxyUsername);
		}
		{
			proxyuserField = new JTextField();
			GridBagConstraints gbc_proxyuserField = new GridBagConstraints();
			gbc_proxyuserField.insets = new Insets(0, 0, 0, 5);
			gbc_proxyuserField.fill = GridBagConstraints.HORIZONTAL;
			gbc_proxyuserField.gridx = 1;
			gbc_proxyuserField.gridy = 4;
			contentPanel.add(proxyuserField, gbc_proxyuserField);
			proxyuserField.setColumns(10);
		}
		{
			JLabel lblPwd = new JLabel("pwd");
			GridBagConstraints gbc_lblPwd = new GridBagConstraints();
			gbc_lblPwd.anchor = GridBagConstraints.EAST;
			gbc_lblPwd.insets = new Insets(0, 0, 0, 5);
			gbc_lblPwd.gridx = 2;
			gbc_lblPwd.gridy = 4;
			contentPanel.add(lblPwd, gbc_lblPwd);
		}
		{
			proxypassField = new JPasswordField();
			GridBagConstraints gbc_proxypassField = new GridBagConstraints();
			gbc_proxypassField.fill = GridBagConstraints.HORIZONTAL;
			gbc_proxypassField.gridx = 3;
			gbc_proxypassField.gridy = 4;
			contentPanel.add(proxypassField, gbc_proxypassField);
			proxypassField.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new GridLayout(0, 1, 0, 0));
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveConfig();
						parent.updateSettings();
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
		loadConfig();
	}

}
