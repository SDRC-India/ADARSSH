package org.sdrc.rmnchadashboard.repository;

import org.sdrc.rmnchadashboard.domain.Sector;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorRepository extends ElasticsearchRepository<Sector, String>{

	
	public Sector save(Sector sector);

	public Sector findBySectorName(String string);
	
	public Iterable<Sector> findAll();

}