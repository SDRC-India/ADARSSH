package org.sdrc.rmnchadashboard.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.sdrc.rmnchadashboard.jpadomain.DataSyncMaster;
import org.sdrc.rmnchadashboard.jpadomain.Feedback;
import org.sdrc.rmnchadashboard.jparepository.SynchronizationDateMasterRepository;
import org.sdrc.rmnchadashboard.model.DataModelTransformerForCustomView;
import org.sdrc.rmnchadashboard.model.DataSyncStatusEnum;
import org.sdrc.rmnchadashboard.model.MasterDataModel;
import org.sdrc.rmnchadashboard.model.MasterDataSyncModel;
import org.sdrc.rmnchadashboard.model.RequestModel;
import org.sdrc.rmnchadashboard.model.ResponseModel;
import org.sdrc.rmnchadashboard.repository.AreaDAL;
import org.sdrc.rmnchadashboard.repository.AreaLevelDAL;
import org.sdrc.rmnchadashboard.repository.AreaRepository;
import org.sdrc.rmnchadashboard.repository.DataDAL;
import org.sdrc.rmnchadashboard.repository.DataRepository;
import org.sdrc.rmnchadashboard.repository.IndicatorDAL;
import org.sdrc.rmnchadashboard.repository.SectorDAL;
import org.sdrc.rmnchadashboard.repository.SourceDAL;
import org.sdrc.rmnchadashboard.repository.SubgroupDAL;
import org.sdrc.rmnchadashboard.repository.UnitDAL;
import org.sdrc.rmnchadashboard.service.MobileSyncService;
import org.sdrc.rmnchadashboard.utils.Constants;
import org.sdrc.rmnchadashboard.utils.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/")
public class SyncController {
//	@Autowired
//	private AreaLevelRepository areaLevelRepository;
//
//	@Autowired
//	private SourceRepository sourceRepository;
//
//	@Autowired
//	private SubgroupRepository subgroupRepository;
//
//	@Autowired
//	private SectorRepository sectorRepository;
//
//	@Autowired
//	private UnitRepository unitRepository;

//	@Autowired
//	private IndicatorRepository indicatorRepository;

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
	SynchronizationDateMasterRepository synchronizationDateMasterRepository;

	@Autowired
	MailService mailService;

	@Autowired
	MobileSyncService mobileSyncService;

	private final Path rootLocation = Paths.get("/rmncha-data");

	private List<DataSyncStatusEnum> dataSyncProgressList = new ArrayList<DataSyncStatusEnum>();

//	@CrossOrigin
//	@GetMapping("/indicators/findAll")
//	public List<Indicator> findAllIndicators() {
//		List<Indicator> indicators = new ArrayList<>();
//		Iterable<Indicator> itr = indicatorRepository.findAll();
//		itr.forEach(indicators::add);
//		return indicators;
//	}
//
//	@GetMapping("/unit/findAll")
//	public List<Unit> findAllUnit() {
//		List<Unit> units = new ArrayList<>();
//		Iterable<Unit> itr = unitRepository.findAll();
//		itr.forEach(units::add);
//		return units;
//	}
//
//	@GetMapping("/subgroup/findAll")
//	public List<Subgroup> findAllSubgroup() {
//		List<Subgroup> subgroups = new ArrayList<>();
//		Iterable<Subgroup> itr = subgroupRepository.findAll();
//		itr.forEach(s -> {
//			subgroups.add(s);
//			System.out.println(s);
//		});
//		return subgroups;
//
//	}
//
//	@GetMapping("/area/findAll")
//	public List<Area> findAllArea() {
//
//		List<Area> areas = new ArrayList<>();
//		Iterable<Area> itr = areaRepository.findAll();
//		itr.forEach(areas::add);
//		return areas;
//	}
//
//	@CrossOrigin
//	@GetMapping("/sector/findAll")
//	public List<Sector> findAllSector() {
//		List<Sector> sectors = new ArrayList<>();
//		Iterable<Sector> itr = sectorRepository.findAll();
//		itr.forEach(sectors::add);
//		return sectors;
//	}
//
//	@GetMapping("/source/findAll")
//	public List<Source> findAllSource() {
//
//		List<Source> sources = new ArrayList<>();
//		Iterable<Source> itr = sourceRepository.findAll();
//		itr.forEach(sources::add);
//		return sources;
//	}
//
//	@GetMapping("/arealevel/findAll")
//	public List<AreaLevel> findAllAreaLevel() {
//
//		List<AreaLevel> levels = new ArrayList<>();
//		Iterable<AreaLevel> itr = areaLevelRepository.findAll();
//		itr.forEach(levels::add);
//		return levels;
//	}
//
//	@GetMapping("/data/findAll/{page}")
//	public List<Data> findAllData(@PathVariable Integer page) {
//		Pageable request = new PageRequest(page, 1000);
//		List<Data> datas = new ArrayList<>();
//		Page<Data> itr = dataRepository.findAll(request);
//		itr.forEach(datas::add);
//		return datas;
//	}

//	@GetMapping("/data/findAllData")
//	public List<Data> findAllData() {
//		List<Data> datas = new ArrayList<>();
//		Iterable<Data> itr = dataRepository.findAll();
//		itr.forEach(datas::add);
//		datas.forEach(d -> {
//			if (!d.getTop().isEmpty())
//				System.out.println(d);
//		});
//		return datas;
//	}

//	@CrossOrigin
//	@GetMapping("/sync/area")
//	public List<Area> getAreaAfterSlugId(@RequestParam Integer slugidarea) {
//		List<Area> datas = new ArrayList<>();
//		Iterable<Area> itr = areaDAL.getAreaAfterSlugId(slugidarea);
//		if(itr!=null)
//		itr.forEach(datas::add);
//		else
//		return new ArrayList<Area>();
//		return datas;
//	}
//	
//	@CrossOrigin
//	@GetMapping("/sync/arealevelafterslugid")
//	public List<AreaLevel> getAreaLevelAfterSlugId(@RequestParam Integer slugidarealevel) {
//		List<AreaLevel> datas = new ArrayList<>();
//		Iterable<AreaLevel> itr = areaLevelDAL.getAreaLevelAfterSlugId(slugidarealevel);
//		if (itr != null)
//			itr.forEach(datas::add);
//		else
//			return new ArrayList<AreaLevel>();
//		return datas;
//	}
//	
//	@CrossOrigin
//	@GetMapping("/sync/data")
//	public List<Data> getDataAfterSlugId(@RequestParam Long slugiddata) {
//		List<Data> datas = new ArrayList<>();
//		Iterable<Data> itr = dataDAL.getDataAfterSlugId(slugiddata);
//		if(itr!=null)
//			itr.forEach(datas::add);
//			else
//		return new ArrayList<Data>();
//		return datas;
//	}
//	
//	@CrossOrigin
//	@GetMapping("/sync/indicator")
//	public List<Indicator> getIndicatorsAfterSlugId(@RequestParam Integer slugidindicator) {
//		List<Indicator> datas = new ArrayList<>();
//		Iterable<Indicator> itr = indicatorDAL.getIndicatorsAfterSlugId(slugidindicator);
//		if(itr!=null)
//			itr.forEach(datas::add);
//			else
//		return new ArrayList<Indicator>();
//		return datas;
//	}
//	
//	
//	@CrossOrigin
//	@GetMapping("/sync/sector")
//	public List<Sector> getSectorAfterSlugId(@RequestParam Integer slugidsector) {
//		List<Sector> datas = new ArrayList<>();
//		Iterable<Sector> itr = sectorDAL.getSectorAfterSlugId(slugidsector);
//		if(itr!=null)
//			itr.forEach(datas::add);
//			else
//		return new ArrayList<Sector>();
//		return datas;
//	}
//	
//	@CrossOrigin
//	@GetMapping("/sync/source")
//	public List<Source> getSourceAfterSlugId(@RequestParam Integer slugidsource) {
//		List<Source> datas = new ArrayList<>();
//		Iterable<Source> itr = sourceDAL.getSourceAfterSlugId(slugidsource);
//		if(itr!=null)
//			itr.forEach(datas::add);
//			else
//		return new ArrayList<Source>();
//		return datas;
//	}
//
//	@CrossOrigin
//	@GetMapping("/sync/subgroup")
//	public List<Subgroup> getSubgroupAfterSlugId(@RequestParam Integer slugidsubgroup) {
//		List<Subgroup> datas = new ArrayList<>();
//		Iterable<Subgroup> itr = subgroupDAL.getSubgroupAfterSlugId(slugidsubgroup);
//		if(itr!=null)
//			itr.forEach(datas::add);
//			else
//		return new ArrayList<Subgroup>();
//		return datas;
//	}
//	@CrossOrigin
//	@GetMapping("/sync/unit")
//	public List<Unit> getUnitAfterSlugId(@RequestParam Integer slugidunit) {
//		List<Unit> datas = new ArrayList<>();
//		Iterable<Unit> itr = unitDAL.getUnitAfterSlugId(slugidunit);
//		if(itr!=null)
//			itr.forEach(datas::add);
//			else
//		return new ArrayList<Unit>();
//		return datas;
//	}

	//// -------------------------------Sync By
	//// Timestamp---------------------------------

	/**
	 * We compare the lastModified timestamp in SynchronizationDateMaster database
	 * table for the api name with the @param timestamp, then all data is sent to
	 * mobile in a single payload along with lastModified date for the API in
	 * response. e.g
	 * 
	 * <pre>
	 * {
		  "lastModified": "2018-04-30 10:43:53.938",
		  "data": [
		  	{
		     "id": "AWMA_aJW6UXShQRE3oaW",
		      "slugidarea": 2,
		      "areaname": "India",
		      "code": "IND",
		      "ccode": "IND",
		      "parentAreaCode": "GLO",
		      "createdDate": 1524730006509,
		      "lastModified": 1524730006509
		      }
		  ]
		}
	 * </pre>
	 * 
	 * If no data is available then a last attempted sync date is sent with blank
	 * data [] array. { "lastModified": "2018-04-30 10:43:53.938", "data": [] }
	 * 
	 * @param timestamp Timestamp sent from server during last sync. Takes parameter
	 *                  in format:
	 *                  <url>http://localhost:8080/api/v1/sync/data?timestamp=2018-04-22%2008:06:46.508</url>
	 * @return Timestamp of starting sync time from server along with all data if
	 *         available after created or modified from @param timestamp
	 */

//	@CrossOrigin
//	@GetMapping("/sync/area")
//	public SyncModel<?> getAreaByTimestamp(@RequestParam String timestamp) {
//
//		List<Area> datas = new ArrayList<>();
//		SynchronizationDateMaster master = synchronizationDateMasterRepository.findByTableName("area");
//		if (master.getLastModifiedDate().compareTo(java.sql.Timestamp.valueOf(timestamp)) > 0) {
//			Iterable<Area> itr = areaRepository.findAll();
//			if (itr != null)
//				itr.forEach(datas::add);
//		}
//
//		SyncModel<Area> syncData = new SyncModel<Area>(datas,
//				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(master.getLastModifiedDate()));
//		return syncData;
//	}
//
//	/**
//	 * We compare the lastModified timestamp in SynchronizationDateMaster database
//	 * table for the api name with the @param timestamp, then all data is sent to
//	 * mobile in a single payload along with lastModified date for the API in
//	 * response. e.g
//	 * 
//	 * <pre>
//	 * {
//		  "lastModified": "2018-04-30 10:43:53.938",
//		  "data": [
//		  	{
//		      "id": "AWMA_Za66UXShQRE3oaO",
//		      "slugidarealevel": 2,
//		      "areaLevelName": "National",
//		      "level": 2,
//		      "isStateAvailable": false,
//		      "isDistrictAvailable": false,
//		      "createdDate": 1524730006509,
//		      "lastModified": 1524730006509
//		    }
//		  ]
//		}
//	 * </pre>
//	 * 
//	 * If no data is available then a last attempted sync date is sent with blank
//	 * data [] array. { "lastModified": "2018-04-30 10:43:53.938", "data": [] }
//	 * 
//	 * @param timestamp Timestamp sent from server during last sync.
//	 * @return Timestamp of starting sync time from server along with all data if
//	 *         available after created or modified from @param timestamp
//	 */
//
//	@CrossOrigin
//	@GetMapping("/sync/arealevel")
//	public SyncModel<?> getAreaLevelByTimestamp(@RequestParam String timestamp) {
//		System.out.println("Timestamp recieved from sql::" + java.sql.Timestamp.valueOf(timestamp));
//		List<AreaLevel> datas = new ArrayList<>();
//		SynchronizationDateMaster master = synchronizationDateMasterRepository.findByTableName("arealevel");
//		if (master.getLastModifiedDate().compareTo(java.sql.Timestamp.valueOf(timestamp)) > 0) {
//			Iterable<AreaLevel> itr = areaLevelRepository.findAll();
//			if (itr != null)
//				itr.forEach(datas::add);
//		}
//		SyncModel<AreaLevel> syncData = new SyncModel<AreaLevel>(datas,
//				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(master.getLastModifiedDate()));
//		return syncData;
//	}
//
//	/**
//	 * We compare the lastModified timestamp in SynchronizationDateMaster database
//	 * table for the api name with the @param timestamp, then all data is sent to
//	 * mobile in a single payload along with lastModified date for the API in
//	 * response. e.g
//	 * 
//	 * <pre>
//	 * {
//		  "lastModified": "2018-04-30 10:43:53.938",
//		  "data": [{},{},{} 
//		  ]
//		}
//	 * </pre>
//	 * 
//	 * If no data is available then a last attempted sync date is sent with blank
//	 * data [] array. { "lastModified": "2018-04-30 10:43:53.938", "data": [] }
//	 * 
//	 * @param timestamp Timestamp sent from server during last sync.
//	 * @return Timestamp of starting sync time from server along with all data if
//	 *         available after created or modified from @param timestamp
//	 */
//	@CrossOrigin
//	@GetMapping("/sync/indicator")
//	public SyncModel<?> getIndicatorsByTimestamp(@RequestParam String timestamp) {
//
//		List<Indicator> datas = new ArrayList<>();
//		SynchronizationDateMaster master = synchronizationDateMasterRepository.findByTableName("arealevel");
//		if (master.getLastModifiedDate().compareTo(java.sql.Timestamp.valueOf(timestamp)) > 0) {
//			Iterable<Indicator> itr = indicatorRepository.findAll();
//			if (itr != null)
//				itr.forEach(datas::add);
//		}
//		SyncModel<Indicator> syncData = new SyncModel<Indicator>(datas,
//				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(master.getLastModifiedDate()));
//		return syncData;
//	}
//
//	/**
//	 * We compare the lastModified timestamp in SynchronizationDateMaster database
//	 * table for the api name with the @param timestamp, then all data is sent to
//	 * mobile in a single payload along with lastModified date for the API in
//	 * response. e.g
//	 * 
//	 * <pre>
//	 * {
//		  "lastModified": "2018-04-30 10:43:53.938",
//		  "data": [{},{},{} 
//		  ]
//		}
//	 * </pre>
//	 * 
//	 * If no data is available then a last attempted sync date is sent with blank
//	 * data [] array. { "lastModified": "2018-04-30 10:43:53.938", "data": [] }
//	 * 
//	 * @param timestamp Timestamp sent from server during last sync.
//	 * @return Timestamp of starting sync time from server along with all data if
//	 *         available after created or modified from @param timestamp
//	 */
//	@CrossOrigin
//	@GetMapping("/sync/sector")
//	public SyncModel<?> getSectorByTimestamp(@RequestParam String timestamp) {
//
//		List<Sector> datas = new ArrayList<>();
//		SynchronizationDateMaster master = synchronizationDateMasterRepository.findByTableName("sector");
//		if (master.getLastModifiedDate().compareTo(java.sql.Timestamp.valueOf(timestamp)) > 0) {
//			Iterable<Sector> itr = sectorRepository.findAll();
//			if (itr != null)
//				itr.forEach(datas::add);
//		}
//		SyncModel<Sector> syncData = new SyncModel<Sector>(datas,
//				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(master.getLastModifiedDate()));
//		return syncData;
//	}
//
//	/**
//	 * We compare the lastModified timestamp in SynchronizationDateMaster database
//	 * table for the api name with the @param timestamp, then all data is sent to
//	 * mobile in a single payload along with lastModified date for the API in
//	 * response. e.g
//	 * 
//	 * <pre>
//	 * {
//		  "lastModified": "2018-04-30 10:43:53.938",
//		  "data": [{},{},{} 
//		  ]
//		}
//	 * </pre>
//	 * 
//	 * If no data is available then a last attempted sync date is sent with blank
//	 * data [] array. { "lastModified": "2018-04-30 10:43:53.938", "data": [] }
//	 * 
//	 * @param timestamp Timestamp sent from server during last sync.
//	 * @return Timestamp of starting sync time from server along with all data if
//	 *         available after created or modified from @param timestamp
//	 */
//	@CrossOrigin
//	@GetMapping("/sync/source")
//	public SyncModel<?> getSourceByTimestamp(@RequestParam String timestamp) {
//
//		List<Source> datas = new ArrayList<>();
//		SynchronizationDateMaster master = synchronizationDateMasterRepository.findByTableName("sector");
//		if (master.getLastModifiedDate().compareTo(java.sql.Timestamp.valueOf(timestamp)) > 0) {
//			Iterable<Source> itr = sourceRepository.findAll();
//			if (itr != null)
//				itr.forEach(datas::add);
//		}
//		SyncModel<Source> syncData = new SyncModel<Source>(datas,
//				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(master.getLastModifiedDate()));
//		return syncData;
//	}
//
//	/**
//	 * We compare the lastModified timestamp in SynchronizationDateMaster database
//	 * table for the api name with the @param timestamp, then all data is sent to
//	 * mobile in a single payload along with lastModified date for the API in
//	 * response. e.g
//	 * 
//	 * <pre>
//	 * {
//		  "lastModified": "2018-04-30 10:43:53.938",
//		  "data": [{},{},{} 
//		  ]
//		}
//	 * </pre>
//	 * 
//	 * If no data is available then a last attempted sync date is sent with blank
//	 * data [] array. { "lastModified": "2018-04-30 10:43:53.938", "data": [] }
//	 * 
//	 * @param timestamp Timestamp sent from server during last sync.
//	 * @return Timestamp of starting sync time from server along with all data if
//	 *         available after created or modified from @param timestamp
//	 */
//	@CrossOrigin
//	@RequestMapping("/sync/subgroup")
//	public SyncModel<?> getSubgroupByTimestamp(@RequestParam(name = "timestamp") String timestamp) {
//		List<Subgroup> datas = new ArrayList<Subgroup>();
//		SynchronizationDateMaster master = synchronizationDateMasterRepository.findByTableName("subgroup");
//		if (master.getLastModifiedDate().compareTo(java.sql.Timestamp.valueOf(timestamp)) > 0) {
//			Iterable<Subgroup> itr = subgroupRepository.findAll();
//			if (itr != null)
//				itr.forEach(datas::add);
//		}
//		SyncModel<Subgroup> syncData = new SyncModel<Subgroup>(datas,
//				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(master.getLastModifiedDate()));
//		return syncData;
//	}
//
//	/**
//	 * We compare the lastModified timestamp in SynchronizationDateMaster database
//	 * table for the api name with the @param timestamp, then all data is sent to
//	 * mobile in a single payload along with lastModified date for the API in
//	 * response. e.g
//	 * 
//	 * <pre>
//	 * {
//		  "lastModified": "2018-04-30 10:43:53.938",
//		  "data": [{},{},{} 
//		  ]
//		}
//	 * </pre>
//	 * 
//	 * If no data is available then a last attempted sync date is sent with blank
//	 * data [] array. { "lastModified": "2018-04-30 10:43:53.938", "data": [] }
//	 * 
//	 * @param timestamp Timestamp sent from server during last sync.
//	 * @return Timestamp of starting sync time from server along with all data if
//	 *         available after created or modified from @param timestamp
//	 */
//	@CrossOrigin
//	@GetMapping("/sync/unit")
//	public SyncModel<?> getUnitByTimestamp(@RequestParam String timestamp) {
//		List<Unit> datas = new ArrayList<Unit>();
//		SynchronizationDateMaster master = synchronizationDateMasterRepository.findByTableName("unit");
//		if (master.getLastModifiedDate().compareTo(java.sql.Timestamp.valueOf(timestamp)) > 0) {
//			Iterable<Unit> itr = unitRepository.findAll();
//			if (itr != null)
//				itr.forEach(datas::add);
//		}
//		SyncModel<Unit> syncData = new SyncModel<Unit>(datas,
//				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(master.getLastModifiedDate()));
//		return syncData;
//	}

	/**
	 * If new updates are available in whose lastModified timestamp is greater
	 * than @param timestamp, then a newly generated current system timestamp along
	 * with new updates sent in response. e.g
	 * 
	 * <pre>
	 * {
		  "lastModified": "2018-04-30 10:43:53.938",
		  "data": [{},{},{} 
		  ]
		}
	 * </pre>
	 * 
	 * If no data is available then a last attempted sync date is sent with blank
	 * data [] array. { "lastModified": "2018-04-30 10:43:53.938", "data": [] }
	 * 
	 * @param timestamp Timestamp sent from server during last sync.
	 * @return Timestamp of starting sync time from server along with all data if
	 *         available after created or modified from @param timestamp
	 */
	// Since is disabled now
//	@CrossOrigin
//	@GetMapping("/sync/data")
//	public SyncModel<?> getDataByTimestamp(@RequestParam String timestamp) {
//		List<Data> datas = new ArrayList<>();
//		SynchronizationDateMaster master = synchronizationDateMasterRepository.findByTableName("data");
//		System.out.println("Timestamp recieved from sql::"+java.sql.Timestamp.valueOf(timestamp));
//		Iterable<Data> itr = dataDAL.getDataAfterTimestamp(java.sql.Timestamp.valueOf(timestamp));
//		if(itr!=null)
//			itr.forEach(datas::add);
//			else
//				return new SyncModel<Data>(new ArrayList<Data>(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(master.getLastModifiedDate()));
//		SyncModel<Data> syncData = new SyncModel<Data>(datas,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(master.getLastModifiedDate()));
//		return syncData;
//	}

	/**
	 * If new updates are available in whose lastModified timestamp is greater
	 * than @param timestamp, then a newly generated current system timestamp along
	 * with new updates sent in response. e.g
	 * 
	 * <pre>
	 * {
		
		  "data": [{},{},{} 
		  ]
		}
	 * </pre>
	 * 
	 * If no data is available then a last attempted sync date is sent with blank
	 * data [] array. { "lastModified": "2018-04-30 10:43:53.938", "data": [] }
	 * 
	 * @param timestamp Timestamp sent from server during last sync.
	 * @return Timestamp of starting sync time from server along with all data if
	 *         available after created or modified from @param timestamp
	 */

	@CrossOrigin
	@GetMapping("/fetch/customview/{page}")
	public List<DataModelTransformerForCustomView> getDataByTimestamp(@PathVariable Integer page,
			@RequestParam List<Integer> indicatorSlugIds, @RequestParam List<String> areaCodes,
			@RequestParam Integer searchType) {
		List<DataModelTransformerForCustomView> datas = new ArrayList<>();
		try {
			Pageable request = new PageRequest(page, 500);
			List<org.sdrc.rmnchadashboard.jpadomain.Data> itr = mobileSyncService.getCustomViewData(page, indicatorSlugIds, areaCodes, searchType,request);
			if (itr != null) {
				itr.forEach(d -> {
					DataModelTransformerForCustomView o = new DataModelTransformerForCustomView();
					o.setdKPIRSrs(d.getdKPIRSrs());
					o.setdNITIRSrs(d.getdNITIRSrs());
					o.setArea(d.getArea());
					o.setIndicator(d.getIndicator());
					o.setRank(d.getRank());
					o.setSlugiddata(d.getSlugiddata());
					o.setSrc(d.getSrc());
					o.setSubgrp(d.getSubgrp());
					o.setTp(d.getTp());
					o.setTps(d.getTps());
					o.setTrend(d.getTrend());
					o.setValue(d.getValue());
					datas.add(o);
				});
			}
			return datas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@CrossOrigin
	@PostMapping("/submitFeedback")
	public ResponseEntity<ResponseModel> submitFeedback(@RequestBody Feedback feedback) {
		return mailService.sendSimpleMessage(feedback);
	}

	@CrossOrigin
	@GetMapping("/sync/data")
	public void syncData(HttpServletResponse response) throws IOException {
		InputStream inputStream;
		try {

			inputStream = new FileInputStream(this.rootLocation.resolve(Constants.ZIP_FILE_NAME).toFile());
			String headerKey = "Content-Disposition";

			response.setHeader(headerKey, "attachment; filename=" + Constants.ZIP_FILE_NAME);
			response.setContentType("application/zip"); // for all file type
			ServletOutputStream outputStream = response.getOutputStream();
			FileCopyUtils.copy(inputStream, outputStream);
			outputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @author Sourav Keshari Nath Generate the excel sheet
	 */
	@RequestMapping(value = "/downloadExcelTemplate", method = RequestMethod.POST)
	public void generateExcelTemplate(HttpServletResponse httpServletResponse) throws IOException {

		File file = mobileSyncService.generateExcelTemplate();
		try {
			String mimeType;
			mimeType = "application/octet-stream";
			httpServletResponse.setContentType(mimeType);
			httpServletResponse.setHeader("Content-Disposition",
					String.format("attachment; filename=\"%s\"", file.getName()));
			httpServletResponse.setContentLength((int) file.length());
			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			FileCopyUtils.copy(inputStream, httpServletResponse.getOutputStream());

		} finally {
			httpServletResponse.getOutputStream().close();
			if (file != null) {
				file.delete();
			}
		}
	}

	@PostMapping("/uploadTemplate")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
		String message = "";
		try {
			String fileName = mobileSyncService.store(file);

			message = fileName;
			return ResponseEntity.status(HttpStatus.OK).body(message);

		} catch (Exception e) {
			message = "FAIL to upload " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}
	}

	@PostMapping("validate")
	public ResponseEntity<ResponseModel> ValidateExcel(@RequestBody String filePath) {
		filePath = filePath.replaceAll("%20", " ");
		return ResponseEntity.status(HttpStatus.OK).body(mobileSyncService.validateData(filePath));

	}

	@PostMapping("importData")
	public ResponseEntity<ResponseModel> importData(@RequestBody RequestModel requestModel) throws IOException {
		dataSyncProgressList.add(DataSyncStatusEnum.IN_PROGRESS);
		dataSyncProgressList.add(DataSyncStatusEnum.DATA_JSON_INSERTED);
		dataSyncProgressList.add(DataSyncStatusEnum.DATA_INSERTED);

		requestModel.setExcelPath(requestModel.getExcelPath().replaceAll("%20", " "));
		ResponseModel responseModel = new ResponseModel();
		mobileSyncService.sendNotfication(new ResponseModel(HttpStatus.ACCEPTED.value(), "Sync Started"),
				requestModel.getFcmToken());
		List<DataSyncMaster> dataSyncStatus = mobileSyncService.getSyncStatus(dataSyncProgressList);
		if (!dataSyncStatus.isEmpty()) {
			responseModel.setStatusCode(HttpStatus.CONFLICT.value());
			responseModel.setMessage(Constants.ALREADY_IN_PROGRESS);
			mobileSyncService.sendNotfication(responseModel, requestModel.getFcmToken());
			return ResponseEntity.status(HttpStatus.OK).body(responseModel);
		}

		DataSyncMaster dataSyncMaster = new DataSyncMaster();
		dataSyncMaster.setDataSyncStatus(DataSyncStatusEnum.IN_PROGRESS);

		dataSyncMaster = mobileSyncService.updateDataSyncMaster(dataSyncMaster);

		responseModel = mobileSyncService.importNewData(requestModel);

		mobileSyncService.sendNotfication(responseModel, requestModel.getFcmToken());
		if (responseModel.getStatusCode() == HttpStatus.OK.value()) {
			mobileSyncService.createJsonAndZip(requestModel.getFcmToken());
			mobileSyncService.sendNotfication(responseModel, requestModel.getFcmToken());
		}

		if (responseModel.getStatusCode() == HttpStatus.OK.value()
				|| responseModel.getStatusCode() == HttpStatus.NOT_MODIFIED.value())
			dataSyncMaster.setDataSyncStatus(DataSyncStatusEnum.FINISHED);

		else if (responseModel.getStatusCode() == HttpStatus.EXPECTATION_FAILED.value())
			dataSyncMaster.setDataSyncStatus(DataSyncStatusEnum.REJECTED);

		else
			dataSyncMaster.setDataSyncStatus(DataSyncStatusEnum.FAILED);

		dataSyncMaster = mobileSyncService.updateDataSyncMaster(dataSyncMaster);
		
		

		return ResponseEntity.status(HttpStatus.OK).body(responseModel);

	}

	@CrossOrigin
	@PostMapping("/syncMasterData")
	public MasterDataModel syncMasterData(@RequestBody MasterDataSyncModel masterDataSyncModel) {
		return mobileSyncService.getMasterData(masterDataSyncModel);
	}
	
	
	@GetMapping("createJsonZip")
	public ResponseEntity<Boolean> createJSONZIPFile() throws Exception
	{
		return ResponseEntity.status(HttpStatus.OK).body(mobileSyncService.createJsonAndZip(""));
	}

}
