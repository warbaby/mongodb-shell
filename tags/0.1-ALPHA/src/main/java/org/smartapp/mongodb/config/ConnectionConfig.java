package org.smartapp.mongodb.config;

public class ConnectionConfig {
	
	private String name;
	
	private String host;
	
	private int port;
	
	private boolean authentication;
	
	private String username;
	
	private String password;
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public boolean isAuthentication() {
		return authentication;
	}
	
	public void setAuthentication(boolean authentication) {
		this.authentication = authentication;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return name;
	}

}
