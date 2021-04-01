package org.sdrc.rmncha.repositories;

import org.sdrc.rmncha.domain.BulkUserRegistration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BulkUserRegistrationRepository extends MongoRepository<BulkUserRegistration, String> {
	
}
