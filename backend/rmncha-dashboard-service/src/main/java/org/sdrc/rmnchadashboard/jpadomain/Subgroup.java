package org.sdrc.rmnchadashboard.jpadomain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="subgroup")
public class Subgroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	private String subgroupName;

	private Integer slugidsubgroup;
	
	private Date createdDate;

	private Date lastModified;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSubgroupName() {
		return subgroupName;
	}

	public void setSubgroupName(String name) {
		this.subgroupName = name;
	}

	@Override
	public String toString() {
		return "Source [id=" + id + ", name=" + subgroupName + "]";
	}

	public Integer getSlugidsubgroup() {
		return slugidsubgroup;
	}

	public void setSlugidsubgroup(Integer slugidsubgroup) {
		this.slugidsubgroup = slugidsubgroup;
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
		result = prime * result + ((slugidsubgroup == null) ? 0 : slugidsubgroup.hashCode());
		result = prime * result + ((subgroupName == null) ? 0 : subgroupName.hashCode());
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
		Subgroup other = (Subgroup) obj;
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
		if (slugidsubgroup == null) {
			if (other.slugidsubgroup != null)
				return false;
		} else if (!slugidsubgroup.equals(other.slugidsubgroup))
			return false;
		if (subgroupName == null) {
			if (other.subgroupName != null)
				return false;
		} else if (!subgroupName.equals(other.subgroupName))
			return false;
		return true;
	}

	
	
	
	
	
}
