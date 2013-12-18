package org.malibu.monacosql.datasource;

import java.io.Serializable;

public class Datasource implements Serializable {
	private static final long serialVersionUID = 7796730989784328348L;
	
	private String datasourceId = null;
	private String name = null;
	private String driverId = null;
	private String url = null;
	private String username = null;
	private String password = null;
	
	public String getDatasourceId() {
		return datasourceId;
	}
	public void setDatasourceId(String datasourceId) {
		this.datasourceId = datasourceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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
		if(this.name == null) {
			return "<unnamed datasource>";
		}
		return this.getName();
	}
	
	
}
