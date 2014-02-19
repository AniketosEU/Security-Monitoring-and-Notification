package eu.aniketos.threatrepository.client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.UUID;

import javax.swing.JToggleButton;
import java.awt.Font;

/** A simple GUI for the Threat Repository Client.
 * 
 * @author balazs
 *
 */
public class ThreatRepositoryClientWindow extends JFrame {

	private static final long serialVersionUID = 1L; /**< Helper variable for serializable attribute. */
	private JPanel contentPane; /**< The main pane containing the client UI elements. */
	private JTextField uuidField; /**< A text field where the user can enter a UUID or a search query. */
	private ThreatRepositoryClient cli; /**< The client object. */

	/**
	 * Launch the application.
	 * 
	 * @param args Command-line arguments (not used)
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ThreatRepositoryClientWindow frame = new ThreatRepositoryClientWindow(new ThreatRepositoryClient());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @param client The ThreatRepositoryClient object to use
	 */
	public ThreatRepositoryClientWindow(ThreatRepositoryClient client) {
		cli = client; 
		setTitle("Simple Threat Repository Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 689, 522);
		contentPane = new JPanel(); 
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		uuidField = new JTextField(); 
		uuidField.setBounds(44, 5, 259, 21);
		contentPane.add(uuidField);
		uuidField.setColumns(10);
		
		JLabel lblUuid = new JLabel("Query:");
		lblUuid.setBounds(5, 5, 40, 21);
		contentPane.add(lblUuid);

		final JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Courier New", Font.PLAIN, 11));
		textArea.setLineWrap(true);

		JScrollPane textOutput = new JScrollPane(textArea);
		textOutput.setBounds(5, 33, 654, 462);
		contentPane.add(textOutput);

		final JToggleButton counterButton = new JToggleButton("Threat");
		counterButton.setFont(new Font("Tahoma", Font.PLAIN, 9));
		counterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (counterButton.isSelected())
					counterButton.setText("Countermeasure");
				else
					counterButton.setText("Threat");
			}
		});
		counterButton.setBounds(309, 4, 71, 23);
		contentPane.add(counterButton);
		
		
		JButton btnDownload = new JButton("Download");
		btnDownload.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (uuidField.getText().isEmpty())
				{
					textArea.append("Please specify a UUID\n");
					return;
				}
				try {
					UUID uuid = UUID.fromString(uuidField.getText());
				}
				catch (IllegalArgumentException e){
					textArea.append("Please use canonical UUID format: hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh");
					return;
				}
				if (counterButton.isSelected())
					textArea.append(cli.processDownloadCountermeasureEvent(uuidField.getText()));
				else
					textArea.append(cli.processDownloadThreatEvent(uuidField.getText()));
			}
		});
		btnDownload.setBounds(390, 4, 89, 23);
		contentPane.add(btnDownload);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (uuidField.getText().isEmpty())
				{
					if (counterButton.isSelected())
						textArea.append(cli.processSearchCountermeasureEvent(null));
					else
						textArea.append(cli.processSearchThreatEvent(null));
				}
				if (counterButton.isSelected())
					textArea.append(cli.processSearchCountermeasureEvent(uuidField.getText()));
				else
					textArea.append(cli.processSearchThreatEvent(uuidField.getText()));
			}
		});		
		btnSearch.setBounds(489, 4, 89, 23);
		contentPane.add(btnSearch);
		
		JButton btnUpload = new JButton("Upload...");
		final JFileChooser ch = new JFileChooser();
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// pop up file open dialog
				int retval = ch.showOpenDialog(contentPane);
				if (retval == JFileChooser.APPROVE_OPTION)
				{
					if (uuidField.getText().isEmpty())
					{
						textArea.append("Please specify a UUID\n");
						return;
					}
					try {
						UUID uuid = UUID.fromString(uuidField.getText());
					}
					catch (IllegalArgumentException ex){
						textArea.append("Please use canonical UUID format: hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh");
						return;
					}
					if(counterButton.isSelected())
					{
						textArea.append(cli.processAddCountermeasureEvent(uuidField.getText(), ch.getSelectedFile()));
					}
					else 
					{
						textArea.append(cli.processAddThreatEvent(uuidField.getText(), ch.getSelectedFile()));
					}
					
				}
				
			}
		});
		btnUpload.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnUpload.setBounds(586, 4, 89, 23);
		contentPane.add(btnUpload);
	}
}
