package org.sdrc.rmncha.mongorepository;

import java.util.List;

import org.sdrc.rmncha.mongodomain.CoverageData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverageDataRepository  extends MongoRepository<CoverageData, String> {

	List<CoverageData> findByAreaIdAndInidIn(Integer areaId, List<Integer> indicatorIds);
}
