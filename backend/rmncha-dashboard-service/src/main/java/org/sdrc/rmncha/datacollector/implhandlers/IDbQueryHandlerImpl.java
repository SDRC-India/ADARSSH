package org.sdrc.rmncha.datacollector.implhandlers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import in.co.sdrc.sdrcdatacollector.document.Question;
import in.co.sdrc.sdrcdatacollector.document.TypeDetail;
import in.co.sdrc.sdrcdatacollector.handlers.IDbQueryHandler;
import in.co.sdrc.sdrcdatacollector.models.OptionModel;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 *
 */
@Component
public class IDbQueryHandlerImpl implements IDbQueryHandler {

	/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 20-Jul-2019 5:41:56 PM
	 * @see in.co.sdrc.sdrcdatacollector.handlers.IDbQueryHandler#getOptions(in.co.sdrc.sdrcdatacollector.models.QuestionModel, java.util.Map, in.co.sdrc.sdrcdatacollector.document.Question, java.lang.String, java.lang.Object, java.util.Map)
	 */
	@Override
	public List<OptionModel> getOptions(QuestionModel questionModel, Map<Integer, TypeDetail> typeDetailsMap,
			Question question, String checkedValue, Object user, Map<String, Object> paramKeyValueMap) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 20-Jul-2019 5:41:56 PM
	 * @see in.co.sdrc.sdrcdatacollector.handlers.IDbQueryHandler#getDropDownValueForRawData(java.lang.String, java.lang.Integer)
	 */
	@Override
	public String getDropDownValueForRawData(String tableName, Integer dropdownId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 20-Jul-2019 5:41:56 PM
	 * @see in.co.sdrc.sdrcdatacollector.handlers.IDbQueryHandler#setValueForTextBoxFromExternal(in.co.sdrc.sdrcdatacollector.models.QuestionModel, in.co.sdrc.sdrcdatacollector.document.Question, java.util.Map, javax.servlet.http.HttpSession, java.lang.Object)
	 */
	@Override
	public QuestionModel setValueForTextBoxFromExternal(QuestionModel qModel, Question question,
			Map<String, Object> paramKeyValMap, HttpSession session, Object user) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
