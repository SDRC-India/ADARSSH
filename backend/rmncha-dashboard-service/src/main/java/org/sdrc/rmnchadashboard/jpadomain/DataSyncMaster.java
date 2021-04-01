/**
 * 
 */
package org.sdrc.rmnchadashboard.jpadomain;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.sdrc.rmnchadashboard.model.DataSyncStatusEnum;

/**
 * @author Harsh Pratyush
 * @version 1.0.0
 *
 *This domain will the details of data update status 
 */

@Entity
@Table
public class DataSyncMaster {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date syncStartDate;
	
	
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date syncEndDate;
	
	@Enumerated(EnumType.STRING)
	private DataSyncStatusEnum dataSyncStatus;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getSyncStartDate() {
		return syncStartDate;
	}

	public void setSyncStartDate(Date syncStartDate) {
		this.syncStartDate = syncStartDate;
	}

	public Date getSyncEndDate() {
		return syncEndDate;
	}

	public void setSyncEndDate(Date syncEndDate) {
		this.syncEndDate = syncEndDate;
	}

	public DataSyncStatusEnum getDataSyncStatus() {
		return dataSyncStatus;
	}

	public void setDataSyncStatus(DataSyncStatusEnum dataSyncStatus) {
		this.dataSyncStatus = dataSyncStatus;
	}
	
	
	
}
