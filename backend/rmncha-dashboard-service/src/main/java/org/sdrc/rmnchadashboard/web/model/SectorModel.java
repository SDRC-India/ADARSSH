package org.sdrc.rmnchadashboard.web.model;

import java.util.List;

import lombok.Data;
@Data
public class SectorModel {
	
	private String sectorName;
	private Integer sectorId;
	private String timePeriod;
	private List<SubSectorModel> subSectors;
}
