package org.sdrc.rmnchadashboard.service;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sdrc.rmnchadashboard.jparepository.AreaJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.AreaLevelJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.DataJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.IndicatorJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SectorJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SourceJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SubgroupJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SynchronizationDateMasterRepository;
import org.sdrc.rmnchadashboard.jparepository.UnitJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SqlConfigurationServiceImpl implements SqlConfigurationService {

	@Autowired
	IndicatorJpaRepository indicatorJpaRepository;

	@Autowired
	AreaJpaRepository areaJpaRepository;

	@Autowired
	AreaLevelJpaRepository areaLevelJpaRepository;

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
	SynchronizationDateMasterRepository synchronizationDateMasterRepository;

	private Logger _log = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

	Map<String, org.sdrc.rmnchadashboard.jpadomain.Indicator> sqlIndicatorMap = new HashMap<>();

	Map<String, org.sdrc.rmnchadashboard.jpadomain.Unit> sqlUnitMap = new HashMap<>();

	Map<String, org.sdrc.rmnchadashboard.jpadomain.Source> sqlSourceMap = new HashMap<>();

	Map<String, org.sdrc.rmnchadashboard.jpadomain.Subgroup> sqlSubgroupMap = new HashMap<>();

	Map<String, org.sdrc.rmnchadashboard.jpadomain.Area> sqlAreaMap = new HashMap<>();

	//////////////////////////// ------------------------SQL
	//////////////////////////// CONFIGURATION--------------------------------------------
	@Transactional
	@Override
	public boolean configureSQLDb() {
		sqlSourceMap = new HashMap<>();
		sqlSubgroupMap = new HashMap<>();

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
		{
			org.sdrc.rmnchadashboard.jpadomain.Source source = new org.sdrc.rmnchadashboard.jpadomain.Source();
			source.setSourceName("NFHS");
			source.setSlugidsource(1);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);

			sqlSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			source = new org.sdrc.rmnchadashboard.jpadomain.Source();
			source.setSourceName("DLHS");
			source.setSlugidsource(2);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			source = new org.sdrc.rmnchadashboard.jpadomain.Source();
			source.setSourceName("RSOC");
			source.setSlugidsource(3);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			source = new org.sdrc.rmnchadashboard.jpadomain.Source();
			source.setSourceName("AHS");
			source.setSlugidsource(4);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			source = new org.sdrc.rmnchadashboard.jpadomain.Source();
			source.setSourceName("SRS");
			source.setSlugidsource(5);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			source = new org.sdrc.rmnchadashboard.jpadomain.Source();
			source.setSourceName("HMIS");
			source.setSlugidsource(6);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			source = new org.sdrc.rmnchadashboard.jpadomain.Source();
			source.setSourceName("Census");
			source.setSlugidsource(7);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			source = new org.sdrc.rmnchadashboard.jpadomain.Source();
			source.setSourceName("SSV");
			source.setSlugidsource(8);
			source.setCreatedDate(currentDate);
			source.setLastModified(currentDate);
			source = sourceJpaRepository.save(source);
			sqlSourceMap.put(source.getSourceName(), source);
			System.out.println(source);

			// Creating Sectors

			org.sdrc.rmnchadashboard.jpadomain.Sector sector = new org.sdrc.rmnchadashboard.jpadomain.Sector();
			sector.setSectorName("Reproductive");
			sector.setSlugidsector(1);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorJpaRepository.save(sector);

			System.out.println(sector);

			sector = new org.sdrc.rmnchadashboard.jpadomain.Sector();
			sector.setSectorName("Maternal");
			sector.setSlugidsector(2);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorJpaRepository.save(sector);
			System.out.println(sector);

			sector = new org.sdrc.rmnchadashboard.jpadomain.Sector();
			sector.setSectorName("Newborn");
			sector.setSlugidsector(3);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorJpaRepository.save(sector);
			System.out.println(sector);

			sector = new org.sdrc.rmnchadashboard.jpadomain.Sector();
			sector.setSectorName("Child");
			sector.setSlugidsector(4);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorJpaRepository.save(sector);
			System.out.println(sector);

			sector = new org.sdrc.rmnchadashboard.jpadomain.Sector();
			sector.setSectorName("Adolescent");
			sector.setSlugidsector(5);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorJpaRepository.save(sector);
			System.out.println(sector);

			sector = new org.sdrc.rmnchadashboard.jpadomain.Sector();
			sector.setSectorName("Health infrastructure");

			sector.setSlugidsector(6);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorJpaRepository.save(sector);
			System.out.println(sector);

			sector = new org.sdrc.rmnchadashboard.jpadomain.Sector();
			sector.setSectorName("Demography");

			sector.setSlugidsector(7);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorJpaRepository.save(sector);
			System.out.println(sector);

			sector = new org.sdrc.rmnchadashboard.jpadomain.Sector();
			sector.setSectorName("Human Resource");
			sector.setSlugidsector(8);
			sector.setCreatedDate(currentDate);
			sector.setLastModified(currentDate);
			sector = sectorJpaRepository.save(sector);
			System.out.println(sector);

			
			org.sdrc.rmnchadashboard.jpadomain.Subgroup subgroup = new org.sdrc.rmnchadashboard.jpadomain.Subgroup();
			subgroup.setSubgroupName("Total");
			subgroup.setSlugidsubgroup(1);
			subgroup.setCreatedDate(currentDate);
			subgroup.setLastModified(currentDate);
			subgroup = subgroupJpaRepository.save(subgroup);

			sqlSubgroupMap.put(subgroup.getSubgroupName(), subgroup);

			org.sdrc.rmnchadashboard.jpadomain.AreaLevel global = new org.sdrc.rmnchadashboard.jpadomain.AreaLevel();
			global.setLevel(1);
			global.setAreaLevelName("Global");
			global.setIsDistrictAvailable(false);
			global.setIsStateAvailable(false);
			global.setSlugidarealevel(1);
			global.setCreatedDate(currentDate);
			global.setLastModified(currentDate);
			areaLevelJpaRepository.save(global);

			org.sdrc.rmnchadashboard.jpadomain.AreaLevel national = new org.sdrc.rmnchadashboard.jpadomain.AreaLevel();
			national.setLevel(2);
			national.setAreaLevelName("National");
			national.setIsDistrictAvailable(false);
			national.setIsStateAvailable(false);
			national.setSlugidarealevel(2);
			national.setCreatedDate(currentDate);
			national.setLastModified(currentDate);
			areaLevelJpaRepository.save(national);

			org.sdrc.rmnchadashboard.jpadomain.AreaLevel state = new org.sdrc.rmnchadashboard.jpadomain.AreaLevel();
			state.setLevel(3);
			state.setAreaLevelName("State");
			state.setIsDistrictAvailable(false);
			state.setIsStateAvailable(true);
			state.setSlugidarealevel(3);
			state.setCreatedDate(currentDate);
			state.setLastModified(currentDate);
			areaLevelJpaRepository.save(state);

			org.sdrc.rmnchadashboard.jpadomain.AreaLevel district = new org.sdrc.rmnchadashboard.jpadomain.AreaLevel();
			district.setLevel(4);
			district.setAreaLevelName("District");
			district.setIsDistrictAvailable(true);
			district.setIsStateAvailable(true);
			district.setSlugidarealevel(4);
			district.setCreatedDate(currentDate);
			district.setLastModified(currentDate);
			areaLevelJpaRepository.save(district);

			org.sdrc.rmnchadashboard.jpadomain.AreaLevel nitiAayog = new org.sdrc.rmnchadashboard.jpadomain.AreaLevel();
			nitiAayog.setLevel(5);
			nitiAayog.setAreaLevelName("NITI Aayog");
			nitiAayog.setIsDistrictAvailable(true);
			nitiAayog.setIsStateAvailable(true);
			nitiAayog.setSlugidarealevel(5);
			nitiAayog.setCreatedDate(currentDate);
			nitiAayog.setLastModified(currentDate);
			areaLevelJpaRepository.save(nitiAayog);

			org.sdrc.rmnchadashboard.jpadomain.Unit unit = null;

			unit = new org.sdrc.rmnchadashboard.jpadomain.Unit();
			unit.setUnitName("Percent");
			unit.setSlugidunit(1);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new org.sdrc.rmnchadashboard.jpadomain.Unit();
			unit.setUnitName("Deaths per 1000 live births");
			unit.setSlugidunit(2);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new org.sdrc.rmnchadashboard.jpadomain.Unit();
			unit.setUnitName("Number");
			unit.setSlugidunit(3);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new org.sdrc.rmnchadashboard.jpadomain.Unit();
			unit.setUnitName("Rupees");
			unit.setSlugidunit(4);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new org.sdrc.rmnchadashboard.jpadomain.Unit();
			unit.setUnitName("Females per 1,000 males");
			unit.setSlugidunit(5);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new org.sdrc.rmnchadashboard.jpadomain.Unit();
			unit.setUnitName("Live births per woman");
			unit.setSlugidunit(6);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new org.sdrc.rmnchadashboard.jpadomain.Unit();
			unit.setUnitName("Deaths per 100,000 live births");
			unit.setSlugidunit(7);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new org.sdrc.rmnchadashboard.jpadomain.Unit();
			unit.setUnitName("Sq km");
			unit.setSlugidunit(8);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new org.sdrc.rmnchadashboard.jpadomain.Unit();
			unit.setUnitName("Per square kilometer");
			unit.setSlugidunit(9);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new org.sdrc.rmnchadashboard.jpadomain.Unit();
			unit.setUnitName("Males per 100 Females");
			unit.setSlugidunit(10);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

			unit = new org.sdrc.rmnchadashboard.jpadomain.Unit();
			unit.setUnitName("Persons per sq km");
			unit.setSlugidunit(11);
			unit.setCreatedDate(currentDate);
			unit.setLastModified(currentDate);
			unit = unitJpaRepository.save(unit);

			sqlUnitMap.put(unit.getUnitName(), unit);

		}
		importAreaIntoSQL(currentDate);

		XSSFWorkbook book = null;
		try {
			sqlIndicatorMap = new HashMap<>();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			URL url = loader.getResource("indicator/");
			String path = url.getPath().replaceAll("%20", " ");
			File files[] = new File(path).listFiles();
			book = new XSSFWorkbook(files[0]);
			int indicatorindex = 1;
			XSSFSheet sheet = book.getSheetAt(0);
			for (int row = 1; row <= sheet.getLastRowNum(); row++) {
				XSSFRow xssfRow = sheet.getRow(row);

				Iterator<Cell> cellItr = xssfRow.cellIterator();
				int cols = 0;
				String indicatorName = "";
				String sectorName = "";
				String isKPI = "", nitiaayog = "", thematicKPI = "", thematicSourceNational = "",
						thematicSourceState = "", thematicSourceDistrict = "";
				String unitC = "";
				boolean highIsGood = false, nuculor = false;
				String subgroupName = "";
				String sourceNational = "", sourceState = "", sourceDistrict = "", ssv = "", hmis = "";

				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();

					switch (cell.getColumnIndex()) {

					case 1:
						indicatorName = cell.getStringCellValue().trim();
						System.out.println(indicatorName);
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
							hmis = "Yes";

						break;
					}
					cols++;
				}

				List<org.sdrc.rmnchadashboard.jpadomain.Source> national = new ArrayList<org.sdrc.rmnchadashboard.jpadomain.Source>();
				List<org.sdrc.rmnchadashboard.jpadomain.Source> state = new ArrayList<org.sdrc.rmnchadashboard.jpadomain.Source>();
				List<org.sdrc.rmnchadashboard.jpadomain.Source> district = new ArrayList<org.sdrc.rmnchadashboard.jpadomain.Source>();
				List<org.sdrc.rmnchadashboard.jpadomain.Sector> sectors = new ArrayList<org.sdrc.rmnchadashboard.jpadomain.Sector>();

				List<org.sdrc.rmnchadashboard.jpadomain.Source> thematicNational = new ArrayList<org.sdrc.rmnchadashboard.jpadomain.Source>();
				List<org.sdrc.rmnchadashboard.jpadomain.Source> thematicState = new ArrayList<org.sdrc.rmnchadashboard.jpadomain.Source>();
				List<org.sdrc.rmnchadashboard.jpadomain.Source> thematicDistrict = new ArrayList<org.sdrc.rmnchadashboard.jpadomain.Source>();

				if (!sourceNational.isEmpty())
					national.add(sourceJpaRepository.findBySourceName(sourceNational));

				if (!sourceState.isEmpty())
					state.add(sourceJpaRepository.findBySourceName(sourceState));

				if (!sourceDistrict.isEmpty())
					district.add(sourceJpaRepository.findBySourceName(sourceDistrict));

				if (!thematicSourceNational.isEmpty())
					thematicNational.add(sourceJpaRepository.findBySourceName(thematicSourceNational));

				if (!thematicSourceState.isEmpty())
					thematicState.add(sourceJpaRepository.findBySourceName(thematicSourceState));

				if (!thematicSourceDistrict.isEmpty())
					thematicDistrict.add(sourceJpaRepository.findBySourceName(thematicSourceDistrict));

				sectors.add(sectorJpaRepository.findBySectorName(sectorName));

				org.sdrc.rmnchadashboard.jpadomain.Indicator indicator = new org.sdrc.rmnchadashboard.jpadomain.Indicator();
				indicator.setKpi((isKPI).equalsIgnoreCase("yes") ? true : false);
				indicator.setNitiaayog((nitiaayog).equalsIgnoreCase("yes") ? true : false);
				indicator.setiName(indicatorName);
				indicator.setSubgroup(subgroupJpaRepository.findBySubgroupName(subgroupName));
				indicator.setDistrict(district);
				indicator.setNational(national);
				indicator.setState(state);
				indicator.setSc(sectors);
				indicator.setHighisgood(highIsGood);
				indicator.setNucolor(nuculor);

				org.sdrc.rmnchadashboard.jpadomain.Unit unit = unitJpaRepository.findByUnitName(unitC.trim());

				indicator.setUnit(unit);
				indicator.setRecSector(sectorJpaRepository.findBySectorName(sectorName));

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
					indicator = indicatorJpaRepository.save(indicator);

					sqlIndicatorMap.put(indicator.getiName().toLowerCase(), indicator);
				}

			}

			importDataIntoSQLDb(currentDate);
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

	@Override
	@Transactional
	public boolean importAreaIntoSQL(Date currentDate) {
		{
			sqlAreaMap = new HashMap<>();
			int areaindex = 1;
			org.sdrc.rmnchadashboard.jpadomain.AreaLevel nitiAyogAreaLevel = areaLevelJpaRepository.findByLevel(5);
			XSSFWorkbook book = null;
			try {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				URL url = loader.getResource("area/");
				String path = url.getPath().replaceAll("%20", " ");
				File files[] = new File(path).listFiles();
				book = new XSSFWorkbook(files[0]);
				XSSFSheet sheet = book.getSheetAt(0);

				for (int row = 0; row <= sheet.getLastRowNum(); row++) {

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
					org.sdrc.rmnchadashboard.jpadomain.Area area = new org.sdrc.rmnchadashboard.jpadomain.Area();
					area.setCcode(areaCode);
					area.setCode(areaCode);
					area.setCreatedDate(currentDate);
					area.setLastModified(currentDate);
					area.setParentAreaCode(parentAreaCode.equals("null") ? "" : parentAreaCode);

					org.sdrc.rmnchadashboard.jpadomain.AreaLevel level = areaLevelJpaRepository
							.findByLevel(++areaLevel);

					// System.out.println("AreaLevel::" + level);
					List<org.sdrc.rmnchadashboard.jpadomain.AreaLevel> levels = new ArrayList<>();
					levels.add(level);
					if (nitiAyog)
						levels.add(nitiAyogAreaLevel);
					area.setAreaLevel(levels);
					area.setActAreaLevel(level);
					area.setAreaname(areaName);
					area.setSlugidarea((((int) (areaindex++))));

					area = areaJpaRepository.save(area);
					sqlAreaMap.put(area.getCode().toLowerCase(), area);
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

	public boolean importDataIntoSQLDb(Date currentDate) {

		int dataindex = 1;

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("data/");
		String path = url.getPath().replaceAll("%20", " ");
		File files[] = new File(path).listFiles();

		for (int f = 0; f < files.length; f++) {
			System.out.println(files[f].toString());
		}

		for (int f = 0; f < files.length; f++) {
			System.out.println("Importing From " + files[f].toString());
			XSSFWorkbook book = null;
			List<org.sdrc.rmnchadashboard.jpadomain.Data> datas = new ArrayList<>();
			int row = 1;
			try {

				book = new XSSFWorkbook(files[f]);

				XSSFSheet sheet = book.getSheetAt(0);

				for (row = 1; row <= sheet.getLastRowNum(); row++) {

					XSSFRow xssfRow = sheet.getRow(row);
					if (xssfRow == null) {
						break;
					}

					int cols = 0;
					String indicatorName = "", unitC = "", subgroupName = "", areaCode = "", tp = "", sourceName = "",
							tpshortName = "", value = "";
					Boolean dKPIRSrs = false, dNITIRSrs = false;
					Boolean dTHEMATICRSrs = false;

					org.sdrc.rmnchadashboard.jpadomain.Unit unit = null;
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

						case 10:
							// sector = cell.getStringCellValue().trim();
							break;
						case 11:
							// highisgood = cell.getBooleanCellValue();
							break;

						default:
							break;

						}
						cols++;
					}

					// org.sdrc.rmnchadashboard.jpadomain.Indicator indicator =
					// indicatorJpaRepository
					// .findByIName(indicatorName);
					org.sdrc.rmnchadashboard.jpadomain.Indicator indicator = sqlIndicatorMap
							.get(indicatorName.toLowerCase());

					if (indicator == null) {
						if ((files[f].getName().equals("SRS_Compilation.xlsx") && row == 1824)|| (files[f].getName().equals("HMIS_Part_7_Sept'18.xlsx") && row == 22768)) {
							break;
						} else {
							throw new NullPointerException("Indicator Not Found:::" + indicatorName);
						}

					} else if (!indicatorName.toLowerCase().equals(indicator.getiName().toLowerCase())) {
						throw new NullPointerException("Indicator MisMatch \n" + "Actual Indicator:::" + indicatorName
								+ "\nFound:::" + indicator.getiName());
					}

					else {

						unit = sqlUnitMap.get(unitC);

						org.sdrc.rmnchadashboard.jpadomain.Area area = sqlAreaMap.get(areaCode.toLowerCase());
						if (area == null) {
							throw new NullPointerException("Area cannot be null::" + areaCode);
						}

						org.sdrc.rmnchadashboard.jpadomain.Subgroup subgroup = sqlSubgroupMap.get(subgroupName);

						if (subgroup == null) {
							throw new NullPointerException("subgroup cannot be null::" + subgroup);
						}

						org.sdrc.rmnchadashboard.jpadomain.Source source = sqlSourceMap.get(sourceName);

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
								if (source.getSlugidsource() == indicator.getThematicNational().get(0)
										.getSlugidsource()) {
									dTHEMATICRSrs = true;
								}
							} else if (area.getActAreaLevel().getLevel() == 3) {
								// state
								if (source.getSlugidsource() == indicator.getThematicState().get(0).getSlugidsource()) {
									dTHEMATICRSrs = true;
								}
							} else if (area.getActAreaLevel().getLevel() == 4) {
								// district
								if (source.getSlugidsource() == indicator.getThematicDistrict().get(0)
										.getSlugidsource()) {
									dTHEMATICRSrs = true;
								}
							}
						}

						org.sdrc.rmnchadashboard.jpadomain.Data data = new org.sdrc.rmnchadashboard.jpadomain.Data();
						data.setdKPIRSrs(dKPIRSrs);
						data.setdNITIRSrs(dNITIRSrs);
						data.setdTHEMATICRSrs(dTHEMATICRSrs);

						data.setArea(area);
						data.setBelow(new ArrayList<org.sdrc.rmnchadashboard.jpadomain.Area>());
						data.setTop(new ArrayList<org.sdrc.rmnchadashboard.jpadomain.Area>());
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

					}
				}
				if (!datas.isEmpty())
					dataJpaRepository.save(datas);
				System.out.println("DATA LOOP FINISHED: " + dataindex);
				book.close();
			} catch (Exception e) {
				System.out.println("Exception in row : " + row + "\n" + e.getStackTrace());
				// e.printStackTrace();
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

	@Override
	public Boolean calculateTopButtomForSQLDb() {

		Iterable<org.sdrc.rmnchadashboard.jpadomain.Indicator> indicators = indicatorJpaRepository.findAll();

		indicators.forEach(indicator -> {
			System.out.println(indicator);
			List<org.sdrc.rmnchadashboard.jpadomain.Data> datass = new ArrayList<>();
			Iterable<org.sdrc.rmnchadashboard.jpadomain.Data> datas;
			Map<String, List<org.sdrc.rmnchadashboard.jpadomain.Data>> map = new HashMap<>();

			datas = dataJpaRepository.findByIndicator(indicator);
			if (datas != null) {
				datas.forEach(datass::add);

				List<org.sdrc.rmnchadashboard.jpadomain.Data> dddd = new ArrayList<>();
				datass.forEach(data -> {
					// System.out.println(data);
					// we are skipping all districts coz we dont want to calculate top and below
					// json
					if ((data.getArea().getActAreaLevel().getLevel() != 4 && data.getIndicator().getKpi() == true)
							|| data.getIndicator().getNitiaayog() == true) {

						List<org.sdrc.rmnchadashboard.jpadomain.Area> topAndEqualValueAreas = new ArrayList<>();
						List<org.sdrc.rmnchadashboard.jpadomain.Area> buttomValueAreas = new ArrayList<>();
						List<org.sdrc.rmnchadashboard.jpadomain.Data> children = dataJpaRepository
								.findByIndicatorAndAreaParentAreaCodeAndSrcAndTp(data.getIndicator(),
										data.getArea().getCode(), data.getSrc(), data.getTp());
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
							for (org.sdrc.rmnchadashboard.jpadomain.Data dataObj : children) {
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

							List<org.sdrc.rmnchadashboard.jpadomain.Data> d = new ArrayList<>();
							d.add(data);
							map.put(data.getIndicator().getiName() + ":" + data.getArea().getAreaname() + ":"
									+ data.getArea().getCode() + ":"
									+ data.getArea().getActAreaLevel().getAreaLevelName() + ":" + data.getSrc().getId()
									+ ":" + data.getTp(), d);
						}

					}

				});
				if (!dddd.isEmpty())
					dataJpaRepository.save(dddd);
			}
		});

		return true;

	}

	@Override
	@Transactional
	public Boolean calculateRankForSQLDb() {

		Iterable<org.sdrc.rmnchadashboard.jpadomain.Indicator> indicators = indicatorJpaRepository.findAll();

		indicators.forEach(indicator -> {
			Map<String, List<org.sdrc.rmnchadashboard.jpadomain.Data>> map = new LinkedHashMap<>();

			List<org.sdrc.rmnchadashboard.jpadomain.Data> datas = null;

			Map<String, List<org.sdrc.rmnchadashboard.jpadomain.Data>> indicatorTpSrcAreaLevelMap = new LinkedHashMap<>();

			datas = new ArrayList<>();

			Iterable<org.sdrc.rmnchadashboard.jpadomain.Data> itrDatas = null;

			itrDatas = dataJpaRepository.findByIndicator(indicator);

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
							
							List<org.sdrc.rmnchadashboard.jpadomain.Data> dd = indicatorTpSrcAreaLevelMap.getOrDefault(
										datas.get(index).getIndicator().getiName() 
										+ ":" + datas.get(index).getSrc().getSourceName()
										+ ":" + datas.get(index).getArea().getActAreaLevel().getAreaLevelName() 
										+ ":" + datas.get(index).getArea().getParentAreaCode() + ":" + datas.get(index).getTp(),
								new ArrayList<>());
							
							dd.add(datas.get(index));
							
							indicatorTpSrcAreaLevelMap.put(
								datas.get(index).getIndicator().getiName() 
								+ ":" + datas.get(index).getSrc().getSourceName()
								+ ":" + datas.get(index).getArea().getActAreaLevel().getAreaLevelName() 
								+ ":" + datas.get(index).getArea().getParentAreaCode() + ":" + datas.get(index).getTp(),
								dd);
							// @formatter:on
					}
				}
				indicatorTpSrcAreaLevelMap.forEach((k, v) -> {

					List<org.sdrc.rmnchadashboard.jpadomain.Data> modChilren = new ArrayList<>();

					modChilren.addAll(v);

					if (modChilren.get(0).getIndicator().getHighisgood() == false) {
						modChilren.sort(new Comparator<org.sdrc.rmnchadashboard.jpadomain.Data>() {

							@Override
							public int compare(org.sdrc.rmnchadashboard.jpadomain.Data o1,
									org.sdrc.rmnchadashboard.jpadomain.Data o2) {
								return new Double(o1.getValue()).compareTo(new Double(o2.getValue()));
							}
						});
					} else {
						modChilren.sort(new Comparator<org.sdrc.rmnchadashboard.jpadomain.Data>() {

							@Override
							public int compare(org.sdrc.rmnchadashboard.jpadomain.Data o1,
									org.sdrc.rmnchadashboard.jpadomain.Data o2) {
								return new Double(o2.getValue()).compareTo(new Double(o1.getValue()));
							}
						});
					}

					Map<String, List<org.sdrc.rmnchadashboard.jpadomain.Data>> rankMapForArea = new LinkedHashMap<String, List<org.sdrc.rmnchadashboard.jpadomain.Data>>();
					for (int index = 0; index < modChilren.size(); index++) {

						List<org.sdrc.rmnchadashboard.jpadomain.Data> dd = rankMapForArea
								.getOrDefault(modChilren.get(index).getValue(), new ArrayList<>());
						dd.add(modChilren.get(index));
						rankMapForArea.put(modChilren.get(index).getValue(), dd);

					}
					int rank = 1;
					List<org.sdrc.rmnchadashboard.jpadomain.Data> fullRankedList = new ArrayList<org.sdrc.rmnchadashboard.jpadomain.Data>();
					Iterator<List<org.sdrc.rmnchadashboard.jpadomain.Data>> iterator = rankMapForArea.values()
							.iterator();
					while (iterator.hasNext()) {
						List<org.sdrc.rmnchadashboard.jpadomain.Data> dd = iterator.next();
						for (int index2 = 0; index2 < dd.size(); index2++) {
							dd.get(index2).setRank(rank);
							fullRankedList.add(dd.get(index2));
						}
						rank++;
					}
					map.put(k, fullRankedList);

				});

				Iterator<List<org.sdrc.rmnchadashboard.jpadomain.Data>> iterator = map.values().iterator();
				while (iterator.hasNext()) {
					List<org.sdrc.rmnchadashboard.jpadomain.Data> dd = iterator.next();
					dataJpaRepository.save(dd);
				}
			}
		});
		return null;
	}

	@Override
	@Transactional
	public Boolean calculateTrendForSQLDb() {

		Iterable<org.sdrc.rmnchadashboard.jpadomain.Indicator> indicators = indicatorJpaRepository.findAll();

		indicators.forEach(indicator -> {
			List<org.sdrc.rmnchadashboard.jpadomain.Data> allData = new ArrayList<>();
			List<org.sdrc.rmnchadashboard.jpadomain.Data> datas = null;
			Iterable<org.sdrc.rmnchadashboard.jpadomain.Data> itrDatas = null;

			// Map<String, List<org.sdrc.rmnchadashboard.jpadomain.Data>> map = new
			// LinkedHashMap<>();
			Map<String, List<org.sdrc.rmnchadashboard.jpadomain.Data>> indicatorTpSrcAreaLevelMap = new LinkedHashMap<>();

			datas = new ArrayList<>();

			itrDatas = dataJpaRepository.findByIndicator(indicator);

			if (itrDatas != null) {
				itrDatas.forEach(datas::add);

				for (int index = 0; index < datas.size(); index++) {

					List<org.sdrc.rmnchadashboard.jpadomain.Data> dd = indicatorTpSrcAreaLevelMap.getOrDefault(
							datas.get(index).getIndicator().getiName() + ":" + datas.get(index).getSrc().getSourceName()
									+ ":" + datas.get(index).getArea().getActAreaLevel().getAreaLevelName() + ":"
									+ datas.get(index).getArea().getCode(),
							new ArrayList<>());

					dd.add(datas.get(index));

					indicatorTpSrcAreaLevelMap.put(
							datas.get(index).getIndicator().getiName() + ":" + datas.get(index).getSrc().getSourceName()
									+ ":" + datas.get(index).getArea().getActAreaLevel().getAreaLevelName() + ":"
									+ datas.get(index).getArea().getCode(),
							dd);
				}

				indicatorTpSrcAreaLevelMap.forEach((k, v) -> {

					List<org.sdrc.rmnchadashboard.jpadomain.Data> dddd = new ArrayList<>();
					List<org.sdrc.rmnchadashboard.jpadomain.Data> modChilren = new ArrayList<>();
					modChilren.addAll(v);
					modChilren.sort(new Comparator<org.sdrc.rmnchadashboard.jpadomain.Data>() {
						@Override
						public int compare(org.sdrc.rmnchadashboard.jpadomain.Data o1,
								org.sdrc.rmnchadashboard.jpadomain.Data o2) {
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
				dataJpaRepository.save(allData);
			}
		});

		return true;

	}

	@Override
	public Boolean calculateRankTrendTopButtomSQLDb() {
		// calculateRankForSQLDb();
		// calculateTrendForSQLDb();
		calculateTopButtomForSQLDb();
		return true;
	}
}
