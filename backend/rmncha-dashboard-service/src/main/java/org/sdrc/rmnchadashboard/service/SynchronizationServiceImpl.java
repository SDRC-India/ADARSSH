package org.sdrc.rmnchadashboard.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sdrc.rmnchadashboard.domain.Subgroup;
import org.sdrc.rmnchadashboard.jpadomain.Indicator;
import org.sdrc.rmnchadashboard.jpadomain.Sector;
import org.sdrc.rmnchadashboard.jpadomain.Source;
import org.sdrc.rmnchadashboard.jpadomain.SynchronizationDateMaster;
import org.sdrc.rmnchadashboard.jpadomain.Unit;
import org.sdrc.rmnchadashboard.jparepository.AreaJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.AreaLevelJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.DataJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.IndicatorJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SectorJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SourceJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SubgroupJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SynchronizationDateMasterRepository;
import org.sdrc.rmnchadashboard.jparepository.UnitJpaRepository;
import org.sdrc.rmnchadashboard.repository.AreaDAL;
import org.sdrc.rmnchadashboard.repository.AreaLevelDAL;
import org.sdrc.rmnchadashboard.repository.AreaLevelRepository;
import org.sdrc.rmnchadashboard.repository.AreaRepository;
import org.sdrc.rmnchadashboard.repository.DataDAL;
import org.sdrc.rmnchadashboard.repository.DataRepository;
import org.sdrc.rmnchadashboard.repository.IndicatorDAL;
import org.sdrc.rmnchadashboard.repository.IndicatorRepository;
import org.sdrc.rmnchadashboard.repository.SectorDAL;
import org.sdrc.rmnchadashboard.repository.SectorRepository;
import org.sdrc.rmnchadashboard.repository.SourceDAL;
import org.sdrc.rmnchadashboard.repository.SourceRepository;
import org.sdrc.rmnchadashboard.repository.SubgroupDAL;
import org.sdrc.rmnchadashboard.repository.SubgroupRepository;
import org.sdrc.rmnchadashboard.repository.UnitDAL;
import org.sdrc.rmnchadashboard.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author azaruddin@sdrc.co.in
 *
 *         This service class is used to synchronize SQL database into Elastic
 *         Search Cluster
 */

@Service
public class SynchronizationServiceImpl implements SynchronizationService {

	@Autowired
	SynchronizationDateMasterRepository synchronizationDateMasterRepository;

	@Autowired
	private UnitJpaRepository unitJpaRepository;

	@Autowired
	private SubgroupJpaRepository subgroupJpaRepository;

	@Autowired
	private AreaJpaRepository areaJpaRepository;

	@Autowired
	private AreaLevelJpaRepository areaLevelJpaRepository;

	@Autowired
	private AreaLevelRepository areaLevelRepository;

	@Autowired
	private SourceRepository sourceRepository;

	@Autowired
	private SubgroupRepository subgroupRepository;

	@Autowired
	private SectorRepository sectorRepository;

	@Autowired
	private UnitRepository unitRepository;

	@Autowired
	private IndicatorRepository indicatorRepository;

	@Autowired
	AreaRepository areaRepository;

	@Autowired
	DataRepository dataRepository;

	@Autowired
	IndicatorDAL indicatorDAL;

	@Autowired
	DataDAL dataDAL;

	@Autowired
	AreaDAL areaDAL;

	@Autowired
	AreaLevelDAL areaLevelDAL;

	@Autowired
	SectorDAL sectorDAL;

	@Autowired
	SourceDAL sourceDAL;

	@Autowired
	SubgroupDAL subgroupDAL;

	@Autowired
	UnitDAL unitDAL;

	@Autowired
	IndicatorJpaRepository indicatorJpaRepository;

	@Autowired
	SourceJpaRepository sourceJpaRepository;
	@Autowired
	DataJpaRepository dataJpaRepository;

	@Autowired
	SectorJpaRepository sectorJpaRepository;

	/**
	 * 
	 * 
	 */
	
	Map<Integer, org.sdrc.rmnchadashboard.domain.Indicator> elasticIndicatorMap = new HashMap<>();

	Map<Integer, org.sdrc.rmnchadashboard.domain.Unit> elasticUnitMap = new HashMap<>();

	Map<Integer, org.sdrc.rmnchadashboard.domain.Source> elasticSourceMap = new HashMap<>();

	Map<Integer, org.sdrc.rmnchadashboard.domain.Subgroup> elasticSubgroupMap = new HashMap<>();
	
	Map<Integer, org.sdrc.rmnchadashboard.domain.Sector> elasticSectorMap = new HashMap<>();

	Map<Integer, org.sdrc.rmnchadashboard.domain.Area> elasticAreaMap = new HashMap<>();
	
	@Override
	
	public boolean startSynchronizationOfUnit(Date lastSynchronizedDate) {
		List<Unit> units = unitJpaRepository.findAllByLastModifiedGreaterThanOrderBySlugidunit(lastSynchronizedDate);
		units.forEach(unit -> {
			org.sdrc.rmnchadashboard.domain.Unit eu = unitDAL.getUnitBySlugId(unit.getId());

			final org.sdrc.rmnchadashboard.domain.Unit elasticUnit = (eu != null) ? eu
					: new org.sdrc.rmnchadashboard.domain.Unit();

			if (eu == null) {
				elasticUnit.setLastModified(unit.getLastModified());
				elasticUnit.setUnitName(unit.getUnitName());
				elasticUnit.setCreatedDate(unit.getCreatedDate());
				elasticUnit.setSlugidunit(unit.getSlugidunit());
			} else {
				elasticUnit.setLastModified(unit.getLastModified());
				elasticUnit.setUnitName(unit.getUnitName());
			}

			unitRepository.save(elasticUnit);
		});

		SynchronizationDateMaster synchronizationDateMaster = synchronizationDateMasterRepository
				.findByTableName("unit");
		synchronizationDateMaster.setLastSynchronized(synchronizationDateMaster.getLastModifiedDate());
		synchronizationDateMasterRepository.save(synchronizationDateMaster);
		return true;
	}

	@Override
//	@Transactional
	public boolean startSynchronizationOfIndicator(Date lastSynchronizedDate) {

		List<Indicator> sqlIndicators = indicatorJpaRepository
				.findAllByLastModifiedGreaterThanOrderBySlugidindicator(lastSynchronizedDate);

		sqlIndicators.forEach(sqlindicator -> {

			org.sdrc.rmnchadashboard.domain.Indicator e = indicatorDAL.getIndicatorBySlugId(sqlindicator.getId());

			final org.sdrc.rmnchadashboard.domain.Indicator elasticIndicator = (e != null) ? e
					: new org.sdrc.rmnchadashboard.domain.Indicator();

			List<org.sdrc.rmnchadashboard.domain.Source> districtsrs = new ArrayList<>();

			sqlindicator.getDistrict().forEach(sqlsource -> {
				org.sdrc.rmnchadashboard.domain.Source src = sourceDAL.findBySourceName(sqlsource.getSourceName());
				districtsrs.add(src);
			});

			elasticIndicator.setDistrict(districtsrs);

			List<org.sdrc.rmnchadashboard.domain.Source> statesrs = new ArrayList<>();

			sqlindicator.getState().forEach(sqlsource -> {
				org.sdrc.rmnchadashboard.domain.Source src = sourceDAL.findBySourceName(sqlsource.getSourceName());
				statesrs.add(src);
			});

			elasticIndicator.setState(statesrs);

			List<org.sdrc.rmnchadashboard.domain.Source> nationalsrs = new ArrayList<>();
			sqlindicator.getState().forEach(sqlsource -> {
				org.sdrc.rmnchadashboard.domain.Source src = sourceDAL.findBySourceName(sqlsource.getSourceName());
				nationalsrs.add(src);
			});
			elasticIndicator.setNational(nationalsrs);

			Sector sqlRecSec = sqlindicator.getRecSector();

			if (elasticIndicator.getRecSector() == null) {
				if (sqlindicator.getRecSector() != null) {
					// Find the sector from elastic search and set it in the nested doc
					org.sdrc.rmnchadashboard.domain.Sector s = sectorDAL
							.findBySectorName(sqlindicator.getRecSector().getSectorName());
					elasticIndicator.setRecSector(s);

				}
			} else {
				if (sqlRecSec.getLastModified().compareTo(elasticIndicator.getRecSector().getLastModified()) > 0) {
					elasticIndicator.getRecSector().setLastModified(sqlRecSec.getLastModified());
					elasticIndicator.getRecSector().setSectorName(sqlRecSec.getSectorName());

				}
			}

			List<Sector> sqlSectors = sqlindicator.getSc();

			List<org.sdrc.rmnchadashboard.domain.Sector> srs = new ArrayList<>();

			sqlSectors.forEach(sqlsector -> {
				org.sdrc.rmnchadashboard.domain.Sector sc = sectorDAL.getSectorBySlugId(sqlsector.getSlugidsector());
				srs.add(sc);
			});

			elasticIndicator.setSc(srs);

			org.sdrc.rmnchadashboard.jpadomain.Subgroup sqlsubgroup = sqlindicator.getSubgroup();

			if (elasticIndicator.getSubgroup() == null) {
				if (sqlsubgroup != null) {
					Subgroup elasticSubgroup = subgroupDAL.getSubgroupBySlugId(sqlsubgroup.getSlugidsubgroup());
					elasticIndicator.setSubgroup(elasticSubgroup);
				}
			} else {
				if (sqlsubgroup != null) {
					if (sqlsubgroup.getLastModified().compareTo(elasticIndicator.getSubgroup().getLastModified()) > 0) {
						elasticIndicator.getSubgroup().setSubgroupName(sqlsubgroup.getSubgroupName());
					}
				}
			}
			org.sdrc.rmnchadashboard.jpadomain.Unit sqlunit = sqlindicator.getUnit();

			if (elasticIndicator.getUnit() == null) {
				if (sqlunit != null) {
					org.sdrc.rmnchadashboard.domain.Unit elasticUnit = unitDAL.getUnitBySlugId(sqlunit.getSlugidunit());
					elasticIndicator.setUnit(elasticUnit);
				}
			} else {
				if (sqlunit != null) {
					if (sqlunit.getLastModified().compareTo(elasticIndicator.getUnit().getLastModified()) > 0) {
						elasticIndicator.getUnit().setUnitName(sqlunit.getUnitName());
					}
				}
			}

			elasticIndicator.setSlugidindicator(sqlindicator.getSlugidindicator());
			elasticIndicator.setCreatedDate(sqlindicator.getCreatedDate());
			elasticIndicator.setLastModified(sqlindicator.getLastModified());
			elasticIndicator.setiName(sqlindicator.getiName());
			elasticIndicator.setHighisgood(sqlindicator.getHighisgood());

			elasticIndicator.setKpi(sqlindicator.getKpi());
			elasticIndicator.setNitiaayog(sqlindicator.getNitiaayog());
			elasticIndicator.setNucolor(sqlindicator.isNucolor());
			
			elasticIndicator.setHmis(sqlindicator.getHmis());
			elasticIndicator.setSsv(sqlindicator.getSsv());
			elasticIndicator.setThematicKpi(sqlindicator.getThematicKpi());
			
			
			
			List<org.sdrc.rmnchadashboard.domain.Source> thematicdistrictsrs = new ArrayList<>();

			sqlindicator.getThematicDistrict().forEach(sqlsource -> {
				org.sdrc.rmnchadashboard.domain.Source src = sourceDAL.findBySourceName(sqlsource.getSourceName());
				thematicdistrictsrs.add(src);
			});

			elasticIndicator.setThematicDistrict(thematicdistrictsrs);

			List<org.sdrc.rmnchadashboard.domain.Source> thematicstatesrs = new ArrayList<>();

			sqlindicator.getThematicState().forEach(sqlsource -> {
				org.sdrc.rmnchadashboard.domain.Source src = sourceDAL.findBySourceName(sqlsource.getSourceName());
				thematicstatesrs.add(src);
			});

			elasticIndicator.setThematicState(thematicstatesrs);

			List<org.sdrc.rmnchadashboard.domain.Source> thematicnationalsrs = new ArrayList<>();
			sqlindicator.getThematicState().forEach(sqlsource -> {
				org.sdrc.rmnchadashboard.domain.Source src = sourceDAL.findBySourceName(sqlsource.getSourceName());
				thematicnationalsrs.add(src);
			});
			elasticIndicator.setThematicNational(thematicnationalsrs);

			indicatorRepository.save(elasticIndicator);
		});

		SynchronizationDateMaster synchronizationDateMaster = synchronizationDateMasterRepository
				.findByTableName("indicator");
		synchronizationDateMaster.setLastSynchronized(synchronizationDateMaster.getLastModifiedDate());
		synchronizationDateMasterRepository.save(synchronizationDateMaster);
		return true;
	}

	@Override
//	@Transactional
	public boolean startSynchronizationOfSubgroup(Date lastSynchronizedDate) {

		List<org.sdrc.rmnchadashboard.jpadomain.Subgroup> subgroups = subgroupJpaRepository
				.findAllByLastModifiedGreaterThanOrderBySlugidsubgroup(lastSynchronizedDate);
		subgroups.forEach(subgroup -> {
			org.sdrc.rmnchadashboard.domain.Subgroup eu = subgroupDAL.getSubgroupBySlugId(subgroup.getSlugidsubgroup());

			final org.sdrc.rmnchadashboard.domain.Subgroup elasticSubgroup = (eu != null) ? eu
					: new org.sdrc.rmnchadashboard.domain.Subgroup();
			if (eu == null) {
				elasticSubgroup.setLastModified(subgroup.getLastModified());
				elasticSubgroup.setSubgroupName(subgroup.getSubgroupName());
				elasticSubgroup.setCreatedDate(subgroup.getCreatedDate());
				elasticSubgroup.setSlugidsubgroup(subgroup.getSlugidsubgroup());
			} else {
				elasticSubgroup.setLastModified(subgroup.getLastModified());
				elasticSubgroup.setSubgroupName(subgroup.getSubgroupName());
			}
			subgroupRepository.save(elasticSubgroup);
		});
		SynchronizationDateMaster synchronizationDateMaster = synchronizationDateMasterRepository
				.findByTableName("subgroup");
		synchronizationDateMaster.setLastSynchronized(synchronizationDateMaster.getLastModifiedDate());
		synchronizationDateMasterRepository.save(synchronizationDateMaster);
		return true;
	}

	@Override
	public boolean startSynchronizationOfArea(Date lastSynchronizedDate) {

		List<org.sdrc.rmnchadashboard.jpadomain.Area> areas = areaJpaRepository
				.findAllByLastModifiedGreaterThanOrderBySlugidarea(lastSynchronizedDate);
		areas.forEach(area -> {
//			org.sdrc.rmnchadashboard.domain.Area eu = areaDAL.getAreaBySlugId(area.getSlugidarea());
			org.sdrc.rmnchadashboard.domain.Area eu = null;
			// compare the timestamps of both records i.e in

			final org.sdrc.rmnchadashboard.domain.Area elasticArea = (eu != null) ? eu
					: new org.sdrc.rmnchadashboard.domain.Area();
			elasticArea.setLastModified(area.getLastModified());
			elasticArea.setAreaname(area.getAreaname());
			elasticArea.setCode(area.getCode());
			elasticArea.setCcode(area.getCcode());
			elasticArea.setParentAreaCode(area.getParentAreaCode());
			elasticArea.setCreatedDate(area.getCreatedDate());
			elasticArea.setSlugidarea(area.getSlugidarea());
			elasticArea.setActAreaLevel(areaLevelDAL.getAreaBySlugId(area.getActAreaLevel().getSlugidarealevel()));

			List<org.sdrc.rmnchadashboard.domain.AreaLevel> areaLevels = new ArrayList<>();
			area.getAreaLevel().forEach(al -> {
				areaLevels.add(areaLevelDAL.getAreaBySlugId(al.getSlugidarealevel()));
			});

			elasticArea.setAreaLevel(areaLevels);
			areaRepository.save(elasticArea);
		});
		SynchronizationDateMaster synchronizationDateMaster = synchronizationDateMasterRepository
				.findByTableName("area");
		synchronizationDateMaster.setLastSynchronized(synchronizationDateMaster.getLastModifiedDate());
		synchronizationDateMasterRepository.save(synchronizationDateMaster);
		return true;
	}

	@Override
	public boolean startSynchronizationOfAreaLevel(Date lastSynchronizedDate) {

		List<org.sdrc.rmnchadashboard.jpadomain.AreaLevel> areaLevels = areaLevelJpaRepository
				.findAllByLastModifiedGreaterThanOrderBySlugidarealevel(lastSynchronizedDate);
		areaLevels.forEach(areaLevel -> {
			org.sdrc.rmnchadashboard.domain.AreaLevel eu = areaLevelDAL.getAreaBySlugId(areaLevel.getSlugidarealevel());
			// compare the timestamps of both records i.e in

			final org.sdrc.rmnchadashboard.domain.AreaLevel elasticAreaLevel = (eu != null) ? eu
					: new org.sdrc.rmnchadashboard.domain.AreaLevel();
			if (eu == null) {
				elasticAreaLevel.setCreatedDate(areaLevel.getCreatedDate());
				elasticAreaLevel.setLastModified(areaLevel.getLastModified());
				elasticAreaLevel.setAreaLevelName(areaLevel.getAreaLevelName());
				elasticAreaLevel.setSlugidarealevel(areaLevel.getSlugidarealevel());
				elasticAreaLevel.setIsDistrictAvailable(areaLevel.getIsDistrictAvailable());
				elasticAreaLevel.setIsStateAvailable(areaLevel.getIsStateAvailable());
				elasticAreaLevel.setLevel(areaLevel.getLevel());
			} else {
				elasticAreaLevel.setLastModified(areaLevel.getLastModified());
				elasticAreaLevel.setAreaLevelName(areaLevel.getAreaLevelName());
				elasticAreaLevel.setSlugidarealevel(areaLevel.getSlugidarealevel());
				elasticAreaLevel.setIsDistrictAvailable(areaLevel.getIsDistrictAvailable());
				elasticAreaLevel.setIsStateAvailable(areaLevel.getIsStateAvailable());
				elasticAreaLevel.setLevel(areaLevel.getLevel());
			}
			areaLevelRepository.save(elasticAreaLevel);
		});
		SynchronizationDateMaster synchronizationDateMaster = synchronizationDateMasterRepository
				.findByTableName("arealevel");
		synchronizationDateMaster.setLastSynchronized(synchronizationDateMaster.getLastModifiedDate());
		synchronizationDateMasterRepository.save(synchronizationDateMaster);
		return true;
	}

	@Override
	public boolean startSynchronizationOfSector(Date lastSynchronizedDate) {

		List<Sector> sectors = sectorJpaRepository
				.findAllByLastModifiedGreaterThanOrderBySlugidsector(lastSynchronizedDate);
		sectors.forEach(jpasector -> {
			org.sdrc.rmnchadashboard.domain.Sector eu = sectorDAL.getSectorBySlugId(jpasector.getId());

			final org.sdrc.rmnchadashboard.domain.Sector elasticSector = (eu != null) ? eu
					: new org.sdrc.rmnchadashboard.domain.Sector();

			if (eu == null) {
				elasticSector.setLastModified(jpasector.getLastModified());
				elasticSector.setSectorName(jpasector.getSectorName());
				elasticSector.setCreatedDate(jpasector.getCreatedDate());
				elasticSector.setSlugidsector(jpasector.getSlugidsector());
			} else {
				elasticSector.setLastModified(jpasector.getLastModified());
				elasticSector.setSlugidsector(jpasector.getSlugidsector());
			}

			sectorRepository.save(elasticSector);
		});

		SynchronizationDateMaster synchronizationDateMaster = synchronizationDateMasterRepository
				.findByTableName("sector");
		synchronizationDateMaster.setLastSynchronized(synchronizationDateMaster.getLastModifiedDate());
		synchronizationDateMasterRepository.save(synchronizationDateMaster);
		return true;
	}

	@Override
	public boolean startSynchronizationOfSource(Date lastSynchronizedDate) {

		List<Source> sources = sourceJpaRepository
				.findAllByLastModifiedGreaterThanOrderBySlugidsource(lastSynchronizedDate);
		sources.forEach(source -> {
			org.sdrc.rmnchadashboard.domain.Source eu = sourceDAL.getSourceBySlugId(source.getId());

			final org.sdrc.rmnchadashboard.domain.Source elasticSource = (eu != null) ? eu
					: new org.sdrc.rmnchadashboard.domain.Source();

			if (eu == null) {
				elasticSource.setLastModified(source.getLastModified());
				elasticSource.setSourceName(source.getSourceName());
				elasticSource.setCreatedDate(source.getCreatedDate());
				elasticSource.setSlugidsource(source.getSlugidsource());
			} else {
				elasticSource.setLastModified(source.getLastModified());
				elasticSource.setSlugidsource(source.getSlugidsource());
			}

			sourceRepository.save(elasticSource);
		});

		SynchronizationDateMaster synchronizationDateMaster = synchronizationDateMasterRepository
				.findByTableName("source");
		synchronizationDateMaster.setLastSynchronized(synchronizationDateMaster.getLastModifiedDate());
		synchronizationDateMasterRepository.save(synchronizationDateMaster);
		return true;
	}

	@Override
	public boolean startSynchronizationOfData(Date lastSynchronizedDate) {

		//Initialize Map
		sourceRepository.findAll().forEach(s->{
			elasticSourceMap.put(s.getSlugidsource(),s);
		});
		
		sectorRepository.findAll().forEach(s->{
			elasticSectorMap.put(s.getSlugidsector(), s);
		});
		
		indicatorRepository.findAll().forEach(i->{
			elasticIndicatorMap.put(i.getSlugidindicator(),  i);
		});
		
		unitRepository.findAll().forEach(u->{
			elasticUnitMap.put(u.getSlugidunit(),u);
		});
		
		subgroupRepository.findAll().forEach(sub->{
			elasticSubgroupMap.put(sub.getSlugidsubgroup(), sub);
		});
		
		areaRepository.findAll().forEach(a->{
			elasticAreaMap.put(a.getSlugidarea(), a);
		});
		
		
		
		List<org.sdrc.rmnchadashboard.jpadomain.Data> jpadatas = dataJpaRepository
				.findAllByLastModifiedGreaterThanOrderBySlugiddata(lastSynchronizedDate);
		List<org.sdrc.rmnchadashboard.domain.Data> dataSize = new ArrayList<>();
		for (int i = 0; i < jpadatas.size(); i++) {
			org.sdrc.rmnchadashboard.jpadomain.Data jpadata = jpadatas.get(i);

			if (dataSize.size() == 10000) {
				dataRepository.save(dataSize);
				dataSize = new ArrayList<>();
//				System.out.println(new Date() +" " +jpadata.getSlugiddata());
			}
			org.sdrc.rmnchadashboard.domain.Data eu = null;
			final org.sdrc.rmnchadashboard.domain.Data elasticData = (eu != null) ? eu
					: new org.sdrc.rmnchadashboard.domain.Data();

			elasticData.setLastModified(jpadata.getLastModified());
			elasticData.setArea(elasticAreaMap.get(jpadata.getArea().getSlugidarea()));

			List<org.sdrc.rmnchadashboard.domain.Area> belows = new ArrayList<>();
			jpadata.getBelow().forEach(below -> {
				belows.add(elasticAreaMap.get(below.getSlugidarea()));
			});
			elasticData.setBelow(belows);

			List<org.sdrc.rmnchadashboard.domain.Area> tops = new ArrayList<>();
			jpadata.getTop().forEach(top -> {
				tops.add(elasticAreaMap.get(top.getSlugidarea()));
			});
			elasticData.setTop(tops);

			elasticData.setCreatedDate(jpadata.getCreatedDate());
			elasticData.setdKPIRSrs(jpadata.getdKPIRSrs());
			elasticData.setdNITIRSrs(jpadata.getdNITIRSrs());
			elasticData.setdTHEMATICRSrs(jpadata.getdTHEMATICRSrs());

			elasticData.setIndicator(elasticIndicatorMap.get(jpadata.getIndicator().getSlugidindicator()));
			elasticData.setIus(jpadata.getIus());
			elasticData.setLastModified(jpadata.getLastModified());
			elasticData.setPeriodicity(jpadata.getPeriodicity());
			elasticData.setRank(jpadata.getRank());
			elasticData.setSlugiddata(jpadata.getSlugiddata());
			elasticData.setSrc(elasticSourceMap.get(jpadata.getSrc().getSlugidsource()));
			elasticData.setSubgrp(elasticSubgroupMap.get(jpadata.getSubgrp().getSlugidsubgroup()));
			elasticData.setTp(jpadata.getTp());
			elasticData.setTps(jpadata.getTps());
			elasticData.setTrend(jpadata.getTrend());
			elasticData.setValue(jpadata.getValue());

			dataSize.add(elasticData);
		}

		if (!dataSize.isEmpty()) {
			dataRepository.save(dataSize);
		}

		SynchronizationDateMaster synchronizationDateMaster = synchronizationDateMasterRepository
				.findByTableName("data");
		synchronizationDateMaster.setLastSynchronized(synchronizationDateMaster.getLastModifiedDate());
		synchronizationDateMasterRepository.save(synchronizationDateMaster);
		return true;
	}

	@Override
	public boolean startSyncProcess() {

		SynchronizationDateMaster sdm = synchronizationDateMasterRepository.findByTableName("arealevel");
		if (sdm.getLastSynchronized().compareTo(sdm.getLastModifiedDate()) != 0)
			startSynchronizationOfAreaLevel(sdm.getLastSynchronized());

		sdm = synchronizationDateMasterRepository.findByTableName("area");
		if (sdm.getLastSynchronized().compareTo(sdm.getLastModifiedDate()) != 0)
			startSynchronizationOfArea(sdm.getLastSynchronized());

		sdm = synchronizationDateMasterRepository.findByTableName("sector");
		if (sdm.getLastSynchronized().compareTo(sdm.getLastModifiedDate()) != 0)
			startSynchronizationOfSector(sdm.getLastSynchronized());

		sdm = synchronizationDateMasterRepository.findByTableName("source");
		if (sdm.getLastSynchronized().compareTo(sdm.getLastModifiedDate()) != 0)
			startSynchronizationOfSource(sdm.getLastSynchronized());

		sdm = synchronizationDateMasterRepository.findByTableName("unit");
		if (sdm.getLastSynchronized().compareTo(sdm.getLastModifiedDate()) != 0)
			startSynchronizationOfUnit(sdm.getLastSynchronized());

		sdm = synchronizationDateMasterRepository.findByTableName("subgroup");
		if (sdm.getLastSynchronized().compareTo(sdm.getLastModifiedDate()) != 0)
			startSynchronizationOfSubgroup(sdm.getLastSynchronized());

		sdm = synchronizationDateMasterRepository.findByTableName("indicator");
		if (sdm.getLastSynchronized().compareTo(sdm.getLastModifiedDate()) != 0)
			startSynchronizationOfIndicator(sdm.getLastSynchronized());

		sdm = synchronizationDateMasterRepository.findByTableName("data");
		if (sdm.getLastSynchronized().compareTo(sdm.getLastModifiedDate()) != 0)
			startSynchronizationOfData(sdm.getLastSynchronized());

		return false;
	}

}
