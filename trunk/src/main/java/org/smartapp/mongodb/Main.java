package org.smartapp.mongodb;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.smartapp.mongodb.ui.MainWindow;

public class Main implements Runnable {

	private MainWindow mainFrame;

	public void run() {
		mainFrame = new MainWindow();
		// textArea = new JTextArea();
		// textArea.setFont(new Font("SansSerif", Font.PLAIN, 22));
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (mainFrame.canClose()) {
					mainFrame.setVisible(false);
					// Perform any other operations you might need
					// before exit.
					System.exit(0);
				}
			}
		});
		// mainFrame.add(textArea);
		// mainFrame.pack();
		mainFrame.setVisible(true);
	}

	public static void main(String[] args) {
		Runnable app = new Main();
		try {
			SwingUtilities.invokeAndWait(app);
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	
}
