package org.sdrc.rmnchadashboard.model;

import java.util.Date;

public class TimeperiodModel {

	private String id;
	private String name;
	private String sname;
	private Date sdt;
	private Date edt;
	private String periodicity;

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

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public Date getSdt() {
		return sdt;
	}

	public void setSdt(Date sdt) {
		this.sdt = sdt;
	}

	public Date getEdt() {
		return edt;
	}

	public void setEdt(Date edt) {
		this.edt = edt;
	}

	public String getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
	}
	
	
}
