package org.sdrc.rmncha.mongorepository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.co.sdrc.sdrcdatacollector.document.EnginesForm;

@Repository
public interface EnginesFormRepository  extends MongoRepository<EnginesForm, String> {

	List<EnginesForm> findByFormIdIn(List<Integer> formIds);

	EnginesForm findByFormId(Integer formId);
}
