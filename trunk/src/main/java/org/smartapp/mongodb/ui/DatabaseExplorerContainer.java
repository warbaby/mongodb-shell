package org.smartapp.mongodb.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class DatabaseExplorerContainer extends JPanel {

	private static final long serialVersionUID = 1755425853296600964L;
	private Mongo mongo;
	private DefaultMutableTreeNode rootNode;
	private JTree tree;
	private EditorContainer editorContainer;
	private DefaultTreeModel treeModel;
	
	private static Icon DB_ICON = MainWindow.createIcon("database.png");
	private static Icon COL_ICON = MainWindow.createIcon("database_table.png");

	public DatabaseExplorerContainer(Mongo mongo, EditorContainer editorContainer) {
		super(new BorderLayout());
		
		this.mongo = mongo;
		this.editorContainer = editorContainer;
		
		rootNode = new DefaultMutableTreeNode("mongo");
		treeModel = new DefaultTreeModel( rootNode );
		tree = new JTree(treeModel);
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = -3809960085441324917L;
			
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				
				DefaultMutableTreeNode node =(DefaultMutableTreeNode) value;
				if (node.getUserObject() instanceof TreeObj)
					setIcon(((TreeObj)node.getUserObject()).getIcon());

				return this;
			};
			
		} );
		
		
		MouseAdapter ma = new MouseAdapter() {
			private void myPopupEvent(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				JTree tree = (JTree)e.getSource();
				TreePath path = tree.getPathForLocation(x, y);
				if (path == null)
					return; 

				tree.setSelectionPath(path);

				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				
				TreeObj obj = (TreeObj) node.getUserObject();

				JPopupMenu popup = obj.getPopup();
				popup.show(tree, x, y);
			}
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) myPopupEvent(e);
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) myPopupEvent(e);
			}
		};
		
		tree.addMouseListener(ma);
		
		
		add(new JScrollPane(tree), BorderLayout.CENTER);
		
	}
	
	private class TreeObj {
		private String name;
		private boolean db;

		public TreeObj(String name, boolean db) {
			this.name = name;
			this.db = db;
		}
		
		public JPopupMenu getPopup() {
			JPopupMenu popup = new JPopupMenu();
			if (db) {
				JMenuItem useDbItem = new JMenuItem("Generate use");
				useDbItem.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						useDatabase();
						
					}
				});
				popup.add(useDbItem);
				JMenuItem dropDbItem = new JMenuItem("Generate drop");
				dropDbItem.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						dropDatabase();
						
					}
				});
				popup.add(dropDbItem);
			} else {
				JMenuItem genFindItem = new JMenuItem("Generate find");
				genFindItem.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						generateFind();
						
					}
				});
				popup.add(genFindItem);
				JMenuItem genCountItem = new JMenuItem("Generate count");
				genCountItem.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						generateCount();
						
					}
				});
				popup.add(genCountItem);
				JMenuItem genRemoveItem = new JMenuItem("Generate remove");
				genRemoveItem.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						generateRemove();
						
					}
				});
				popup.add(genRemoveItem);
				JMenuItem genDropItem = new JMenuItem("Generate drop");
				genDropItem.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						generateDrop();
						
					}
				});
				popup.add(genDropItem);
				
				
			}
			return popup;
		}

		protected void generateDrop() {
			if (name.indexOf('.') >= 0)
				editorContainer.appendText("\ndb['" + name + "'].drop();\n");
			else
				editorContainer.appendText("\ndb." + name + ".drop();\n");
			
		}

		protected void generateRemove() {
			if (name.indexOf('.') >= 0)
				editorContainer.appendText("\ndb['" + name + "'].remove();\n");
			else
				editorContainer.appendText("\ndb." + name + ".remove();\n");
			
		}

		protected void dropDatabase() {
			editorContainer.appendText("\ndb.dropDatabase();\n");
			
		}

		protected void generateCount() {
			if (name.indexOf('.') >= 0)
				editorContainer.appendText("\ndb['" + name + "'].count();\n");
			else
				editorContainer.appendText("\ndb." + name + ".count();\n");
			
		}

		protected void generateFind() {
			if (name.indexOf('.') >= 0)
				editorContainer.appendText("\ndb['" + name + "'].find();\n");
			else
				editorContainer.appendText("\ndb." + name + ".find();\n");
			
		}

		protected void useDatabase() {
			editorContainer.appendText("\nuse " + name + ";\n");
			
		}

		public Icon getIcon() {
			if (db)
				return DB_ICON;
			else
				return COL_ICON;
		}
		

		@Override
		public String toString() {
			return name;
		}
		
	}
	
	
	public void collapseAll() {
		for (int i = 0; i < tree.getRowCount(); i++) 
	         tree.collapseRow(i);
		
	}

	public void expandAll() {
		for (int i = 0; i < tree.getRowCount(); i++) 
	         tree.expandRow(i);
		
	}
	
	

	public void refreshDatabase() {
		rootNode.removeAllChildren();
		for (String dbName: mongo.getDatabaseNames()) {
			rootNode.add(createDbNode(dbName));
		}
		
		treeModel.reload();

		tree.setRootVisible(true);
		tree.expandRow(0);
		tree.setRootVisible(false);
		
	}

	private MutableTreeNode createDbNode(String dbName) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TreeObj(dbName, true));
		DB db = mongo.getDB(dbName);
		
		for (String colName: db.getCollectionNames()) {
			node.add(createColNode(colName));
		}
		return node;
	}

	private MutableTreeNode createColNode(String colName) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TreeObj(colName, false));
		return node;
	}


}
