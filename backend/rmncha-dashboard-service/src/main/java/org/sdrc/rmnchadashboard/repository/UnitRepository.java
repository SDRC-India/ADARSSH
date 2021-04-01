package org.sdrc.rmnchadashboard.repository;

import org.sdrc.rmnchadashboard.domain.Unit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends ElasticsearchRepository<Unit, String> {

	public Unit save(Unit unit);

	public Unit findByUnitName(String unitC);

	public Iterable<Unit> findAll();	
}
