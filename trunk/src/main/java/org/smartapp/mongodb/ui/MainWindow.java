package org.smartapp.mongodb.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.smartapp.mongodb.config.ConnectionConfig;
import org.smartapp.mongodb.console.Console;
import org.smartapp.mongodb.console.TextAreaConsoleImpl;

import com.mongodb.Mongo;

public class MainWindow extends JFrame {

	static final String VERSION = "0.3-alpha";
	/** */
	private static final long serialVersionUID = -951506042833748903L;
	private static JFileChooser fileChooser;
	private JTabbedPane editorTabContainer;
	private TextAreaConsoleImpl console;
	
	public Console getConsole() {
		return console;
	}

	public MainWindow() {
		
		super("mongodb-shell " + VERSION);

		console = new TextAreaConsoleImpl();
		JPanel consoleResizableWrapper = new JPanel(new BorderLayout(0,0));
		consoleResizableWrapper.add(console);
		

		Component configContailer = new ConfigContainer(this);

		editorTabContainer = new JTabbedPane();
		
//		JSplitPane mainContainer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, configContailer, editorTabContailer);
//		mainContainer.setDividerLocation(180);
//		mainContainer.setEnabled(false);
		
		JPanel mainContainer = new JPanel(new BorderLayout());
		mainContainer.add(configContailer, BorderLayout.WEST);
		mainContainer.add(editorTabContainer, BorderLayout.CENTER);


		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainContainer, new JScrollPane(consoleResizableWrapper));
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
			String title = config.getName();
			int count = 1;
			while (titleExists(title)) {
				title = config.getName() + " (" + (++count) +")";
			}
			SessionContainer editorContainer = new SessionContainer(mongo, title, console);
			editorTabContainer.addTab(title, editorContainer);
			editorTabContainer.setTabComponentAt(editorTabContainer.getTabCount() - 1, new TabLabel(title, editorContainer));
			editorTabContainer.setSelectedIndex(editorTabContainer.getTabCount() - 1);
			console.info("Connected to: ", title);
		} catch (Exception e) {
			e.printStackTrace();
			console.error("Error: ", e.getClass().getSimpleName(), ": ", e.getMessage());
			JOptionPane.showMessageDialog(this, e.getClass().getSimpleName() + ": " + e.getMessage(), "Unable to connect", JOptionPane.ERROR_MESSAGE);
			
		}
		
	}
	
	private boolean titleExists(String title) {
		for (int i = 0; i < editorTabContainer.getTabCount(); i++) {
			String tabTitle = ((TabLabel)editorTabContainer.getTabComponentAt(i)).getTitle();
			if (title.equals(tabTitle) || ("*" + title).equals(tabTitle) )
				return true;
		}
		
		return false;
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
		while (editorTabContainer.getTabCount() > 0) {
			SessionContainer editorContainer = (SessionContainer) editorTabContainer.getComponentAt(editorTabContainer.getSelectedIndex());
			if (! editorContainer.close())
				return false;
		}
		return true;
	}
	
}
