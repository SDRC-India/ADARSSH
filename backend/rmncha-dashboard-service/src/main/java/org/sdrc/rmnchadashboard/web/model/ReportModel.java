package org.sdrc.rmnchadashboard.web.model;

import java.util.List;
import java.util.Map;

import org.sdrc.rmncha.domain.TimePeriod;

import in.co.sdrc.sdrcdatacollector.document.EnginesForm;
import lombok.Data;

@Data
public class ReportModel {
	
	private List<TimePeriod> timeperiod;
	private Map<String, List<AreaModel>> area;
	private List<EnginesForm> enginesForm;
	private Map<Integer,List<TypeDetailModel>> facilityType;
//	private List<TypeDetail> facilityLevel;
	
	

}
