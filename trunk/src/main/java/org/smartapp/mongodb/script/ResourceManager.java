package org.smartapp.mongodb.script;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;

public class ResourceManager {

	private final Context context;
	private final ImporterTopLevel scope;

	public ResourceManager(Context context, ImporterTopLevel scope) {
		this.context = context;
		this.scope = scope;
	}
	
	public void load(String name) throws MalformedURLException {
		URL url = new URL(name);
		Reader reader = null;
		try {
			reader = new InputStreamReader(url.openStream());
			context.evaluateReader(scope, reader, name, 0, null);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
