package org.sdrc.rmnchadashboard.web.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.sdrc.rmnchadashboard.web.model.ParamModel;
import org.sdrc.rmnchadashboard.web.model.SVGModel;

public interface ExportService {

	String downloadChartDataPDF(List<SVGModel> listOfSvgs, String districtName, String blockName,
			HttpServletRequest request, String stateName, String areaLevel, String dashboardType, String checkListName,
			String timePeriod, Integer typeId, Integer levelId);

	String downloadChartDataExcel(List<SVGModel> listOfSvgs, ParamModel paramModel, HttpServletRequest request);

}
