package org.sdrc.rmnchadashboard.web.model;

import lombok.Data;

@Data
public class AggregateLegacyDataStatusModel {

	private String timePeriod;
	private String status;
	private String startTime;
	private String endTime;
}
