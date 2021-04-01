package org.sdrc.rmnchadashboard.repository;

import java.util.Date;

import org.sdrc.rmnchadashboard.domain.Subgroup;

public interface SubgroupDAL {

	
	
	Subgroup findBySubgroupName(String subgroupName);

	Iterable<Subgroup> getSubgroupAfterSlugId(Integer slugidsubgroup);

	Iterable<Subgroup> getSubgroupAfterTimestamp(Date timestamp);

	Subgroup getSubgroupBySlugId(Integer slugidsubgroup);

}
