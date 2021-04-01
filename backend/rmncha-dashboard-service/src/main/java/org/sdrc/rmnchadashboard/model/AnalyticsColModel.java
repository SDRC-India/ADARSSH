/**
 * 
 */
package org.sdrc.rmnchadashboard.model;

/**
 * @author Harsh Pratyush
 *
 */
public class AnalyticsColModel {
	
	private	int id;
	
	private int colType; // 1 - Number 2- Categorical
	
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getColType() {
		return colType;
	}

	public void setColType(int colType) {
		this.colType = colType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	 
	

}
