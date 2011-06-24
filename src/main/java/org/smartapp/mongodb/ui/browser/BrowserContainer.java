package org.smartapp.mongodb.ui.browser;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class BrowserContainer extends JPanel {

	private static final long serialVersionUID = 7761718182393645727L;
	private JTextArea textPane;
	
	public BrowserContainer() {
		super(new BorderLayout());
		textPane = new JTextArea();
		textPane.setEditable(false);
		add(new JScrollPane(textPane));
	}

	public void updateText(String text) {
		textPane.setText(text);
	}

}
