package org.sdrc.rmnchadashboard.jparepository;

import java.util.Date;
import java.util.List;

import org.sdrc.rmnchadashboard.jpadomain.Indicator;
import org.sdrc.rmnchadashboard.utils.JpaRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@JpaRepo
public interface IndicatorJpaRepository extends org.springframework.data.repository.Repository<Indicator,Long>{

	public Indicator save(Indicator indicator);
	
	public List<Indicator> findAll();


	public Indicator findByIName(String indicatorName);


	public List<Indicator> findAllByLastModifiedGreaterThanOrderBySlugidindicator(Date lastSynchronizedDate);
	
	@Query(value="select i.id,i.i_name,u.unit_name,s.subgroup_name,i.highisgood "
			+ "from indicator i INNER JOIN unit u on i.unit_id_fk = u.id INNER JOIN "
			+ "subgroup s on i.subgroup_id_fk = s.id", nativeQuery=true)
	public List<Object[]> getAllIndicatorUnitSubgroupNameList();
}