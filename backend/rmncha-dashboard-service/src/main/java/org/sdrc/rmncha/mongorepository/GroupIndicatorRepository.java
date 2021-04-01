package org.sdrc.rmncha.mongorepository;

import java.util.List;

import org.sdrc.rmncha.mongodomain.GroupIndicator;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupIndicatorRepository extends MongoRepository<GroupIndicator, String>{

	List<GroupIndicator> findBySector(String sector);
	
	List<GroupIndicator> findBySectorIdIn(List<String> sectorIds);
	
	List<GroupIndicator> findBySectorIdInAndLevel(List<String> sectorIds, String level);
}
