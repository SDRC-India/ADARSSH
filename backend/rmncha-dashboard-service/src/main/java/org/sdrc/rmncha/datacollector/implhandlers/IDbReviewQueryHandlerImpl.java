package org.sdrc.rmncha.datacollector.implhandlers;

import java.util.Map;

import org.springframework.stereotype.Component;

import in.co.sdrc.sdrcdatacollector.document.Question;
import in.co.sdrc.sdrcdatacollector.document.TypeDetail;
import in.co.sdrc.sdrcdatacollector.handlers.IDbReviewQueryHandler;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.DataObject;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 * @author Azaruddin (azaruddin@sdrc.co.in)
 *
 */
/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 20-Jul-2019 5:42:17 PM
 */
@Component
public class IDbReviewQueryHandlerImpl implements IDbReviewQueryHandler {

	/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 20-Jul-2019 5:42:16 PM
	 * @see in.co.sdrc.sdrcdatacollector.handlers.IDbReviewQueryHandler#setReviewHeaders(in.co.sdrc.sdrcdatacollector.models.DataObject, in.co.sdrc.sdrcdatacollector.document.Question, java.util.Map, in.co.sdrc.sdrcdatacollector.models.DataModel, java.lang.String)
	 */
	@Override
	public DataObject setReviewHeaders(DataObject dataObject, Question question,
			Map<Integer, TypeDetail> typeDetailsMap, DataModel submissionData, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
