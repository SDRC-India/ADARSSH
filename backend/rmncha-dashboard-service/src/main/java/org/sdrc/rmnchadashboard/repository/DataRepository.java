package org.sdrc.rmnchadashboard.repository;

import java.util.List;

import org.sdrc.rmnchadashboard.domain.Data;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataRepository extends ElasticsearchRepository<Data, String>{

//	public Data save(Data data);
	
	public <S extends Data> Iterable<S> save(List<Data> data);
	
	//public Page<Data> findAll(Pageable pageable);
	
	public Iterable<Data> findAll();

	
	
}
