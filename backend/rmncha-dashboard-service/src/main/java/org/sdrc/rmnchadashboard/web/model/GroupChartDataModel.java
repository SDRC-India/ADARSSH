package org.sdrc.rmnchadashboard.web.model;

import java.util.List;

import lombok.Data;

@Data
public class GroupChartDataModel {
	
	private String headerIndicatorName;
//	private Integer headerindicatorId;
	private Integer headerIndicatorValue;
	private List<List<ChartDataModel>> chartDataValue;
	private List<LegendModel> legends;

}
