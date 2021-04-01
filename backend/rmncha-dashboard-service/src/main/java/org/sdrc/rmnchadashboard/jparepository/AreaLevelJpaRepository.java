package org.sdrc.rmnchadashboard.jparepository;

import java.util.Date;
import java.util.List;

import org.sdrc.rmnchadashboard.jpadomain.AreaLevel;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaLevelJpaRepository extends org.springframework.data.repository.Repository<AreaLevel,Long> {
	
	public AreaLevel save(AreaLevel areaLevel);
	
	public AreaLevel findByLevel(Integer id);
	
	public List<AreaLevel> findAll();

	public List<AreaLevel> findAllByLastModifiedGreaterThan(Date date);

	public List<AreaLevel> findAllByLastModifiedGreaterThanOrderBySlugidarealevel(Date lastSynchronizedDate);

}
