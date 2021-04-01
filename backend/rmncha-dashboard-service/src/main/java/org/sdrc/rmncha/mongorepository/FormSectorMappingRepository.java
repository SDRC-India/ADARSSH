package org.sdrc.rmncha.mongorepository;

import java.util.List;

import org.sdrc.rmncha.mongodomain.FormSectorMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormSectorMappingRepository extends MongoRepository<FormSectorMapping, String> {

	List<FormSectorMapping> findByFormId(Integer formId);
	
	List<FormSectorMapping> findByFormIdIn(List<Integer> formIds);
}
