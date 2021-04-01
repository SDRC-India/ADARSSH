package org.sdrc.rmnchadashboard.repository;

import org.sdrc.rmnchadashboard.domain.Source;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface SourceRepository extends ElasticsearchRepository<Source, String>{

	
	public Source save(Source source);

	public Source findBySourceName(String string);
	

	public Iterable<Source> findAll();
}