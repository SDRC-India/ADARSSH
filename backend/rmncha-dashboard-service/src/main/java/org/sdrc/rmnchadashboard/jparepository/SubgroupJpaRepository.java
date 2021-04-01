package org.sdrc.rmnchadashboard.jparepository;

import java.util.Date;
import java.util.List;

import org.sdrc.rmnchadashboard.jpadomain.Subgroup;
import org.springframework.stereotype.Repository;

@Repository
public interface SubgroupJpaRepository extends org.springframework.data.repository.Repository<Subgroup,Long> {

	Subgroup save(Subgroup subgroup);

//	Subgroup findBySubgroupName(String name);

	public List<Subgroup> findAll();

	Subgroup findBySubgroupName(String subgroupName);


	List<Subgroup> findAllByLastModifiedGreaterThanOrderBySlugidsubgroup(Date lastSynchronizedDate);	
}
