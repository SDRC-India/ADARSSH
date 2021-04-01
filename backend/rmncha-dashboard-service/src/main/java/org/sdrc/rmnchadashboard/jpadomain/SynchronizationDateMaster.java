package org.sdrc.rmnchadashboard.jpadomain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;


@Entity
public class SynchronizationDateMaster {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer synchronizationDateMasterId;
	
	private String tableName;
	
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;
	
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private Date lastSynchronized;

	public Integer getSynchronizationDateMasterId() {
		return synchronizationDateMasterId;
	}

	public void setSynchronizationDateMasterId(Integer synchronizationDateMasterId) {
		this.synchronizationDateMasterId = synchronizationDateMasterId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Date getLastSynchronized() {
		return lastSynchronized;
	}

	public void setLastSynchronized(Date lastSynchronized) {
		this.lastSynchronized = lastSynchronized;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lastModifiedDate == null) ? 0 : lastModifiedDate.hashCode());
		result = prime * result + ((lastSynchronized == null) ? 0 : lastSynchronized.hashCode());
		result = prime * result + ((synchronizationDateMasterId == null) ? 0 : synchronizationDateMasterId.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SynchronizationDateMaster other = (SynchronizationDateMaster) obj;
		if (lastModifiedDate == null) {
			if (other.lastModifiedDate != null)
				return false;
		} else if (!lastModifiedDate.equals(other.lastModifiedDate))
			return false;
		if (lastSynchronized == null) {
			if (other.lastSynchronized != null)
				return false;
		} else if (!lastSynchronized.equals(other.lastSynchronized))
			return false;
		if (synchronizationDateMasterId == null) {
			if (other.synchronizationDateMasterId != null)
				return false;
		} else if (!synchronizationDateMasterId.equals(other.synchronizationDateMasterId))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}
	
	
	
	
	
}
