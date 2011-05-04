package org.smartapp.mongodb.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.mongodb.Mongo;

public class DatabaseSwitcher {

	private Mongo mongo;
	private Context context;
	private Scriptable scope;

	public DatabaseSwitcher(Mongo mongo, Context context, Scriptable scope) {
		this.mongo = mongo;
		this.context = context;
		this.scope = scope;
		
	}
	
	public void switchDatabase(String name) {
		scope.put("db", scope, new DatabaseObject(mongo.getDB(name), context));
	}

}
