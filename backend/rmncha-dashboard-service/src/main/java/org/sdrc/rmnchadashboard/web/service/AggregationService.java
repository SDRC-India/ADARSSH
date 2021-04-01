package org.sdrc.rmnchadashboard.web.service;

public interface AggregationService {

	String getAggregatedDataInExcel(String formId, Integer tpId);

	String updateSubmissionStatusToApprove();

}
