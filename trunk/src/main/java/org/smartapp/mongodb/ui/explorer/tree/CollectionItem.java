package org.smartapp.mongodb.ui.explorer.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.smartapp.mongodb.ui.SessionContainer;
import org.smartapp.mongodb.ui.browser.BrowserContainer;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

public class CollectionItem extends TreeItem {

	private String databaseName;

	public CollectionItem(SessionContainer editorContainer,String name, String databaseName) {
		super(editorContainer, name, ItemType.COLLECTION);
		this.databaseName = databaseName;
	}

	@Override
	public JPopupMenu getPopup() {
		JPopupMenu popup = new JPopupMenu();

		JMenuItem genFindItem = new JMenuItem("Generate find");
		genFindItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (name.indexOf('.') >= 0)
					editorContainer.insertText("\ndb['" + name + "'].find();\n");
				else
					editorContainer.insertText("\ndb." + name + ".find();\n");
				
			}
		});
		popup.add(genFindItem);
		JMenuItem genCountItem = new JMenuItem("Generate count");
		genCountItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (name.indexOf('.') >= 0)
					editorContainer.insertText("\ndb['" + name + "'].count();\n");
				else
					editorContainer.insertText("\ndb." + name + ".count();\n");
				
			}
		});
		popup.add(genCountItem);
		JMenuItem genRemoveItem = new JMenuItem("Generate remove");
		genRemoveItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (name.indexOf('.') >= 0)
					editorContainer.insertText("\ndb['" + name + "'].remove();\n");
				else
					editorContainer.insertText("\ndb." + name + ".remove();\n");
				
			}
		});
		popup.add(genRemoveItem);
		JMenuItem genDropItem = new JMenuItem("Generate drop");
		genDropItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (name.indexOf('.') >= 0)
					editorContainer.insertText("\ndb['" + name + "'].drop();\n");
				else
					editorContainer.insertText("\ndb." + name + ".drop();\n");
				
			}
		});
		popup.add(genDropItem);

		return popup ;
	}
	
	@Override
	public void populateBrowseContainer(Mongo mongo, BrowserContainer browserContainer) {
		DBCollection collection = mongo.getDB(databaseName).getCollection(name);
		
		DBCursor cursor = collection.find();
		
		StringBuilder sb = new StringBuilder();
		
		while(cursor.hasNext()) {
			sb.append(cursor.next()).append("\n");
		}
		
		browserContainer.updateText(sb.toString());
		
		
	}


}
