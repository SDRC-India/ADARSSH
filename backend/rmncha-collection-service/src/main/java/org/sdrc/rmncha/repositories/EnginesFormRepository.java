package org.sdrc.rmncha.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import in.co.sdrc.sdrcdatacollector.document.EnginesForm;

public interface EnginesFormRepository  extends MongoRepository<EnginesForm, String> {

	List<EnginesForm> findByFormIdIn(List<Integer> formIds);

	EnginesForm findByFormId(Integer formId);
	
	@Query("{'updatedDate':{ $gte:?0}}")
	List<EnginesForm> findAllByUpdatedDate(Date createdDate);

	@Query(value = "{ $and: [{'formId' :{$in:?0 }},{'updatedDate':{ $gte:?1}}]}")
	List<EnginesForm> findByFormIdInAndUpdatedDate(List<Integer> formIds,Date createdDate);
}
