package org.smartapp.mongodb.console;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class TextAreaConsoleImpl extends JTextPane implements Console {

	private static final long serialVersionUID = 224299704510446251L;
	
	public TextAreaConsoleImpl() {
		setEditable(false);
		
	}
	
	public void append(Color c, String s) { 
		
		Element rootElement = getDocument().getDefaultRootElement();
		while (rootElement.getElementCount() > 5000) {
			Element e = rootElement.getElement(0);
			try {
				getDocument().remove(e.getStartOffset(), e.getEndOffset());
			} catch (BadLocationException e1) {
				e1.printStackTrace();
				
			}
		}
			
		
		// StyleContext
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		int len = getDocument().getLength(); // same value as
		setCaretPosition(len); // place caret at the end (with no selection)
		try {
			getDocument().insertString(len, s, aset);
		} catch (BadLocationException e) {
			e.printStackTrace();
			
		}
	}	

	@Override
	public void info(Object... value) {
		append(Color.black, buildText(value));
	}
	
	@Override
	public void error(Object... value) {
		append(Color.red, buildText(value));
	}
	

	private String buildText(Object... value) {
		if (value == null)
			return "null\n";
		
		StringBuffer buffer = new StringBuffer();
		for (Object obj : value) {
			buffer.append(obj);
		}
		buffer.append("\n");
		
		String s = buffer.toString();
		return s;
	}

	
	

}
