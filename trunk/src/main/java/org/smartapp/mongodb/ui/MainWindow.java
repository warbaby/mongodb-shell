package org.smartapp.mongodb.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.mongodb.Mongo;

import org.smartapp.mongodb.config.ConnectionConfig;
import org.smartapp.mongodb.console.Console;
import org.smartapp.mongodb.console.TextAreaConsoleImpl;

public class MainWindow extends JFrame {

	static final String VERSION = "0.2-alpha";
	/** */
	private static final long serialVersionUID = -951506042833748903L;
	private static JFileChooser fileChooser;
	private JTabbedPane editorTabContailer;
	private TextAreaConsoleImpl console;
	
	public Console getConsole() {
		return console;
	}

	public MainWindow() {
		
		super("mongodb-shell " + VERSION);

		console = new TextAreaConsoleImpl();

		Component configContailer = new ConfigContainer(this);

		editorTabContailer = new JTabbedPane();
		
//		JSplitPane mainContainer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, configContailer, editorTabContailer);
//		mainContainer.setDividerLocation(180);
//		mainContainer.setEnabled(false);
		
		JPanel mainContainer = new JPanel(new BorderLayout());
		mainContainer.add(configContailer, BorderLayout.WEST);
		mainContainer.add(editorTabContailer, BorderLayout.CENTER);


		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainContainer, new JScrollPane(console));
		splitPanel.setDividerLocation(650);
		add(splitPanel);
		setSize(1000, 800);
		
		setLocation(100, 100);
		
		
		

	}
	
	public void connect(ConnectionConfig config) {
		try {
			Mongo mongo = new Mongo(config.getHost(), config.getPort());
			// validate connection
			mongo.getDatabaseNames();
			Component editorContainer = new EditorContainer(mongo, config.getName(), console);
			editorTabContailer.addTab(config.getName(), editorContainer);
			editorTabContailer.setSelectedIndex(editorTabContailer.getTabCount() - 1);
			console.info("Connected to: ", config.getName());
		} catch (Exception e) {
			e.printStackTrace();
			console.error("Error: ", e.getClass().getSimpleName(), ": ", e.getMessage());
			JOptionPane.showMessageDialog(this, e.getClass().getSimpleName() + ": " + e.getMessage(), "Unable to connect", JOptionPane.ERROR_MESSAGE);
			
		}
		
	}
	
	public static Icon createIcon(String name) {
		return new ImageIcon(MainWindow.class.getResource("/icons/" + name));
	}

	public static JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser("/");
		}
		
		return fileChooser;
	}

	public boolean canClose() {
		while (editorTabContailer.getTabCount() > 0) {
			EditorContainer editorContainer = (EditorContainer) editorTabContailer.getComponentAt(editorTabContailer.getSelectedIndex());
			if (! editorContainer.closeCurrentSession())
				return false;
		}
		return true;
	}
	
}
