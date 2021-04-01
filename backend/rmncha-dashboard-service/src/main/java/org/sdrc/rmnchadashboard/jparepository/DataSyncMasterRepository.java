/**
 * 
 */
package org.sdrc.rmnchadashboard.jparepository;

import java.util.List;

import org.sdrc.rmnchadashboard.jpadomain.DataSyncMaster;
import org.sdrc.rmnchadashboard.model.DataSyncStatusEnum;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */

@Repository
public interface DataSyncMasterRepository
		extends org.springframework.data.repository.Repository<DataSyncMaster, Integer> {

	List<DataSyncMaster> findByDataSyncStatus(DataSyncStatusEnum datasyncStatus);

	@Transactional
	DataSyncMaster save(DataSyncMaster dataSyncMaster);

	List<DataSyncMaster> findByDataSyncStatusIn(List<DataSyncStatusEnum> datasyncStatus);

}
