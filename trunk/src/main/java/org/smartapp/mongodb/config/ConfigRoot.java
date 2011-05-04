package org.smartapp.mongodb.config;

import java.util.List;

public class ConfigRoot {
	private List<ConnectionConfig> connections;
	
	public List<ConnectionConfig> getConnections() {
		return connections;
	}
	
	public void setConnections(List<ConnectionConfig> connections) {
		this.connections = connections;
	}
}
