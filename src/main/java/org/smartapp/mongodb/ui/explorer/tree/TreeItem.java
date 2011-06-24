package org.smartapp.mongodb.ui.explorer.tree;

import javax.swing.Icon;
import javax.swing.JPopupMenu;

import org.smartapp.mongodb.ui.SessionContainer;
import org.smartapp.mongodb.ui.browser.BrowserContainer;

import com.mongodb.Mongo;

public abstract class TreeItem {

	protected String name;
	private ItemType type;
	protected SessionContainer editorContainer;

	public TreeItem(SessionContainer editorContainer, String name, ItemType type) {
		this.editorContainer = editorContainer;
		this.name = name;
		this.type = type;
	}
	
	


	public Icon getIcon() {
		return type.getIcon();
	}
	

	@Override
	public String toString() {
		return name;
	}

	public abstract JPopupMenu getPopup();

	public abstract void populateBrowseContainer(Mongo mongo, BrowserContainer browserContainer);




	
}