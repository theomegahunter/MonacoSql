package org.malibu.monacosql.datasource;

import java.io.Serializable;

public class DatasourceConfiguration implements Serializable {
	private static final long serialVersionUID = -4497352985276271363L;
	
	private String id = null;
	private String name = null;
	private String library = null;
	private String className = null;
	private String sampleUrl = null;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLibrary() {
		return library;
	}
	public void setLibrary(String library) {
		this.library = library;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getSampleUrl() {
		return sampleUrl;
	}
	public void setSampleUrl(String sampleUrl) {
		this.sampleUrl = sampleUrl;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
