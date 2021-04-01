package org.sdrc.rmncha.datacollector.implhandlers;

import java.util.Map;

import org.springframework.stereotype.Service;

import in.co.sdrc.sdrcdatacollector.handlers.ICameraAndAttachmentsDataHandler;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 20-Jul-2019 5:42:27 PM
 */
@Service
@Slf4j
public class ICameraDataHandlerImpl implements ICameraAndAttachmentsDataHandler {/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 20-Jul-2019 5:42:35 PM
	 * @see in.co.sdrc.sdrcdatacollector.handlers.ICameraAndAttachmentsDataHandler#readExternal(in.co.sdrc.sdrcdatacollector.models.QuestionModel, in.co.sdrc.sdrcdatacollector.models.DataModel, java.util.Map)
	 */
	@Override
	public QuestionModel readExternal(QuestionModel model, DataModel dataModel, Map<String, Object> paramKeyValMap) {
		// TODO Auto-generated method stub
		return null;
	}

	
		
}
