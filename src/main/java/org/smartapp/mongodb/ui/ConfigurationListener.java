package org.smartapp.mongodb.ui;

import org.smartapp.mongodb.config.ConnectionConfig;

public interface ConfigurationListener {
	void configurationUpdated(ConnectionConfig connectionConfig);
}
