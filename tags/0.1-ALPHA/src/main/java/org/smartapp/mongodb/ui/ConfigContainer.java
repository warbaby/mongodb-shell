package org.smartapp.mongodb.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import com.thoughtworks.xstream.XStream;

import org.smartapp.mongodb.config.ConfigRoot;
import org.smartapp.mongodb.config.ConnectionConfig;

public class ConfigContainer extends JPanel {
	
	private static final long serialVersionUID = 84089093022263727L;
	private DefaultListModel configListModel;
	private MainWindow mainWindow;
	private XStream xstream;

	public ConfigContainer(MainWindow mainWindow) {
		super(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		
		configListModel = new DefaultListModel();
		final JList configList = new JList(configListModel);
		
		configList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		this.mainWindow = mainWindow;
		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton connectButton = new JButton(MainWindow.createIcon("connect.png"));
		connectButton.setFocusable(false);
		connectButton.setPreferredSize(new Dimension(20, 20));
		connectButton.setToolTipText("Open connection");
		connectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int tmp = configList.getSelectedIndex();
				if (tmp >= 0) {
					ConfigContainer.this.mainWindow.connect((ConnectionConfig) configListModel.getElementAt(tmp));
				}
				
			}
		});
		toolbar.add(connectButton);
		
		Component spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(20, 20));
		toolbar.add(spacer );
		
		JButton addButton = new JButton(MainWindow.createIcon("add.png"));
		addButton.setFocusable(false);
		addButton.setPreferredSize(new Dimension(20, 20));
		addButton.setToolTipText("Add new connection");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addNewConnection();
				
			}
		});
		toolbar.add(addButton);

		JButton deleteButton = new JButton(MainWindow.createIcon("delete.png"));
		deleteButton.setFocusable(false);
		deleteButton.setPreferredSize(new Dimension(20, 20));
		deleteButton.setToolTipText("Remove a connection");
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int tmp = configList.getSelectedIndex();
				if (tmp >= 0) {
					if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(ConfigContainer.this.mainWindow, "Are you sure to remove selected connection?", "Remove Connection", JOptionPane.YES_NO_OPTION)) {
						configListModel.removeElementAt(tmp);
						saveConfiguration();
					}
				}
				
			}
		});
		toolbar.add(deleteButton);
		
		JButton editButton = new JButton(MainWindow.createIcon("pencil.png"));
		editButton.setFocusable(false);
		editButton.setPreferredSize(new Dimension(20, 20));
		editButton.setToolTipText("Edit a connection");
		editButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int tmp = configList.getSelectedIndex();
				if (tmp >= 0) {
					ConfigContainer.this.editConnection(tmp);
				}
				
			}
		});
		toolbar.add(editButton);
		spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(20, 20));
		toolbar.add(spacer );
		
		JButton aboutButton = new JButton(MainWindow.createIcon("information.png"));
		aboutButton.setFocusable(false);
		aboutButton.setPreferredSize(new Dimension(20, 20));
		aboutButton.setToolTipText("About...");
		aboutButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(ConfigContainer.this.mainWindow, 
						"<html><h2>The mongodb-shell 0.1-alpha.</h2>\n" +
						"Visit http://code.google.com/p/mongodb-shell\n" +
						"for more information.", "About", JOptionPane.INFORMATION_MESSAGE);
				
			}
		});
		toolbar.add(aboutButton);
		

		add(toolbar, BorderLayout.NORTH);
		add(configList, BorderLayout.CENTER);
		
		
		loadConfiguration();
		
		MouseListener mouseListener = new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
		        if (e.getClickCount() == 2) {
		            int index = configList.locationToIndex(e.getPoint());
		            ConnectionConfig connectioncConfig = (ConnectionConfig) configListModel.get(index);
		            if (connectioncConfig != null) {
		            	ConfigContainer.this.mainWindow.connect(connectioncConfig);
		            }
		            
		         }
		    }
		};
		configList.addMouseListener(mouseListener);
		
		

		
	}
	
	
	protected void editConnection(int index) {
		ConnectionDialog dialog = new ConnectionDialog(mainWindow, true);
		final ConnectionConfig toUpdate = (ConnectionConfig) configListModel.getElementAt(index);
		dialog.setConnectionConfig(toUpdate);
		dialog.setListener(new ConfigurationListener() {
			
			@Override
			public void configurationUpdated(ConnectionConfig value) {
				toUpdate.setName(value.getName());
				toUpdate.setHost(value.getHost());
				toUpdate.setPort(value.getPort());
				saveConfiguration();
				
			}
		});
		dialog.setModal(true);
		dialog.setVisible(true);
		
	}


	private void addNewConnection() {
		ConnectionDialog dialog = new ConnectionDialog(mainWindow, false);
		dialog.setListener(new ConfigurationListener() {
			
			@Override
			public void configurationUpdated(ConnectionConfig connectionConfig) {
				configListModel.addElement(connectionConfig);
				
				saveConfiguration();
				
			}
		});
		dialog.setModal(true);
		dialog.setVisible(true);
	}

	private void loadConfiguration() {
		File configFile = new File(System.getProperty("user.home") + "/.mongo-shell-config.xml");
		if (configFile.exists()) {
			try {
				XStream xstream = getXStream();
				
				ConfigRoot root = (ConfigRoot) xstream.fromXML(new FileInputStream(configFile));
				if (root.getConnections() != null)
					for (ConnectionConfig connection: root.getConnections()) {
						configListModel.addElement(connection);
					}
			} catch (IOException e) {
				mainWindow.getConsole().error("Error reading config file: " + e.getMessage());
				e.printStackTrace();
				
			}
		}
		
	}
	
	private void saveConfiguration() {
		File configFile = new File(System.getProperty("user.home") + "/.mongo-shell-config.xml");
		try {
			XStream xstream = getXStream();
			
			ConfigRoot root = new ConfigRoot();
			
			ArrayList<ConnectionConfig> connections = new ArrayList<ConnectionConfig>();
			root.setConnections(connections);
			for (int i = 0; i < configListModel.getSize(); i++) {
				ConnectionConfig connection = (ConnectionConfig) configListModel.getElementAt(i);
				connections.add(connection);
			}
				
			xstream.toXML(root, new FileOutputStream(configFile));
		} catch (IOException e) {
			mainWindow.getConsole().error("Error writing config file: " + e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	


	private XStream getXStream() {
		if (xstream == null) {
			xstream = new XStream();
			xstream.alias("configuration", ConfigRoot.class);
			xstream.alias("connections", ArrayList.class);
			xstream.alias("connection", ConnectionConfig.class);
			xstream.useAttributeFor(ConnectionConfig.class, "name");
			xstream.useAttributeFor(ConnectionConfig.class, "host");
			xstream.useAttributeFor(ConnectionConfig.class, "port");
			xstream.useAttributeFor(ConnectionConfig.class, "authentication");
			xstream.useAttributeFor(ConnectionConfig.class, "username");
			xstream.useAttributeFor(ConnectionConfig.class, "password");
		}
		return xstream;
	}

}
