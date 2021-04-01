package org.sdrc.rmnchadashboard.web.model;

import java.util.List;

import lombok.Data;

@Data
public class SubSectorModel {

	private String subSectorName;
	private List<IndicatorGroupModel> indicators;
}
