package org.sdrc.rmnchadashboard.model;

import org.sdrc.rmnchadashboard.jpadomain.Area;
import org.sdrc.rmnchadashboard.jpadomain.Indicator;
import org.sdrc.rmnchadashboard.jpadomain.Source;
import org.sdrc.rmnchadashboard.jpadomain.Subgroup;

public class DataModelTransformerForCustomView {
	

	private Long slugiddata;	
	
	private Indicator indicator;
	
	private Subgroup subgrp;

	
	private Source src;
	
	private Area area;

	private String value;

	private Integer rank;

	private String trend;

	private String tp;

	private String tps;

	private Boolean dKPIRSrs;
	
	private Boolean dNITIRSrs;

	public Long getSlugiddata() {
		return slugiddata;
	}

	public void setSlugiddata(Long slugiddata) {
		this.slugiddata = slugiddata;
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
	
	
	
	
}
