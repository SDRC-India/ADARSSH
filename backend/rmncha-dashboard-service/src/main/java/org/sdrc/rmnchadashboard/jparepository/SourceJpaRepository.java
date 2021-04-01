package org.sdrc.rmnchadashboard.jparepository;

import java.util.Date;
import java.util.List;

import org.sdrc.rmnchadashboard.jpadomain.Source;
import org.springframework.stereotype.Repository;

@Repository

public interface SourceJpaRepository extends org.springframework.data.repository.Repository<Source,Long>{

	
	public Source save(Source source);

	public Source findBySourceName(String string);
	

	public List<Source> findAll();

	public List<Source> findAllByLastModifiedGreaterThan(Date date);

	public List<Source> findAllByLastModifiedGreaterThanOrderBySlugidsource(Date lastSynchronizedDate);
}