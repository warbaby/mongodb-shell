package org.smartapp.mongodb.ui;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.tree.DefaultMutableTreeNode;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Undefined;
import org.smartapp.mongodb.console.Console;
import org.smartapp.mongodb.script.DatabaseObject;
import org.smartapp.mongodb.script.DatabaseSwitcher;
import org.smartapp.mongodb.script.PreProcessor;
import org.smartapp.mongodb.script.ScriptQueueEntry;
import org.smartapp.mongodb.ui.browser.BrowserContainer;
import org.smartapp.mongodb.ui.explorer.DatabaseExplorerContainer;
import org.smartapp.mongodb.ui.explorer.tree.TreeItem;

import com.Ostermiller.Syntax.HighlightedDocument;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

public class SessionContainer extends JPanel implements Closeable {

	private static final long serialVersionUID = -4549370027086652349L;
	
	private JTextPane editor;

	private JTabbedPane resultContainer;

	private Context context;

	private ImporterTopLevel scope;

	private Console console;

	private Mongo mongo;
	
	private String fileName;

	private boolean dirty;

	private Thread workerThread;
	
	private BlockingQueue<ScriptQueueEntry> scriptQueue;

	private String sessionName;

	private DatabaseExplorerContainer dbExplorer;

	private JLabel carretPosition;

	private JTabbedPane sessionTabbedPane;

	private BrowserContainer browserContainer;
	
	
	
	
	
	public SessionContainer(final Mongo mongo, String sessionName, Console console) {
		super(new BorderLayout());
		
		this.console = console;
		this.sessionName = sessionName;
		
		
		
		HighlightedDocument doc = new HighlightedDocument();
		doc.setHighlightStyle(HighlightedDocument.JAVASCRIPT_STYLE);
		
		editor = new JTextPane(doc);

		// editorResizableWrapper is a workaround to disable JTextPane line wrapping
		JPanel editorResizableWrapper = new JPanel(new BorderLayout(0,0));
		editorResizableWrapper.add(editor);
		
		initEditor();
		
		resultContainer = new JTabbedPane();
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(editorResizableWrapper), resultContainer);
		splitPane.setDividerLocation(300);
		
		
		JPanel toolbar = new JPanel(new BorderLayout(0, 0));

		JPanel leftSideBar = new JPanel();
		JButton openFileButton = new JButton(MainWindow.createIcon("folder.png"));
		openFileButton.setFocusable(false);
		openFileButton.setPreferredSize(new Dimension(20, 20));
		openFileButton.setToolTipText("Open a file");
		openFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();
				
			}
		});
		leftSideBar.add(openFileButton);
		JButton saveFileButton = new JButton(MainWindow.createIcon("disk.png"));
		saveFileButton.setFocusable(false);
		saveFileButton.setPreferredSize(new Dimension(20, 20));
		saveFileButton.setToolTipText("Save a file");
		saveFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
				
			}
		});
		leftSideBar.add(saveFileButton);
		
		Component spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(20, 20));
		leftSideBar.add(spacer );
		
		JButton refreshDatabaseButton = new JButton(MainWindow.createIcon("database_refresh.png"));
		refreshDatabaseButton.setFocusable(false);
		refreshDatabaseButton.setPreferredSize(new Dimension(20, 20));
		refreshDatabaseButton.setToolTipText("Refresh database");
		refreshDatabaseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshDatabase();
				
			}
		});
		leftSideBar.add(refreshDatabaseButton);
		JButton expandButton = new JButton(MainWindow.createIcon("bullet_toggle_plus.png"));
		expandButton.setFocusable(false);
		expandButton.setPreferredSize(new Dimension(20, 20));
		expandButton.setToolTipText("Expand all");
		expandButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				expandAll();
				
			}
		});
		leftSideBar.add(expandButton);
		
		JButton collapseButton = new JButton(MainWindow.createIcon("bullet_toggle_minus.png"));
		collapseButton.setFocusable(false);
		collapseButton.setPreferredSize(new Dimension(20, 20));
		collapseButton.setToolTipText("Collapse all");
		collapseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				collapseAll();
				
			}
		});
		leftSideBar.add(collapseButton);
		
		spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(20, 20));
		leftSideBar.add(spacer );

		
		JButton executeButton = new JButton(MainWindow.createIcon("lightning.png"));
		executeButton.setFocusable(false);
		executeButton.setPreferredSize(new Dimension(20, 20));
		executeButton.setToolTipText("Execute current statement [ctrl+Enter]");
		executeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				execute();
				
			}
		});
		leftSideBar.add(executeButton);
		
		
		
		JPanel rightSideBar = new JPanel();
		
		
		JButton disconnectButton = new JButton(MainWindow.createIcon("disconnect.png"));
		disconnectButton.setFocusable(false);
		disconnectButton.setPreferredSize(new Dimension(20, 20));
		disconnectButton.setToolTipText("Close connection");
		disconnectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
				
			}
		});
		rightSideBar.add(disconnectButton);
		
		
		toolbar.add(leftSideBar,  BorderLayout.WEST);
		toolbar.add(new JPanel(),  BorderLayout.CENTER);
		toolbar.add(rightSideBar,  BorderLayout.EAST);
		
		add(toolbar, BorderLayout.NORTH);
		
		dbExplorer = new DatabaseExplorerContainer(mongo, this);
		
		sessionTabbedPane = new JTabbedPane();
		sessionTabbedPane.addTab("Editor", splitPane);
		browserContainer = new BrowserContainer();
		sessionTabbedPane.addTab("Browser", browserContainer );
		
		dbExplorer.addTreeSelectionListener(new TreeSelectionListener() {


			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();

				TreeItem obj = (TreeItem) node.getUserObject();
				if (sessionTabbedPane.getSelectedIndex() == 1)
					obj.populateBrowseContainer(mongo, browserContainer);

			}
		});
		
		JSplitPane splitHPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, dbExplorer, sessionTabbedPane);
		splitHPane.setDividerLocation(150);

		add(splitHPane, BorderLayout.CENTER);
		
		JPanel statusBar = new JPanel(new BorderLayout(0,0));
		carretPosition = new JLabel("Ln 0, Col 0");
		statusBar.add(carretPosition, BorderLayout.EAST);
		add(statusBar, BorderLayout.SOUTH);
		
		
		scriptQueue = new LinkedBlockingDeque<ScriptQueueEntry>();
		
		workerThread = new Thread("Worker:"+sessionName) {
			public void run() {
				initScriptEngine(mongo);
				while(true) {
					try {
						ScriptQueueEntry entry = scriptQueue.take();
						evaluateExpression(entry.getScript(), entry.getLineNumber());
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
						
					} catch (Exception e) {
						SessionContainer.this.console.error("Error: ", e.getMessage());
						e.printStackTrace();
					}
				}
			};
		};
		
		
		workerThread.start();
		
		refreshDatabase();
		
	}
	
	

	protected void collapseAll() {
		dbExplorer.collapseAll();		
	}

	protected void expandAll() {
		dbExplorer.expandAll();		
	}

	protected void refreshDatabase() {
		dbExplorer.refreshDatabase();
		
	}
	


	protected void saveFile() {
		JFileChooser fc = MainWindow.getFileChooser();
		if (fileName == null && JFileChooser.APPROVE_OPTION == fc.showSaveDialog(this)) {
			fileName = fc.getSelectedFile().getAbsolutePath();
		}
		
		if (fileName != null) {
			Writer writer = null;
			try {
				writer = new FileWriter(fileName);
				editor.write(writer);
				console.info("Saved to ", fileName);
				dirty = false;
				updateTabText();
				
			} catch (IOException e) {
				console.error("Error writing to file: ", fileName);
				e.printStackTrace();
				
			} finally {
				try {
					writer.close();
				} catch (IOException e) {
					console.error("Error closing file: ", fileName);
					e.printStackTrace();
				}
			}
		}
		
		
	}


	protected void openFile() {
		checkDocumentDirty();
		JFileChooser fc = MainWindow.getFileChooser();
		if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(this)) {
			fileName = fc.getSelectedFile().getAbsolutePath();
			Reader reader = null;
			try {
				reader = new FileReader(fileName);

				EditorKit kit = editor.getUI().getEditorKit(editor);
				HighlightedDocument doc = new HighlightedDocument();

	            kit.read(reader, doc, 0);
				editor.setDocument(doc);

				doc.setHighlightStyle(HighlightedDocument.JAVASCRIPT_STYLE);

				console.info("Loaded from ", fileName);
				dirty = false;
				updateTabText();
				initDocumentListener();
			} catch (Exception e) {
				console.error("Error reading from file: ", fileName);
				e.printStackTrace();
				
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					console.error("Error closing file: ", fileName);
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public boolean close() {
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Are you sure to close current connection?", "Close connection", JOptionPane.YES_NO_OPTION)) {
			workerThread.interrupt();
			if (mongo!=null) {
				mongo.close();
			}
			checkDocumentDirty();
			getParent().remove(this);
			return true;
		}
		
		return false;
		
	}


	private void checkDocumentDirty() {
		if (dirty && JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "The document contains unsaved chnages. Do you want to save it now?", "Save Changes", JOptionPane.YES_NO_OPTION))
			saveFile();
		
	}


	private void initScriptEngine(Mongo mongo) {
		try {
			context = Context.enter();
			scope = new ImporterTopLevel(context);
			scope.put("console", scope, console);
			scope.put("connection", scope, mongo);
			scope.put("databaseSwitcher", scope, new DatabaseSwitcher(mongo, context, scope));
			context.initStandardObjects();
			Reader in = new InputStreamReader(getClass().getResourceAsStream("/mongo-shell.js"));
			context.evaluateReader(scope, in, "<initfile>", 0, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private PreProcessor p = new PreProcessor();
	
	private void evaluateExpression(String expr, int line)
	{
		Long time = System.currentTimeMillis();
		Object result = context.evaluateString(scope, p.preprocess(expr), sessionName, line, null);
		time = System.currentTimeMillis() - time;
		console.info("OK: ", time, "ms");
		if (result instanceof Undefined) {
			// do nothing
		}
		else {
			populateResultPane(expr, result);
		}
	}
	
	private void formatObject(Object object, StringBuffer buffer) {
		if (object == null) {
			buffer.append("null");
		} else if (object instanceof NativeObject) {
			formatNativeObject((NativeObject) object, buffer);
		} else if (object instanceof NativeArray) {
			formatNativeArray(((NativeArray) object), buffer);
		} else if (object instanceof NativeJavaObject) {
			formatNativeJavaObject((NativeJavaObject) object, buffer);
		} else
			formatJavaObject(object, buffer);
	}
	
	private void formatNativeArray(NativeArray object, StringBuffer buffer) {
		buffer.append("[");
		boolean notEmpty = false;
		for (Object id : object.getIds()) {
			Object value = id;
			if (id instanceof Integer) {
				Object tmp = object.get((Integer)id, scope);
				if (tmp != null)
					value = tmp;
			}
			formatObject(value, buffer);
			buffer.append(", ");
			notEmpty = true;
		}
		
		// remove trailing comma and space
		if (notEmpty)
			buffer.setLength(buffer.length() - 2);
		
		buffer.append("]");		
		
	}


	private void formatJavaObject(Object object, StringBuffer buffer) {
		if (object instanceof Collection<?>) {
			formatCollection((Collection<?>)object, buffer);
		} else if (object instanceof Map<?,?>) {
			formatMap((Map<?, ?>) object, buffer);
		} else if (object instanceof Object[]) {
			formatArray((Object[])object, buffer);
		} else if (object instanceof String) {
			buffer.append('"').append(object).append('"');
		} else
			buffer.append(object);
		
	}


	private void formatArray(Object[] object, StringBuffer buffer) {
		buffer.append("[");
		boolean notEmpty = false;
		for (Object id : object) {
			formatObject(id, buffer);
			buffer.append(", ");
			notEmpty = true;
		}
		
		// remove trailing comma and space
		if (notEmpty)
			buffer.setLength(buffer.length() - 2);
		
		buffer.append("]");		
	}


	private void formatMap(Map<?, ?> object, StringBuffer buffer) {
		buffer.append("(");
		boolean notEmpty = false;
		for (Object id : object.keySet()) {
			buffer.append(id).append("=");
			formatObject(id, buffer);
			buffer.append(", ");
			notEmpty = true;
		}
		
		// remove trailing comma and space
		if (notEmpty)
			buffer.setLength(buffer.length() - 2);
		
		buffer.append(")");
	}


	private void formatCollection(Collection<?> object, StringBuffer buffer) {
		buffer.append("[");
		boolean notEmpty = false;
		for (Object id : object) {
			formatObject(id, buffer);
			buffer.append(", ");
			notEmpty = true;
		}
		
		// remove trailing comma and space
		if (notEmpty)
			buffer.setLength(buffer.length() - 2);
		
		buffer.append("]");
	}


	private void formatNativeJavaObject(NativeJavaObject object, StringBuffer buffer) {
		formatObject(object.unwrap(), buffer);
	}





	private void formatNativeObject(NativeObject object, StringBuffer buffer) {
		
		// check if the result is a DBCursor
		if (! (object instanceof DatabaseObject) && NativeObject.getProperty(object, "cursor") instanceof NativeJavaObject) {
			NativeJavaObject nativeJava = (NativeJavaObject) NativeObject.getProperty(object, "cursor");
			Object unwrapped = nativeJava.unwrap();
			if (unwrapped instanceof DBCursor)
			{
				DBCursor cursor = (DBCursor) unwrapped;
				if (cursor.hasNext())
					while (cursor.hasNext())
					{
						buffer.append(cursor.next()).append("\n");
					}
				else
					buffer.append("Empty");

			}
		} 
		else {
			//format as regular javascript object
			buffer.append("{");
			boolean notEmpty = false;
			for (Object id : object.getIds()) {
				buffer.append("\"");
				buffer.append(id);
				buffer.append("\": ");
				
				Object value = null;
				
				if (id instanceof Integer)
					value = object.get((Integer)id, scope);
				else
					value = object.get(String.valueOf(id), scope);
				
				formatObject(value, buffer);

				buffer.append(", ");
				notEmpty = true;
			}
			
			// remove trailing comma and space
			if (notEmpty)
				buffer.setLength(buffer.length() - 2);
			
			buffer.append("}");
		}
	}
	


	private void initEditor() {
//		editor.setTabSize(2);
		
		Toolkit t = Toolkit.getDefaultToolkit();
		FontMetrics fm = t.getFontMetrics(editor.getFont()); // deprecated!
		int cw = fm.stringWidth("   ");
		float f = (float)cw;
		TabStop[] tabs = new TabStop[50]; // this sucks
	 
		for(int i = 0; i < tabs.length; i++){
			tabs[i] = new TabStop(f * 3 * (i + 1), TabStop.ALIGN_LEFT, TabStop.LEAD_NONE);
		}
	   
		TabSet tabset = new TabSet(tabs);
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.TabSet, tabset);
		editor.setParagraphAttributes(aset, false);	
		
		
		editor.addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent e) {
				int pos = editor.getCaretPosition();
				Element root = editor.getDocument().getDefaultRootElement();  
				// next line works on TextArea but not (in general) on JTextPane  
				int lineNum = root.getElementIndex(pos);  
				// next line treats tab as only one column  
				int colNum = pos - root.getElement(lineNum).getStartOffset();
				
				carretPosition.setText("Ln " + (lineNum+1) + ", Col " + colNum);
				
			}
		});
		initDocumentListener();
		editor.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
					execute();
				}
				
			}
			
		});
	}


	private void initDocumentListener() {
		editor.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				markAsDirty(e);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				markAsDirty(e);
				
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				markAsDirty(e);
				
			}
		});
	}




	protected void markAsDirty(DocumentEvent e) {
		// a hack to _not_ mark as dirty by changes related to code highlighting.
		String eventString = e.toString();
		if (!"[]".equals(eventString) && eventString.indexOf("AttributeUndoableEdit") < 0 ) {
			this.dirty = true;
			updateTabText();
		}

	}


	private void updateTabText() {
		JTabbedPane parent = (JTabbedPane) getParent();
		int index = parent.getSelectedIndex();
		((TabLabel)parent.getTabComponentAt(index)).setTitle(dirty ? "*" + sessionName : sessionName);
	}


	private void populateResultPane(String expr, Object value) {
		while (resultContainer.getTabCount() >= 5)
			resultContainer.removeTabAt(0);
		
		final JPanel resultPanel = new JPanel(new BorderLayout());
		
		StringBuffer buffer = new StringBuffer();
		formatObject(value, buffer);
		
		JTextArea result = new JTextArea();
		result.setTabSize(4);
		result.setText(buffer.toString());
		resultPanel.add(new JScrollPane(result));
		
		String title = expr.length() > 20 ? expr.substring(0, 20) : expr;
		resultContainer.addTab(title, resultPanel);
		
		resultContainer.setTabComponentAt(resultContainer.getTabCount() - 1, new TabLabel(title, new Closeable(){

			@Override
			public boolean close() {
				resultContainer.remove(resultPanel);
				return true;
			}}));
		resultContainer.setSelectedIndex(resultContainer.getTabCount() - 1);
	}


	private void execute() {
		int firstLine = 0;
		String expression = editor.getSelectedText();
		if (expression == null || expression.length() == 0) {
			// if the selection is empty take current command
			StringBuffer buffer = new StringBuffer();

			Element rootElement = editor.getDocument().getDefaultRootElement();

			
			int currentLine = rootElement.getElementIndex(editor.getCaretPosition());
			
			if (0 < getLine(rootElement, currentLine).trim().length()) {
				
				//go backwards only if current line is not empty
				currentLine --;
				while (currentLine >= 0 ) {
					String s = getLine(rootElement, currentLine);
					if (s.trim().length() == 0) 
						break;
						
					buffer.insert(0, s);
					currentLine --;
				}
			}
			firstLine = currentLine + 1;
			currentLine = rootElement.getElementIndex(editor.getCaretPosition()); 
			
			while (currentLine < rootElement.getElementCount() ) {
				String s = getLine(rootElement, currentLine);
				if (s.trim().length() == 0) 
					break;
				
				buffer.append(s);
				currentLine ++;
			}
			
			expression = buffer.toString();
		}
		
		if (expression != null && expression.length() > 0) {
			try {
				scriptQueue.put(new ScriptQueueEntry(firstLine + 1, expression));
			} catch (Exception e1) {
				console.error("Error: ", e1.getMessage());
				e1.printStackTrace();
			}
		}
	}
	
	private String getLine(Element rootElement, int number) {
		Element line = rootElement.getElement(number);
		int rangeStart = line.getStartOffset();
		int rangeEnd = line.getEndOffset();

		try {
			return editor.getText(rangeStart, rangeEnd - rangeStart);
		} catch (BadLocationException e1) {
			e1.printStackTrace();

		}
		
		return "";
	}


	public void insertText(String text) {
        Document doc = editor.getDocument();
        if (doc != null) {
            try {
                doc.insertString(editor.getCaretPosition(), text, null);
            } catch (BadLocationException e) {
            	e.printStackTrace();
            }
        }
		
	}
	
	

}
