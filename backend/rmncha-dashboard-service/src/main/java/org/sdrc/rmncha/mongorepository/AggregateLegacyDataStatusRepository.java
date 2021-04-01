package org.sdrc.rmncha.mongorepository;

import java.util.List;

import org.sdrc.rmncha.mongodomain.AggregateLegacyDataStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AggregateLegacyDataStatusRepository extends MongoRepository<AggregateLegacyDataStatus, String> {

	List<AggregateLegacyDataStatus> findAllByLegacyTrueOrderByStartTimeDesc();
	AggregateLegacyDataStatus findById(String id);
	
}
