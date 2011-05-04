package org.smartapp.mongodb.script;

public class PreProcessor {

	public String preprocess(String script) {
		if (script == null || script.length() == 0)
			return script;
		
		return script.replaceAll("use +(\\w+)", "use('$1')");
	}
}
