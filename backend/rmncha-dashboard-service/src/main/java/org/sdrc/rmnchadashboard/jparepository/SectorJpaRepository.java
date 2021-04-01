package org.sdrc.rmnchadashboard.jparepository;

import java.util.Date;
import java.util.List;

import org.sdrc.rmnchadashboard.jpadomain.Sector;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorJpaRepository extends org.springframework.data.repository.Repository<Sector,Long>{

	
	public Sector save(Sector sector);

	public Sector findBySectorName(String string);
	
	public List<Sector> findAll();

	public List<Sector> findAllByLastModifiedGreaterThan(Date date);

	public List<Sector> findAllByLastModifiedGreaterThanOrderBySlugidsector(Date lastSynchronizedDate);

}