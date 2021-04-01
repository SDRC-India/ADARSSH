package org.sdrc.rmnchadashboard.web.model;

import lombok.Data;

@Data
public class SVGModel {
	
	private String indicatorGroupName;
	private String svg;
	private String chartType;
	private String chartAlign;
	private Integer showValue;
	private String showNName;
	private String indName;

}