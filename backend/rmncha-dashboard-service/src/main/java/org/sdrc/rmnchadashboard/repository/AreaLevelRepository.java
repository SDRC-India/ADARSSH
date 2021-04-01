package org.sdrc.rmnchadashboard.repository;

import org.sdrc.rmnchadashboard.domain.AreaLevel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaLevelRepository extends ElasticsearchRepository<AreaLevel, String> {

	public AreaLevel save(AreaLevel areaLevel);

	public Iterable<AreaLevel> findAll();

	public AreaLevel findByLevel(Integer integer);

}
