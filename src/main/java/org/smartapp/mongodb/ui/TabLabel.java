package org.smartapp.mongodb.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TabLabel extends JPanel {

	private static final long serialVersionUID = 3702724617508499481L;
	private JLabel label;

	public TabLabel(String title, final Closeable closable) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setOpaque(false);
		JButton closeButton = new JButton(MainWindow.createIcon("cross_grey.png"));
		closeButton.setRolloverIcon(MainWindow.createIcon("cross.png"));
		closeButton.setFocusable(false);
//		disconnectButton.setUI(new BasicButtonUI());
        //Make it transparent
		closeButton.setContentAreaFilled(false);
        //No need to be focusable
        closeButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
//        disconnectButton.setBorderPainted(false);		
		closeButton.setPreferredSize(new Dimension(20, 20));
		closeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				closable.close();
				
			}
		});
		 label = new JLabel(title);
		label.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 5));
		label.setOpaque(false);
		add(label);
		add(closeButton);
		
		
	}
	
	public String getTitle() {
		return label.getText();
	}
	
	public void setTitle(String title) {
		this.label.setText(title);
	}

	
	

}
