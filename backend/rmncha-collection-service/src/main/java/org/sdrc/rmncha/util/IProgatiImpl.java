package org.sdrc.rmncha.util;

import java.util.Date;
import java.util.List;

import org.sdrc.rmncha.repositories.EnginesFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.co.sdrc.sdrcdatacollector.document.EnginesForm;
import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.util.IProgatiInterface;

@Service
public class IProgatiImpl implements IProgatiInterface{

	@Autowired
	private EnginesFormRepository enginesFormRepository;
	
	@Override
	public List<EnginesForm> getAssignesFormsForDataEntry(AccessType arg0) {
		return enginesFormRepository.findAll();
//		return enginesFormRepository.findByFormIdIn(Arrays.asList(3,4));
	}

	@Override
	public List<EnginesForm> getAssignesFormsForReview(AccessType arg0) {
		return enginesFormRepository.findAll();
	}

	@Override
	public List<EnginesForm> getAssignesFormsForDataEntryByCreatedDate(AccessType accessType, Date createdDate) {
		return enginesFormRepository.findAllByUpdatedDate(createdDate);
	}

}
