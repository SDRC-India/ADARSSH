package org.sdrc.rmnchadashboard.jpadomain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity(name="area")
public class Area {

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer slugidarea;

	private String areaname;

	private String code;

	private String ccode;
	
	private String parentAreaCode;
	
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="area_arealevel_mapping",
	joinColumns= {@JoinColumn(name="area_id_fk",referencedColumnName="id")},
	inverseJoinColumns=@JoinColumn(name="arealevel_id_fk",referencedColumnName="id"))
	private List<AreaLevel> areaLevel;//List<AreaLevel>

	
	@ManyToOne
	@JoinColumn(name="act_area_level_id_fk")
	private AreaLevel actAreaLevel;

	
	private Date createdDate;

	private Date lastModified;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String name) {
		this.areaname = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCcode() {
		return ccode;
	}

	public void setCcode(String ccode) {
		this.ccode = ccode;
	}

	


	public List<AreaLevel> getAreaLevel() {
		return areaLevel;
	}

	public void setAreaLevel(List<AreaLevel> areaLevel) {
		this.areaLevel = areaLevel;
	}

	public String getParentAreaCode() {
		return parentAreaCode;
	}

	public void setParentAreaCode(String parentAreaCode) {
		this.parentAreaCode = parentAreaCode;
	}

	public AreaLevel getActAreaLevel() {
		return actAreaLevel;
	}

	public void setActAreaLevel(AreaLevel actAreaLevel) {
		this.actAreaLevel = actAreaLevel;
	}
	
	


	

	public Integer getSlugidarea() {
		return slugidarea;
	}

	public void setSlugidarea(Integer slugidarea) {
		this.slugidarea = slugidarea;
	}

	@Override
	public String toString() {
		return "Area [id=" + id + ", areaname=" + areaname + ", code=" + code + ", ccode=" + ccode + ", parentAreaCode="
				+ parentAreaCode + ", areaLevel=" + areaLevel + ", actAreaLevel=" + actAreaLevel + "]";
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
		result = prime * result + ((actAreaLevel == null) ? 0 : actAreaLevel.hashCode());
		result = prime * result + ((areaLevel == null) ? 0 : areaLevel.hashCode());
		result = prime * result + ((areaname == null) ? 0 : areaname.hashCode());
		result = prime * result + ((ccode == null) ? 0 : ccode.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
		result = prime * result + ((parentAreaCode == null) ? 0 : parentAreaCode.hashCode());
		result = prime * result + ((slugidarea == null) ? 0 : slugidarea.hashCode());
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
		Area other = (Area) obj;
		if (actAreaLevel == null) {
			if (other.actAreaLevel != null)
				return false;
		} else if (!actAreaLevel.equals(other.actAreaLevel))
			return false;
		if (areaLevel == null) {
			if (other.areaLevel != null)
				return false;
		} else if (!areaLevel.equals(other.areaLevel))
			return false;
		if (areaname == null) {
			if (other.areaname != null)
				return false;
		} else if (!areaname.equals(other.areaname))
			return false;
		if (ccode == null) {
			if (other.ccode != null)
				return false;
		} else if (!ccode.equals(other.ccode))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
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
		if (lastModified == null) {
			if (other.lastModified != null)
				return false;
		} else if (!lastModified.equals(other.lastModified))
			return false;
		if (parentAreaCode == null) {
			if (other.parentAreaCode != null)
				return false;
		} else if (!parentAreaCode.equals(other.parentAreaCode))
			return false;
		if (slugidarea == null) {
			if (other.slugidarea != null)
				return false;
		} else if (!slugidarea.equals(other.slugidarea))
			return false;
		return true;
	}

	

	

}
