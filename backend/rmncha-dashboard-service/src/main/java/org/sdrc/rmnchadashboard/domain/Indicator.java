package org.sdrc.rmnchadashboard.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Document(indexName = "rmncha-index", type = "rmncha-indicator-type",
shards=1,replicas=1, createIndex =false)

public class Indicator {

	@Id
	private String id;

	@Field(type=FieldType.String,searchAnalyzer="keyword",analyzer="keyword",index=FieldIndex.not_analyzed)
	private String iName;
	
	private Integer slugidindicator;
	
	private boolean nucolor;

	@Field(type = FieldType.Nested)
	private List<Sector> sc;

	private Boolean kpi;
	
	private Boolean nitiaayog;
	
	private Boolean thematicKpi;//added 14.08.2018
	
	private Boolean ssv;//added 14.08.2018
	
	private Boolean hmis;//added 14.08.2018


	@Field(type = FieldType.Nested)
	private List<Source> national;

	@Field(type = FieldType.Nested)
	private List<Source> state;

	@Field(type = FieldType.Nested)
	private List<Source> district;
	
	@Field(type = FieldType.Nested)
	private List<Source> thematicNational;//added 14.08.2018

	@Field(type = FieldType.Nested)
	private List<Source> thematicState;//added 14.08.2018

	@Field(type = FieldType.Nested)
	private List<Source> thematicDistrict;//added 14.08.2018

	@Field(type = FieldType.Nested)
	private Unit unit;

	private Boolean highisgood;

	@Field(type = FieldType.Nested)
	private Sector recSector;

	@Field(type = FieldType.Nested)
	public Subgroup subgroup;

	@Field(type=FieldType.Date)
	private Date createdDate;
	
	@Field(type=FieldType.Date)
	private Date lastModified;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public Boolean getThematicKpi() {
		return thematicKpi;
	}

	public void setThematicKpi(Boolean thematicKpi) {
		this.thematicKpi = thematicKpi;
	}
	
	
	

}
