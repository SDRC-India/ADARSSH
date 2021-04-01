package org.sdrc.rmnchadashboard.jpadomain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="source")
public class Source {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	private String sourceName;

	private Integer slugidsource;

	private Date createdDate;

	private Date lastModified;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String name) {
		this.sourceName = name;
	}

	@Override
	public String toString() {
		return "Source [id=" + id + ", name=" + sourceName + "]";
	}

	public Integer getSlugidsource() {
		return slugidsource;
	}

	public void setSlugidsource(Integer slugidsource) {
		this.slugidsource = slugidsource;
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
		result = prime * result + ((slugidsource == null) ? 0 : slugidsource.hashCode());
		result = prime * result + ((sourceName == null) ? 0 : sourceName.hashCode());
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
		Source other = (Source) obj;
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
		if (slugidsource == null) {
			if (other.slugidsource != null)
				return false;
		} else if (!slugidsource.equals(other.slugidsource))
			return false;
		if (sourceName == null) {
			if (other.sourceName != null)
				return false;
		} else if (!sourceName.equals(other.sourceName))
			return false;
		return true;
	}



	
	
	
}
