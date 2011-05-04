package org.smartapp.mongodb.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import com.mongodb.DB;

public class DatabaseObject extends NativeObject {

	private static final long serialVersionUID = 38668250593492201L;
	private DB db;
	private Context context;
	
	public DatabaseObject(DB db, Context context) {
		this.db = db;
		this.context = context;
		super.put("database", this, this.db);
	}
	

	@Override
	public Object get(String name, Scriptable start) {
		Object value = super.get(name, start);
		if (value != Scriptable.NOT_FOUND) {
			return value;
		}

		Function function = (Function) super.get("createCollection", start);
		Object collection = function.call(context, start, this, new Object[] {name});
		super.put(name, start, collection);
		return collection;
	}

}
