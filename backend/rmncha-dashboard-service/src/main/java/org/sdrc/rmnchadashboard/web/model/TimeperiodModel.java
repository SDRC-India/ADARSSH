package org.sdrc.rmnchadashboard.web.model;

import java.util.Date;

import lombok.Data;

@Data
public class TimeperiodModel {

	private Integer tpId;
	private String tpName;
	private Date sdt;
	private Date edt;
	private String financialYear;
	private String periodicity;	
	private Integer year;
	
}