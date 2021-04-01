package org.sdrc.rmnchadashboard.model;

import java.util.List;

import org.sdrc.rmnchadashboard.jpadomain.Area;
import org.sdrc.rmnchadashboard.jpadomain.AreaLevel;
import org.sdrc.rmnchadashboard.jpadomain.Indicator;
import org.sdrc.rmnchadashboard.jpadomain.Sector;
import org.sdrc.rmnchadashboard.jpadomain.Source;
import org.sdrc.rmnchadashboard.jpadomain.Subgroup;
import org.sdrc.rmnchadashboard.jpadomain.SynchronizationDateMaster;
import org.sdrc.rmnchadashboard.jpadomain.Unit;

public class MasterDataModel {
	
	private List<Area> areaList;
	private List<AreaLevel> areaLevelList;
	private List<Unit> unitList;
	private List<Indicator> indicatorList;
	private List<Subgroup> subgroupList;
	private List<Sector> sectorList;
	private List<Source> sourceList;
	private List<SynchronizationDateMaster> synchronizationDateMasterList;
	public List<Area> getAreaList() {
		return areaList;
	}
	public void setAreaList(List<Area> areaList) {
		this.areaList = areaList;
	}
	public List<AreaLevel> getAreaLevelList() {
		return areaLevelList;
	}
	public void setAreaLevelList(List<AreaLevel> areaLevelList) {
		this.areaLevelList = areaLevelList;
	}
	public List<Unit> getUnitList() {
		return unitList;
	}
	public void setUnitList(List<Unit> unitList) {
		this.unitList = unitList;
	}
	public List<Indicator> getIndicatorList() {
		return indicatorList;
	}
	public void setIndicatorList(List<Indicator> indicatorList) {
		this.indicatorList = indicatorList;
	}
	public List<Subgroup> getSubgroupList() {
		return subgroupList;
	}
	public void setSubgroupList(List<Subgroup> subgroupList) {
		this.subgroupList = subgroupList;
	}
	public List<Sector> getSectorList() {
		return sectorList;
	}
	public void setSectorList(List<Sector> sectorList) {
		this.sectorList = sectorList;
	}
	public List<Source> getSourceList() {
		return sourceList;
	}
	public void setSourceList(List<Source> sourceList) {
		this.sourceList = sourceList;
	}
	public List<SynchronizationDateMaster> getSynchronizationDateMasterList() {
		return synchronizationDateMasterList;
	}
	public void setSynchronizationDateMasterList(List<SynchronizationDateMaster> synchronizationDateMasterList) {
		this.synchronizationDateMasterList = synchronizationDateMasterList;
	}
	
	
}
