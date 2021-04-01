package org.sdrc.rmnchadashboard.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.sdrc.rmnchadashboard.domain.Area;
import org.sdrc.rmnchadashboard.domain.AreaLevel;
import org.sdrc.rmnchadashboard.domain.Data;
import org.sdrc.rmnchadashboard.domain.Indicator;
import org.sdrc.rmnchadashboard.domain.Sector;
import org.sdrc.rmnchadashboard.domain.Source;
import org.sdrc.rmnchadashboard.domain.Subgroup;
import org.sdrc.rmnchadashboard.domain.Unit;
import org.sdrc.rmnchadashboard.exception.FileNotFoundInProvidedPathException;
import org.sdrc.rmnchadashboard.jpadomain.SynchronizationDateMaster;
import org.sdrc.rmnchadashboard.jparepository.AreaJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.AreaLevelJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.DataJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.IndicatorJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SectorJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SourceJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SubgroupJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SynchronizationDateMasterRepository;
import org.sdrc.rmnchadashboard.jparepository.UnitJpaRepository;
import org.sdrc.rmnchadashboard.model.MasterDataModel;
import org.sdrc.rmnchadashboard.model.MasterDataSyncModel;
import org.sdrc.rmnchadashboard.repository.AreaLevelDAL;
import org.sdrc.rmnchadashboard.repository.AreaLevelRepository;
import org.sdrc.rmnchadashboard.repository.AreaRepository;
import org.sdrc.rmnchadashboard.repository.DataDAL;
import org.sdrc.rmnchadashboard.repository.DataRepository;
import org.sdrc.rmnchadashboard.repository.IndicatorDAL;
import org.sdrc.rmnchadashboard.repository.IndicatorRepository;
import org.sdrc.rmnchadashboard.repository.SectorDAL;
import org.sdrc.rmnchadashboard.repository.SectorRepository;
import org.sdrc.rmnchadashboard.repository.SourceRepository;
import org.sdrc.rmnchadashboard.repository.SubgroupDAL;
import org.sdrc.rmnchadashboard.repository.SubgroupRepository;
import org.sdrc.rmnchadashboard.repository.UnitDAL;
import org.sdrc.rmnchadashboard.repository.UnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	@Autowired
	private SourceRepository sourceRepository;

	@Autowired
	private SubgroupRepository subgroupRepository;

	@Autowired
	private SectorRepository sectorRepository;

	@Autowired
	private UnitRepository unitRepository;

	@Autowired
	private UnitDAL unitDAL;

	@Autowired
	private SectorDAL sectorDAL;

	@Autowired
	private SubgroupDAL subgroupDAL;

	@Autowired
	private IndicatorRepository indicatorRepository;

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private  AreaLevelRepository areaLevelRepository;

	@Autowired
	private DataRepository dataRepository;

	@Autowired
	private IndicatorDAL indicatorDAL;

	@Autowired
	private DataDAL dataDAL;

	@Autowired
	private AreaLevelDAL areaLevelDAL;

	@Autowired
	private IndicatorJpaRepository indicatorJpaRepository;

	@Autowired
	private AreaJpaRepository areaJpaRepository;

	@Autowired
	private AreaLevelJpaRepository areaLevelJpaRepository;

	@Autowired
	private SourceJpaRepository sourceJpaRepository;

	@Autowired
	private SubgroupJpaRepository subgroupJpaRepository;

	@Autowired
	private SectorJpaRepository sectorJpaRepository;

	@Autowired
	private UnitJpaRepository unitJpaRepository;

	@Autowired
	private DataJpaRepository dataJpaRepository;

	@Autowired
	private SynchronizationDateMasterRepository synchronizationDateMasterRepository;

	private Logger _log = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

	Map<String, org.sdrc.rmnchadashboard.jpadomain.Indicator> sqlIndicatorMap = new HashMap<>();
	Map<String, Indicator> elasticIndicatorMap = new HashMap<>();

	Map<String, org.sdrc.rmnchadashboard.jpadomain.Unit> sqlUnitMap = new HashMap<>();
	Map<String, Unit> elasticUnitMap = new HashMap<>();

	Map<String, org.sdrc.rmnchadashboard.jpadomain.Source> sqlSourceMap = new HashMap<>();
	Map<String, Source> elasticSourceMap = new HashMap<>();

	Map<String, org.sdrc.rmnchadashboard.jpadomain.Subgroup> sqlSubgroupMap = new HashMap<>();
	Map<String, Subgroup> elasticSubgroupMap = new HashMap<>();

	Map<String, org.sdrc.rmnchadashboard.jpadomain.Area> sqlAreaMap = new HashMap<>();
	Map<String, Area> elasticAreaMap = new HashMap<>();
	
	private final Path jsonDataLocation = Paths.get("/json-data");
	private final Path rootPath = Paths.get("/rmncha-data");

	/**
	 * 1) Imports data to elasticsearch cluster. a) Inserts Source,Sector,AreaLevel
	 * and Unit from codebase. b) Imports indicator from excel sheet found in
	 * resources/indicator folder c) Imports area from excel sheet found in
	 * resources/area folder d) Imports data from excel sheets found in
	 * resources/data folder
	 *
	 * 
	 * @return the value <code>true</code> if importing data completes successfully.
	 * @since 1.0
	 * @exception FileNotFoundInProvidedPathException
	 *                if <code>anotherDate</code> is null.
	 */
	// public static Date from(Instant instant)
	@Override
	public boolean configureApplication() {
		Date currentDate = new Date();
		System.out.println(
				"Timestamp recieved from sql::" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(currentDate));
		System.out.println(currentDate);
		System.out.println(currentDate.getTime());

		synchronizationDateMasterRepository.findAll().forEach(s -> {
			s.setLastModifiedDate(currentDate);
			s.setLastSynchronized(currentDate);
			synchronizationDateMasterRepository.save(s);
		});

		elasticSourceMap = new HashMap<>();
		elasticSubgroupMap = new HashMap<>();
		elasticUnitMap = new HashMap<>();
		elasticIndicatorMap = new HashMap<>();
		elasticAreaMap = new HashMap<>();
		{
			Source source = new Source();
			source.setSourceName("NFHS");
			source.setSlugidsource(1);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceRepository.save(source);

			elasticSourceMap.put(source.getSourceName(), source);

			System.out.println(source);

			source = new Source();
			source.setSourceName("DLHS");
			source.setSlugidsource(2);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceRepository.save(source);
			elasticSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			source = new Source();
			source.setSourceName("RSOC");
			source.setSlugidsource(3);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceRepository.save(source);

			elasticSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			source = new Source();
			source.setSourceName("AHS");
			source.setSlugidsource(4);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceRepository.save(source);

			elasticSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			source = new Source();
			source.setSourceName("SRS");
			source.setSlugidsource(5);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceRepository.save(source);
			elasticSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			source = new Source();
			source.setSourceName("HMIS");
			source.setSlugidsource(6);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceRepository.save(source);
			elasticSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			source = new Source();
			source.setSourceName("Census");
			source.setSlugidsource(7);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceRepository.save(source);
			elasticSourceMap.put(source.getSourceName(), source);
			System.out.println(source);
			
			source = new Source();
			source.setSourceName("SSV");
			source.setSlugidsource(8);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceRepository.save(source);
			elasticSourceMap.put(source.getSourceName(), source);
			System.out.println(source);
			
			// Creating Sectors

			Sector sector = new Sector();
			sector.setSectorName("Reproductive");
			sector.setSlugidsector(1);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorRepository.save(sector);

			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Maternal");
			sector.setSlugidsector(2);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorRepository.save(sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Newborn");
			// sector.setId((long) 3);
			sector.setSlugidsector(3);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorRepository.save(sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Child");
			sector.setSlugidsector(4);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorRepository.save(sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Adolescent");
			sector.setSlugidsector(5);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorRepository.save(sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Health infrastructure");

			sector.setSlugidsector(6);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorRepository.save(sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Demography");

			sector.setSlugidsector(7);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorRepository.save(sector);
			System.out.println(sector);

			sector = new Sector();
			sector.setSectorName("Human Resource");
			sector.setSlugidsector(8);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorRepository.save(sector);
			System.out.println(sector);

			Subgroup subgroup = new Subgroup();
			subgroup.setSubgroupName("Total");
			subgroup.setSlugidsubgroup(1);
			subgroup.setCreatedDate(currentDate);
			subgroup.setLastModified(currentDate);
			subgroup = subgroupRepository.save(subgroup);

			elasticSubgroupMap.put(subgroup.getSubgroupName(), subgroup);

			AreaLevel global = new AreaLevel();
			global.setLevel(1);
			global.setAreaLevelName("Global");
			global.setIsDistrictAvailable(false);
			global.setIsStateAvailable(false);
			global.setSlugidarealevel(1);
			global.setCreatedDate(currentDate);
			global.setLastModified(currentDate);
			areaLevelRepository.save(global);

			AreaLevel national = new AreaLevel();
			national.setLevel(2);
			national.setAreaLevelName("National");
			national.setIsDistrictAvailable(false);
			national.setIsStateAvailable(false);
			national.setSlugidarealevel(2);
			national.setCreatedDate(currentDate);
			national.setLastModified(currentDate);
			areaLevelRepository.save(national);

			AreaLevel state = new AreaLevel();
			state.setLevel(3);
			state.setAreaLevelName("State");
			state.setIsDistrictAvailable(false);
			state.setIsStateAvailable(true);
			state.setSlugidarealevel(3);
			state.setCreatedDate(currentDate);
			state.setLastModified(currentDate);
			areaLevelRepository.save(state);

			AreaLevel district = new AreaLevel();
			// district.setId((long) 4);
			district.setLevel(4);
			district.setAreaLevelName("District");
			district.setIsDistrictAvailable(true);
			district.setIsStateAvailable(true);
			district.setSlugidarealevel(4);
			district.setCreatedDate(currentDate);
			district.setLastModified(currentDate);
			areaLevelRepository.save(district);

			AreaLevel nitiAayog = new AreaLevel();
			nitiAayog.setLevel(5);
			nitiAayog.setAreaLevelName("NITI Aayog");
			nitiAayog.setIsDistrictAvailable(true);
			nitiAayog.setIsStateAvailable(true);
			nitiAayog.setSlugidarealevel(5);
			nitiAayog.setCreatedDate(currentDate);
			nitiAayog.setLastModified(currentDate);
			areaLevelRepository.save(nitiAayog);

			Unit unit = null;

			unit = new Unit();
			unit.setUnitName("Percent");
			unit.setSlugidunit(1);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitRepository.save(unit);

			elasticUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Deaths per 1000 live births");
			unit.setSlugidunit(2);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitRepository.save(unit);

			elasticUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Number");
			unit.setSlugidunit(3);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitRepository.save(unit);

			elasticUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Rupees");
			unit.setSlugidunit(4);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitRepository.save(unit);

			elasticUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Females per 1,000 males");
			unit.setSlugidunit(5);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitRepository.save(unit);

			elasticUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Live births per woman");
			unit.setSlugidunit(6);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitRepository.save(unit);

			elasticUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Deaths per 100,000 live births");
			unit.setSlugidunit(7);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitRepository.save(unit);

			elasticUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Sq km");
			unit.setSlugidunit(8);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitRepository.save(unit);

			elasticUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Per square kilometer");
			unit.setSlugidunit(9);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitRepository.save(unit);

			elasticUnitMap.put(unit.getUnitName(), unit);

			unit = new Unit();
			unit.setUnitName("Males per 100 Females");
			unit.setSlugidunit(10);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitRepository.save(unit);

			elasticUnitMap.put(unit.getUnitName(), unit);
			
			unit = new Unit();
			unit.setUnitName("Persons per sq km");
			unit.setSlugidunit(11);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitRepository.save(unit);

			elasticUnitMap.put(unit.getUnitName(), unit);

		}

		importArea(currentDate);

		XSSFWorkbook book = null;
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			URL url = loader.getResource("indicator/");
			String path = url.getPath().replaceAll("%20", " ");
			File files[] = new File(path).listFiles();

			if (files == null) {
				throw new FileNotFoundInProvidedPathException("No file found in path " + path);
			}

			for (int f = 0; f < files.length; f++) {
				System.out.println(files[f].toString());
			}

			book = new XSSFWorkbook(files[0]);

			int indicatorindex = 1;
			XSSFSheet sheet = book.getSheetAt(0);

			for (int row = 1; row <= sheet.getLastRowNum(); row++) {
				XSSFRow xssfRow = sheet.getRow(row);

				Iterator<Cell> cellItr = xssfRow.cellIterator();
				int cols = 0;
				String indicatorName = "";
				String sectorName = "";
				String isKPI = "", nitiaayog = "",thematicKPI="",thematicSourceNational="",thematicSourceState="",thematicSourceDistrict="";
				String unitC = "";
				boolean highIsGood = false, nuculor = false;
				String subgroupName = "";
				String sourceNational = "", sourceState = "", sourceDistrict = "",ssv="",hmis="";

				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();

					switch (cell.getColumnIndex()) {

					case 1:
						indicatorName = cell.getStringCellValue().trim();
						break;

					case 2:
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							isKPI = "No";
							cols++;
						} else
							isKPI = cell.getStringCellValue();
						break;

					case 3:
						unitC = cell.getStringCellValue().trim();
						break;
					case 4:
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
							highIsGood = cell.getBooleanCellValue();
							nuculor = false;
						}
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_STRING) {

							if (cell.getStringCellValue().trim().equalsIgnoreCase("Neutral")) {
								highIsGood = false;
								nuculor = true;
							} else {
								throw new IllegalArgumentException("Invalid argument::" + cell.toString());
							}
						}

						break;
					case 5:
						subgroupName = cell.getStringCellValue().trim();
						break;
					case 6:
						sectorName = cell.getStringCellValue().trim();
						break;
					case 7:
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							sourceNational = "";
							cols++;
						} else
							sourceNational = cell.getStringCellValue().trim();

						break;
					case 8:
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							sourceState = "";
							cols++;
						} else
							sourceState = cell.getStringCellValue().trim();

						break;

					case 9:
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							sourceDistrict = "";
							cols++;
						} else
							sourceDistrict = cell.getStringCellValue().trim();

						break;

					case 10:
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							nitiaayog = "No";
							cols++;
						} else
							nitiaayog = "Yes";
						break;
					case 11:
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							thematicKPI = "No";
							cols++;
						} else
							thematicKPI = "Yes";
						
						break;
						
					case 12:
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							thematicSourceNational = "";
							cols++;
						} else
							thematicSourceNational = cell.getStringCellValue().trim();
						
						break;
					case 13:
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							thematicSourceState = "";
							cols++;
						} else
							thematicSourceState = cell.getStringCellValue().trim();
						break;
					case 14:
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							thematicSourceDistrict = "";
							cols++;
						} else
							thematicSourceDistrict = cell.getStringCellValue().trim();
						break;
						
					case 15:
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							ssv = "No";
							cols++;
						} else
							ssv = "Yes";
						
						break;
					case 16:
						if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							hmis = "No";
							cols++;
						} else
							ssv = "Yes";
						
						break;
					}
					cols++;
				}

				List<Source> national = new ArrayList<Source>();
				List<Source> state = new ArrayList<Source>();
				List<Source> district = new ArrayList<Source>();
				
				List<Source> thematicNational = new ArrayList<Source>();
				List<Source> thematicState = new ArrayList<Source>();
				List<Source> thematicDistrict = new ArrayList<Source>();
				
				List<Sector> sectors = new ArrayList<Sector>();

				if (!sourceNational.isEmpty())
					national.add(sourceRepository.findBySourceName(sourceNational));

				if (!sourceState.isEmpty())
					state.add(sourceRepository.findBySourceName(sourceState));

				if (!sourceDistrict.isEmpty())
					district.add(sourceRepository.findBySourceName(sourceDistrict));
				
				
				if (!thematicSourceNational.isEmpty())
					thematicNational.add(sourceRepository.findBySourceName(thematicSourceNational));

				if (!thematicSourceState.isEmpty())
					thematicState.add(sourceRepository.findBySourceName(thematicSourceState));

				if (!thematicSourceDistrict.isEmpty())
					thematicDistrict.add(sourceRepository.findBySourceName(thematicSourceDistrict));

				sectors.add(sectorDAL.findBySectorName(sectorName));

				Indicator indicator = new Indicator();
				indicator.setKpi((isKPI).equalsIgnoreCase("yes") ? true : false);
				indicator.setNitiaayog((nitiaayog).equalsIgnoreCase("yes") ? true : false);
				indicator.setiName(indicatorName);
				indicator.setSubgroup(subgroupDAL.findBySubgroupName(subgroupName));
				indicator.setDistrict(district);
				indicator.setNational(national);
				indicator.setState(state);
				
				indicator.setSc(sectors);
				indicator.setHighisgood(highIsGood);

				indicator.setNucolor(nuculor);

				Unit unit = unitDAL.findByUnitName(unitC.trim());
				
				indicator.setUnit(unit);
				indicator.setRecSector(sectorDAL.findBySectorName(sectorName));
				indicator.setSlugidindicator(indicatorindex++);
				indicator.setCreatedDate(currentDate);
				indicator.setLastModified(currentDate);
				indicator.setSsv((ssv).equalsIgnoreCase("yes") ? true : false);
				indicator.setHmis((hmis).equalsIgnoreCase("yes") ? true : false);
				indicator.setThematicKpi((thematicKPI).equalsIgnoreCase("yes") ? true : false);

				
				indicator.setThematicDistrict(thematicDistrict);
				indicator.setThematicState(thematicState);
				indicator.setThematicNational(thematicNational);
				
				
				if (!indicatorName.isEmpty()) {
					indicator = indicatorRepository.save(indicator);
					elasticIndicatorMap.put(indicator.getiName().toLowerCase(), indicator);
					System.out.println(indicator);
				}

			}
			System.out.println("----------------------------------------------------------------------");
			elasticIndicatorMap.forEach((k, v) -> {
				System.out.println(k);
			});

			importData(currentDate);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				book.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return false;
	}

	@Transactional
	@Override
	public boolean importData(Date currentDate) {
		int dataindex = 1;

		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		URL url = loader.getResource("data/");
		String path = url.getPath().replaceAll("%20", " ");
		File files[] = new File(path).listFiles();

		if (files == null) {
			throw new NullPointerException("No files found in path " + path);
		}

		for (int f = 0; f < files.length; f++) {
			_log.info(files[f].toString());
		}

		for (int f = 0; f < files.length; f++) {

			XSSFWorkbook book = null;
			List<Data> datas = new ArrayList<>();
			try {

				book = new XSSFWorkbook(files[f]);

				XSSFSheet sheet = book.getSheetAt(0);

				Indicator indicator;
				String indicatorName = "";
				String unitC = "";
				String subgroupName = "";
				String areaCode = "", tp = "", sourceName = "", tpshortName = "", value = "";

				Boolean dKPIRSrs = false, dNITIRSrs = false;Boolean dTHEMATICRSrs=false;
				Unit unit = null;
				Data data = null;
				for (int row = 1; row <= sheet.getLastRowNum(); row++) {
					XSSFRow xssfRow = sheet.getRow(row);
					if (xssfRow == null) {
						break;
					}
					int cols = 0;
					indicator = null;
					indicatorName = "";
					unitC = "";
					subgroupName = "";
					areaCode = "";
					tp = "";
					sourceName = "";
					tpshortName = "";
					value = "";
					unit = null;
					data = null;
					dKPIRSrs = false;
					dNITIRSrs = false;
					dTHEMATICRSrs=false;
					for (int index = 0; index < 9; index++) {

						Cell cell = xssfRow.getCell(index);
						if (cell == null) {
							break;
						}
						switch (cols) {
						case 0:
							int cellType = cell.getCellType();
							if (cellType == 0) {
								tp = String.valueOf((int) cell.getNumericCellValue());
							} else {
								tp = cell.getStringCellValue().trim();
							}
							break;
						case 1:
							areaCode = cell.getStringCellValue().trim();
							break;
						
						case 4:
							indicatorName = cell.getStringCellValue().trim();
							break;
						case 5:
							unitC = cell.getStringCellValue().trim();
							break;
						case 6:
							subgroupName = cell.getStringCellValue().trim();
							break;
						case 7:
							int valcell = cell.getCellType();
							if (valcell == 0) {
								value = String.valueOf(cell.getNumericCellValue());
							} else {
								value = cell.getStringCellValue().trim();
							}
							break;
						case 8:
							sourceName = cell.getStringCellValue().trim();
							break;
						case 9:
							if (cell != null)
								tpshortName = cell.getStringCellValue().trim();
						
							break;
						default:
							break;
						case 10:
							// sector = cell.getStringCellValue().trim();
							break;
						case 11:
							// highisgood = cell.getBooleanCellValue();
							break;
						}
						cols++;
					}

					indicator = elasticIndicatorMap.get(indicatorName.toLowerCase());
					if (indicator == null) {

						if (files[f].getName().equals("SRS_Compilation.xlsx") && row == 1803) {
							break;
						} else {
							throw new NullPointerException("Indicator Not Found:::" + indicatorName);
						}

					} else if (!indicatorName.toLowerCase().equals(indicator.getiName().toLowerCase())) {
						throw new NullPointerException("Indicator MisMatch \n" + "Actual Indicator:::" + indicatorName
								+ "\nFound:::" + indicator.getiName());
					}

					else {
						// unit = unitDAL.findByUnitName(unitC);
						// Area area = areaRepository.findByCode(areaCode);
						// Subgroup subgroup = subgroupDAL.findBySubgroupName(subgroupName);
						// Source source = sourceRepository.findBySourceName(sourceName);

						unit = elasticUnitMap.get(unitC);
						Area area = elasticAreaMap.get(areaCode.toLowerCase());
						Subgroup subgroup = elasticSubgroupMap.get(subgroupName);
						Source source = elasticSourceMap.get(sourceName);

						if (area == null) {
							throw new NullPointerException("Area cannot be null::" + areaCode);
						}
						if (subgroup == null) {
							throw new NullPointerException("subgroup cannot be null::" + subgroupName);
						}
						if (source == null) {
							throw new NullPointerException("source Not Found:::" + sourceName);
						}

						if (unit == null) {
							throw new NullPointerException("Unit Not Found:::" + unitC);
						}

						String ius = indicator.getiName().concat(":").concat(unit.getUnitName()).concat(":")
								.concat(subgroup.getSubgroupName());

						if (indicator.getKpi()) {
							if (area.getActAreaLevel().getLevel() == 2) {
								// national
								if (source.getSlugidsource() == indicator.getNational().get(0).getSlugidsource()) {
									dKPIRSrs = true;
								}
							} else if (area.getActAreaLevel().getLevel() == 3) {
								// state
								if (source.getSlugidsource() == indicator.getState().get(0).getSlugidsource()) {
									dKPIRSrs = true;
								}
							} else if (area.getActAreaLevel().getLevel() == 4) {
								// district
								if (source.getSlugidsource() == indicator.getDistrict().get(0).getSlugidsource()) {
									dKPIRSrs = true;
								}
							}
						}

						if (indicator.getNitiaayog()) {
							if (area.getActAreaLevel().getLevel() == 2) {
								// national
								if (source.getSlugidsource() == indicator.getNational().get(0).getSlugidsource()) {
									dNITIRSrs = true;
								}
							} else if (area.getActAreaLevel().getLevel() == 3) {
								// state
								if (source.getSlugidsource() == indicator.getState().get(0).getSlugidsource()) {
									dNITIRSrs = true;
								}
							} else if (area.getActAreaLevel().getLevel() == 4) {
								// district
								if (source.getSlugidsource() == indicator.getDistrict().get(0).getSlugidsource()) {
									dNITIRSrs = true;
								}
							}
						}
						
						if (indicator.getThematicKpi()) {
							if (area.getActAreaLevel().getLevel() == 2) {
								// national
								if (source.getSlugidsource() == indicator.getThematicNational().get(0).getSlugidsource()) {
									dTHEMATICRSrs = true;
								}
							} else if (area.getActAreaLevel().getLevel() == 3) {
								// state
								if (source.getSlugidsource() == indicator.getThematicState().get(0).getSlugidsource()) {
									dTHEMATICRSrs = true;
								}
							} else if (area.getActAreaLevel().getLevel() == 4) {
								// district
								if (source.getSlugidsource() == indicator.getThematicDistrict().get(0).getSlugidsource()) {
									dTHEMATICRSrs = true;
								}
							}
						}

						data = new Data();
						data.setdKPIRSrs(dKPIRSrs);
						data.setdNITIRSrs(dNITIRSrs);
						data.setdTHEMATICRSrs(dTHEMATICRSrs);
						data.setArea(area);
						data.setBelow(new ArrayList<Area>());
						data.setTop(new ArrayList<Area>());
						data.setIndicator(indicator);
						data.setIus(ius);
						data.setRank(1);
						data.setSrc(source);
						data.setSubgrp(subgroup);
						data.setTrend("up");
						data.setValue(value);
						data.setTp(tp);
						data.setTps(tpshortName);
						data.setSlugiddata((((long) (dataindex++))));
						data.setPeriodicity("Yearly");
						data.setCreatedDate(currentDate);
						data.setLastModified(currentDate);
						datas.add(data);
						// dataRepository.save(data);
					}
				}
				if (!datas.isEmpty())
					dataRepository.save(datas);
				System.out.println("DATA LOOP FINISHED : " + (f + 1));
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				try {
					book.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
		return false;
	}

	@Override
	@Transactional
	public boolean importArea(Date currentDate) {
		{
			_log.debug("Importing areas from excel files");
			ClassLoader loader = Thread.currentThread().getContextClassLoader();

			URL url = loader.getResource("area/");
			String path = url.getPath().replaceAll("%20", " ");
			File files[] = new File(path).listFiles();

			if (files == null) {
				throw new FileNotFoundInProvidedPathException("No file found in path " + path);
			}

			for (int f = 0; f < files.length; f++) {
				_log.debug(files[f].toString());
			}
			AreaLevel nitiAyogAreaLevel = areaLevelDAL.findBySlugId(5);
			XSSFWorkbook book = null;
			try {
				book = new XSSFWorkbook(files[0]);
				List<Area> areas = new ArrayList<>();
				XSSFSheet sheet = book.getSheetAt(0);
				int areaindex = 1;
				for (int row = 0; row <= sheet.getLastRowNum(); row++) {
					// System.out.println("AREA : " + row);
					XSSFRow xssfRow = sheet.getRow(row);

					Iterator<Cell> cellItr = xssfRow.cellIterator();
					int cols = 0;
					String areaCode = "";
					String areaName = "";
					Integer areaLevel = null;
					String parentAreaCode = null;
					boolean nitiAyog = false;
					while (cellItr.hasNext()) {

						Cell cell = cellItr.next();

						switch (cols) {
						case 0:

							break;
						case 1:
							areaCode = cell.getStringCellValue().trim();
							break;
						case 2:
							areaName = cell.getStringCellValue().trim();
							break;
						case 3:
							parentAreaCode = cell.getStringCellValue().trim();
							break;
						case 4:
							areaLevel = (int) cell.getNumericCellValue();
							break;
						case 5:
							if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							} else
								nitiAyog = cell.getStringCellValue().trim().equalsIgnoreCase("yes") ? true : false;
							break;
						}
						cols++;
					}

					// Area area = areaRepository.findByCode(areaCode);
					// if (area == null) {
					Area area = new Area();
					area.setCcode(areaCode);
					area.setCode(areaCode);
					area.setCreatedDate(currentDate);
					area.setLastModified(currentDate);
					area.setParentAreaCode(parentAreaCode.equals("null") ? "" : parentAreaCode);

					AreaLevel level = areaLevelRepository.findByLevel(++areaLevel);

					// System.out.println("AreaLevel::" + level);
					List<AreaLevel> levels = new ArrayList<>();
					levels.add(level);
					if (nitiAyog == true) {
						levels.add(nitiAyogAreaLevel);
					}
					area.setAreaLevel(levels);
					area.setActAreaLevel(level);
					area.setAreaname(areaName);
					area.setSlugidarea((((int) (areaindex++))));
					area = areaRepository.save(area);
					elasticAreaMap.put(area.getCode().toLowerCase(), area);
					System.out.println(area);
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				try {
					book.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
		return false;
	}

	/**
	 * @Description For each document in rmncha-data type, we have to find the list
	 *              of areas that are below this area level for that particular
	 *              timeperiod,source,unit that have values greater than and less
	 *              than the value of a particular area and store respective areas
	 *              in two list named top and buttom. Then update that selected area
	 *              with the list of these two areas having top and buttom lists.
	 * 
	 * @return boolean true if method completes successfully.
	 */
	public Boolean calculateTopButtom() {
		System.out.println("calculateTopButtom STARTED:::" + new Date());
		Iterable<Indicator> indicators = indicatorRepository.findAll();

		indicators.forEach(indicator -> {
			// Map<String, List<Data>> map = new HashMap<>();
			List<Data> datass = new ArrayList<>();
			Iterable<Data> datas;

			int counter = 0;
			datas = dataDAL.findByIndicator(indicator);
			System.out.println("calculateTopButtom FOR INDICATOR:::" + (++counter) + " : " + indicator.getiName());
			if (datas != null) {
				datas.forEach(datass::add);

				List<Data> dddd = new ArrayList<>();

				datass.forEach(data -> {
					// System.out.println(data);
					// we are skipping all districts coz we dont want to calculate top and below
					// json
					if ((data.getArea().getActAreaLevel().getLevel() != 4 && data.getIndicator().getKpi() == true)
							|| data.getIndicator().getNitiaayog() == true) {

						List<Area> topAndEqualValueAreas = new ArrayList<>();
						List<Area> buttomValueAreas = new ArrayList<>();
						List<Data> children = dataDAL.findByIndicatorAndParentAreaCodeAndSourceAndTimePeriod(
								data.getIndicator().getiName(), data.getArea().getCode(), data.getSrc().getId(),
								data.getTp());
						Double value = Double.valueOf(data.getValue());

						if (data.getArea().getCode().equals("IND")) {
						// @formatter:off
							_log.debug(" Indicator Name  : {}\n "
									+  " Area Code       : {}\n"
									+ "  Parent AreaCode : {}\n"
									+ "  Source          : {}\n"
									+ "  Tp              : {}",data.getIndicator().getiName(),data.getArea().getCode(),data.getArea().getParentAreaCode(),data.getSrc().getSourceName(),data.getTp());
						
						// @formatter:on
						}

						if (children != null) {
							for (Data dataObj : children) {
								if ((dataObj.getIndicator().getHighisgood() == true)) {
									if ((new Double(dataObj.getValue()).compareTo(value) < 0)) {
										buttomValueAreas.add(dataObj.getArea());
									} else {
										topAndEqualValueAreas.add(dataObj.getArea());
									}
								} else {
									if ((new Double(dataObj.getValue()).compareTo(value) <= 0)) {
										topAndEqualValueAreas.add(dataObj.getArea());
									} else {
										buttomValueAreas.add(dataObj.getArea());
									}
								}

							}
							data.setTop(topAndEqualValueAreas);
							data.setBelow(buttomValueAreas);
							dddd.add(data);

						}

					}

				});
				if (dddd != null && !dddd.isEmpty())
					dataRepository.save(dddd);
			}
		});
		System.out.println("calculateTopButtom FINISHED:::" + new Date());
		return true;

	}

	@Override
	@Transactional
	public Map<String, List<Data>> calculateRank() {
		System.out.println("calculateRank STARTED:::" + new Date());
		Iterable<Indicator> indicators = indicatorRepository.findAll();

		indicators.forEach(indicator -> {
			Map<String, List<Data>> map = new LinkedHashMap<>();
			List<Data> datas = null;

			Map<String, List<Data>> indicatorTpSrcAreaLevelMap = new LinkedHashMap<>();

			datas = new ArrayList<>();
			Iterable<Data> itrDatas = null;

			itrDatas = dataDAL.findByIndicator(indicator);
			System.out.println("calculateTrend FOR INDICATOR:::" + indicator.getiName());
			if (itrDatas != null) {

				itrDatas.forEach(datas::add);

				for (int index = 0; index < datas.size(); index++) {
					if (datas.get(index).getArea().getActAreaLevel().getLevel() != 2) {

						// @formatter:off
						_log.debug(" Indicator Name  : {}\n "
								+  " Area Code       : {}\n"
								+ "  Parent AreaCode : {}\n"
								+ "  Source          : {}\n"
								+ "  Tp              : {}",
								datas.get(index).getIndicator().getiName(),
								datas.get(index).getArea().getCode(),
								datas.get(index).getArea().getParentAreaCode(),
								datas.get(index).getSrc().getSourceName(),
								datas.get(index).getTp());
					
					// @formatter:on

						List<Data> dd = indicatorTpSrcAreaLevelMap
								.getOrDefault(datas.get(index).getIndicator().getiName() + ":"
										+ datas.get(index).getSrc().getSourceName() + ":"
										+ datas.get(index).getArea().getActAreaLevel().getAreaLevelName() + ":"
										+ datas.get(index).getArea().getParentAreaCode() + ":"
										+ datas.get(index).getTp(), new ArrayList<>());

						dd.add(datas.get(index));

						indicatorTpSrcAreaLevelMap.put(datas.get(index).getIndicator().getiName() + ":"
								+ datas.get(index).getSrc().getSourceName() + ":"
								+ datas.get(index).getArea().getActAreaLevel().getAreaLevelName() + ":"
								+ datas.get(index).getArea().getParentAreaCode() + ":" + datas.get(index).getTp(), dd);
					}
				}

				indicatorTpSrcAreaLevelMap.forEach((k, v) -> {

					List<Data> modChilren = new ArrayList<>();

					modChilren.addAll(v);

					if (modChilren.get(0).getIndicator().getHighisgood() == false) {
						modChilren.sort(new Comparator<Data>() {

							@Override
							public int compare(Data o1, Data o2) {
								return new Double(o1.getValue()).compareTo(new Double(o2.getValue()));
							}
						});
					} else {
						modChilren.sort(new Comparator<Data>() {

							@Override
							public int compare(Data o1, Data o2) {
								return new Double(o2.getValue()).compareTo(new Double(o1.getValue()));
							}
						});
					}

					Map<String, List<Data>> rankMapForArea = new LinkedHashMap<String, List<Data>>();
					for (int index = 0; index < modChilren.size(); index++) {

						List<Data> dd = rankMapForArea.getOrDefault(modChilren.get(index).getValue(),
								new ArrayList<>());
						dd.add(modChilren.get(index));
						rankMapForArea.put(modChilren.get(index).getValue(), dd);

					}
					int rank = 1;
					List<Data> fullRankedList = new ArrayList<Data>();
					Iterator<List<Data>> iterator = rankMapForArea.values().iterator();
					while (iterator.hasNext()) {
						List<Data> dd = iterator.next();
						for (int index2 = 0; index2 < dd.size(); index2++) {
							dd.get(index2).setRank(rank);
							fullRankedList.add(dd.get(index2));
						}
						rank++;
					}
					map.put(k, fullRankedList);

				});

				Iterator<List<Data>> iterator = map.values().iterator();
				while (iterator.hasNext()) {
					List<Data> dd = iterator.next();
					dataRepository.save(dd);
				}
			}
		});
		System.out.println("calculateRank FINISHED:::" + new Date());
		return null;
	}

	public Indicator updateIndicator() {

		Indicator i = indicatorDAL.findByName("Maternal Mortality");
		i.setiName("Maternal Mortality Ratio (MMR)");

		i = indicatorRepository.save(i);
		return i;

	}

	@Transactional
	@Override
	public boolean calculateTrend() {
		System.out.println("calculateTrend STARTED:::" + new Date());
		Iterable<Indicator> indicators = indicatorRepository.findAll();

		indicators.forEach(indicator -> {
			List<Data> allData = new ArrayList<>();
			List<Data> datas = null;
			Iterable<Data> itrDatas = null;

			Map<String, List<Data>> indicatorSrcAreaLevelAreaMap = new LinkedHashMap<>();

			datas = new ArrayList<>();

			itrDatas = dataDAL.findByIndicator(indicator);
			System.out.println("calculateTrend FOR INDICATOR:::" + indicator.getiName());
			if (itrDatas != null) {
				itrDatas.forEach(datas::add);

				for (int index = 0; index < datas.size(); index++) {

					List<Data> dd = indicatorSrcAreaLevelAreaMap.getOrDefault(
							datas.get(index).getIndicator().getiName() + ":" + datas.get(index).getSrc().getSourceName()
									+ ":" + datas.get(index).getArea().getActAreaLevel().getAreaLevelName() + ":"
									+ datas.get(index).getArea().getCode(),
							new ArrayList<>());

					dd.add(datas.get(index));

					indicatorSrcAreaLevelAreaMap.put(
							datas.get(index).getIndicator().getiName() + ":" + datas.get(index).getSrc().getSourceName()
									+ ":" + datas.get(index).getArea().getActAreaLevel().getAreaLevelName() + ":"
									+ datas.get(index).getArea().getCode(),
							dd);

				}

				indicatorSrcAreaLevelAreaMap.forEach((k, v) -> {
					List<Data> dddd = new ArrayList<>();
					List<Data> modChilren = new ArrayList<>();

					modChilren.addAll(v);

					modChilren.sort(new Comparator<Data>() {
						@Override
						public int compare(Data o1, Data o2) {
							return o1.getTp().compareTo(o2.getTp());
						}
					});

					modChilren.get(0).setTrend("na");
					dddd.add(modChilren.get(0));
					if (modChilren.size() > 1) {

						Double prevValue = new Double(modChilren.get(0).getValue());

						for (int index = 1; index < modChilren.size(); index++) {

							if (new Double(modChilren.get(index).getValue()).compareTo(prevValue) == 0) {
								modChilren.get(index).setTrend("eq");

							} else if (new Double(modChilren.get(index).getValue()).compareTo(prevValue) < 0) {
								if (modChilren.get(index).getIndicator().getHighisgood())
									modChilren.get(index).setTrend("dn");
								else
									modChilren.get(index).setTrend("up");
							} else {
								if (modChilren.get(index).getIndicator().getHighisgood())
									modChilren.get(index).setTrend("up");
								else
									modChilren.get(index).setTrend("dn");
							}

							dddd.add(modChilren.get(index));
							prevValue = new Double(modChilren.get(index).getValue());
						}
					}
					allData.addAll(dddd);
				});
				dataRepository.save(allData);
			}
		});
		System.out.println("calculateTrend FINISHED:::" + new Date());
		return true;

	}

	@Override
	public String createJSON() throws Exception {
		try {
			Map<Integer, List<org.sdrc.rmnchadashboard.model.Data>> indicatorWiseData = new HashMap<>();
			Map<String, List<org.sdrc.rmnchadashboard.model.Data>> areaWiseData = new HashMap<>();
		System.out.println("Query Starts");
			List<Object[]> dataList = dataJpaRepository.findJsonData();
			System.out.println("Query Ends");
			for(int i = 0; i < dataList.size();i++) {
				
			
				Object[] data = dataList.get(i);
				
				
				//domain to model
				
				org.sdrc.rmnchadashboard.model.Data dataModel = new org.sdrc.rmnchadashboard.model.Data();
				
				dataModel.set_id(data[0].toString());
				dataModel.setArea(data[14].toString());	
				
				JSONArray jsonArrayDataTop = (JSONArray) new JSONParser().parse(data[18].toString());
				JSONArray jsonArrayDataBelow = (JSONArray) new JSONParser().parse(data[19].toString());
				Set<String> below = new HashSet<String>();
				if(jsonArrayDataBelow.get(0)!=null)
				{
				for(int j = 0; j < jsonArrayDataBelow.size();j++) {
					below.add(jsonArrayDataBelow.get(j).toString());
				}
				}
				dataModel.setBelow(below.stream().collect(Collectors.toList()));
				
				Set<String> top = new HashSet<String>();
				if(jsonArrayDataTop.get(0)!=null)
				{
				for(int j = 0; j < jsonArrayDataTop.size();j++) {
					top.add(jsonArrayDataTop.get(j).toString());
				}		
				}
//				dataModel.setBelow(below);
				dataModel.setTop(top.stream().collect(Collectors.toList()));
				
				
//				System.out.println(data[19]);
				dataModel.setIndicator(Integer.parseInt(data[15].toString()));
				dataModel.setIus(data[5].toString());
				dataModel.setRank(Integer.parseInt(data[8].toString()));
				dataModel.setSrc(Integer.parseInt(data[16].toString()));
				dataModel.setSubgrp(Integer.parseInt(data[17].toString()));
//				for(int j = 0; j < data.getTop().size();j++) {
//					top.add(data.getTop().get(j).getCode());
//				}
//				dataModel.setTop(top);
				dataModel.setTp((data[10].toString()));
				dataModel.setTrend((data[12].toString()));
				dataModel.setValue(data[13].toString());
				dataModel.setSlugiddata(Integer.parseInt(data[9].toString()));
				
				dataModel.setCreatedDate(data[1].toString());
				dataModel.setLastModified(data[6].toString());
				dataModel.setdKPIRSrs(Boolean.parseBoolean(data[2].toString()));
				dataModel.setdNITIRSrs(Boolean.parseBoolean(data[3].toString()));
				dataModel.setTps((data[11].toString()));
				dataModel.setdTHEMATICRSrs(Boolean.parseBoolean(data[4].toString()));
				
				
				
				
				//indicator work
				List<org.sdrc.rmnchadashboard.model.Data> existingIndicatorDataList = indicatorWiseData.get(dataModel.getIndicator());
				if(existingIndicatorDataList == null) {
					existingIndicatorDataList = new ArrayList<>();
				}
				
				
				
				existingIndicatorDataList.add(dataModel);			
				indicatorWiseData.put(dataModel.getIndicator(), existingIndicatorDataList);
				
				//area work
				List<org.sdrc.rmnchadashboard.model.Data> existingAreaDataList = areaWiseData.get(dataModel.getArea());
				if(existingAreaDataList == null) {
					existingAreaDataList = new ArrayList<>();
				}
				existingAreaDataList.add(dataModel);			
				areaWiseData.put(dataModel.getArea(), existingAreaDataList);
				
			}
			
			//Indicator json file creation
			for (Map.Entry<Integer, List<org.sdrc.rmnchadashboard.model.Data>> entry : indicatorWiseData.entrySet()) {
				String fileName = entry.getKey().toString();
				List<org.sdrc.rmnchadashboard.model.Data> fileContent = entry.getValue();
				
				ObjectMapper mapper = new ObjectMapper();
//				_log.info("creating indicator file: " + fileName + ".json");
		        File file = this.jsonDataLocation.resolve(fileName + ".json").toFile();
		        mapper.writeValue(file, fileContent);
			}
			
			//Area json file creation
			for (Map.Entry<String, List<org.sdrc.rmnchadashboard.model.Data>> entry : areaWiseData.entrySet()) {
				String fileName = entry.getKey().toString();
				List<org.sdrc.rmnchadashboard.model.Data> fileContent = entry.getValue();
				
				ObjectMapper mapper = new ObjectMapper();
//				_log.info("creating area file: " + fileName + ".json");
	
				File file = this.jsonDataLocation.resolve(fileName + ".json").toFile();
		        mapper.writeValue(file, fileContent);
			}
			
			//code to zip the folder	         
	        
	         
            // create byte buffer
            byte[] buffer = new byte[1024];
 
            File file = this.rootPath.resolve("rmncha-archive.zip").toFile();
            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
 
            ZipOutputStream zos = new ZipOutputStream(fos);
 
            File dir = this.jsonDataLocation.toFile();
 
            File[] files = dir.listFiles();
 
            for (int i = 0; i < files.length; i++) {
            	
//            	_log.info("Adding file: " + files[i].getName());
                 
 
                FileInputStream fis = new FileInputStream(files[i]);
 
                // begin writing a new ZIP entry, positions the stream to the start of the entry data
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                 
                int length;
 
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
 
                zos.closeEntry();
 
                // close the InputStream
                fis.close();
            }
 
            // close the ZipOutputStream
            zos.close();	
			return "success";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
	}

	@Override
	public MasterDataModel getMasterData(MasterDataSyncModel masterDataSyncModel) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
			MasterDataModel masterDataModel = new MasterDataModel();
			List<SynchronizationDateMaster> synchronizationDateMasterList = synchronizationDateMasterRepository.findAll();
			
			Date areaSyncDateMobile = new Timestamp(sdf.parse(masterDataSyncModel.getAreaSyncDate()).getTime());
			Date areaLevelSyncDateMobile = new Timestamp(sdf.parse(masterDataSyncModel.getAreaLevelSyncDate()).getTime());
			Date unitSyncDateMobile = new Timestamp(sdf.parse(masterDataSyncModel.getUnitSyncDate()).getTime());
			Date indicatorSyncDateMobile = new Timestamp(sdf.parse(masterDataSyncModel.getIndicatorSyncDate()).getTime());
			Date subgroupSyncDateMobile = new Timestamp(sdf.parse(masterDataSyncModel.getSubgroupSyncDate()).getTime());
			Date sectorSyncDateMobile = new Timestamp(sdf.parse(masterDataSyncModel.getSectorSyncDate()).getTime());
			Date sourceSyncDateMobile = new Timestamp(sdf.parse(masterDataSyncModel.getSourceSyncDate()).getTime());
			
			Date areaSyncDate = null;
			Date areaLevelSyncDate = null;
			Date unitSyncDate = null;
			Date indicatorSyncDate = null;
			Date subgroupSyncDate = null;
			Date sectorSyncDate = null;
			Date sourceSyncDate = null;
			
			boolean shouldSendAreaData = true;
			boolean shouldSendAreaLevelData = true;
			boolean shouldSendUnitData = true;
			boolean shouldSendIndicatorData = true;
			boolean shouldSendSubgroupData = true;
			boolean shouldSendSectorData = true;
			boolean shouldSendSourceData = true;
			
			for(int i = 0; i < synchronizationDateMasterList.size();i++) {
				SynchronizationDateMaster synchronizationDateMaster = synchronizationDateMasterList.get(i);
				switch(synchronizationDateMaster.getTableName()) {
				case "area":
					areaSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if(areaSyncDate.compareTo(areaSyncDateMobile) < 0) {
						shouldSendAreaData = false;
					}
					break;
				case "arealevel":
					areaLevelSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if(areaLevelSyncDate.compareTo(areaLevelSyncDateMobile) < 0) {
						shouldSendAreaLevelData = false;
					}
					break;
				case "unit":
					unitSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if(unitSyncDate.compareTo(unitSyncDateMobile) < 0) {
						shouldSendUnitData = false;
					}
					break;
				case "indicator":
					indicatorSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if(indicatorSyncDate.compareTo(indicatorSyncDateMobile) < 0) {
						shouldSendIndicatorData = false;
					}
					break;
				case "subgroup":
					subgroupSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if(subgroupSyncDate.compareTo(subgroupSyncDateMobile) < 0) {
						shouldSendSubgroupData = false;
					}
					break;
				case "sector":
					sectorSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if(sectorSyncDate.compareTo(sectorSyncDateMobile) < 0) {
						shouldSendSectorData = false;
					}
					break;
				case "source":
					sourceSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if(sourceSyncDate.compareTo(sourceSyncDateMobile) < 0) {
						shouldSendSourceData = false;
					}
					break;	
				}
			}
			
			if(shouldSendAreaData) {
				masterDataModel.setAreaList(areaJpaRepository.findAll());
			}
			
			if(shouldSendAreaLevelData) {
				masterDataModel.setAreaLevelList(areaLevelJpaRepository.findAll());
			}
			
			if(shouldSendUnitData) {
				masterDataModel.setUnitList(unitJpaRepository.findAll());
			}
			
			if(shouldSendIndicatorData) {
				masterDataModel.setIndicatorList(indicatorJpaRepository.findAll());
			}
			
			if(shouldSendSubgroupData) {
				masterDataModel.setSubgroupList(subgroupJpaRepository.findAll());
			}
			
			if(shouldSendSectorData) {
				masterDataModel.setSectorList(sectorJpaRepository.findAll());
			}
			
			if(shouldSendSourceData) {
				masterDataModel.setSourceList(sourceJpaRepository.findAll());			
			}
			
			
			masterDataModel.setSynchronizationDateMasterList(synchronizationDateMasterList);
			return masterDataModel;
		}catch(ParseException e) {
			e.printStackTrace();
			return null;
		}
	}


	

}
