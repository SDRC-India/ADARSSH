package org.sdrc.rmnchadashboard.web.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.sdrc.rmncha.mongodomain.AllChecklistFormData;
import org.sdrc.rmnchadashboard.web.model.ReportModel;
import org.springframework.http.ResponseEntity;

public interface ReportService {
	
	ReportModel getReportData();
	
	ResponseEntity<String> getRawDataReport(Integer formId, Integer facilityTypeId, String sDate, String eDate, Integer stateId, Integer district, Integer blockId,HttpServletResponse response,Boolean inline,List<String> submissionIds) throws Exception;
	
	void getNotification(AllChecklistFormData savesData) throws Exception;

	String updateTpInSubmission();

}
