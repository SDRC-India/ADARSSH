package org.sdrc.rmncha.datacollector.implhandlers;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import in.co.sdrc.sdrcdatacollector.document.EnginesForm;
import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.util.IProgatiInterface;

@Service
public class IProgatiImpl implements IProgatiInterface{

	/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 20-Jul-2019 5:43:29 PM
	 * @see in.co.sdrc.sdrcdatacollector.util.IProgatiInterface#getAssignesFormsForDataEntry(in.co.sdrc.sdrcdatacollector.models.AccessType)
	 */
	@Override
	public List<EnginesForm> getAssignesFormsForDataEntry(AccessType dataEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 20-Jul-2019 5:43:29 PM
	 * @see in.co.sdrc.sdrcdatacollector.util.IProgatiInterface#getAssignesFormsForReview(in.co.sdrc.sdrcdatacollector.models.AccessType)
	 */
	@Override
	public List<EnginesForm> getAssignesFormsForReview(AccessType review) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EnginesForm> getAssignesFormsForDataEntryByCreatedDate(AccessType arg0, Date arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
