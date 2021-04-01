package org.sdrc.rmnchadashboard.repository;

import org.sdrc.rmnchadashboard.domain.Subgroup;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubgroupRepository extends ElasticsearchRepository<Subgroup, String> {

	Subgroup save(Subgroup subgroup);

//	Subgroup findBySubgroupName(String name);

	public Iterable<Subgroup> findAll();	
}
