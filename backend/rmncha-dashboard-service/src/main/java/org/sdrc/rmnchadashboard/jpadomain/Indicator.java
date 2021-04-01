package org.sdrc.rmnchadashboard.jpadomain;

import java.util.ArrayList;
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


@Entity(name = "indicator")
public class Indicator {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String iName;

	private Integer slugidindicator;

	private boolean nucolor;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "indicator_sector_mapping", joinColumns = {
			@JoinColumn(name = "indicator_id_fk", referencedColumnName = "id") }, inverseJoinColumns = @JoinColumn(name = "sector_id_fk", referencedColumnName = "id"))
	private List<Sector> sc = new ArrayList<>();// List<Sector>

	private Boolean kpi;

	private Boolean nitiaayog;

	private Boolean thematicKpi;// added 14.08.2018

	private Boolean ssv;// added 14.08.2018

	private Boolean hmis;// added 14.08.2018

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "indicator_source_national_mapping", joinColumns = {
			@JoinColumn(name = "indicator_id_fk", referencedColumnName = "id") }, inverseJoinColumns = @JoinColumn(name = "source_id_fk", referencedColumnName = "id"))
	private List<Source> national = new ArrayList<>();// List<Source>

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "indicator_source_state_mapping", joinColumns = {
			@JoinColumn(name = "indicator_id_fk", referencedColumnName = "id") }, inverseJoinColumns = @JoinColumn(name = "source_id_fk", referencedColumnName = "id"))
	private List<Source> state = new ArrayList<>();;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "indicator_source_district_mapping", joinColumns = {
			@JoinColumn(name = "indicator_id_fk", referencedColumnName = "id") }, inverseJoinColumns = @JoinColumn(name = "source_id_fk", referencedColumnName = "id"))
	private List<Source> district = new ArrayList<>();

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "indicator_thematicsource_national_mapping", joinColumns = {
			@JoinColumn(name = "indicator_id_fk", referencedColumnName = "id") }, inverseJoinColumns = @JoinColumn(name = "source_id_fk", referencedColumnName = "id"))
	private List<Source> thematicNational= new ArrayList<>();// added 14.08.2018

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "indicator_thematicsource_state_mapping", joinColumns = {
			@JoinColumn(name = "indicator_id_fk", referencedColumnName = "id") }, inverseJoinColumns = @JoinColumn(name = "source_id_fk", referencedColumnName = "id"))
	private List<Source> thematicState= new ArrayList<>();// added 14.08.2018

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "indicator_thematicsource_district_mapping", joinColumns = {
			@JoinColumn(name = "indicator_id_fk", referencedColumnName = "id") }, inverseJoinColumns = @JoinColumn(name = "source_id_fk", referencedColumnName = "id"))
	private List<Source> thematicDistrict= new ArrayList<>();// added 14.08.2018

	@ManyToOne
	@JoinColumn(name = "unit_id_fk")
	private Unit unit;

	private Boolean highisgood;

	@ManyToOne
	@JoinColumn(name = "rec_sector_id_fk")
	private Sector recSector;

	@ManyToOne
	@JoinColumn(name = "subgroup_id_fk")
	public Subgroup subgroup;

	private Date createdDate;

	private Date lastModified;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getiName() {
		return iName;
	}

	public void setiName(String name) {
		this.iName = name;
	}

	public List<Sector> getSc() {
		return sc;
	}

	public void setSc(List<Sector> sc) {
		this.sc = sc;
	}

	public Boolean getKpi() {
		return kpi;
	}

	public void setKpi(Boolean kpi) {
		this.kpi = kpi;
	}

	public List<Source> getNational() {
		return national;
	}

	public void setNational(List<Source> national) {
		this.national = national;
	}

	public List<Source> getState() {
		return state;
	}

	public void setState(List<Source> state) {
		this.state = state;
	}

	public List<Source> getDistrict() {
		return district;
	}

	public void setDistrict(List<Source> district) {
		this.district = district;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Boolean getHighisgood() {
		return highisgood;
	}

	public void setHighisgood(Boolean highisgood) {
		this.highisgood = highisgood;
	}

	public Sector getRecSector() {
		return recSector;
	}

	public void setRecSector(Sector recSector) {
		this.recSector = recSector;
	}

	public Subgroup getSubgroup() {
		return subgroup;
	}

	public void setSubgroup(Subgroup subgroup) {
		this.subgroup = subgroup;
	}

	public boolean isNucolor() {
		return nucolor;
	}

	public void setNucolor(boolean nucolor) {
		this.nucolor = nucolor;
	}

	public Integer getSlugidindicator() {
		return slugidindicator;
	}

	public void setSlugidindicator(Integer slugidindicator) {
		this.slugidindicator = slugidindicator;
	}

	@Override
	public String toString() {
		return "\nIndicator [id=" + id + ", iName=" + iName + ", nucolor=" + nucolor + ", sc=" + sc + ", kpi=" + kpi
				+ ", national=" + national + ", state=" + state + ", district=" + district + ", unit=" + unit
				+ ", highisgood=" + highisgood + ", recSector=" + recSector + ", subgroup=" + subgroup + "]";
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

	public Boolean getNitiaayog() {
		return nitiaayog;
	}

	public void setNitiaayog(Boolean nitiaayog) {
		this.nitiaayog = nitiaayog;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((district == null) ? 0 : district.hashCode());
		result = prime * result + ((highisgood == null) ? 0 : highisgood.hashCode());
		result = prime * result + ((iName == null) ? 0 : iName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((kpi == null) ? 0 : kpi.hashCode());
		result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
		result = prime * result + ((national == null) ? 0 : national.hashCode());
		result = prime * result + ((nitiaayog == null) ? 0 : nitiaayog.hashCode());
		result = prime * result + (nucolor ? 1231 : 1237);
		result = prime * result + ((recSector == null) ? 0 : recSector.hashCode());
		result = prime * result + ((sc == null) ? 0 : sc.hashCode());
		result = prime * result + ((slugidindicator == null) ? 0 : slugidindicator.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((subgroup == null) ? 0 : subgroup.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
		Indicator other = (Indicator) obj;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (district == null) {
			if (other.district != null)
				return false;
		} else if (!district.equals(other.district))
			return false;
		if (highisgood == null) {
			if (other.highisgood != null)
				return false;
		} else if (!highisgood.equals(other.highisgood))
			return false;
		if (iName == null) {
			if (other.iName != null)
				return false;
		} else if (!iName.equals(other.iName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (kpi == null) {
			if (other.kpi != null)
				return false;
		} else if (!kpi.equals(other.kpi))
			return false;
		if (lastModified == null) {
			if (other.lastModified != null)
				return false;
		} else if (!lastModified.equals(other.lastModified))
			return false;
		if (national == null) {
			if (other.national != null)
				return false;
		} else if (!national.equals(other.national))
			return false;
		if (nitiaayog == null) {
			if (other.nitiaayog != null)
				return false;
		} else if (!nitiaayog.equals(other.nitiaayog))
			return false;
		if (nucolor != other.nucolor)
			return false;
		if (recSector == null) {
			if (other.recSector != null)
				return false;
		} else if (!recSector.equals(other.recSector))
			return false;
		if (sc == null) {
			if (other.sc != null)
				return false;
		} else if (!sc.equals(other.sc))
			return false;
		if (slugidindicator == null) {
			if (other.slugidindicator != null)
				return false;
		} else if (!slugidindicator.equals(other.slugidindicator))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (subgroup == null) {
			if (other.subgroup != null)
				return false;
		} else if (!subgroup.equals(other.subgroup))
			return false;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		return true;
	}

	public Boolean getThematicKpi() {
		return thematicKpi;
	}

	public void setThematicKpi(Boolean thematicKpi) {
		this.thematicKpi = thematicKpi;
	}

	public Boolean getSsv() {
		return ssv;
	}

	public void setSsv(Boolean ssv) {
		this.ssv = ssv;
	}

	public Boolean getHmis() {
		return hmis;
	}

	public void setHmis(Boolean hmis) {
		this.hmis = hmis;
	}

	public List<Source> getThematicNational() {
		return thematicNational;
	}

	public void setThematicNational(List<Source> thematicNational) {
		this.thematicNational = thematicNational;
	}

	public List<Source> getThematicState() {
		return thematicState;
	}

	public void setThematicState(List<Source> thematicState) {
		this.thematicState = thematicState;
	}

	public List<Source> getThematicDistrict() {
		return thematicDistrict;
	}

	public void setThematicDistrict(List<Source> thematicDistrict) {
		this.thematicDistrict = thematicDistrict;
	}

}
