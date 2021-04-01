/**
 * 
 */
package org.sdrc.rmnchadashboard.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.sdrc.rmnchadashboard.jpadomain.Area;
import org.sdrc.rmnchadashboard.jpadomain.Data;
import org.sdrc.rmnchadashboard.jpadomain.DataSyncMaster;
import org.sdrc.rmnchadashboard.jpadomain.Indicator;
import org.sdrc.rmnchadashboard.jpadomain.Source;
import org.sdrc.rmnchadashboard.jpadomain.Subgroup;
import org.sdrc.rmnchadashboard.jpadomain.SynchronizationDateMaster;
import org.sdrc.rmnchadashboard.jpadomain.Unit;
import org.sdrc.rmnchadashboard.jparepository.AreaJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.AreaLevelJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.DataJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.DataSyncMasterRepository;
import org.sdrc.rmnchadashboard.jparepository.IndicatorJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SectorJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SourceJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SubgroupJpaRepository;
import org.sdrc.rmnchadashboard.jparepository.SynchronizationDateMasterRepository;
import org.sdrc.rmnchadashboard.jparepository.UnitJpaRepository;
import org.sdrc.rmnchadashboard.model.DataSyncStatusEnum;
import org.sdrc.rmnchadashboard.model.MasterDataModel;
import org.sdrc.rmnchadashboard.model.MasterDataSyncModel;
import org.sdrc.rmnchadashboard.model.RequestModel;
import org.sdrc.rmnchadashboard.model.ResponseModel;
import org.sdrc.rmnchadashboard.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Harsh Pratyush
 * @email harsh@sdrc.co.in
 *
 */

@Service
public class MobileSyncServiceImpl implements MobileSyncService {

	@Autowired
	IndicatorJpaRepository indicatorJpaRepository;

	@Autowired
	AreaJpaRepository areaJpaRepository;

	@Autowired
	private SourceJpaRepository sourceJpaRepository;

	@Autowired
	private DataJpaRepository dataJpaRepository;

	@Autowired
	private DataSyncMasterRepository dataSyncMasterRepository;

	@Autowired
	private AreaLevelJpaRepository areaLevelJpaRepository;

	@Autowired
	private SubgroupJpaRepository subgroupJpaRepository;

	@Autowired
	private SectorJpaRepository sectorJpaRepository;

	@Autowired
	private UnitJpaRepository unitJpaRepository;

	@Autowired
	private SynchronizationDateMasterRepository synchronizationDateMasterRepository;

	Map<String, Indicator> sqlIndicatorMap = new HashMap<>();

	Map<String, Area> sqlAreaMap = new HashMap<>();

	Map<String, Source> sqlSourceMap = new HashMap<>();

	private final Path rootLocation = Paths.get("/rmncha-data");

	private final Path jsonDataLocation = Paths.get("/json-data");

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY-hh-mm-ss");
	private final SimpleDateFormat sdfDb = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
	
	private Logger log = LoggerFactory.getLogger(MobileSyncServiceImpl.class);
	
	@Value("${authorization.key.fcm}")
    String authorizationKey;
	
//	private final Path rootPath = Paths.get("/");

	@Override
	public ResponseModel validateData(String excelPath) {

		ResponseModel responseModel = new ResponseModel();
		List<Indicator> indicators = indicatorJpaRepository.findAll();
		List<Area> areas = areaJpaRepository.findAllByOrderByActAreaLevelIdAsc();
		List<Source> sources = sourceJpaRepository.findAll();
		indicators.forEach(indicator -> {
			sqlIndicatorMap.put(indicator.getiName().toLowerCase().trim(), indicator);
		});

		areas.forEach(area -> {
			if (area.getActAreaLevel().getSlugidarealevel() <= Constants.State_Area_Level) {
				sqlAreaMap.put(area.getAreaname().toLowerCase(), area);
				if (area.getActAreaLevel().getSlugidarealevel() == Constants.State_Area_Level) {
					sqlAreaMap.put(area.getCode(), area);
				}
			} else {
				sqlAreaMap.put(area.getAreaname().toLowerCase().trim() + "_"
						+ sqlAreaMap.get(area.getParentAreaCode()).getAreaname().toLowerCase().trim(), area);
			}
		});

		sources.forEach(source -> {
			sqlSourceMap.put(source.getSourceName().toLowerCase().trim(), source);
		});

		try {
			FileInputStream fileInputStream = new FileInputStream(excelPath);
			boolean validated = true;

			XSSFWorkbook book = new XSSFWorkbook(fileInputStream);

			XSSFSheet sheet = book.getSheet(Constants.DATA_SHEET);
			if (sheet != null) {

				CellStyle style = book.createCellStyle();
				style.setBorderTop(BorderStyle.THIN);
				style.setTopBorderColor(IndexedColors.BLACK.getIndex());
				style.setBorderBottom(BorderStyle.THIN);
				style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				style.setBorderLeft(BorderStyle.THIN);
				style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				style.setBorderRight(BorderStyle.THIN);
				style.setRightBorderColor(IndexedColors.BLACK.getIndex());
				style.setFillForegroundColor(IndexedColors.RED.index);
				style.setLocked(true);
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				if (sheet.getRow(0) == null || sheet.getLastRowNum() <= 1 || sheet.getRow(1) == null) {

					responseModel.setFilePath(excelPath);
					responseModel.setFileName(new File(excelPath).getName());
					responseModel.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
					responseModel.setMessage(Constants.BLANK_DATA_SHEET);

				}
				XSSFRow row = sheet.getRow(0);
				row.createCell(6).setCellValue("Remarks");

				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					String remarks = "";
					XSSFCell timeCell = sheet.getRow(i).getCell(0);
					XSSFCell stateCell = sheet.getRow(i).getCell(1);
					XSSFCell districtCell = sheet.getRow(i).getCell(2);
					XSSFCell indicatorCell = sheet.getRow(i).getCell(3);
					XSSFCell dataCell = sheet.getRow(i).getCell(4);
					XSSFCell sourceCell = sheet.getRow(i).getCell(5);

					XSSFCell remarkCell = sheet.getRow(i).createCell(6);
					if(timeCell==null)
						timeCell=sheet.getRow(i).createCell(0);
					
					if(stateCell==null)
						stateCell=sheet.getRow(i).createCell(1);
					
					if(districtCell==null)
						districtCell=sheet.getRow(i).createCell(2);
					
					if(indicatorCell==null)
						indicatorCell=sheet.getRow(i).createCell(3);
					
					if(dataCell==null)
						dataCell=sheet.getRow(i).createCell(4);
					
					if(sourceCell==null)
						sourceCell=sheet.getRow(i).createCell(5);

					if (timeCell == null || timeCell.getCellType() == Cell.CELL_TYPE_BLANK) {
						validated = false;
						remarks += Constants.BLANK_TIME_PERIOD;
					}
					if ((stateCell == null || stateCell.getCellType() == Cell.CELL_TYPE_BLANK)
							&& (districtCell != null && districtCell.getCellType() != Cell.CELL_TYPE_BLANK
									&& !districtCell.getStringCellValue().trim().equalsIgnoreCase(""))) {
						validated = false;
						remarks += Constants.BLANK_STATE;
					}

					if (stateCell != null && stateCell.getCellType() != Cell.CELL_TYPE_BLANK
							&& !sqlAreaMap.containsKey(stateCell.getStringCellValue().toLowerCase().trim())) {
						validated = false;
						remarks += Constants.INVALID_STATE_NAME;

					}

					if (districtCell != null && districtCell.getCellType() != Cell.CELL_TYPE_BLANK
							&& !districtCell.getStringCellValue().trim().equalsIgnoreCase("") && stateCell != null
							&& stateCell.getCellType() != Cell.CELL_TYPE_BLANK) {

						if (!sqlAreaMap.containsKey(districtCell.getStringCellValue().toLowerCase().trim() + "_"
								+ stateCell.getStringCellValue().toLowerCase().trim())) {
							validated = false;
							remarks += Constants.INVALID_DISTRICT_NAME;
						}
					}

					if (indicatorCell == null || indicatorCell.getCellType() == Cell.CELL_TYPE_BLANK) {
						validated = false;
						remarks += Constants.BLANK_INDICATOR;
					}

					if (indicatorCell != null && indicatorCell.getCellType() != Cell.CELL_TYPE_BLANK
							&& !sqlIndicatorMap.containsKey(indicatorCell.getStringCellValue().toLowerCase().trim())) {
						validated = false;
						remarks += Constants.INVALID_INDIACTOR_NAME;
					}

					if (dataCell == null || dataCell.getCellType() == Cell.CELL_TYPE_BLANK) {
						validated = false;
						remarks += Constants.BLANK_DATA_VALUE;
					}

					if (dataCell != null && dataCell.getCellType() != Cell.CELL_TYPE_BLANK) {
						if (dataCell.getCellType() == Cell.CELL_TYPE_STRING) {
							try {
								Double.parseDouble(dataCell.getStringCellValue());
							} catch (NumberFormatException e) {
								validated = false;
								remarks += Constants.INVALID_TYPE_DATA_VALUE;
							}
						}

					}
					
					//Checking data value error
					try {
						String value = (dataCell.getCellType() == Cell.CELL_TYPE_NUMERIC) ? String.valueOf(dataCell.getNumericCellValue()) : dataCell.getStringCellValue();
					} catch(IllegalStateException ise) {
						validated = false;
						remarks += Constants.INVALID_TYPE_DATA_VALUE;
					}

					if (sourceCell == null || sourceCell.getCellType() == Cell.CELL_TYPE_BLANK) {
						validated = false;
						remarks += Constants.BLANK_SOURCE;
					}

					if (sourceCell != null && sourceCell.getCellType() != Cell.CELL_TYPE_BLANK
							&& !sqlSourceMap.containsKey(sourceCell.getStringCellValue().toLowerCase().trim())) {
						validated = false;
						remarks += Constants.SOURCE_NOT_FOUND;
					}

					remarkCell.setCellValue(remarks);
					if (!remarks.equalsIgnoreCase("")) {
						timeCell.setCellStyle(style);
						stateCell.setCellStyle(style);
						districtCell.setCellStyle(style);
						indicatorCell.setCellStyle(style);
						dataCell.setCellStyle(style);
						sourceCell.setCellStyle(style);

						remarkCell.setCellStyle(style);
					}

				}
				if (book.getSheet(Constants.VALIDATE_SHEET) == null)
					sheet = book.createSheet(Constants.VALIDATE_SHEET);
				else
					sheet = book.getSheet(Constants.VALIDATE_SHEET);
				sheet.createRow(0).createCell(0).setCellValue(validated);
				book.setSheetHidden(book.getSheetIndex(sheet), HSSFWorkbook.SHEET_STATE_VERY_HIDDEN);

				String filePath = this.rootLocation
						.resolve(new File(excelPath).getName().replaceAll(".xlsx", "") + new Date().getTime() + ".xlsx")
						.toAbsolutePath().toString();
				FileOutputStream fileOutputStream = new FileOutputStream(filePath);
				book.write(fileOutputStream);
				book.close();

				responseModel.setFilePath(filePath);
				responseModel.setFileName(new File(filePath).getName());

				if (validated) {
					responseModel.setStatusCode(HttpStatus.OK.value());
					responseModel.setMessage(Constants.TEMPLATE_VALIDATED);
				}

				else {
					responseModel.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
					responseModel.setMessage(Constants.TEMPLATE_HAS_ERROR);
				}

			}

			else {
				responseModel.setFilePath(excelPath);
				responseModel.setFileName(new File(excelPath).getName());
				responseModel.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
				responseModel.setMessage(Constants.INVALID_TEMPLATE);
			}

		} catch (Exception e) {
			e.printStackTrace();
			responseModel.setFilePath(excelPath);
			responseModel.setFileName(new File(excelPath).getName());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseModel.setMessage(Constants.INTERNAL_SERVER_ERROR);
		}
		return responseModel;
	}

	@Override
	@Transactional
	public ResponseModel importNewData(RequestModel requestModel) {
		ResponseModel responseModel = new ResponseModel();
		try {
			FileInputStream fileInputStream = new FileInputStream(requestModel.getExcelPath());

			XSSFWorkbook book = new XSSFWorkbook(fileInputStream);
			List<Indicator> updatedIndicator = new ArrayList<Indicator>();
			Map<String, Set<String>> updatedIndicatorTimePeriodMap = new HashMap<String, Set<String>>();
			Set<String> updatedTP = new HashSet<String>();
			XSSFSheet sheet = book.getSheet(Constants.VALIDATE_SHEET);

			if (sheet != null && sheet.getRow(0) != null && sheet.getRow(0).getCell(0) != null
					&& sheet.getRow(0).getCell(0).getBooleanCellValue()) {
				List<Indicator> indicators = indicatorJpaRepository.findAll();
				List<Area> areas = areaJpaRepository.findAllByOrderByActAreaLevelIdAsc();
				List<Source> sources = sourceJpaRepository.findAll();
				indicators.forEach(indicator -> {
					this.sqlIndicatorMap.put(indicator.getiName().toLowerCase().trim(), indicator);
				});

				areas.forEach(area -> {
					if (area.getActAreaLevel().getSlugidarealevel() <= Constants.State_Area_Level) {
						this.sqlAreaMap.put(area.getAreaname().toLowerCase(), area);
						this.sqlAreaMap.put(area.getCode(), area);
					} else {
						this.sqlAreaMap.put(area.getAreaname().toLowerCase().trim() + "_"
								+ this.sqlAreaMap.get(area.getParentAreaCode()).getAreaname().toLowerCase().trim(),
								area);
					}
				});

				sources.forEach(source -> {
					this.sqlSourceMap.put(source.getSourceName().toLowerCase().trim(), source);
				});
				
				sendNotfication(new ResponseModel(HttpStatus.ACCEPTED.value(),Constants.EXCEL_READ_START), requestModel.getFcmToken());
				XSSFSheet dataSheet = book.getSheet(Constants.DATA_SHEET);
				sendNotfication(new ResponseModel(HttpStatus.ACCEPTED.value(),Constants.EXCEL_LOAED), requestModel.getFcmToken());
				if (dataSheet != null) {

					System.out.println("search query");
					List<Data> oldData = dataJpaRepository.findAll();

					Map<String, Data> sqlDataMap = new HashMap<String, Data>();
					oldData.forEach(data -> {
						sqlDataMap.put(data.getArea().getCode() + ":" + data.getIndicator().getiName() + ":"
								+ data.getTp().toLowerCase() + ":" + data.getSrc().getSourceName(), data);
					});

					Data data;
					long dataindex = dataJpaRepository.findMaxSlugId() + 1;
					Map<String, Data> newSqlDataMap = new HashMap<String, Data>();
					System.out.println("Inserting datas");
					for (int i = 1; i <= dataSheet.getLastRowNum(); i++) {
						XSSFCell timeCell = dataSheet.getRow(i).getCell(0);
						XSSFCell stateCell = dataSheet.getRow(i).getCell(1);
						XSSFCell districtCell = dataSheet.getRow(i).getCell(2);
						XSSFCell indicatorCell = dataSheet.getRow(i).getCell(3);
						XSSFCell dataCell = dataSheet.getRow(i).getCell(4);
						XSSFCell sourceCell = dataSheet.getRow(i).getCell(5);

						boolean dKPIRSrs = false, dNITIRSrs = false;
						boolean dTHEMATICRSrs = false;

						Area area;
						Indicator indicator = this.sqlIndicatorMap
								.get(indicatorCell.getStringCellValue().toLowerCase());
						Source source = this.sqlSourceMap.get(sourceCell.getStringCellValue().toLowerCase());

						if (districtCell != null && districtCell.getCellType() != Cell.CELL_TYPE_BLANK
								&& !districtCell.getStringCellValue().trim().equalsIgnoreCase("")) {
							area = this.sqlAreaMap.get(districtCell.getStringCellValue().toLowerCase().trim() + "_"
									+ stateCell.getStringCellValue().toLowerCase().trim());
						} else if (stateCell != null && stateCell.getCellType() != Cell.CELL_TYPE_BLANK
								&& !stateCell.getStringCellValue().trim().equalsIgnoreCase("")) {
							area = this.sqlAreaMap.get(stateCell.getStringCellValue().toLowerCase().trim());
						} else {
							area = this.sqlAreaMap.get("IND");
						}

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
						Unit unit = indicator.getUnit();
						Subgroup subgroup = indicator.getSubgroup();
						String ius = indicator.getiName().concat(":").concat(unit.getUnitName()).concat(":")
								.concat(subgroup.getSubgroupName());

						if (sqlDataMap.containsKey(area.getCode() + ":" + indicator.getiName() + ":"
								+ (timeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
										? String.valueOf((int)timeCell.getNumericCellValue()).toLowerCase()
												: timeCell.getStringCellValue().toLowerCase()) + ":" + source.getSourceName())) {
							data = sqlDataMap.get(area.getCode() + ":" + indicator.getiName() + ":"
									+ (timeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
									? String.valueOf((int)timeCell.getNumericCellValue()).toLowerCase()
											: timeCell.getStringCellValue().toLowerCase()) + ":" + source.getSourceName());
							String value = dataCell.getCellType() == Cell.CELL_TYPE_NUMERIC
									? String.valueOf(dataCell.getNumericCellValue())
									: dataCell.getStringCellValue();
							if (data.getValue().trim().equalsIgnoreCase(value)) {
								continue;
							}
						} else
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
						data.setValue(dataCell.getCellType() == Cell.CELL_TYPE_NUMERIC
								? String.valueOf(dataCell.getNumericCellValue())
								: dataCell.getStringCellValue());
						
					
						data.setTp(timeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
								? String.valueOf((int)timeCell.getNumericCellValue())
								: timeCell.getStringCellValue());
						data.setTps("");
						data.setSlugiddata(dataindex++);
						data.setPeriodicity("Yearly");

						updatedTP.add(timeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
								? String.valueOf((int)timeCell.getNumericCellValue())
								: timeCell.getStringCellValue());
						if (updatedIndicatorTimePeriodMap.containsKey(indicator.getiName())) {
							updatedIndicatorTimePeriodMap.get(indicator.getiName()).add(timeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
									? String.valueOf((int)timeCell.getNumericCellValue())
											: timeCell.getStringCellValue());
						} else {
							Set<String> timeSet = new HashSet<String>();
							timeSet.add(timeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
									? String.valueOf((int)timeCell.getNumericCellValue())
											: timeCell.getStringCellValue());
							updatedIndicatorTimePeriodMap.put(indicator.getiName(), timeSet);
							updatedIndicator.add(indicator);
						}

						sqlDataMap.put(
								area.getCode() + ":" + indicator.getiName() + ":"
										+(timeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
										? String.valueOf((int)timeCell.getNumericCellValue()).toLowerCase()
												: timeCell.getStringCellValue().toLowerCase()) + ":" + source.getSourceName(),
								data);
						newSqlDataMap.put(
								area.getCode() + ":" + indicator.getiName() + ":"
										+ (timeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
										? String.valueOf((int)timeCell.getNumericCellValue()).toLowerCase()
												: timeCell.getStringCellValue().toLowerCase()) + ":" + source.getSourceName(),
								data);

					}

					List<Data> sortedData = newSqlDataMap.values().stream().collect(Collectors.toList());

					sortedData.sort(new Comparator<Data>() {

						@Override
						public int compare(org.sdrc.rmnchadashboard.jpadomain.Data o1,
								org.sdrc.rmnchadashboard.jpadomain.Data o2) {
							return new Double(o1.getSlugiddata()).compareTo(new Double(o2.getSlugiddata()));
						}
					});
					if (newSqlDataMap.values().size() == 0) {
						responseModel.setStatusCode(HttpStatus.NOT_MODIFIED.value());
						responseModel.setMessage(Constants.NO_DATA_SYNC);
						book.close();
						return responseModel;
					}

					sendNotfication(new ResponseModel(HttpStatus.ACCEPTED.value(),Constants.INSERTING_DATA_DB), requestModel.getFcmToken());
					dataJpaRepository.save(sortedData);
					sendNotfication(new ResponseModel(HttpStatus.ACCEPTED.value(),Constants.DATA_INSERTED), requestModel.getFcmToken());
					
					List<Data> datas = dataJpaRepository.findByIndicatorInOrderBySlugiddataAsc(updatedIndicator);
					SynchronizationDateMaster dataSync = synchronizationDateMasterRepository
							.findByTableName(Constants.DATA_TABLE);
					dataSync.setLastModifiedDate(new Date());
					synchronizationDateMasterRepository.save(dataSync);
					Map<Integer, List<Data>> sqlDataMapForCalculation = new HashMap<Integer, List<Data>>();
					datas.forEach(d -> {
						if (sqlDataMapForCalculation.containsKey(d.getIndicator().getId())) {
							sqlDataMapForCalculation.get(d.getIndicator().getId()).add(d);
						} else {
							List<Data> data1 = new ArrayList<Data>();
							data1.add(d);
							sqlDataMapForCalculation.put(d.getIndicator().getId(), data1);

						}

					});
					sendNotfication(new ResponseModel(HttpStatus.ACCEPTED.value(),Constants.CALCULATING_RANK), requestModel.getFcmToken());
					calculateRank(sqlDataMapForCalculation, updatedIndicator, updatedIndicatorTimePeriodMap);
					sendNotfication(new ResponseModel(HttpStatus.ACCEPTED.value(),Constants.CALCULATING_RANK_DONE), requestModel.getFcmToken());

					sendNotfication(new ResponseModel(HttpStatus.ACCEPTED.value(),Constants.CALCULATING_TREND), requestModel.getFcmToken());
					calculateTrendForSQLDb(sqlDataMapForCalculation, updatedIndicator, updatedIndicatorTimePeriodMap);
					sendNotfication(new ResponseModel(HttpStatus.ACCEPTED.value(),Constants.TREND_CALCULATION_DONE), requestModel.getFcmToken());

					sendNotfication(new ResponseModel(HttpStatus.ACCEPTED.value(),Constants.TOP_BOTTOM_START), requestModel.getFcmToken());
					calculateTopButtomForSQLDb(sqlDataMapForCalculation, updatedIndicator,
							updatedIndicatorTimePeriodMap);
					sendNotfication(new ResponseModel(HttpStatus.ACCEPTED.value(),Constants.TOP_BOTTOM_DONE), requestModel.getFcmToken());

					responseModel.setStatusCode(HttpStatus.OK.value());
					responseModel.setMessage(Constants.SUCCESS_FILE);
				} else {
					responseModel.setMessage(Constants.BLANK_DATA_SHEET);
					responseModel.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());

				}

			} else {
				responseModel.setMessage(Constants.NOT_VALIDATED_SHEET);
				responseModel.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
			}

			book.close();
		} catch (Exception e) {
			e.printStackTrace();
			responseModel.setFilePath(e.getLocalizedMessage()+":"+e.getMessage());
			responseModel.setMessage(Constants.INTERNAL_SERVER_ERROR);
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		}

		return responseModel;

	}

	/**
	 * @author Sourav Keshari Nath Generate the template data excel sheet
	 */
	@Override
	public File generateExcelTemplate() {

		File fileWritten = null;
		try {
			String path = Constants.DATA_SHEET_PATH; // messageSource.getMessage("childinfo.output.path", null, null);
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}

			String filename = path + "Data_Entry_Template_";
			String[] areaHeaderNameList = { "Sl.No", "Area Code", "Area Name", "Parent Area Name", "Area Level" };
			String[] indicatorHeaderNameList = { "Sl.No", "Indicator", "Unit", "Subgroup",
					"High is Good (TRUE/FALSE)" };
			String[] dataHeaderNameList = { "Time Period", "State", "District", "Indicator", "Data Value", "Source" };

			FileOutputStream outputStream;
			XSSFWorkbook workbook = new XSSFWorkbook();

			XSSFFont font = workbook.createFont();
			font.setBold(true);
			CellStyle style = workbook.createCellStyle();

			style.setAlignment(HorizontalAlignment.CENTER);
			// Setting font to style
			style.setFont(font);

			// Create a Sheet
			XSSFSheet sheet1 = workbook.createSheet(Constants.DATA_SHEET);
			XSSFSheet sheet2 = workbook.createSheet("Indicator");
			XSSFSheet sheet3 = workbook.createSheet("Area");

			sheet1.setColumnWidth(0, 3000);
			sheet1.setColumnWidth(1, 4000);
			sheet1.setColumnWidth(2, 4000);
			sheet1.setColumnWidth(3, 4000);
			sheet1.setColumnWidth(4, 4000);

			sheet2.setColumnWidth(0, 3000);
			sheet2.setColumnWidth(1, 16000);
			sheet2.setColumnWidth(2, 8000);
			sheet2.setColumnWidth(3, 4000);
			sheet2.setColumnWidth(4, 6000);

			sheet3.setColumnWidth(0, 3000);
			sheet3.setColumnWidth(1, 4000);
			sheet3.setColumnWidth(2, 8000);
			sheet3.setColumnWidth(3, 8000);
			sheet3.setColumnWidth(4, 4000);

			List<Object[]> indicatorDataRetrived = indicatorJpaRepository.getAllIndicatorUnitSubgroupNameList();
			List<Object[]> areaDataRetrived = areaJpaRepository.getAllAreaNames();
			setValueInSheet(sheet1, dataHeaderNameList, null, style);
			setValueInSheet(sheet2, indicatorHeaderNameList, indicatorDataRetrived, style);
			setValueInSheet(sheet3, areaHeaderNameList, areaDataRetrived, style);
			// Write the output to a file
			fileWritten = File.createTempFile(filename + "_", ".xlsx");
			outputStream = new FileOutputStream(fileWritten);
			workbook.write(outputStream);
			workbook.close();
		} catch (Exception e) {
			// LOGGER.error("Error while workbook write."+e);
		}
		return fileWritten;
	}

	private void setValueInSheet(XSSFSheet sheet, String[] headerNameList, List<Object[]> dataRetrived,
			CellStyle style) {
		int rowNum = 0;
		int colNum = 0;
		// Create a Row
		XSSFRow headerRow = sheet.createRow(rowNum);
		Cell cell = headerRow.createCell(colNum);

		headerRow = sheet.createRow(0);
		rowNum = 1;
		// Create header cells
		for (int i = 0; i < headerNameList.length; i++) {
			cell = headerRow.createCell(i);
			cell.setCellValue(headerNameList[i]);
			cell.setCellStyle(style);
		}
		XSSFRow row;
		if (dataRetrived != null) {
			for (Object[] objects : dataRetrived) {
				row = sheet.createRow(rowNum++);
				for (int i = 0; i < objects.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(objects[i] == null ? "" : objects[i].toString());
				}
			}
		}

		sheet.createFreezePane(0, 1);

	}

	/**
	 * @author Harsh Pratyush This method will calculate rank for current indicator
	 *         timeperiod area
	 * @param sqlDataMapForCalculation
	 * @param updatedIndicatorTimePeriodMap
	 * @param updatedIndicator
	 * @return
	 */
	@Transactional
	private boolean calculateRank(Map<Integer, List<Data>> sqlDataMapForCalculation, List<Indicator> updatedIndicator,
			Map<String, Set<String>> updatedIndicatorTimePeriodMap) {

		Iterable<org.sdrc.rmnchadashboard.jpadomain.Indicator> indicators = updatedIndicator;

		indicators.forEach(indicator -> {
			Map<String, List<org.sdrc.rmnchadashboard.jpadomain.Data>> map = new LinkedHashMap<>();

			List<org.sdrc.rmnchadashboard.jpadomain.Data> datas = null;

			Map<String, List<org.sdrc.rmnchadashboard.jpadomain.Data>> indicatorTpSrcAreaLevelMap = new LinkedHashMap<>();

			datas = new ArrayList<>();

			Iterable<org.sdrc.rmnchadashboard.jpadomain.Data> itrDatas = null;

			itrDatas = sqlDataMapForCalculation.get(indicator.getId()).stream()
					.filter(data -> updatedIndicatorTimePeriodMap.get(indicator.getiName()).contains(data.getTp()))
					.collect(Collectors.toList());
			;

			if (itrDatas != null) {
				itrDatas.forEach(datas::add);
				for (int index = 0; index < datas.size(); index++) {
					if (datas.get(index).getArea().getActAreaLevel().getLevel() != 2) {

						List<org.sdrc.rmnchadashboard.jpadomain.Data> dd = indicatorTpSrcAreaLevelMap
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
		return false;
	}

	/**
	 * @author Harsh Pratyush This method will calculate trend for current indicator
	 *         timeperiod area
	 * @param sqlDataMapForCalculation
	 * @param updatedIndicator
	 * @param updatedIndicatorTimePeriodMap
	 * @return
	 */
	@Transactional
	private Boolean calculateTrendForSQLDb(Map<Integer, List<Data>> sqlDataMapForCalculation,
			List<Indicator> updatedIndicator, Map<String, Set<String>> updatedIndicatorTimePeriodMap) {

		Iterable<Indicator> indicators = updatedIndicator;

		indicators.forEach(indicator -> {
			List<org.sdrc.rmnchadashboard.jpadomain.Data> allData = new ArrayList<>();
			List<org.sdrc.rmnchadashboard.jpadomain.Data> datas = null;
			Iterable<org.sdrc.rmnchadashboard.jpadomain.Data> itrDatas = null;

			// Map<String, List<org.sdrc.rmnchadashboard.jpadomain.Data>> map = new
			// LinkedHashMap<>();
			Map<String, List<Data>> indicatorTpSrcAreaLevelMap = new LinkedHashMap<>();

			datas = new ArrayList<>();

			itrDatas = sqlDataMapForCalculation.get(indicator.getId());
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

	/**
	 * @author Harsh Pratyush This method will calculate the top and bottom child
	 *         areas for current indicator timeperiod area
	 * @param sqlDataMapForCalculation
	 * @param updatedIndicator
	 * @param updatedIndicatorTimePeriodMap
	 * @return
	 */
	private Boolean calculateTopButtomForSQLDb(Map<Integer, List<Data>> sqlDataMapForCalculation,
			List<Indicator> updatedIndicator, Map<String, Set<String>> updatedIndicatorTimePeriodMap) {

		Iterable<org.sdrc.rmnchadashboard.jpadomain.Indicator> indicators = updatedIndicator;

		indicators.forEach(indicator -> {
			List<org.sdrc.rmnchadashboard.jpadomain.Data> datass = new ArrayList<>();
//			List<org.sdrc.rmnchadashboard.jpadomain.Data> datas;
			Map<String, List<org.sdrc.rmnchadashboard.jpadomain.Data>> map = new HashMap<>();

			datass = sqlDataMapForCalculation.get(indicator.getId()).stream()
					.filter(data -> updatedIndicatorTimePeriodMap.get(indicator.getiName()).contains(data.getTp()))
					.collect(Collectors.toList());
			if (datass != null) {
//				datas.forEach(datass::add);

				List<org.sdrc.rmnchadashboard.jpadomain.Data> dddd = new ArrayList<>();
				for (Data data : datass) {
					// System.out.println(data);
					// we are skipping all districts coz we dont want to calculate top and below
					// json
					if (data.getArea().getActAreaLevel().getLevel() != 4
							&& (data.getIndicator().getKpi() == true || data.getIndicator().getNitiaayog() == true)) {

						List<Area> topAndEqualValueAreas = new ArrayList<>();
						List<Area> buttomValueAreas = new ArrayList<>();
						List<Data> children = sqlDataMapForCalculation.get(indicator.getId()).stream()
								.filter(d -> d.getArea().getParentAreaCode().equals(data.getArea().getCode())
										&& data.getSrc().getId() == d.getSrc().getId()
										&& data.getTp().equals(d.getTp()))
								.collect(Collectors.toList());
						Double value = Double.valueOf(data.getValue());

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

				}
				if (!dddd.isEmpty())
					dataJpaRepository.save(dddd);
			}
		});

		return true;

	}

	@Override
	public boolean createJsonAndZip(String fcmToken) throws IOException {
		try {
			Map<Integer, List<org.sdrc.rmnchadashboard.model.Data>> indicatorWiseData = new HashMap<>();
			Map<String, List<org.sdrc.rmnchadashboard.model.Data>> areaWiseData = new HashMap<>();
			
			List<Object[]> dataList = dataJpaRepository.findJsonData();
			
			sendNotfication(new ResponseModel(HttpStatus.ACCEPTED.value(),Constants.CREATING_JSON), fcmToken);
			for (int i = 0; i < dataList.size(); i++) {

				Object[] data = dataList.get(i);

				// domain to model

				org.sdrc.rmnchadashboard.model.Data dataModel = new org.sdrc.rmnchadashboard.model.Data();

				dataModel.set_id(data[0].toString());
				dataModel.setArea(data[14].toString());

				JSONArray jsonArrayDataTop = (JSONArray) new JSONParser().parse(data[18].toString());
				JSONArray jsonArrayDataBelow = (JSONArray) new JSONParser().parse(data[19].toString());
				Set<String> below = new HashSet<String>();
				if (jsonArrayDataBelow.get(0) != null) {
					for (int j = 0; j < jsonArrayDataBelow.size(); j++) {
						below.add(jsonArrayDataBelow.get(j).toString());
					}
				}
				dataModel.setBelow(below.stream().collect(Collectors.toList()));

				Set<String> top = new HashSet<String>();
				if (jsonArrayDataTop.get(0) != null) {
					for (int j = 0; j < jsonArrayDataTop.size(); j++) {
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

				// indicator work
				List<org.sdrc.rmnchadashboard.model.Data> existingIndicatorDataList = indicatorWiseData
						.get(dataModel.getIndicator());
				if (existingIndicatorDataList == null) {
					existingIndicatorDataList = new ArrayList<>();
				}

				existingIndicatorDataList.add(dataModel);
				indicatorWiseData.put(dataModel.getIndicator(), existingIndicatorDataList);

				// area work
				List<org.sdrc.rmnchadashboard.model.Data> existingAreaDataList = areaWiseData.get(dataModel.getArea());
				if (existingAreaDataList == null) {
					existingAreaDataList = new ArrayList<>();
				}
				existingAreaDataList.add(dataModel);
				areaWiseData.put(dataModel.getArea(), existingAreaDataList);

			}

			// Indicator json file creation
			for (Map.Entry<Integer, List<org.sdrc.rmnchadashboard.model.Data>> entry : indicatorWiseData.entrySet()) {
				String fileName = entry.getKey().toString();
				List<org.sdrc.rmnchadashboard.model.Data> fileContent = entry.getValue();

				ObjectMapper mapper = new ObjectMapper();
//				_log.info("creating indicator file: " + fileName + ".json");
				File file = this.jsonDataLocation.resolve(fileName + ".json").toFile();
				mapper.writeValue(file, fileContent);
			}

			// Area json file creation
			for (Map.Entry<String, List<org.sdrc.rmnchadashboard.model.Data>> entry : areaWiseData.entrySet()) {
				String fileName = entry.getKey().toString();
				List<org.sdrc.rmnchadashboard.model.Data> fileContent = entry.getValue();

				ObjectMapper mapper = new ObjectMapper();
//				_log.info("creating area file: " + fileName + ".json");

				File file = this.jsonDataLocation.resolve(fileName + ".json").toFile();
				mapper.writeValue(file, fileContent);
			}

			// code to zip the folder

			// create byte buffer
			byte[] buffer = new byte[1024];

			sendNotfication(new ResponseModel(HttpStatus.ACCEPTED.value(),Constants.CREATE_ZIP), fcmToken);
			File file = this.rootLocation.resolve(Constants.ZIP_FILE_NAME).toFile();
			FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());

			ZipOutputStream zos = new ZipOutputStream(fos);

			File dir = this.jsonDataLocation.toFile();

			File[] files = dir.listFiles();

			for (int i = 0; i < files.length; i++) {

//            	_log.info("Adding file: " + files[i].getName());

				FileInputStream fis = new FileInputStream(files[i]);

				// begin writing a new ZIP entry, positions the stream to the start of the entry
				// data
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
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String store(MultipartFile file) throws Exception {
		try {

			String fileName;
			if (file.getOriginalFilename().trim().length() > 12) {
				fileName = file.getOriginalFilename().trim().replaceAll(".xlsx", "").substring(0, 6)
						+ sdf.format(new Date().getTime()) + ".xlsx";
			} else {
				fileName = file.getOriginalFilename().trim().replaceAll(".xlsx", "") + new Date().getTime() + ".xlsx";
			}
			Files.copy(file.getInputStream(), this.rootLocation.resolve(fileName));
			return this.rootLocation.resolve(fileName).toAbsolutePath().toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to upload");

		}
	}

	@Override
	public MasterDataModel getMasterData(MasterDataSyncModel masterDataSyncModel) {
		MasterDataModel masterDataModel = new MasterDataModel();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");

			List<SynchronizationDateMaster> synchronizationDateMasterList = synchronizationDateMasterRepository
					.findAll();

			Date areaSyncDateMobile = new Timestamp(sdf.parse(masterDataSyncModel.getAreaSyncDate()).getTime());
			Date areaLevelSyncDateMobile = new Timestamp(
					sdf.parse(masterDataSyncModel.getAreaLevelSyncDate()).getTime());
			Date unitSyncDateMobile = new Timestamp(sdf.parse(masterDataSyncModel.getUnitSyncDate()).getTime());
			Date indicatorSyncDateMobile = new Timestamp(
					sdf.parse(masterDataSyncModel.getIndicatorSyncDate()).getTime());
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

			for (int i = 0; i < synchronizationDateMasterList.size(); i++) {
				SynchronizationDateMaster synchronizationDateMaster = synchronizationDateMasterList.get(i);
				switch (synchronizationDateMaster.getTableName()) {
				case "area":
					areaSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if (areaSyncDate.compareTo(areaSyncDateMobile) < 0) {
						shouldSendAreaData = false;
					}
					break;
				case "arealevel":
					areaLevelSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if (areaLevelSyncDate.compareTo(areaLevelSyncDateMobile) < 0) {
						shouldSendAreaLevelData = false;
					}
					break;
				case "unit":
					unitSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if (unitSyncDate.compareTo(unitSyncDateMobile) < 0) {
						shouldSendUnitData = false;
					}
					break;
				case "indicator":
					indicatorSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if (indicatorSyncDate.compareTo(indicatorSyncDateMobile) < 0) {
						shouldSendIndicatorData = false;
					}
					break;
				case "subgroup":
					subgroupSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if (subgroupSyncDate.compareTo(subgroupSyncDateMobile) < 0) {
						shouldSendSubgroupData = false;
					}
					break;
				case "sector":
					sectorSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if (sectorSyncDate.compareTo(sectorSyncDateMobile) < 0) {
						shouldSendSectorData = false;
					}
					break;
				case "source":
					sourceSyncDate = synchronizationDateMaster.getLastModifiedDate();
					if (sourceSyncDate.compareTo(sourceSyncDateMobile) < 0) {
						shouldSendSourceData = false;
					}
					break;
				}
			}

			if (shouldSendAreaData) {
				masterDataModel.setAreaList(areaJpaRepository.findAll());
			}

			if (shouldSendAreaLevelData) {
				masterDataModel.setAreaLevelList(areaLevelJpaRepository.findAll());
			}

			if (shouldSendUnitData) {
				masterDataModel.setUnitList(unitJpaRepository.findAll());
			}

			if (shouldSendIndicatorData) {
				masterDataModel.setIndicatorList(indicatorJpaRepository.findAll());
			}

			if (shouldSendSubgroupData) {
				masterDataModel.setSubgroupList(subgroupJpaRepository.findAll());
			}

			if (shouldSendSectorData) {
				masterDataModel.setSectorList(sectorJpaRepository.findAll());
			}

			if (shouldSendSourceData) {
				masterDataModel.setSourceList(sourceJpaRepository.findAll());
			}

			masterDataModel.setSynchronizationDateMasterList(synchronizationDateMasterList);
			return masterDataModel;
		} catch (ParseException e) {
			e.printStackTrace();
			return masterDataModel;
		}
	}

	@Override
	public boolean checkDataSyncData(String lastModifiedData) {
		SynchronizationDateMaster dataSync = synchronizationDateMasterRepository.findByTableName(Constants.DATA_TABLE);
		return sdfDb.format(dataSync.getLastModifiedDate()).equals(lastModifiedData);
	}

	@Override
	public DataSyncMaster updateDataSyncMaster(DataSyncMaster dataSyncMaster) {
		
		
		return dataSyncMasterRepository.save(dataSyncMaster);
	}

	@Override
	public List<DataSyncMaster> getSyncStatus(List<DataSyncStatusEnum> datasyncStatus) {
		return dataSyncMasterRepository.findByDataSyncStatusIn(datasyncStatus);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendNotfication(ResponseModel responseModel,String token) {
		
		try {
		  final String apiKey = authorizationKey;
		  URL url = new URL("https://fcm.googleapis.com/fcm/send");
		  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		  conn.setDoOutput(true);
		  conn.setRequestMethod("POST");
		  conn.setRequestProperty("Content-Type", "application/json");
		  conn.setRequestProperty("Authorization", "key=" + apiKey);

		  conn.setDoOutput(true);

			JSONObject message = new JSONObject();
			message.put("to",
					token);
			message.put("priority", "high");

			JSONObject notification = new JSONObject();
			notification.put("title", responseModel.getStatusCode()==HttpStatus.OK.value()?"Success":
				responseModel.getStatusCode()==HttpStatus.NOT_MODIFIED.value()||responseModel.getStatusCode()==HttpStatus.ACCEPTED.value()?"Info":"Error");
			notification.put("body", responseModel.getMessage());
			notification.put("statusCode",responseModel.getStatusCode() );

			
			JSONObject data = new JSONObject();

			data.put("statusCode",responseModel.getStatusCode() );
			
			
			message.put("notification", notification);
			message.put("data", data);
			


		  OutputStream os = conn.getOutputStream();
		  os.write(message.toString().getBytes());
		  os.flush();
		  os.close();

		  int responseCode = conn.getResponseCode();
		  System.out.println("\nSending 'POST' request to URL : " + url);
		  System.out.println("Post parameters : " + message.toString());
		  System.out.println("Response Code : " + responseCode);

		  BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		  String inputLine;
		  StringBuffer response = new StringBuffer();

		  while ((inputLine = in.readLine()) != null) {
		     response.append(inputLine);
		  }
		  in.close();

		  // print result
		  System.out.println(response.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		  
		
	}

	@Override
	public List<Data> getCustomViewData(Integer page, List<Integer> indicatorSlugIds, List<String> areaCodes,
			Integer searchType,Pageable pageable) {
		List<Data> datas=dataJpaRepository.findByAreaCodeInAndIndicatorSlugidindicatorIn(areaCodes,indicatorSlugIds,pageable);
		return datas;
	}

}
