package org.sdrc.rmncha.datacollector.implhandlers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import in.co.sdrc.sdrcdatacollector.document.EnginesForm;
import in.co.sdrc.sdrcdatacollector.handlers.IDbFetchDataHandler;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.RawDataModel;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 * @author Azaruddin (azaruddin@sdrc.co.in)
 */
@Component
public class IDbFetchDataHandlerImpl implements IDbFetchDataHandler {

	/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 20-Jul-2019 5:41:41 PM
	 * @see in.co.sdrc.sdrcdatacollector.handlers.IDbFetchDataHandler#fetchDataFromDb(in.co.sdrc.sdrcdatacollector.document.EnginesForm, java.lang.String, java.util.Map, java.util.Date, java.util.Date, java.util.Map, javax.servlet.http.HttpSession, java.lang.Object)
	 */
	@Override
	public List<DataModel> fetchDataFromDb(EnginesForm form, String type, Map<Integer, String> mapOfForms,
			Date startDate, Date endDate, Map<String, Object> paramKeyValMap, HttpSession session, Object user) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 20-Jul-2019 5:41:41 PM
	 * @see in.co.sdrc.sdrcdatacollector.handlers.IDbFetchDataHandler#findAllByRejectedFalseAndSyncDateBetween(java.lang.Integer, java.util.Date, java.util.Date)
	 */
	@Override
	public RawDataModel findAllByRejectedFalseAndSyncDateBetween(Integer formId, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 20-Jul-2019 5:41:41 PM
	 * @see in.co.sdrc.sdrcdatacollector.handlers.IDbFetchDataHandler#getSubmittedData(java.lang.String, java.lang.Integer)
	 */
	@Override
	public DataModel getSubmittedData(String submissionId, Integer formId) {
		// TODO Auto-generated method stub
		return null;
	}



}
