package org.sdrc.rmnchadashboard.repository;

import org.sdrc.rmnchadashboard.domain.Indicator;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndicatorRepository extends ElasticsearchRepository<Indicator, String>{

	public Indicator save(Indicator indicator);
	
	public Iterable<Indicator> findAll();

	public Indicator findByiName(String indicatorName);
}