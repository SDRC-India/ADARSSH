package org.sdrc.rmncha.repositories;

import org.sdrc.usermgmt.mongodb.domain.Designation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomDesignationRepository extends MongoRepository<Designation, String> {

	Designation findByName(String name);
}
