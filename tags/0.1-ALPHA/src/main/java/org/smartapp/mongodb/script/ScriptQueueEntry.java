package org.smartapp.mongodb.script;

public class ScriptQueueEntry {
	private int lineNumber;
	private String script;

	public ScriptQueueEntry(int lineNumber, String script) {
		this.lineNumber = lineNumber;
		this.script = script;
	}
	
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public String getScript() {
		return script;
	}
}
