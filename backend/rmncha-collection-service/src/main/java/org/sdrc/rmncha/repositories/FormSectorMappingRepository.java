package org.sdrc.rmncha.repositories;

import java.util.List;

import org.sdrc.rmncha.domain.FormSectorMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FormSectorMappingRepository extends MongoRepository<FormSectorMapping, String> {

	List<FormSectorMapping> findByFormId(Integer formId);
	
	List<FormSectorMapping> findByFormIdIn(List<Integer> formIds);
}
