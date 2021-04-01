package org.sdrc.rmnchadashboard.model;

import java.util.List;

public class SyncModel<T> {
	
	
	public SyncModel(List<T> data, String lastModified) {
		super();
		this.data = data;
		this.lastModified = lastModified;
	}

	 
	private String lastModified;

	private List<T> data;

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

}
