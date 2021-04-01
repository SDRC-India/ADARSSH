package org.sdrc.rmnchadashboard.jpadomain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="sector")
public class Sector {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String sectorName;

	private Integer slugidsector;
	
	private Date createdDate;

	private Date lastModified;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSectorName() {
		return sectorName;
	}

	public void setSectorName(String name) {
		this.sectorName = name;
	}

	@Override
	public String toString() {
		return "Sector [id=" + id + ", name=" + sectorName + "]";
	}

	public Integer getSlugidsector() {
		return slugidsector;
	}

	public void setSlugidsector(Integer slugidsector) {
		this.slugidsector = slugidsector;
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
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
		result = prime * result + ((sectorName == null) ? 0 : sectorName.hashCode());
		result = prime * result + ((slugidsector == null) ? 0 : slugidsector.hashCode());
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
		Sector other = (Sector) obj;
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
		if (lastModified == null) {
			if (other.lastModified != null)
				return false;
		} else if (!lastModified.equals(other.lastModified))
			return false;
		if (sectorName == null) {
			if (other.sectorName != null)
				return false;
		} else if (!sectorName.equals(other.sectorName))
			return false;
		if (slugidsector == null) {
			if (other.slugidsector != null)
				return false;
		} else if (!slugidsector.equals(other.slugidsector))
			return false;
		return true;
	}

	




	
	
}
