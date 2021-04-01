package org.sdrc.rmnchadashboard.repository;

import org.sdrc.rmnchadashboard.domain.Area;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends  ElasticsearchRepository<Area, String>{

	public Area save(Area area);

//	public Area findByCode(String areaCode);
	
	public Iterable<Area> findAll();

	public Area findByCode(String areaCode);

}