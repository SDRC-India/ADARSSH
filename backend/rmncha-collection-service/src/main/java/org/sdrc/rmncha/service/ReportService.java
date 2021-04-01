package org.sdrc.rmncha.service;

import org.sdrc.rmncha.rabbitMQ.AllChecklistFormDataEvent;

public interface ReportService {
	
	void getNotification(AllChecklistFormDataEvent savedData) throws Exception;

}
