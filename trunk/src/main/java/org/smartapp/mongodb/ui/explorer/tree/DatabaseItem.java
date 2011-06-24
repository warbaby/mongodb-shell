package org.smartapp.mongodb.ui.explorer.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.smartapp.mongodb.ui.SessionContainer;
import org.smartapp.mongodb.ui.browser.BrowserContainer;

import com.mongodb.Mongo;

public class DatabaseItem extends TreeItem {

	public DatabaseItem(SessionContainer editorContainer, String name) {
		super(editorContainer, name, ItemType.DATABASE);
	}

	@Override
	public JPopupMenu getPopup() {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem useDbItem = new JMenuItem("Generate use");
		useDbItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				editorContainer.insertText("\nuse " + name + ";\n");
				
			}
		});
		popup.add(useDbItem);
		JMenuItem dropDbItem = new JMenuItem("Generate drop");
		dropDbItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				editorContainer.insertText("\ndb.dropDatabase();\n");
				
			}
		});
		popup.add(dropDbItem);
		
		return popup;
	}

	@Override
	public void populateBrowseContainer(Mongo mongo, BrowserContainer browserContainer) {
		// provide some useful information about the database;
		StringBuilder sb = new StringBuilder();
		sb.append("Database: ").append(name).append("\n");
		Set<String> colNames = mongo.getDB(name).getCollectionNames();
		if (colNames.size() == 0)
			sb.append("No collections found");
		else {
			sb.append(colNames.size()).append(" collections").append(" found\n");
			for(String colName: colNames)
				sb.append("- ").append(colName).append("\n");
		}
		browserContainer.updateText(sb.toString());
		
	}

}
