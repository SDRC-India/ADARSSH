package org.sdrc.rmncha.repositories;

import org.sdrc.rmncha.domain.AreaLevel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("mongoAreaLevelRepository")
public interface AreaLevelRepository extends MongoRepository<AreaLevel, String>{

	AreaLevel findByAreaLevelId(Integer areaLevelId);

	AreaLevel findByAreaLevelName(String string);

}
