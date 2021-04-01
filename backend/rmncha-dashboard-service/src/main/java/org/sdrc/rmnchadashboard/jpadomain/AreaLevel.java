package org.sdrc.rmnchadashboard.jpadomain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;




@Entity(name="arealevel")
public class AreaLevel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer slugidarealevel;

	private String areaLevelName;

	private Integer level;

	private Boolean isStateAvailable;

	private Boolean isDistrictAvailable;

	private Date createdDate;

	private Date lastModified;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAreaLevelName() {
		return areaLevelName;
	}

	public void setAreaLevelName(String name) {
		this.areaLevelName = name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Boolean getIsStateAvailable() {
		return isStateAvailable;
	}

	public void setIsStateAvailable(Boolean isStateAvailable) {
		this.isStateAvailable = isStateAvailable;
	}

	public Boolean getIsDistrictAvailable() {
		return isDistrictAvailable;
	}

	public void setIsDistrictAvailable(Boolean isDistrictAvailable) {
		this.isDistrictAvailable = isDistrictAvailable;
	}

	

	public Integer getSlugidarealevel() {
		return slugidarealevel;
	}

	public void setSlugidarealevel(Integer slugidarealevel) {
		this.slugidarealevel = slugidarealevel;
	}

	@Override
	public String toString() {
		return "AreaLevel [id=" + id + ", areaLevelName=" + areaLevelName + ", level=" + level + ", isStateAvailable="
				+ isStateAvailable + ", isDistrictAvailable=" + isDistrictAvailable + "]";
	}
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((areaLevelName == null) ? 0 : areaLevelName.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isDistrictAvailable == null) ? 0 : isDistrictAvailable.hashCode());
		result = prime * result + ((isStateAvailable == null) ? 0 : isStateAvailable.hashCode());
		result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((slugidarealevel == null) ? 0 : slugidarealevel.hashCode());
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
		AreaLevel other = (AreaLevel) obj;
		if (areaLevelName == null) {
			if (other.areaLevelName != null)
				return false;
		} else if (!areaLevelName.equals(other.areaLevelName))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isDistrictAvailable == null) {
			if (other.isDistrictAvailable != null)
				return false;
		} else if (!isDistrictAvailable.equals(other.isDistrictAvailable))
			return false;
		if (isStateAvailable == null) {
			if (other.isStateAvailable != null)
				return false;
		} else if (!isStateAvailable.equals(other.isStateAvailable))
			return false;
		if (lastModified == null) {
			if (other.lastModified != null)
				return false;
		} else if (!lastModified.equals(other.lastModified))
			return false;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		if (slugidarealevel == null) {
			if (other.slugidarealevel != null)
				return false;
		} else if (!slugidarealevel.equals(other.slugidarealevel))
			return false;
		return true;
	}
	
	

}
