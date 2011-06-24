package org.smartapp.mongodb.ui.explorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.smartapp.mongodb.ui.SessionContainer;
import org.smartapp.mongodb.ui.explorer.tree.CollectionItem;
import org.smartapp.mongodb.ui.explorer.tree.DatabaseItem;
import org.smartapp.mongodb.ui.explorer.tree.TreeItem;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class DatabaseExplorerContainer extends JPanel {

	private static final long serialVersionUID = 1755425853296600964L;
	private Mongo mongo;
	private DefaultMutableTreeNode rootNode;
	private JTree tree;
	private SessionContainer editorContainer;
	private DefaultTreeModel treeModel;
	
	public DatabaseExplorerContainer(Mongo mongo, SessionContainer editorContainer) {
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
				if (node.getUserObject() instanceof TreeItem)
					setIcon(((TreeItem)node.getUserObject()).getIcon());

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
				
				TreeItem obj = (TreeItem) node.getUserObject();

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
	
	public void addTreeSelectionListener(TreeSelectionListener tsl) {
		tree.addTreeSelectionListener(tsl);
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
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new DatabaseItem(editorContainer, dbName));
		DB db = mongo.getDB(dbName);
		
		for (String colName: db.getCollectionNames()) {
			node.add(createColNode(colName, dbName));
		}
		return node;
	}

	private MutableTreeNode createColNode(String colName, String dbName) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new CollectionItem(editorContainer, colName, dbName));
		return node;
	}


}
