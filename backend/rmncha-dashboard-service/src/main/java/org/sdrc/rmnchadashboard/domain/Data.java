package org.sdrc.rmnchadashboard.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "rmncha-index", type = "rmncha-data-type",shards=3,replicas=1, createIndex =false)
public class Data {

	@Id
	private String id;
	
	private Long slugiddata;

	@Field(type = FieldType.Nested)
	private Indicator indicator;

	@Field(type = FieldType.Nested)
	private Subgroup subgrp;

	@Field(type = FieldType.Nested)
	private Source src;

	private String ius;// concatenate indicator,unit,subgroup,timperiod (ids or names) as string
						// separated by : (index this column)

	private String periodicity;
	
	@Field(type = FieldType.Nested)
	private Area area;

	private String value;

	private Integer rank;

	private String trend;

	@Field(type = FieldType.Object)
	private List<Area> top;

	@Field(type = FieldType.Object)
	private List<Area> below;

	private String tp;

	private String tps;

	@Field(type=FieldType.Date)
	private Date createdDate;
	
	@Field(type=FieldType.Date)
	private Date lastModified;

	private Boolean dKPIRSrs;
	
	private Boolean dNITIRSrs;
	
	private Boolean dTHEMATICRSrs;//added 14.08.2018
	

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Indicator getIndicator() {
		return indicator;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}

	public Subgroup getSubgrp() {
		return subgrp;
	}

	public void setSubgrp(Subgroup subgrp) {
		this.subgrp = subgrp;
	}

	public Source getSrc() {
		return src;
	}

	public void setSrc(Source src) {
		this.src = src;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getTrend() {
		return trend;
	}

	public void setTrend(String trend) {
		this.trend = trend;
	}

	

	

	public List<Area> getTop() {
		return top;
	}

	public void setTop(List<Area> top) {
		this.top = top;
	}

	public List<Area> getBelow() {
		return below;
	}

	public void setBelow(List<Area> below) {
		this.below = below;
	}

	public String getIus() {
		return ius;
	}

	public void setIus(String ius) {
		this.ius = ius;
	}

	public String getTp() {
		return tp;
	}

	public void setTp(String tp) {
		this.tp = tp;
	}

	public String getTps() {
		return tps;
	}

	public void setTps(String tps) {
		this.tps = tps;
	}
	

	public Long getSlugiddata() {
		return slugiddata;
	}

	public void setSlugiddata(Long slugiddata) {
		this.slugiddata = slugiddata;
	}

	
	
	public String getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
	}

	@Override
	public String toString() {
		return "\nData [id=" + id + ", indicator=" + indicator.getiName() + ", subgrp=" + subgrp.getSubgroupName() + ", src=" + src.getSourceName() + ", ius=" + ius
				+ ", area=" + area.getAreaname() +":"+area.getCode()+":"+area.getActAreaLevel().getAreaLevelName()
				+ ", parentArea=" + area.getParentAreaCode()
				+ ", value=" + value + ", rank=" + rank + ", trend=" + trend + ", top=" + top
				+ ", below=" + below + ", tp=" + tp + ", tps=" + tps + "]";
	}
//	
//	@Override
//	public String toString() {
//		return "Data [id=" + id + ", indicator=" + indicator.getiName() + ", subgrp=" + subgrp.getSubgroupName() + ", src=" + src.getSourceName() + ", ius=" + ius
//				+ ", area=" + area.getAreaname() +":"+area.getCode()+":"+area.getActAreaLevel().getAreaLevelName() + ", value=" + value + ", rank=" + rank + ", trend=" + trend + ", top=" + top
//				+ ", below=" + below + ", tp=" + tp + ", tps=" + tps + "]";
//	}

	
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

	public Boolean getdKPIRSrs() {
		return dKPIRSrs;
	}

	public void setdKPIRSrs(Boolean dKPIRSrs) {
		this.dKPIRSrs = dKPIRSrs;
	}

	public Boolean getdNITIRSrs() {
		return dNITIRSrs;
	}

	public void setdNITIRSrs(Boolean dNITIRSrs) {
		this.dNITIRSrs = dNITIRSrs;
	}

	public Boolean getdTHEMATICRSrs() {
		return dTHEMATICRSrs;
	}

	public void setdTHEMATICRSrs(Boolean dTHEMATICRSrs) {
		this.dTHEMATICRSrs = dTHEMATICRSrs;
	}

	
	
	

}
