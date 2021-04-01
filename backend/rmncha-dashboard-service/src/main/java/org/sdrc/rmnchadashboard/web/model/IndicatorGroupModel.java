package org.sdrc.rmnchadashboard.web.model;

import java.util.List;

import lombok.Data;

@Data
public class IndicatorGroupModel {
	
	private String indicatorName;
	private Integer indicatorId;
	private String indicatorValue;
	private String tooltipValue;
	private String indicatorGroupName;
	private String timeperiod;
	private String periodicity;
	private Integer timeperiodId;
	private List<String> chartsAvailable;
	private String align;
	private String cardType;
	private String unit;
	private String chartAlign;
	private List<GroupChartDataModel> chartData;
	private String chartGroup;
	private boolean isLevelWise = false;
	//in case of card set numerator and denominator value
	private String numerator;
	
	private String denominator;
}
