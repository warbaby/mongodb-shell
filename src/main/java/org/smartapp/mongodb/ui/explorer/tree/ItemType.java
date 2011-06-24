package org.smartapp.mongodb.ui.explorer.tree;

import javax.swing.Icon;

import org.smartapp.mongodb.ui.MainWindow;

public enum ItemType {
	
	DATABASE(MainWindow.createIcon("database.png")), 
	COLLECTION(MainWindow.createIcon("database_table.png"));
	
	private ItemType(Icon icon) {
		this.icon = icon;
	}
	
	private Icon icon;

	public Icon getIcon() {
		return this.icon;
	}
	
		
}
