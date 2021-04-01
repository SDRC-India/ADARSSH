package org.sdrc.rmnchadashboard.jparepository;

import org.sdrc.rmnchadashboard.jpadomain.SynchronizationDateMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SynchronizationDateMasterRepository extends  JpaRepository<SynchronizationDateMaster,Integer> {

	SynchronizationDateMaster findByTableName(String tableName);
	
	
}
