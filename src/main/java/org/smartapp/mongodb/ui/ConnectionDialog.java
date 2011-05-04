package org.smartapp.mongodb.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import com.mongodb.Mongo;

import org.smartapp.mongodb.config.ConnectionConfig;


public class ConnectionDialog extends JDialog {


	private static final long serialVersionUID = 1997795810534114366L;
	private JTextField nameField;
	private JTextField hostField;
	private JTextField portField;
	
	private JTextField usernameField;
	private JTextField passwordField;
	private JCheckBox authCheckbox;
	
	
	public ConnectionDialog(final MainWindow parent, boolean editMode) {
		super(parent, editMode ? "Edit Connection" : "Add Connection");
		
		JPanel mainPanel = new JPanel();
		
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		mainPanel.setLayout(new BorderLayout());
		JPanel formContainer = new JPanel(new BorderLayout());
		
		JPanel formPanel = new JPanel(new GridBagLayout());
		
		
		JPanel bottomPanel = new JPanel();

		JButton testButton = new JButton("Test");
		bottomPanel.add(testButton);
		
		testButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e1) {
				if (validateForm()) {
					try {
						ConnectionConfig config = buildConnectionConfig();
						Mongo mongo = new Mongo(config.getHost(), config.getPort());
						// validate connection
						mongo.getDatabaseNames();
						parent.getConsole().info("Connection test successful!");
						mongo.close();
					} catch (Exception e) {
						e.printStackTrace();
						parent.getConsole().error("Error: ", e.getClass().getSimpleName(), ": ", e.getMessage());
						JOptionPane.showMessageDialog(ConnectionDialog.this, e.getClass().getSimpleName() + ": " + e.getMessage(), "Unable to connect", JOptionPane.ERROR_MESSAGE);
						
					}
				} else {
					JOptionPane.showMessageDialog(ConnectionDialog.this, "Please correct highlighted fields", "Validation Error", JOptionPane.WARNING_MESSAGE);
				}
				
			}
		});
		
		
		JButton confirmButton = new JButton(editMode ? "Save" : "Add");
		bottomPanel.add(confirmButton);
		
		confirmButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				confirm();
				
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		bottomPanel.add(cancelButton);
		
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
				
			}
		});
		
		
		
		formPanel.add(new JLabel("Name"), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0,0));

		nameField = new JTextField();
		formPanel.add(nameField,   new GridBagConstraints(1, 0, 1, 1, 4, 1, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0,0));
		
		formPanel.add(new JLabel("Host"), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0,0));
		
		hostField = new JTextField();
		formPanel.add(hostField,   new GridBagConstraints(1, 1, 1, 1, 4, 1, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0,0));
		
		formPanel.add(new JLabel("Port"), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0,0));
		
		portField = new JTextField();
		formPanel.add(portField,   new GridBagConstraints(1, 2, 1, 1, 4, 1, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0,0));
		
		authCheckbox = new JCheckBox("Authentication");
//		authCheckbox.addChangeListener(new ChangeListener() {
//			
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				updateAuthFields();
//			}
//		});
//		formPanel.add(authCheckbox,   new GridBagConstraints(0, 3, 2, 1, 1, 1, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0,0));
//		
//		
//
//		formPanel.add(new JLabel("Username"), new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0,0));
//		
		usernameField = new JTextField();
//		usernameField.setEnabled(false);
//		formPanel.add(usernameField,   new GridBagConstraints(1, 4, 1, 1, 4, 1, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(00, 0, 0, 0), 0,0));
//		
//		formPanel.add(new JLabel("Password"), new GridBagConstraints(0, 5, 1, 1, 1, 1, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0,0));
//		
		passwordField = new JPasswordField();
//		passwordField.setEnabled(false);
//		formPanel.add(passwordField,   new GridBagConstraints(1, 5, 1, 1, 4, 1, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0,0));
		
		
		
		formPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		formContainer.setBorder(BorderFactory.createEtchedBorder());
		
		formContainer.add(formPanel);
		mainPanel.add(formContainer, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		
		add(mainPanel);
		
		setSize(400, 160);
		
		setLocation(parent.getLocation().x + parent.getSize().width / 2 - 200, parent.getLocation().y + parent.getSize().height / 2 - 80);
		setResizable(false);
		
		
	}
	
	
	protected void updateAuthFields() {
		usernameField.setEnabled(authCheckbox.isSelected());
		passwordField.setEnabled(authCheckbox.isSelected());
		
	}


	public void setConnectionConfig(ConnectionConfig connectionConfig) {
		nameField.setText(connectionConfig.getName());
		hostField.setText(connectionConfig.getHost());
		portField.setText(String.valueOf(connectionConfig.getPort()));
		authCheckbox.setSelected(connectionConfig.isAuthentication());
		usernameField.setText(connectionConfig.getUsername());
		passwordField.setText(connectionConfig.getPassword());
		updateAuthFields();
	}
	
	private ConfigurationListener listener;
	
	public void setListener(ConfigurationListener listener) {
		this.listener = listener;
	}

	protected void confirm() {
		
		if (validateForm()) {
		
			setVisible(false);
			if (listener != null) {
				ConnectionConfig connectionConfig = buildConnectionConfig();
				listener.configurationUpdated(connectionConfig );
			}
		} else {
			JOptionPane.showMessageDialog(this, "Please correct highlighted fields", "Validation Error", JOptionPane.WARNING_MESSAGE);
		}
		
		
	}


	private ConnectionConfig buildConnectionConfig() {
		ConnectionConfig connectionConfig = new ConnectionConfig();
		connectionConfig.setName(nameField.getText());
		connectionConfig.setHost(hostField.getText());
		connectionConfig.setPort(Integer.parseInt(portField.getText()));
		connectionConfig.setAuthentication(authCheckbox.isSelected());
		connectionConfig.setUsername(usernameField.getText());
		connectionConfig.setPassword(passwordField.getText());
		return connectionConfig;
	}

	private boolean validateForm() {
		boolean result = true;
		if (nameField.getText() == null || nameField.getText().trim().length() == 0) {
			nameField.setBackground(new Color(255, 222, 222));
			nameField.setToolTipText("Name can't be empty");
			result = false;
		} else {
			nameField.setBackground(Color.white);
			nameField.setToolTipText("");
			
		}
		if (hostField.getText() == null || hostField.getText().trim().length() == 0) {
			hostField.setBackground(new Color(255, 222, 222));
			hostField.setToolTipText("Host can't be empty");
			result = false;
		} else {
			hostField.setBackground(Color.white);
			hostField.setToolTipText("");
			
		}
		String port = portField.getText();
		if (port == null || port.trim().length() == 0) {
			portField.setBackground(new Color(255, 222, 222));
			portField.setToolTipText("Port can't be empty");
			result = false;
		} else {
			boolean validPort = true;
			try {
				int tmp = Integer.parseInt(port);
				if (tmp < 0 || tmp > 65565)
					validPort = false;
			} catch (Exception e) {
				validPort = false;
			}
			if (validPort) {
				portField.setBackground(Color.white);
				portField.setToolTipText("");
			} else {
				portField.setBackground(new Color(255, 222, 222));
				portField.setToolTipText("Invalid port number");
				result = false;
			}
			
		}
		return result;
	}


	protected void cancel() {
		setVisible(false);
		
	}
	

}
