package org.sdrc.rmnchadashboard.web.service;

import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sdrc.rmncha.domain.Area;
import org.sdrc.rmncha.domain.AreaLevel;
import org.sdrc.rmncha.domain.TimePeriod;
import org.sdrc.rmncha.mongodomain.AggregateLegacyDataStatus;
import org.sdrc.rmncha.mongodomain.AllChecklistFormData;
import org.sdrc.rmncha.mongodomain.CoverageData;
import org.sdrc.rmncha.mongodomain.DataValue;
import org.sdrc.rmncha.mongodomain.FormSectorMapping;
import org.sdrc.rmncha.mongodomain.GroupIndicator;
import org.sdrc.rmncha.mongodomain.Indicator;
import org.sdrc.rmncha.mongorepository.AggregateLegacyDataStatusRepository;
import org.sdrc.rmncha.mongorepository.CoverageDataRepository;
import org.sdrc.rmncha.mongorepository.DataDomainRepository;
import org.sdrc.rmncha.mongorepository.EnginesFormRepository;
import org.sdrc.rmncha.mongorepository.FormSectorMappingRepository;
import org.sdrc.rmncha.mongorepository.GroupIndicatorRepository;
import org.sdrc.rmncha.mongorepository.IndicatorRepository;
import org.sdrc.rmncha.repositories.AreaLevelRepository;
import org.sdrc.rmncha.repositories.AreaRepository;
import org.sdrc.rmncha.repositories.CustomTypeDetailRepository;
import org.sdrc.rmncha.repositories.TimePeriodRepository;
import org.sdrc.rmnchadashboard.rabbitMQ.CollectionQueryChannel;
import org.sdrc.rmnchadashboard.utils.AreaMapObject;
import org.sdrc.rmnchadashboard.web.model.AreaLevelModel;
import org.sdrc.rmnchadashboard.web.model.AreaModel;
import org.sdrc.rmnchadashboard.web.model.ChartDataModel;
import org.sdrc.rmnchadashboard.web.model.FormSectorModel;
import org.sdrc.rmnchadashboard.web.model.GroupChartDataModel;
import org.sdrc.rmnchadashboard.web.model.IndicatorGroupModel;
import org.sdrc.rmnchadashboard.web.model.LegendModel;
import org.sdrc.rmnchadashboard.web.model.SectorModel;
import org.sdrc.rmnchadashboard.web.model.SubSectorModel;
import org.sdrc.rmnchadashboard.web.model.TimeperiodModel;
import org.sdrc.rmnchadashboard.web.model.TypeDetailModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Divide;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Multiply;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import in.co.sdrc.sdrcdatacollector.document.EnginesForm;
import in.co.sdrc.sdrcdatacollector.document.TypeDetail;

/**
 * @author Debiprasad Parida Created Date: 17-04-2019
 *
 */
@Service
public class DashboardServiceImpl implements DashboardService {
	
	@Autowired
	private TimePeriodRepository timePeriodRepository;
	
	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Autowired
	private GroupIndicatorRepository groupIndicatorRepository;
	
	@Autowired
	private IndicatorRepository mongoIndicatorRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private FormSectorMappingRepository formSectorMappingRepository;
	
	@Autowired
	private AreaRepository mongoAreaRepository;
	
	@Autowired
	private AreaLevelRepository mongoAreaLevelRepository;
	
	@Autowired
	private DataDomainRepository dataValueRepository;
	
	@Autowired
	private CoverageDataRepository coverageDataRepository;

	@Autowired
	private EnginesFormRepository enginesFormRepository;

	@Autowired
	private CustomTypeDetailRepository customTypeDetailRepository;
	
	@Autowired
	private AggregateLegacyDataStatusRepository aggregateLegacyDataStatusRepository;
	
	@Autowired
	private CollectionQueryChannel collectionQueryChannel;
	
	private DateFormat ymdDateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	
	private volatile boolean stopThread = false;
	
//	@Value("${l2.typedetail.id}") int 43;
//	@Value("${l2.typedetail.id}") int 44;
//	@Value("${l2.typedetail.id}") int 45;
//	Integer 44=Integer.parseInt(configurableEnvironment.getProperty("l2.typedetail.id"));
	
	@Override
	public String pushIndicatorGroupData() {

		groupIndicatorRepository.deleteAll();
		formSectorMappingRepository.deleteAll();
		
		FormSectorMapping formSectorMapping = null;
		try {
			List < GroupIndicator > indicatorModels = new ArrayList <  > ();

			GroupIndicator groupIndicatorModel = null;

			// File file = new File(context.getRealPath(indicatorGroupTemplateFilepath));
			FileInputStream excelfile = new FileInputStream(
					new File(configurableEnvironment.getProperty("indicator.group.template.uri")));

			XSSFWorkbook workbook = new XSSFWorkbook(excelfile);
			XSSFSheet sheet = workbook.getSheet("Sheet1");

			XSSFRow row = null;
			XSSFCell cell = null;
			for (int rowNum = 1; rowNum <= 559; rowNum++) {
//			for (int rowNum = 1; rowNum <= Integer.parseInt(configurableEnvironment.getProperty("pushindicator.last.rownum")); rowNum++) {

				groupIndicatorModel = new GroupIndicator();

				int colNum = 0;

				row = sheet.getRow(rowNum);
				cell = row.getCell(colNum);
				groupIndicatorModel.setIndicatorGroup(cell.getStringCellValue());

//				indicatorGroup	kpiIndicator	chartType	chartIndicators	sector	sectorId	subSector	

				colNum++;
				cell = row.getCell(colNum);
				if(cell!=null && cell.getCellTypeEnum() == CellType.NUMERIC) {
					groupIndicatorModel.setKpiIndicator((int)cell.getNumericCellValue());
				}
				colNum++;
				cell = row.getCell(colNum);
				groupIndicatorModel.setChartType(cell!=null && cell.getCellTypeEnum() != CellType.BLANK ? new ArrayList <  > (Arrays.asList(cell.getStringCellValue().split(","))): Arrays.asList(""));

				
				colNum++;
				cell = row.getCell(colNum);
				if(cell!=null)
				groupIndicatorModel.setChartIndicators(getBarChartIndicators(cell.getStringCellValue()));
				
				colNum++;
				cell = row.getCell(colNum);
				groupIndicatorModel.setSector(cell.getStringCellValue());
				
				colNum++;
				cell = row.getCell(colNum);
				
				if(cell!=null && cell.getCellTypeEnum() == CellType.NUMERIC) {
					groupIndicatorModel.setSectorId(String.valueOf((int) cell.getNumericCellValue()));
				}else if(cell!=null && cell.getCellTypeEnum() == CellType.STRING) {
					groupIndicatorModel.setSectorId(cell.getStringCellValue());
				}
				
//				groupIndicatorModel.setSectorId(cell.getStringCellValue());
				
				colNum++;
				cell = row.getCell(colNum);
				groupIndicatorModel.setSubSector(cell.getStringCellValue());

				
//				kpiChartHeader	chartHeader	
				
				colNum++;
				cell = row.getCell(colNum);
				if(cell!=null)
				groupIndicatorModel.setKpiChartHeader(cell.getStringCellValue());
				
				colNum++;
				cell = row.getCell(colNum);
				if(cell!=null)
				groupIndicatorModel.setChartHeader(cell.getStringCellValue());
				
//				cardType	chartLegends	colorLegends	
				colNum++;
				cell = row.getCell(colNum);
				groupIndicatorModel.setCardType(cell.getStringCellValue());
				
				colNum++;
				cell = row.getCell(colNum);
				if(cell!=null)
				groupIndicatorModel.setChartLegends(cell.getStringCellValue());
				
				colNum++;
				cell = row.getCell(colNum);
				if(cell!=null)
				groupIndicatorModel.setColorLegends(cell.getStringCellValue());
				
//				align	valueFrom	unit	chartGroup
				colNum++;
				cell = row.getCell(colNum);
				groupIndicatorModel.setAlign(cell.getStringCellValue());
				
				colNum++;
				cell = row.getCell(colNum);
				
				if(cell!=null && cell.getCellTypeEnum() == CellType.NUMERIC) {
					groupIndicatorModel.setValueFrom(String.valueOf((int) cell.getNumericCellValue()));
				}else if(cell!=null && cell.getCellTypeEnum() == CellType.STRING) {
					groupIndicatorModel.setValueFrom(cell.getStringCellValue());
				}
				
				
				colNum++;
				cell = row.getCell(colNum);
				groupIndicatorModel.setUnit(cell.getStringCellValue());

				colNum++;
				cell = row.getCell(colNum);
				groupIndicatorModel.setChartGroup(cell.getStringCellValue());
				
				colNum++;
				cell = row.getCell(colNum);
				if(cell!=null)
					groupIndicatorModel.setLevel(cell.getStringCellValue());

				indicatorModels.add(groupIndicatorModel);

			}
			sheet = null;
			sheet = workbook.getSheet("Sheet2");

			row = null;
			cell = null;
			List < FormSectorMapping > formSectorMappingList = new ArrayList <  > ();
			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
				formSectorMapping = new FormSectorMapping();
				int colNum = 0;

				row = sheet.getRow(rowNum);
				cell = row.getCell(colNum);
				formSectorMapping.setFormId((int)cell.getNumericCellValue());

				colNum++;
				cell = row.getCell(colNum);
				formSectorMapping.setSectorName(cell.getStringCellValue());
				
				colNum++;
				cell = row.getCell(colNum);
				formSectorMapping.setSectorId((int)cell.getNumericCellValue());

				formSectorMappingList.add(formSectorMapping);

			}
			

			workbook.close();
			
			groupIndicatorRepository.save(indicatorModels);
			formSectorMappingRepository.save(formSectorMappingList);
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			
		}
		
		return "done";
		}


	private List<Integer> getValue(String stringCellValue) {
		List<Integer> indicators = new ArrayList<>();
		if (!stringCellValue.equals("")) {
			for (int i = 0; i < stringCellValue.split(",").length; i++) {
				if (!stringCellValue.split(",")[i].equals("")) {
					indicators.add(Integer.valueOf(stringCellValue.split(",")[i].trim()));
				}
			}
		}
		return indicators;
	}


	private List<List<Integer>> getBarChartIndicators(String stringCellValue) {
		List<List<Integer>> barChartIndicators = new ArrayList<>();
		if (!stringCellValue.equals("")) {
			for (int i = 0; i < stringCellValue.split("@").length; i++) {
				barChartIndicators.add(getValue(stringCellValue.split("@")[i]));
			}
		}
		return barChartIndicators;
	}
	
	@Override
	public Map<String,List<FormSectorModel>> getAllChecklistSectors(){
		
		List<FormSectorMapping> formSectorMappings = formSectorMappingRepository.findAll();
//		List<FormSectorMapping> formSectorMappings = formSectorMappingRepository.findByFormIdIn(Arrays.asList(2,3,4));
		
		
		formSectorMappings = formSectorMappings.stream().filter(formSec -> formSec.getSectorId()!=1 &&
				!formSec.getSectorName().equalsIgnoreCase(configurableEnvironment.getProperty("supportive.supervision.sector.name"))).collect(Collectors.toList());
		
		List<EnginesForm> enginesForms = enginesFormRepository.findAll();
		
		Map<Integer, String> enginesFormsMap = new LinkedHashMap<>();
		
		for (EnginesForm enginesForm : enginesForms) {
			enginesFormsMap.put(enginesForm.getFormId(), enginesForm.getName());
		}
	
		Map<String,List<FormSectorModel>> formSectorMap = new LinkedHashMap<>();
		
		for (FormSectorMapping formSectorMapping : formSectorMappings) {
			
			//remove sector id 1, optimize 1
//			if(formSectorMapping.getSectorId()!=1) {
				if(!formSectorMap.containsKey(enginesFormsMap.get(formSectorMapping.getFormId()))){
					List<FormSectorModel> formSectorModels = new ArrayList<>();
					FormSectorModel formSectorModel = new FormSectorModel();
				
					formSectorModel.setFormId(formSectorMapping.getFormId());
					formSectorModel.setSectorId(formSectorMapping.getSectorId());
					formSectorModel.setSectorName(formSectorMapping.getSectorName());
					
					formSectorModels.add(formSectorModel);
					
					formSectorMap.put(enginesFormsMap.get(formSectorMapping.getFormId()), formSectorModels);
				}else {
					FormSectorModel formSectorModel = new FormSectorModel();
					
					formSectorModel.setFormId(formSectorMapping.getFormId());
					formSectorModel.setSectorId(formSectorMapping.getSectorId());
					formSectorModel.setSectorName(formSectorMapping.getSectorName());
					
					formSectorMap.get(enginesFormsMap.get(formSectorMapping.getFormId())).add(formSectorModel);
				}
//			}
		
		}
		
		for(String entry : formSectorMap.keySet()) {
			
			FormSectorModel formSectorModel = new FormSectorModel();
			
			formSectorModel.setFormId(formSectorMap.get(entry).get(0).getFormId());
			formSectorModel.setSectorId(0);
			formSectorModel.setSectorName("All");
			
			formSectorMap.get(entry).add(formSectorModel);
		}
		
		return formSectorMap;
	}
	
	@Override
	public boolean isDataAvailable() {
		return coverageDataRepository.findAll().size() > 0 ? true : false;
	}
	
	@Override
	public List<SectorModel> getDashboardData(Integer areaLevel, Integer areaId, Integer sectorId, Integer tpId,
			Integer formId, String dashboardType, Integer typeId, Integer levelId) {
		List<SectorModel> sectorModels = new LinkedList<>();
		
		Map<String, Map<String, List<IndicatorGroupModel>>> map = new LinkedHashMap<>();
		
		try {
			
//			String sectorName = "";
			String lastAggregatedTime = "";
			List<String> sectors= new ArrayList<>();
		/*	if(sectorId == null) {
//				sectorName = "Supportive supervision in aspirational districts";
				sectors =  Arrays.asList("14","15","16","17");
				
				if(areaLevel>=3) {
					sectors =  Arrays.asList("14","15","17");
				}
			}*/
			if(formId!=null) {
				switch (formId) {
				case 3:
					sectors.add("27");
					break;

				case 2:
					sectors.add("26");
					break;
				
				case 4:
					sectors.add("30");
					break;
					
				default:
					break;
				}
			}
			
			
			if (sectorId != null) {

				//in case of all
				if (sectorId == 0) {
				
					List<FormSectorMapping> formSectorMappings = formSectorMappingRepository.findByFormId(formId);
					for (FormSectorMapping formSectorMapping : formSectorMappings) {
						sectors.add(formSectorMapping.getSectorId().toString());
					}
					
					sectors = sectors.stream().filter(sec -> !sec.equals("14") && !sec.equals("15")   && !sec.equals("16")   && !sec.equals("17")).collect(Collectors.toList());

				}else {
					sectors.addAll(Arrays.asList(sectorId.toString(), "1"));//sector 1 added to get total districts assessed value to show 'n' in indicator charts
				}
			}else {
				sectors.addAll( Arrays.asList("14","15","16","17"));
				
				if(areaLevel>=3) {
					
					sectors.remove("16"); //remove district assesment in case of district level coverage
//					sectors .addAll(Arrays.asList("14","15","17"));
				}	
			}
		
//			Integer 43=Integer.parseInt(configurableEnvironment.getProperty("l1.typedetail.id"));
//			Integer 44=Integer.parseInt(configurableEnvironment.getProperty("l2.typedetail.id"));
//			Integer 45=Integer.parseInt(configurableEnvironment.getProperty("l3.typedetail.id"));
			
			List<GroupIndicator> groupIndicatorModels = new ArrayList<>();
			
			if(formId!=null && formId == 1 && levelId==null) {
				
				String level = "All";
				groupIndicatorModels = groupIndicatorRepository.findBySectorIdInAndLevel(sectors, level);
			}
			else if(formId!=null && formId == 1 && levelId!=null) {
				
				String level = levelId == 43 ? level = "L1" : levelId == 44 ? level = "L2" : "L3";
				groupIndicatorModels = groupIndicatorRepository.findBySectorIdInAndLevel(sectors, level);
			}else {
				groupIndicatorModels = groupIndicatorRepository.findBySectorIdIn(sectors);
			}
			
			
			//add this to get all facilities indicator
			sectors.addAll( Arrays.asList("14","15","16","17"));
			List<Indicator> indicatorList = mongoIndicatorRepository.getIndicatorBySectorIds(sectors);
			
			List<Integer> indicatorIds = new LinkedList<>() ;
			Map<Integer, String> indicatorNameMap = new HashMap<>();
			
			for (Indicator indicator : indicatorList) {
				indicatorIds.add(Integer.valueOf((String)indicator.getIndicatorDataMap().get("indicatorNid")));
				indicatorNameMap.put(Integer.valueOf((String)indicator.getIndicatorDataMap().get("indicatorNid")),
						(String) indicator.getIndicatorDataMap().get("indicatorName"));
			}
			
			if(areaLevel>=3) { // if area level is block, then do not show district checklist
				groupIndicatorModels = groupIndicatorModels.stream().filter(ind -> !ind.getSubSector().equals("District Assesment Checklist")).collect(Collectors.toList());
			}
			
			List<GroupIndicator> trendGroupIndicators = groupIndicatorModels.stream().filter(ind -> ind.getChartType().contains("trend")).collect(Collectors.toList());
			List<GroupIndicator> staticIndicators = groupIndicatorModels.stream().filter(ind -> ind.getCardType().contains("static")).collect(Collectors.toList());
			List<Integer> trendGroupIndicatorIds = new ArrayList<>();
			
			
			for (GroupIndicator groupIndicator : trendGroupIndicators) {
				trendGroupIndicatorIds.add(groupIndicator.getChartIndicators().get(0).get(0));
			}
			
			List<Integer> staticIndicatorIds = new ArrayList<>();
			for (GroupIndicator groupIndicator : staticIndicators) {
				staticIndicatorIds.add(groupIndicator.getKpiIndicator());
			}
			

			//in case of coverage indicators
			List<DataValue> coveragedataValues = new ArrayList<>();
			List<DataValue> dataValues = new ArrayList<>();
			List<DataValue> levelWiseDataValues = new ArrayList<>();
			Map<Integer, DataValue> mapData = null;
			Map<Integer, DataValue> allMapData = new LinkedHashMap <>();
			Map<Integer,Map<Integer, DataValue>> levelAllMapData = null;
			Map<Integer,Map<Integer, DataValue>> trendData = new LinkedHashMap <>();
			
			
			Map<Integer,TimePeriod > timePeriodIdMap = new LinkedHashMap<>();
			
			List<TimePeriod> utTimeperiodList = null;
			//remove trend indicators from coverage data
//			indicatorIds.removeAll(trendGroupIndicatorIds);
			indicatorIds.removeAll(staticIndicatorIds); //remove all static indicator ids
			
			if (tpId == null) { // in case of coverage view

				// get all tp for trend
				utTimeperiodList = timePeriodRepository.findTop13ByPeriodicityOrderByStartDateDesc("1");
				// timePeriodRepository.findTop12ByPeriodicityOrderByTimePeriodIdDesc("1");

				Collections.reverse(utTimeperiodList);
//				utTimeperiodList.remove(utTimeperiodList.size() - 1); //uncomment post UAT

				dataValues = dataValueRepository.findTop12ByAreaIdAndInidInOrderByTpDesc(areaId,
						trendGroupIndicatorIds);

				for (DataValue dataValue : dataValues) {

					if (!trendData.containsKey(dataValue.getInid())) {
						Map<Integer, DataValue> newMapData = new LinkedHashMap<>();
						newMapData.put(dataValue.getTp(), dataValue);
						trendData.put(dataValue.getInid(), newMapData);
					} else {
						mapData = trendData.get(dataValue.getInid());
						if (!mapData.containsKey(dataValue.getTp())) {
							mapData.put(dataValue.getTp(), dataValue);
						}
					}

				}

				utTimeperiodList.forEach(timePeriod -> {
					timePeriodIdMap.put(timePeriod.getTimePeriodId(), timePeriod);
				});

				List<CoverageData> coverageDatas = coverageDataRepository.findByAreaIdAndInidIn(areaId, indicatorIds);
				if (!coverageDatas.isEmpty()) {
					lastAggregatedTime = ymdDateTimeFormat.format(coverageDatas.get(0).getCreatedDate());
				}

				coverageDatas.forEach(coverageData -> {
					DataValue dataValue = new DataValue();
					BeanUtils.copyProperties(coverageData, dataValue);
					coveragedataValues.add(dataValue);

				});

				mapData = new LinkedHashMap<>();

				for (DataValue dataValue : coveragedataValues) {
					allMapData.put(dataValue.getInid(), dataValue);

				}
			}else {
				
				groupIndicatorModels = groupIndicatorModels.stream().filter(ind -> !ind.getChartType().get(0).equals("trend")).collect(Collectors.toList());
				
				if(formId!=null && formId == 1 && levelId!=null && typeId!=null) {
					dataValues = dataValueRepository.findByAreaIdAndTpAndF1FacilityTypeAndF1FacilityLevelAndInidIn(areaId, tpId, typeId, levelId, indicatorIds);
				}else if(formId!=null && formId == 1 && typeId!=null) {
					dataValues = dataValueRepository.findByAreaIdAndTpAndF1FacilityTypeAndInidInAndF1FacilityLevelIsNull(areaId, tpId, typeId, indicatorIds);
				}else if(formId!=null && formId == 1 && levelId!=null) {
					dataValues = dataValueRepository.findByAreaIdAndTpAndF1FacilityLevelAndInidInAndF1FacilityTypeIsNull(areaId, tpId, levelId, indicatorIds);
				}else if(formId!=null && formId == 1) {
					levelAllMapData = new LinkedHashMap <>();
					dataValues = dataValueRepository.findByAreaIdAndTpAndInidInAndF1FacilityTypeIsNullAndF1FacilityLevelIsNull(areaId, tpId, indicatorIds);
					levelWiseDataValues  = dataValueRepository.findByAreaIdAndTpAndInidInAndF1FacilityTypeIsNull(areaId, tpId, indicatorIds);
				}
				else {
					dataValues = dataValueRepository.findByAreaIdAndTpAndInidInAndF1FacilityTypeIsNullAndF1FacilityLevelIsNull(areaId, tpId, indicatorIds);
				}
			
				for (DataValue dataValue : dataValues) {
					allMapData.put(dataValue.getInid(), dataValue);
				}
				
				if(formId!=null && formId == 1 && levelId==null && typeId==null) {
					for (DataValue dataValue : levelWiseDataValues) {
						
						if(dataValue.getF1FacilityLevel()!=null) {
							
							if(levelAllMapData.containsKey(dataValue.getF1FacilityLevel())){
								levelAllMapData.get(dataValue.getF1FacilityLevel()).put(dataValue.getInid(), dataValue);
							}else {
								Map<Integer, DataValue> innerMapData = new LinkedHashMap <>();
								innerMapData.put(dataValue.getInid(), dataValue);
								levelAllMapData.put(dataValue.getF1FacilityLevel(), innerMapData);
								
							}
						}
					}
				}
			}
			
			
			for (GroupIndicator groupIndicatorModel : groupIndicatorModels) {

				IndicatorGroupModel indicatorGroupModel = new IndicatorGroupModel();

				List<GroupChartDataModel> listOfGroupChartData = null;
				GroupChartDataModel chartDataModel = null;
				List<LegendModel> legendModels = null;
				
				//set static indicator value
				if(groupIndicatorModel.getCardType().equals("static")) {
					indicatorGroupModel.setIndicatorValue(groupIndicatorModel.getKpiIndicator().toString());
					indicatorGroupModel.setTooltipValue(indicatorGroupModel.getIndicatorValue());
				}else {
					if (allMapData != null) {
						indicatorGroupModel
						.setIndicatorValue(allMapData.get(groupIndicatorModel.getKpiIndicator()) == null ? null
								: allMapData.get(groupIndicatorModel.getKpiIndicator()).getDataValue() == null ? null :
								groupIndicatorModel.getUnit().equalsIgnoreCase("percentage") || groupIndicatorModel.getUnit().equalsIgnoreCase("Average")? 
								String.valueOf(Math.round((allMapData.get(groupIndicatorModel.getKpiIndicator()).getDataValue())* 10.0) / 10.0) : 
									String.valueOf(allMapData.get(groupIndicatorModel.getKpiIndicator()).getDataValue().intValue()));
//						indicatorGroupModel
//								.setIndicatorValue(allMapData.get(groupIndicatorModel.getKpiIndicator()) == null ? null
//										: String.valueOf(allMapData.get(groupIndicatorModel.getKpiIndicator())
//														.getDataValue().intValue()));
						
						indicatorGroupModel.setTooltipValue(indicatorGroupModel.getIndicatorValue());
						
						indicatorGroupModel.setNumerator(
								(allMapData.get(groupIndicatorModel.getKpiIndicator()) == null || allMapData.get(groupIndicatorModel.getKpiIndicator()).getNumerator() == null)
										? null
										: String.valueOf(Math.round(Double.parseDouble ( allMapData.get(groupIndicatorModel.getKpiIndicator()).getNumerator()))));
						indicatorGroupModel.setDenominator(
								(allMapData.get(groupIndicatorModel.getKpiIndicator()) == null || allMapData.get(groupIndicatorModel.getKpiIndicator()).getDenominator() == null)
										? null
										: String.valueOf(Math.round(Double.parseDouble ( allMapData.get(groupIndicatorModel.getKpiIndicator()).getDenominator()))));
						if(levelAllMapData!=null) {
							
							indicatorGroupModel.setLevelWise(true);
							
//							if(groupIndicatorModel.getUnit().equalsIgnoreCase("Number") || groupIndicatorModel.getUnit().equalsIgnoreCase("Average")) {
								String dv = null;
								
								dv = levelAllMapData.containsKey(43)
										? levelAllMapData.get(43).get(groupIndicatorModel.getKpiIndicator()) != null
												&& levelAllMapData.get(43).get(groupIndicatorModel.getKpiIndicator())
														.getDataValue() != null
																? "L1 : " + levelAllMapData.get(43)
																		.get(groupIndicatorModel.getKpiIndicator())
																		.getDataValue().intValue()
																: "L1 : " + "NA"
										: "L1 : " + "NA";
							
								dv = levelAllMapData.containsKey(44) ? levelAllMapData.get(44).get(groupIndicatorModel.getKpiIndicator()) != null
										&& levelAllMapData.get(44).get(groupIndicatorModel.getKpiIndicator()).getDataValue() != null
												? dv!= null ? dv + ", L2 : " + levelAllMapData.get(44).get(groupIndicatorModel.getKpiIndicator()).getDataValue().intValue() : " L2 : " 
										+ levelAllMapData.get(44).get(groupIndicatorModel.getKpiIndicator()).getDataValue().intValue()
												: dv +", L2 : " + "NA"
										: dv +", L2 : " + "NA";
								dv = levelAllMapData.containsKey(45) ? levelAllMapData.get(45).get(groupIndicatorModel.getKpiIndicator()) != null
										&& levelAllMapData.get(45).get(groupIndicatorModel.getKpiIndicator()).getDataValue() != null
												? dv!= null ? dv + ", L3 : " + levelAllMapData.get(45).get(groupIndicatorModel.getKpiIndicator()).getDataValue().intValue()
														: " L3 : " + levelAllMapData.get(45).get(groupIndicatorModel.getKpiIndicator()).getDataValue().intValue()
												: dv +", L3 : " + "NA"
										: dv +", L3 : " + "NA";
								
								indicatorGroupModel.setTooltipValue(dv);
//							}
						
							
							String num = null;
							num = levelAllMapData.containsKey(43)
									? levelAllMapData.get(43).get(groupIndicatorModel.getKpiIndicator()) != null
									&& levelAllMapData.get(43).get(groupIndicatorModel.getKpiIndicator()).getNumerator() != null
											?  "L1 : " + levelAllMapData.get(43).get(groupIndicatorModel.getKpiIndicator()).getNumerator()
											: "L1 : " + "NA"  : "L1 : " + "NA" ;
							num = levelAllMapData.containsKey(44) ? levelAllMapData.get(44).get(groupIndicatorModel.getKpiIndicator()) != null
									&& levelAllMapData.get(44).get(groupIndicatorModel.getKpiIndicator()).getNumerator() != null
											? num!= null ? num + ", L2 : " + levelAllMapData.get(44).get(groupIndicatorModel.getKpiIndicator()).getNumerator() : " L2 : " 
									+ levelAllMapData.get(44).get(groupIndicatorModel.getKpiIndicator()).getNumerator()
											: num +", L2 : " + "NA"
									: num +", L2 : " + "NA";
							num = levelAllMapData.containsKey(45) ? levelAllMapData.get(45).get(groupIndicatorModel.getKpiIndicator()) != null
									&& levelAllMapData.get(45).get(groupIndicatorModel.getKpiIndicator()).getNumerator() != null
											? num!= null ? num + ", L3 : " + levelAllMapData.get(45).get(groupIndicatorModel.getKpiIndicator()).getNumerator()
													: " L3 : " + levelAllMapData.get(45).get(groupIndicatorModel.getKpiIndicator()).getNumerator()
											: num +", L3 : " + "NA"
									: num +", L3 : " + "NA";
							
							indicatorGroupModel.setNumerator(num);
							
							String deno = null;
							deno = levelAllMapData.containsKey(43) ? levelAllMapData.get(43).get(groupIndicatorModel.getKpiIndicator()) != null
									&& levelAllMapData.get(43).get(groupIndicatorModel.getKpiIndicator()).getDenominator() != null
											? "L1 : " + levelAllMapData.get(43).get(groupIndicatorModel.getKpiIndicator()).getDenominator()
											: "L1 : " + "NA" 
									: "L1 : " + "NA" ;
							deno = levelAllMapData.containsKey(44) ? levelAllMapData.get(44).get(groupIndicatorModel.getKpiIndicator()) != null
									&& levelAllMapData.get(44).get(groupIndicatorModel.getKpiIndicator()).getDenominator() != null
											? deno!=null ? deno + ", L2 : " + levelAllMapData.get(44).get(groupIndicatorModel.getKpiIndicator()).getDenominator() :
												" L2 : " + levelAllMapData.get(44).get(groupIndicatorModel.getKpiIndicator()).getDenominator()
											: deno +", L2 : " + "NA"
									: deno +", L2 : " + "NA";
							deno = levelAllMapData.containsKey(45) ? levelAllMapData.get(45).get(groupIndicatorModel.getKpiIndicator()) != null
									&& levelAllMapData.get(45).get(groupIndicatorModel.getKpiIndicator()).getDenominator() != null
											? deno!=null ? deno + ", L3 : " + levelAllMapData.get(45).get(groupIndicatorModel.getKpiIndicator()).getDenominator()
													: " L3 : " + levelAllMapData.get(45).get(groupIndicatorModel.getKpiIndicator()).getDenominator()
											: deno +", L3 : " + "NA"
									: deno +", L3 : " + "NA";
							indicatorGroupModel.setDenominator(deno);
						}
					}
				}

				
//				indicatorGroupModel.setTimeperiod(tp.getTimePeriod() + ", " + tp.getYear());
//				indicatorGroupModel.setTimeperiodId(tp.getTimePeriodId());
//				indicatorGroupModel.setPeriodicity(tp.getPeriodicity());

				String kpiInd = null;
				
				if(groupIndicatorModel.getValueFrom()!=null && groupIndicatorModel.getValueFrom().contains("=")) {
					String[] arrs = groupIndicatorModel.getValueFrom().split(",");
					for(String ar: arrs) {
						
						String[] each = ar.split("=");
						
						if(each[0].equals(areaLevel.toString())) {
							kpiInd = each[1];
						}
					}
					
				}else if(groupIndicatorModel.getValueFrom()!=null) {
					kpiInd = groupIndicatorModel.getValueFrom();
				}
				
					
				if(!groupIndicatorModel.getChartType().get(0).contains("card") && kpiInd!=null && !kpiInd.equals("")){
					indicatorGroupModel
							.setIndicatorValue(allMapData.get(Integer.parseInt(kpiInd))!=null ?
									String.valueOf(allMapData.get(Integer.parseInt(kpiInd)).getDataValue().intValue()) : null);
				}
//				groupIndicatorModel.getValueFrom().contains("=")? groupIndicatorModel.getValueFrom();
//				Number of aspirational district === is aggregated in state level,
				//to show district level data put value as 1

				String kpiIndName = groupIndicatorModel.getKpiChartHeader()!=null ?
						groupIndicatorModel.getKpiChartHeader().contains("@")
						? (groupIndicatorModel.getKpiChartHeader().split("@")[0]
								+ (allMapData.get(Integer.parseInt(kpiInd))!=null ? allMapData.get(Integer.parseInt(kpiInd))
										.getDataValue().intValue() : 1)
								+ groupIndicatorModel.getKpiChartHeader().split("@")[1])
						: groupIndicatorModel.getKpiChartHeader() : "";

				indicatorGroupModel.setIndicatorName(kpiIndName);
				
				indicatorGroupModel.setIndicatorId(groupIndicatorModel.getKpiIndicator());
				indicatorGroupModel.setChartsAvailable(groupIndicatorModel.getChartType());
				indicatorGroupModel.setAlign(groupIndicatorModel.getAlign());
				indicatorGroupModel.setCardType(groupIndicatorModel.getCardType());
				indicatorGroupModel.setIndicatorGroupName(groupIndicatorModel.getIndicatorGroup());
				indicatorGroupModel.setUnit(groupIndicatorModel.getUnit());
				indicatorGroupModel.setChartAlign(groupIndicatorModel.getChartAlign());
				indicatorGroupModel.setChartGroup(groupIndicatorModel.getChartGroup());
				
				
				//new code 04-06-2019
				
				
				//for trend chart indicators
				if (groupIndicatorModel.getChartType().get(0).contains("trend") && groupIndicatorModel.getChartIndicators()!=null &&
						groupIndicatorModel.getChartIndicators().size() > 0) {
					listOfGroupChartData = new ArrayList<GroupChartDataModel>();
					chartDataModel = new GroupChartDataModel();

					String indName = groupIndicatorModel.getChartHeader().contains("@")
							? (groupIndicatorModel.getChartHeader().split("@")[0]
									+ allMapData.get(Integer.parseInt(kpiInd))
											.getDataValue().intValue()
									+ groupIndicatorModel.getChartHeader().split("@")[1])
							: groupIndicatorModel.getChartHeader();

					chartDataModel.setHeaderIndicatorName(indName);
//					chartDataModel.setHeaderIndicatorValue(
//							trendData.get(groupIndicatorModel.getChartIndicators().get(0).get(0)) == null ? null :
//											 trendData.get(groupIndicatorModel.getChartIndicators().get(0).get(0))
//													.get(groupIndicatorModel.getHeaderIndicator()).getDataValue()
//													.intValue());
					chartDataModel.setChartDataValue(getChartDataValue(groupIndicatorModel,
							trendData.isEmpty() ? null : trendData.get(groupIndicatorModel.getChartIndicators().get(0).get(0)),
							indicatorNameMap, "trend", null, timePeriodIdMap, utTimeperiodList, groupIndicatorModel.getUnit(),dashboardType, null));

					listOfGroupChartData.add(chartDataModel);
					indicatorGroupModel.setChartData(listOfGroupChartData);

				}else if(groupIndicatorModel.getChartIndicators()!=null &&
						groupIndicatorModel.getChartIndicators().size() > 0 && allMapData!=null){ //for all other chart types
					listOfGroupChartData = new ArrayList<GroupChartDataModel>();
					chartDataModel = new GroupChartDataModel();

					String indName = groupIndicatorModel.getChartHeader().contains("@")
							? (groupIndicatorModel.getChartHeader().split("@")[0]
									+( !allMapData.isEmpty() ? allMapData.get(Integer.parseInt(kpiInd))
											.getDataValue().intValue() : 0)
									+ groupIndicatorModel.getChartHeader().split("@")[1])
							: groupIndicatorModel.getChartHeader();

					chartDataModel.setHeaderIndicatorName(indName);
					
//					chartDataModel.setHeaderIndicatorValue(allMapData.get(groupIndicatorModel.getHeaderIndicator()) == null ? null
//									: allMapData.get(groupIndicatorModel.getHeaderIndicator()).getDataValue().intValue());
					
					chartDataModel.setChartDataValue(getChartDataValue(groupIndicatorModel, allMapData, indicatorNameMap, groupIndicatorModel.getChartType().get(0)
							, null, timePeriodIdMap, null, groupIndicatorModel.getUnit(),dashboardType, levelAllMapData));
					
					listOfGroupChartData.add(chartDataModel);
					indicatorGroupModel.setChartData(listOfGroupChartData);
					
					if (groupIndicatorModel.getColorLegends()!=null &&
							groupIndicatorModel.getColorLegends().length() > 0 && allMapData!=null) {
						legendModels = new ArrayList<>();
						String[] legendsList = groupIndicatorModel.getColorLegends().split(",");
						
						for (String string : legendsList) {
							LegendModel legendModel = new LegendModel();
							legendModel.setCssClass(string.split("_")[0]);
							legendModel.setValue(string.split("_")[1]);
							legendModels.add(legendModel);
						}
						chartDataModel.setLegends(legendModels);
					}
					
				}
				
				//end
				

				if (!map.containsKey(groupIndicatorModel.getSector())) {

					Map<String, List<IndicatorGroupModel>> subsectorGrMapModel = new LinkedHashMap<>();

					List<IndicatorGroupModel> sectorNewIndicators = new LinkedList<>();
					sectorNewIndicators.add(indicatorGroupModel);

					subsectorGrMapModel.put(groupIndicatorModel.getSubSector(), sectorNewIndicators);
					
					map.put(groupIndicatorModel.getSector(), subsectorGrMapModel);
				}
				else {
					
					if(!map.get(groupIndicatorModel.getSector()).containsKey(groupIndicatorModel.getSubSector())) {
						
						List<IndicatorGroupModel> newIndicators = new LinkedList<>();
						newIndicators.add(indicatorGroupModel);

						map.get(groupIndicatorModel.getSector()).put(groupIndicatorModel.getSubSector(), newIndicators);
					}else {
						map.get(groupIndicatorModel.getSector()).get(groupIndicatorModel.getSubSector()).add(indicatorGroupModel);
					}
				}
			}
			
			for (Entry<String, Map<String, List<IndicatorGroupModel>>> entry : map.entrySet()) {
				SectorModel sectorModel = new SectorModel();
				sectorModel.setSectorName(entry.getKey());
				
				List<SubSectorModel> listOfSubsector = new ArrayList<>();
			
				for (Entry<String, List<IndicatorGroupModel>> entry2 : entry.getValue().entrySet()) {
					SubSectorModel subSectorModel = new SubSectorModel();
					subSectorModel.setSubSectorName(entry2.getKey());
					listOfSubsector.add(subSectorModel);
					subSectorModel.setIndicators(entry2.getValue());
				}
				sectorModel.setSubSectors(listOfSubsector);
				sectorModel.setTimePeriod(lastAggregatedTime);
				sectorModels.add(sectorModel);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sectorModels;
	}

	private List<List<ChartDataModel>> getChartDataValue(GroupIndicator groupIndicatorModel,
			Map<Integer, DataValue> mapData, Map<Integer, String> indicatorNameMap, String chartType,
			Map<Integer, String> cssGroupMap, Map<Integer, TimePeriod> timePeriodIdMap,
			List<TimePeriod> utTimeperiodList, String unit, String dashboardType,
			Map<Integer,Map<Integer, DataValue>> levelAllMapData) {
		
		List<List<Integer>> chartIndicators = null;
		
		String[] axisList = null ;
		chartIndicators = new ArrayList<>();

		chartIndicators = groupIndicatorModel.getChartIndicators();
		
		if(groupIndicatorModel.getChartLegends()!=null)
			axisList = groupIndicatorModel.getChartLegends().split(",");
		
		if(chartType.equals("pie") || chartType.equals("donut"))
			axisList = groupIndicatorModel.getColorLegends().split(",");
				
		List<List<ChartDataModel>> listChartDataModels = new LinkedList<>();
		List<ChartDataModel> chartDataModels = null;
		
		if(chartType.equals("trend")) {
			for (List<Integer> indList : chartIndicators) {
				chartDataModels = new LinkedList<>();
				
				for (int i=0; i< indList.size(); i++) {
					
					if(null!=utTimeperiodList) {
						for(TimePeriod timePeriod :utTimeperiodList) {
							String axis = null;

							axis = mapData ==null || mapData.get(timePeriod.getTimePeriodId()) == null ? timePeriod.getTimePeriod() + "-" +timePeriod.getYear()  :
								timePeriodIdMap.get(timePeriod.getTimePeriodId()).getTimePeriod() + "-"+
									timePeriodIdMap.get(timePeriod.getTimePeriodId()).getYear();
						
							getChartDataModel(indicatorNameMap, indList.get(i), mapData,"trend", 
									chartDataModels, cssGroupMap, axis, timePeriod.getTimePeriodId(), 
									unit, dashboardType, null, null, chartType, groupIndicatorModel.getChartHeader());
						};
					}
					
					listChartDataModels.add(chartDataModels);
				}
					}
					
					
		}else {
			int grCount = 0;
			
			for (List<Integer> indList : chartIndicators) {
					chartDataModels = new LinkedList<>();
					
					for (int i=0; i< indList.size(); i++) {
						
						String axis = null;
						axis = axisList!= null? axisList[i].split("_")[1] : null;
						
						String label = null;
						
						if(chartType.equals("stack") || chartType.equals("table"))
							label = groupIndicatorModel.getColorLegends()!= null? groupIndicatorModel.getColorLegends().split(",")[grCount].split("_")[1] : null;
						
					
						getChartDataModel(indicatorNameMap, indList.get(i), mapData,"all", chartDataModels, 
								cssGroupMap, axis, null, unit, dashboardType, label, levelAllMapData, chartType, groupIndicatorModel.getChartHeader());
					}
						listChartDataModels.add(chartDataModels);
						
						grCount++;
				}
			
			
		}
		
		return listChartDataModels;
	}


	private List<ChartDataModel> getChartDataModel(Map<Integer, String> indicatorNameMap, Integer indiId,
			Map<Integer, DataValue> mapData, String numeDeno, List<ChartDataModel> chartDataModels,
			Map<Integer, String> cssGroupMap, String axis, Integer timePeriodId,
			String unit, String dashboardType, String label, Map<Integer,Map<Integer, DataValue>> levelAllMapData, String chartType, String chartName) {
		
		ChartDataModel chartDataModel = new ChartDataModel();
		chartDataModel.setAxis(axis);
		chartDataModel.setLabel(label);
		if(numeDeno.equals("all")) {
			
//			Integer 43=Integer.parseInt(configurableEnvironment.getProperty("l1.typedetail.id"));
//			Integer 44=Integer.parseInt(configurableEnvironment.getProperty("l2.typedetail.id"));
//			Integer 45=Integer.parseInt(configurableEnvironment.getProperty("l3.typedetail.id"));
			
			if(dashboardType.equals("COVERAGE")) { //in coverage dashboard put 0 instead of null
				chartDataModel.setValue(
						(mapData == null || mapData.get(indiId) == null || mapData.get(indiId).getDataValue() == null) ? "0"
								: unit.equalsIgnoreCase("percentage")
										? String.valueOf(Math.round((mapData.get(indiId).getDataValue()) * 10.0) / 10.0)
										: String.valueOf(mapData.get(indiId).getDataValue().intValue()));
				
				chartDataModel.setTooltipValue(chartDataModel.getValue());
			}else {
				chartDataModel.setValue(
						(mapData == null || mapData.get(indiId) == null || mapData.get(indiId).getDataValue() == null) ? null
								: unit.equalsIgnoreCase("percentage")
										? String.valueOf(Math.round((mapData.get(indiId).getDataValue()) * 10.0) / 10.0)
										: String.valueOf(mapData.get(indiId).getDataValue().intValue()));
				
				if(  levelAllMapData==null) {
					chartDataModel.setTooltipValue(chartDataModel.getValue());
				}else {
					String dv = null;
					
					if (!chartName.equals(configurableEnvironment.getProperty("export.pdf.ind6"))) {
						dv = levelAllMapData.containsKey(43)
								? levelAllMapData.get(43).get(indiId) != null
								&& levelAllMapData.get(43).get(indiId).getDataValue() != null
										?  "L1 : " + levelAllMapData.get(43).get(indiId).getDataValue().intValue()
										: "L1 : " + "NA"  : "L1 : " + "NA" ;
					}
					
					if(dv!= null ) {
						dv = levelAllMapData.containsKey(44) ? levelAllMapData.get(44).get(indiId) != null
								&& levelAllMapData.get(44).get(indiId).getDataValue() != null
								? dv + ", L2 : " + levelAllMapData.get(44).get(indiId).getDataValue().intValue(): dv +", L2 : " + "NA"
						: dv +", L2 : " + "NA";
					}else {
						dv = levelAllMapData.containsKey(44) ? levelAllMapData.get(44).get(indiId) != null
								&& levelAllMapData.get(44).get(indiId).getDataValue() != null
								? "L2 : " + levelAllMapData.get(44).get(indiId).getDataValue().intValue()
								: "L2 : " + "NA"
						: "L2 : " + "NA";
						
					}
					
					
					dv = levelAllMapData.containsKey(45) ? levelAllMapData.get(45).get(indiId) != null
							&& levelAllMapData.get(45).get(indiId).getDataValue() != null
									? dv!= null ? dv + ", L3 : " + levelAllMapData.get(45).get(indiId).getDataValue().intValue()
											: " L3 : " + levelAllMapData.get(45).get(indiId).getDataValue().intValue()
									: dv +", L3 : " + "NA"
							: dv +", L3 : " + "NA";
									
				chartDataModel.setTooltipValue(dv);
				}
			}
			

			if(levelAllMapData!=null) {
				String num = null;
				
				if (!chartName.equals(configurableEnvironment.getProperty("export.pdf.ind6"))) {
					num = levelAllMapData.containsKey(43)
							? levelAllMapData.get(43).get(indiId) != null
							&& levelAllMapData.get(43).get(indiId).getNumerator() != null
									?  "L1 : " +  String.valueOf(Math.round(Double.parseDouble (levelAllMapData.get(43).get(indiId).getNumerator())))
									: "L1 : " + "NA"  : "L1 : " + "NA" ;
				}
				
		
						
			
				if(num!= null) {
					num = levelAllMapData.containsKey(44) ? levelAllMapData.get(44).get(indiId) != null
							&& levelAllMapData.get(44).get(indiId).getNumerator() != null
							? num + ", L2 : " + String.valueOf(Math.round(Double.parseDouble (levelAllMapData.get(44).get(indiId).getNumerator())))
							: num  +", L2 : " + "NA"
					: num +", L2 : " + "NA";
				}else {
					num = levelAllMapData.containsKey(44) ? levelAllMapData.get(44).get(indiId) != null
							&& levelAllMapData.get(44).get(indiId).getNumerator() != null
							?" L2 : " +  String.valueOf(Math.round(Double.parseDouble (levelAllMapData.get(44).get(indiId).getNumerator())))
							:"L2 : " + "NA"
					: "L2 : " + "NA";
				}
				
				num = levelAllMapData.containsKey(45) ? levelAllMapData.get(45).get(indiId) != null
						&& levelAllMapData.get(45).get(indiId).getNumerator() != null
								? num!= null ? num + ", L3 : " +  String.valueOf(Math.round(Double.parseDouble (levelAllMapData.get(45).get(indiId).getNumerator())))
								: " L3 : " +  String.valueOf(Math.round(Double.parseDouble (levelAllMapData.get(45).get(indiId).getNumerator())))
								: num  +", L3 : " + "NA"
						: num  +", L3 : " + "NA";
				
				chartDataModel.setNumerator(num);
				
				String deno = null;
				
				if (!chartName.equals(configurableEnvironment.getProperty("export.pdf.ind6"))) {
					deno = levelAllMapData.containsKey(43) ? levelAllMapData.get(43).get(indiId) != null
							&& levelAllMapData.get(43).get(indiId).getDenominator() != null
									? "L1 : " +  String.valueOf(Math.round(Double.parseDouble (levelAllMapData.get(43).get(indiId).getDenominator())))
									: "L1 : " + "NA" 
							: "L1 : " + "NA" ;
				}
				
				if(deno!=null) {
					deno = levelAllMapData.containsKey(44) ? levelAllMapData.get(44).get(indiId) != null
							&& levelAllMapData.get(44).get(indiId).getDenominator() != null
									? deno + ", L2 : " +  String.valueOf(Math.round(Double.parseDouble (levelAllMapData.get(44).get(indiId).getDenominator())))
									: deno +", L2 : " + "NA"  : deno  +", L2 : " + "NA";
				}else {
					deno = levelAllMapData.containsKey(44) ? levelAllMapData.get(44).get(indiId) != null
							&& levelAllMapData.get(44).get(indiId).getDenominator() != null
									? " L2 : " +  String.valueOf(Math.round(Double.parseDouble (levelAllMapData.get(44).get(indiId).getDenominator())))
									: "L2 : " + "NA"  : "L2 : " + "NA";
				}
				
				deno = levelAllMapData.containsKey(45) ? levelAllMapData.get(45).get(indiId) != null
						&& levelAllMapData.get(45).get(indiId).getDenominator() != null
								? deno!=null ? deno + ", L3 : " +  String.valueOf(Math.round(Double.parseDouble (levelAllMapData.get(45).get(indiId).getDenominator())))
								: " L3 : " +  String.valueOf(Math.round(Double.parseDouble (levelAllMapData.get(45).get(indiId).getDenominator())))
								: deno  +", L3 : " + "NA"
						: deno +", L3 : " + "NA";
				chartDataModel.setDenominator(deno);
			}else {
				if(!chartType.equals("table")) {
					chartDataModel.setNumerator(
							(mapData == null || mapData.get(indiId) == null || mapData.get(indiId).getNumerator() == null)
									? null
									: String.valueOf(Math.round(Double.parseDouble ( mapData.get(indiId).getNumerator()))));
					chartDataModel.setDenominator(
							(mapData == null || mapData.get(indiId) == null || mapData.get(indiId).getDenominator() == null)
									? null
									: String.valueOf(Math.round(Double.parseDouble ( mapData.get(indiId).getDenominator()))));
				}
				
			}
			
			
		}else { //trend chart
			
			if(dashboardType.equals("COVERAGE")) { //in coverage dashboard put 0 instead of null
				chartDataModel
				.setValue((mapData == null || mapData.get(timePeriodId) == null
						|| mapData.get(timePeriodId).getDataValue() == null)
								? "0"
								: unit.equalsIgnoreCase("percentage") ? String.valueOf(
										Math.round((mapData.get(timePeriodId).getDataValue()) * 10.0) / 10.0)
										: String.valueOf(mapData.get(timePeriodId).getDataValue().intValue()));
			}else {
				chartDataModel
				.setValue((mapData == null || mapData.get(timePeriodId) == null
						|| mapData.get(timePeriodId).getDataValue() == null)
								? null
								: unit.equalsIgnoreCase("percentage") ? String.valueOf(
										Math.round((mapData.get(timePeriodId).getDataValue()) * 10.0) / 10.0)
										: String.valueOf(mapData.get(timePeriodId).getDataValue().intValue()));
			}

			chartDataModel.setNumerator((mapData == null || mapData.get(timePeriodId) == null
					|| mapData.get(timePeriodId).getNumerator() == null) ? null
							: String.valueOf(Math.round(Double.parseDouble(mapData.get(timePeriodId).getNumerator()))));
			chartDataModel.setDenominator((mapData == null || mapData.get(timePeriodId) == null
					|| mapData.get(timePeriodId).getDenominator() == null) ? null
							: String.valueOf(
									Math.round(Double.parseDouble(mapData.get(timePeriodId).getDenominator()))));
		}
		chartDataModel.setLegend(axis);
		chartDataModel.setId(indiId);
		chartDataModel.setUnit(unit);
		chartDataModels.add(chartDataModel);
		
		return chartDataModels;
	}


	@Override
//	@Scheduled(cron="0 0 3 1/1 * ?")	
	
	//cron="0 0 3 1/1 * ?" -> every day  3:00 AM
	
//	@Scheduled(cron="0 0 0/7 1/1 * ?")
	/*cron="0 0 0/7 1/1 * ?" -> 7hrs gap
	1.	Wednesday, September 4, 2019 7:00 AM
	2.	Wednesday, September 4, 2019 2:00 PM
	3.	Wednesday, September 4, 2019 9:00 PM
	4.	Thursday, September 5, 2019 12:00 AM
	5.	Thursday, September 5, 2019 7:00 AM*/

	public String aggregate(){
		List<CoverageData> dataValueList;
		dataValueList=new ArrayList<>();
		
		Date createdDate = new Date();
		
		List<String> sectors = new ArrayList<>();
		sectors = Arrays.asList("14", "15", "16", "17");
		
		List<Indicator> indicatorList = mongoIndicatorRepository.getIndicatorBySectorIds(sectors);
		
		indicatorList.stream().filter(indicator->!indicator.getIndicatorDataMap().get("collection").equals("dataValue")).forEach(indicator->{
			Class<?> clazz=null;
			if(String.valueOf(indicator.getIndicatorDataMap().get("collection")).contains("AllChecklistFormData")) {
				clazz=AllChecklistFormData.class;
			}else {
				clazz=Area.class;
			}
			switch (String.valueOf(indicator.getIndicatorDataMap().get("parentType"))) {
			case "dropdown":
				List<Integer> tdlist=new ArrayList<>();
				Arrays.asList(String.valueOf(indicator.getIndicatorDataMap().get("typeDetailId")).split("#")).stream().forEach(i->{tdlist.add(Integer.parseInt(i));});
				List<Map> dataList= mongoTemplate.aggregate(getDropdownAggregationResults(
						Integer.valueOf((String) indicator.getIndicatorDataMap().get("formId")),
						 String.valueOf(indicator.getIndicatorDataMap().get("area")),
						String.valueOf(indicator.getIndicatorDataMap().get("collection")),
						String.valueOf(indicator.getIndicatorDataMap().get("numerator")),
						tdlist,
						String.valueOf(indicator.getIndicatorDataMap().get("indicatorName"))),clazz, Map.class).getMappedResults();
				dataList.forEach(data->{
					CoverageData datadoc=new CoverageData();
					datadoc.setInid(Integer.valueOf(String.valueOf(indicator.getIndicatorDataMap().get("indicatorNid"))));
					datadoc.setAreaId(Integer.valueOf(String.valueOf(data.get("_id"))));
					datadoc.setDataValue(Double.valueOf(String.valueOf(data.get("value"))));
					datadoc.set_case(String.valueOf(indicator.getIndicatorDataMap().get("aggregationType")));
					datadoc.setCreatedDate(createdDate);
					dataValueList.add(datadoc);
				});
				break;
				
			case "table":
				List<Map> tableDataList= mongoTemplate.aggregate(getTableAggregationResults(
						Integer.valueOf((String) indicator.getIndicatorDataMap().get("formId")),
						 String.valueOf(indicator.getIndicatorDataMap().get("area")),
						String.valueOf(indicator.getIndicatorDataMap().get("collection")),
						String.valueOf(indicator.getIndicatorDataMap().get("numerator")),
						 String.valueOf(indicator.getIndicatorDataMap().get("parentColumn")),
						 String.valueOf(indicator.getIndicatorDataMap().get("indicatorName"))),clazz, Map.class).getMappedResults();
				tableDataList.forEach(data->{
					CoverageData datadoc=new CoverageData();
					datadoc.setInid(Integer.valueOf(String.valueOf(indicator.getIndicatorDataMap().get("indicatorNid"))));
					datadoc.setAreaId(Integer.valueOf(String.valueOf(data.get("_id"))));
					datadoc.setDataValue(Double.valueOf(String.valueOf(data.get("value"))));
					datadoc.set_case(String.valueOf(indicator.getIndicatorDataMap().get("aggregationType")));
					datadoc.setCreatedDate(createdDate);
					dataValueList.add(datadoc);
				});
				
				break;

			case "numeric":
				List<Map> numericDataList= mongoTemplate.aggregate(getNumericAggregationResults(
						Integer.valueOf((String) indicator.getIndicatorDataMap().get("formId")),
						 String.valueOf(indicator.getIndicatorDataMap().get("area")),
						String.valueOf(indicator.getIndicatorDataMap().get("collection")),
						String.valueOf(indicator.getIndicatorDataMap().get("numerator")),
						String.valueOf(indicator.getIndicatorDataMap().get("indicatorName")),
						String.valueOf(indicator.getIndicatorDataMap().get("aggregationRule"))),clazz, Map.class).getMappedResults();
				
				numericDataList.forEach(data->{
					CoverageData datadoc=new CoverageData();
					datadoc.setInid(Integer.valueOf(String.valueOf(indicator.getIndicatorDataMap().get("indicatorNid"))));
					datadoc.setAreaId(Integer.valueOf(String.valueOf(data.get("_id"))));
					datadoc.setDataValue(Double.valueOf(String.valueOf(data.get("value"))));
					datadoc.set_case(String.valueOf(indicator.getIndicatorDataMap().get("aggregationType")));
					datadoc.setCreatedDate(createdDate);
					dataValueList.add(datadoc);
				});
				break;
				
			case "form":
				switch (String.valueOf(indicator.getIndicatorDataMap().get("aggregationRule")).split(":")[0]) {
				case "unique":
					List<Map> uniqueCountData=mongoTemplate.aggregate(getUniqueCount(
							Integer.valueOf((String) indicator.getIndicatorDataMap().get("formId")), 
							String.valueOf(indicator.getIndicatorDataMap().get("area")), 
							String.valueOf(indicator.getIndicatorDataMap().get("collection")), 
							String.valueOf(indicator.getIndicatorDataMap().get("indicatorName")),
							String.valueOf(indicator.getIndicatorDataMap().get("numerator")),
							String.valueOf(indicator.getIndicatorDataMap().get("aggregationRule")).split(":").length>1
							?String.valueOf(indicator.getIndicatorDataMap().get("aggregationRule")).split(":")[1]:""), clazz,Map.class).getMappedResults();
//					System.out.println("uniqueCountData :: "+uniqueCountData);
					uniqueCountData.forEach(data->{
						CoverageData datadoc=new CoverageData();
						datadoc.setInid(Integer.valueOf(String.valueOf(indicator.getIndicatorDataMap().get("indicatorNid"))));
						datadoc.setAreaId(Integer.valueOf(String.valueOf(data.get("_id"))));
						datadoc.setDataValue(Double.valueOf(String.valueOf(data.get("dataValue"))));
						datadoc.set_case(String.valueOf(indicator.getIndicatorDataMap().get("aggregationType")));
						datadoc.setCreatedDate(createdDate);
						dataValueList.add(datadoc);
					});
					break;
					
				case "total":
				List<Map> visitCountData=mongoTemplate.aggregate(getTotalVisitCount(
						Integer.valueOf((String) indicator.getIndicatorDataMap().get("formId")), 
						String.valueOf(indicator.getIndicatorDataMap().get("area"))), clazz,Map.class).getMappedResults();
				visitCountData.forEach(data->{
					CoverageData datadoc=new CoverageData();
					datadoc.setInid(Integer.valueOf(String.valueOf(indicator.getIndicatorDataMap().get("indicatorNid"))));
					if(data.get("_id")!=null) {
						datadoc.setAreaId(Integer.valueOf(String.valueOf(data.get("_id"))));
					}
				
					datadoc.setDataValue(Double.valueOf(String.valueOf(data.get("dataValue"))));
					datadoc.set_case(String.valueOf(indicator.getIndicatorDataMap().get("aggregationType")));
					datadoc.setCreatedDate(createdDate);
					dataValueList.add(datadoc);
				});
					break;
					
				case "gte":
				case "lte":
				case "eq":
				case "gt":
				case "lt":
				Integer value=Integer.parseInt(String.valueOf(indicator.getIndicatorDataMap().get("typeDetailId")));
				List<Map> gteCountData=mongoTemplate.aggregate(getCount(
						Integer.valueOf((String) indicator.getIndicatorDataMap().get("formId")), 
						String.valueOf(indicator.getIndicatorDataMap().get("area")),
						String.valueOf(indicator.getIndicatorDataMap().get("numerator")),
						value,String.valueOf(indicator.getIndicatorDataMap().get("aggregationRule"))),clazz,Map.class).getMappedResults();
				gteCountData.forEach(data->{
					CoverageData datadoc=new CoverageData();
					datadoc.setInid(Integer.valueOf(String.valueOf(indicator.getIndicatorDataMap().get("indicatorNid"))));
					datadoc.setAreaId(Integer.valueOf(String.valueOf(data.get("_id"))));
					datadoc.setDataValue(Double.valueOf(String.valueOf(data.get("dataValue"))));
					datadoc.set_case(String.valueOf(indicator.getIndicatorDataMap().get("aggregationType")));
					datadoc.setCreatedDate(createdDate);
					dataValueList.add(datadoc);
				});
					break;
				case "repeatCount":
					List<Integer> valueList=new ArrayList<>();
							Arrays.asList(
									String.valueOf(indicator.getIndicatorDataMap().get("typeDetailId")).split("#"))
									.stream().forEach(i -> {
										if (!i.equals(""))
											valueList.add(Integer.parseInt(i));
									});
							
					List<Map> repeatCountData=mongoTemplate.aggregate(getRepeatCountQuery(
							Integer.valueOf((String) indicator.getIndicatorDataMap().get("formId")), 
							String.valueOf(indicator.getIndicatorDataMap().get("area")),
							String.valueOf(indicator.getIndicatorDataMap().get("numerator")),
							valueList,String.valueOf(indicator.getIndicatorDataMap().get("aggregationRule"))),clazz,Map.class).getMappedResults();
					repeatCountData.forEach(data->{
						
						if(data.get(String.valueOf(indicator.getIndicatorDataMap().get("area")).split("\\.")[1])!=null) {
							CoverageData datadoc=new CoverageData();
							datadoc.setInid(Integer.valueOf(String.valueOf(indicator.getIndicatorDataMap().get("indicatorNid"))));
							datadoc.setAreaId(Integer.valueOf(String.valueOf(data.get(String.valueOf(indicator.getIndicatorDataMap().get("area")).split("\\.")[1]))));
							datadoc.setDataValue(Double.valueOf(String.valueOf(data.get("dataValue"))));
							datadoc.set_case(String.valueOf(indicator.getIndicatorDataMap().get("aggregationType")));
							datadoc.setCreatedDate(createdDate);
							dataValueList.add(datadoc);
						}
						
					});
					break;

				default:
					break;
				}
				
				break;
				
			case "area":
				String[] rules= String.valueOf(indicator.getIndicatorDataMap().get("aggregationRule")).split(";");
				Integer value1=Integer.parseInt(String.valueOf(indicator.getIndicatorDataMap().get("typeDetailId")));
				List<Map> areaCountData=mongoTemplate.aggregate(getAreaCount(
						String.valueOf(indicator.getIndicatorDataMap().get("area")),
						String.valueOf(indicator.getIndicatorDataMap().get("numerator")),
						value1,rules),clazz,Map.class).getMappedResults();
				areaCountData.forEach(data->{
					if(!String.valueOf(data.get("_id")).equals("null")) {
						CoverageData datadoc=new CoverageData();
						datadoc.setInid(Integer.valueOf(String.valueOf(indicator.getIndicatorDataMap().get("indicatorNid"))));
						datadoc.setAreaId(Integer.valueOf(String.valueOf(data.get("_id"))));
						datadoc.setDataValue(Double.valueOf(String.valueOf(data.get("dataValue"))));
						datadoc.set_case(String.valueOf(indicator.getIndicatorDataMap().get("aggregationType")));
						datadoc.setCreatedDate(createdDate);
						dataValueList.add(datadoc);
					}
				});
				break;
				
			default:
				break;
			}
			
		});
		

		//first clear all entity then persist new
		coverageDataRepository.deleteAll();
		coverageDataRepository.save(dataValueList);
		try {
			int areaLevel = Integer.parseInt(configurableEnvironment.getProperty("spark.aggregation.arealevel"));
			for (int i = areaLevel; i >0; i--) {
				AreaMapObject amb = getAreaForAggregation(i);
				List<Integer> areaList=amb.getAreaList();
//				System.out.println(areaList);
			List<Map> areaDataMap=mongoTemplate.aggregate(aggregateAreaTree(areaList),CoverageData.class,Map.class).getMappedResults();
			List<CoverageData> datalist2=new ArrayList<>();
			areaDataMap.forEach(data->{
				CoverageData datavalue=new CoverageData();
				datavalue.setInid(Integer.valueOf(String.valueOf(data.get("inid"))));
				datavalue.setAreaId(Integer.valueOf(String.valueOf(data.get("parentAreaId"))));
				datavalue.setDataValue(Double.valueOf(String.valueOf(data.get("dataValue"))));
				datavalue.set_case(String.valueOf(data.get("_case")));
				datavalue.setCreatedDate(createdDate);
				datalist2.add(datavalue);
			});
			coverageDataRepository.save(datalist2);
			}
			
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			URL url = loader.getResource("aggregation/HWC numbers in Aspirational District_r2.xlsx");
			String path = url.getPath().replaceAll("%20", " ");
			File file = new File(path);

//			if (files == null) {
//				throw new RuntimeException("No file found in path " + path);
//			}
			Map<String, Area> areaCodeObjMap = new HashMap<>();
			List<Area> areas = mongoAreaRepository.findAllByAreaLevelAreaLevelIdInOrderByAreaIdAsc(Arrays.asList(1,2,3));
			for (Area area : areas) {
				areaCodeObjMap.put(area.getAreaCode(), area);
			}

//			for (int f = 0; f < 1; f++) {

				XSSFWorkbook workbook = null;

				try {
					workbook = new XSSFWorkbook(file);
				} catch (InvalidFormatException | IOException e) {

					e.printStackTrace();
				}
				
				XSSFSheet hwcSheet = workbook.getSheetAt(0);
				
				
				List<CoverageData> coverageDatas = new ArrayList<>();
				
				XSSFRow indRow = hwcSheet.getRow(2);
				
				for (int row = 3; row <= hwcSheet.getLastRowNum(); row++) {
					
					XSSFRow xssfRow = hwcSheet.getRow(row);
					
					for (int cols = 1; cols <5; cols++) {// column loop

						Cell cell = xssfRow.getCell(cols);

						CoverageData datavalue=new CoverageData();
						datavalue.setInid((int) indRow.getCell(cols).getNumericCellValue()); //indRow -- 1st row contains indicatorid
//						System.out.println(xssfRow.getCell(0).getStringCellValue());
						datavalue.setAreaId(areaCodeObjMap.get(xssfRow.getCell(0).getStringCellValue()).getAreaId()); //first cell contains area id
						datavalue.setDataValue(cell==null ?0 : cell.getCellTypeEnum() == CellType.BLANK ? 0 : cell.getNumericCellValue()); //put 0 in case of blank cell as per requirement
						datavalue.set_case("number");
						datavalue.setCreatedDate(createdDate);
						coverageDatas.add(datavalue);
					}
					
				}
				
				coverageDataRepository.save(coverageDatas);
			
			aggregateFinalIndicators("indicator", sectors);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("error encountered :: ");
			e.printStackTrace();
		}
		return "aggregation complete";
	}
	
	public AreaMapObject getAreaForAggregation(Integer areaLevel) {
		AreaMapObject amb = new AreaMapObject();
		Map<Integer, Integer> areaMap = new HashMap<Integer, Integer>();
		List<Integer> childIds = new ArrayList<Integer>();
		List<Area> childList = mongoAreaRepository.findByAreaLevel(mongoAreaLevelRepository.findByAreaLevelId(areaLevel));
		childList.forEach(child -> {
			childIds.add(child.getAreaId());
			areaMap.put(child.getAreaId(), child.getParentAreaId());
		});
		amb.setAreaList(childIds);
		amb.setAreaMap(areaMap);
		return amb;
	}
	
	List<CoverageData> percentDataMap=null;
	List<CoverageData> percentDataMapAll=null;
	
	
	public List<CoverageData> aggregateFinalIndicators( String indicatorType,List<String> sectors) {
		
		percentDataMap=new ArrayList<>();
		percentDataMapAll=new ArrayList<>();
		List<Indicator> indicatorList = mongoIndicatorRepository.getPercentageSectorWiseIndicators(sectors,indicatorType);
		indicatorList.forEach(indicator->{
			List<Integer> dependencies=new ArrayList<>();
			List<Integer> numlist=new ArrayList<>();
			String[] numerators=String.valueOf(indicator.getIndicatorDataMap().get("numerator")).split(",");
			Integer inid=Integer.parseInt(String.valueOf(indicator.getIndicatorDataMap().get("indicatorNid")));
			String aggrule=String.valueOf(indicator.getIndicatorDataMap().get("aggregationRule"));
			for (int i = 0; i < numerators.length; i++) {
				numlist.add(Integer.parseInt(numerators[i]));
				dependencies.add(Integer.parseInt(numerators[i]));
			}
			List<Integer> denolist=new ArrayList<>();
			String[] denominators=String.valueOf(indicator.getIndicatorDataMap().get("denominator")).split(",");
			for (int i = 0; i < denominators.length; i++) {
				denolist.add(Integer.parseInt(denominators[i]));
				dependencies.add(Integer.parseInt(denominators[i]));
			}
			try {
				percentDataMap=mongoTemplate.aggregate(getPercentData(dependencies,numlist,denolist,aggrule),CoverageData.class,CoverageData.class).getMappedResults();
//				List<Map> x = 	mongoTemplate.aggregate(getPercentData(dependencies,numlist,denolist),CoverageData.class,Map.class).getMappedResults();
//				System.out.println(x);
				percentDataMap.forEach(dv->{
					dv.setInid(inid);
					dv.set_case("percent");
				});
				percentDataMapAll.addAll(percentDataMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		coverageDataRepository.save(percentDataMapAll);
		return percentDataMapAll;
	}
	
	
	private TypedAggregation<CoverageData> aggregateAreaTree(List<Integer> areaList) {
		
		MatchOperation matchOperation=Aggregation.match(Criteria.where("areaId").in(areaList));
		LookupOperation lookupOperation=Aggregation.lookup("area", "areaId", "areaId", "parent");
		GroupOperation groupOperation=Aggregation.group("parent.parentAreaId","inid","_case").sum("dataValue").as("dataValue");
		UnwindOperation unwindOperation=Aggregation.unwind("$_id.parentAreaId");
		
		return Aggregation.newAggregation(CoverageData.class,matchOperation,lookupOperation,groupOperation,unwindOperation);
	}
	
	private TypedAggregation<CoverageData> getPercentData(List<Integer> dep,List<Integer> num,List<Integer> deno, String rule){
		MatchOperation matchOperation = Aggregation.match(Criteria.where("inid").in(dep));
		
		GroupOperation groupOperation=Aggregation.group("areaId","createdDate").sum(when(where("inid").in(num)).thenValueOf("$dataValue").otherwise(0)).as("numerator")
				.sum(when(where("inid").in(deno)).thenValueOf("$dataValue").otherwise(0)).as("denominator")
				;
		
		ProjectionOperation projectionOperation=Aggregation.project().and("_id.areaId").as("areaId")
				.and("numerator").as("numerator").and("denominator").as("denominator")
				.and("_id.createdDate").as("createdDate")
				.andExclude("_id")
				.and(when(where("denominator").gt(0)).thenValueOf(Divide.valueOf(Multiply.valueOf("numerator")
				.multiplyBy(100)).divideBy("denominator")).otherwise(0)).as("dataValue");
		return Aggregation.newAggregation(CoverageData.class,matchOperation,groupOperation, projectionOperation);
	}
	
	
	public Aggregation getDropdownAggregationResults(Integer formId, String area, String collection, String path, List<Integer> tdlist,String name) {
		MatchOperation matchOperation = Aggregation.match(Criteria.where("formId").is(formId).and("data."+path).in(tdlist).and("duplicate").is(false));
		ProjectionOperation projectionOperation=Aggregation.project().and("data").as("data");
		GroupOperation groupOperation= Aggregation.group(area).count().as("value");
		return Aggregation.newAggregation(matchOperation,projectionOperation,groupOperation);
	}
	
	public Aggregation getTableAggregationResults(Integer formId, String area, String collection, String path, String table,String name) {
		MatchOperation matchOperation = Aggregation.match(Criteria.where("formId").is(formId).and("duplicate").is(false));
		ProjectionOperation projectionOperation=Aggregation.project().and("data").as("data");
		UnwindOperation unwindOperation = Aggregation.unwind("data."+table);
		GroupOperation groupOperation= Aggregation.group(area).sum("data."+table+"."+path).as("value");
		return Aggregation.newAggregation(matchOperation,projectionOperation,unwindOperation,groupOperation);
	}
	
	public Aggregation getNumericAggregationResults(Integer formId, String area, String collection, String path,String name,String conditions) {
		
		List<String> condarr=new ArrayList<>();
		if(!conditions.equals("null")&&!conditions.isEmpty())
			condarr=Arrays.asList(conditions.split(";"));
		Criteria matchCriteria=Criteria.where("formId").is(formId).and("duplicate").is(false);
		if(!condarr.isEmpty()) {
		condarr.forEach(_cond->{
			matchCriteria.andOperator(Criteria.where(_cond.split(":")[0].split("\\(")[1]).is(Integer.parseInt(_cond.split(":")[1].split("\\)")[0])));
		});
		}
		String pathString="";
		path="data."+path;
		path=path.replace("+", "+data.");
		path=path.replace("-", "-data.");
		pathString=path;
		MatchOperation matchOperation = Aggregation.match(matchCriteria);
		ProjectionOperation projectionOperation=null;
		ProjectionOperation pop=null;
		GroupOperation groupOperation=null;
		if(pathString.contains("+")||pathString.contains("-")) {
			projectionOperation=Aggregation.project().and("data").as("data");
			pop=Aggregation.project().and(area).as("area").andExpression(pathString).as("value1");
			groupOperation= Aggregation.group("area").sum("value1").as("value");
			return Aggregation.newAggregation(matchOperation,projectionOperation,pop,groupOperation);
		}else {
			projectionOperation=Aggregation.project().and("data").as("data");
			groupOperation= Aggregation.group(area).sum(pathString).as("value");
			return Aggregation.newAggregation(matchOperation,projectionOperation,groupOperation);
		}
//		MatchOperation matchOperation = Aggregation.match(Criteria.where("formId").is(formId));
//		ProjectionOperation projectionOperation=Aggregation.project().and("data").as("data");
//		GroupOperation groupOperation= Aggregation.group(area).sum("data."+path).as("value");
//		return Aggregation.newAggregation(matchOperation,projectionOperation,groupOperation);
	}
	
	public Aggregation getUniqueCount(Integer formId, String area, String collection, String name,String childArea,String conditions) {
		
		List<String> condarr=new ArrayList<>();
		if(!conditions.isEmpty())
			condarr=Arrays.asList(conditions.split(","));
		Criteria criteria = Criteria.where("formId").is(formId).and("duplicate").is(false);
		if(!condarr.isEmpty()) {
		condarr.forEach(_cond->{
			criteria.andOperator(Criteria.where("data."+_cond.split("=")[0]).is(Integer.parseInt(_cond.split("=")[1])));
		});
		}
		
		MatchOperation matchOperation=Aggregation.match(criteria);
		ProjectionOperation projectionOperation=Aggregation.project().and("data").as("data");
		GroupOperation groupOperation=Aggregation.group(area).addToSet("data."+childArea).as("childArea");
		UnwindOperation unwindOperation=Aggregation.unwind("childArea");
		GroupOperation groupOperation2=Aggregation.group("$_id").count().as("dataValue");
		return Aggregation.newAggregation(matchOperation,projectionOperation,groupOperation,unwindOperation,groupOperation2);
	}
	
	public Aggregation getTotalVisitCount(Integer formId, String area) {
		MatchOperation matchOperation=Aggregation.match(Criteria.where("formId").is(formId).and("duplicate").is(false));
		ProjectionOperation projectionOperation=Aggregation.project().and("data").as("data");
		GroupOperation groupOperation=Aggregation.group(area).count().as("dataValue");
		return Aggregation.newAggregation(matchOperation,projectionOperation,groupOperation);
	}
	
	public Aggregation getCount(Integer formId, String area, String path, Integer value, String rule) {
		MatchOperation matchOperation=null;
		switch (rule) {
		case "eq":
			matchOperation=Aggregation.match(Criteria.where("formId").is(formId).and("data."+path).is(value).and("duplicate").is(false));
			break;
		case "lte":
			matchOperation=Aggregation.match(Criteria.where("formId").is(formId).and("data."+path).lte(value).and("duplicate").is(false));
			break;
		case "gte":
			matchOperation=Aggregation.match(Criteria.where("formId").is(formId).and("data."+path).gte(value).and("duplicate").is(false));
			break;
		case "gt":
			matchOperation=Aggregation.match(Criteria.where("formId").is(formId).and("data."+path).gt(value).and("duplicate").is(false));
			break;
		case "lt":
			matchOperation=Aggregation.match(Criteria.where("formId").is(formId).and("data."+path).lt(value).and("duplicate").is(false));
			break;

		default:
			break;
		}
		
		ProjectionOperation projectionOperation=Aggregation.project().and("data").as("data");
		GroupOperation groupOperation=Aggregation.group(area).count().as("dataValue");
		return Aggregation.newAggregation(matchOperation,projectionOperation,groupOperation);
	}
	
	public Aggregation getRepeatCountQuery(Integer formId, String area, String path, List<Integer> valueList,
			String query) {

		MatchOperation matchOperation = null;
		ProjectionOperation projectionOperation = null;
		GroupOperation groupOperation = null;
		ProjectionOperation projectionOperation2 = null;
		GroupOperation groupOperation2 = null;
		
		ProjectionOperation projectionOperation3 = null;
		if (path.equals("")) {
			matchOperation = Aggregation.match(Criteria.where("formId").is(formId).and("duplicate").is(false));
			projectionOperation = Aggregation.project().and("data").as("data");
			groupOperation = Aggregation.group(query.split(":")[1], area).count().as("totalcount");
			projectionOperation2 = Aggregation.project(area.split("\\.")[1])
					.and(when(where("totalcount").gt(1)).then(1).otherwise(0))
					.as("repeatCount");
			groupOperation2 = Aggregation.group(area.split("\\.")[1]).sum("repeatCount").as("dataValue");
			
			projectionOperation3 = Aggregation.project().and("_id").as(area.split("\\.")[1])
					.and("dataValue").as("dataValue");
			
			
		} else {
			matchOperation = Aggregation.match(Criteria.where("formId").is(formId).and("data."+path).in(valueList).and("duplicate").is(false));
			projectionOperation = Aggregation.project().and("data").as("data");
			groupOperation = Aggregation.group(query.split(":")[1], "data."+path, area).count()
					.as("totalcount");
			projectionOperation2 = Aggregation.project(path, area.split("\\.")[1])
					.and(when(where("totalcount").gt(1)).then(1).otherwise(0))
					.as("repeatCount");
			groupOperation2 = Aggregation.group(path, area.split("\\.")[1]).sum("repeatCount").as("dataValue");
			
			projectionOperation3 = Aggregation.project().and("_id."+area.split("\\.")[1]).as(area.split("\\.")[1])
					.and("dataValue").as("dataValue");
			
		}
		
		return Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation, projectionOperation2,groupOperation2,projectionOperation3);

		
	}
	
	public Aggregation getAreaCount(String area, String path,Integer value,String[] rules) {
		Criteria finalCriteria=new Criteria();
		Criteria criteria = Criteria.where(path).is(value);
		Criteria orCriteria=new Criteria();
//		Criteria andCriteria=new Criteria();

		List<Criteria> orCriterias = new ArrayList<Criteria>();
		List<Criteria> andCriterias=new ArrayList<Criteria>();

		for (String rule: rules) {
			switch (rule.split("\\(")[0]) {
			case "eq":
				criteria=criteria.and(rule.split("\\(")[1].split(":")[0]).is(Integer.parseInt(rule.split("\\(")[1].split(":")[1].split("\\)")[0]));
//				docCriterias.add(Criteria.where(rule.split("\\(")[1].split(":")[0]).is(rule.split("\\(")[1].split(":")[1].split("\\)")[0]));
				break;
			case "and$in" :
//				criteria=criteria.and(rule.split("\\(")[1].split(":")[0]).in(Arrays.asList(rule.split("\\[")[1].split("\\]")[0].split(",")));
				List<Integer> andtd=new ArrayList<>();
				Arrays.asList(rule.split("\\[")[1].split("\\]")[0].split(",")).forEach(v->andtd.add(Integer.parseInt(v)));
				andCriterias.add(Criteria.where(rule.split("\\(")[1].split(":")[0]).in(andtd));
				break;
			case "or$in" :
//				orCriteria=orCriteria.orOperator(Criteria.where(rule.split("\\(")[1].split(":")[0]).in(Arrays.asList(rule.split("\\[")[1].split("\\]")[0].split(","))));
				List<Integer> ortd=new ArrayList<>();
				Arrays.asList(rule.split("\\[")[1].split("\\]")[0].split(",")).forEach(v->ortd.add(Integer.parseInt(v)));
				orCriterias.add(Criteria.where(rule.split("\\(")[1].split(":")[0]).in(ortd));
				break;
			}
		    
		}
		if (!andCriterias.isEmpty()) {
			criteria=criteria.andOperator(andCriterias.toArray(new Criteria[andCriterias.size()]));
		}
		if(orCriterias.size()!=0)
			criteria = criteria.orOperator(orCriterias.toArray(new Criteria[orCriterias.size()]));
//		finalCriteria=finalCriteria.andOperator(criteria,orCriteria);
		MatchOperation matchOperation = Aggregation.match(criteria);
		GroupOperation groupOperation=Aggregation.group(area).count().as("dataValue");
		return Aggregation.newAggregation(matchOperation,groupOperation);
	}

	@Override
	public ResponseEntity<List<AreaLevelModel>> getAreaLevels() {

		List<AreaLevel> areaLevels = mongoAreaLevelRepository.findAll();
		List<AreaLevelModel> areaLevelModels = new ArrayList<>();
		
		for (AreaLevel areaLevel : areaLevels) {
			
			if(areaLevel.getAreaLevelName().equalsIgnoreCase(configurableEnvironment.getProperty("facility.area.level.name")) ||
					areaLevel.getAreaLevelName().equalsIgnoreCase(configurableEnvironment.getProperty("block.area.level.name"))){
				continue;
			}
			AreaLevelModel areaLevelModel = new AreaLevelModel();
			areaLevelModel.setAreaLevelId(areaLevel.getAreaLevelId());
			areaLevelModel.setAreaLevelName(areaLevel.getAreaLevelName());
			areaLevelModels.add(areaLevelModel);
			
		}

		return new ResponseEntity<>(areaLevelModels, HttpStatus.OK);
	}

//	public Predicate<AreaLevel> findFacilityLevel(String facility) {
//		return f -> f.getAreaLevelName().equalsIgnoreCase(facility);
//	}
	

	// get all typdetails by type name
	@Override
	public ResponseEntity<List<TypeDetailModel>> getTypeDetailsByType(String typeName) {

		List<TypeDetail> typeDetails = customTypeDetailRepository.findByTypeTypeName(typeName);
		List<TypeDetailModel> models= new ArrayList<>();
		
		for (TypeDetail typeDetail : typeDetails) {
			TypeDetailModel detailModel = new TypeDetailModel();
			detailModel.setSlugId(typeDetail.getSlugId());
			detailModel.setName(typeDetail.getName());
			detailModel.setOrderLevel(typeDetail.getOrderLevel());
			models.add(detailModel);
		}
		return new ResponseEntity<>(models, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<List<TypeDetailModel>> getTypeDetailsByFormAndType(Integer formId, String typeName) {

		List<TypeDetail> typeDetails = customTypeDetailRepository.findByFormIdAndTypeTypeName(formId, typeName);
		List<TypeDetailModel> models= new ArrayList<>();
		
		for (TypeDetail typeDetail : typeDetails) {
			TypeDetailModel detailModel = new TypeDetailModel();
			detailModel.setSlugId(typeDetail.getSlugId());
			detailModel.setName(typeDetail.getName());
			detailModel.setOrderLevel(typeDetail.getOrderLevel());
			models.add(detailModel);
		}
		return new ResponseEntity<>(models, HttpStatus.OK);
	}
	
	@Override
	public Map<String, List<AreaModel>> getAllAreaList() {

		List<Area> areas = mongoAreaRepository.findAllByAreaLevelAreaLevelIdIn(Arrays.asList(2, 3, 4));
		List<AreaModel> areaModelList = new ArrayList<>();
		Map<String, List<AreaModel>> areaMap = new LinkedHashMap<>();
		// setting areas is area-model list
		for (Area area : areas) {
			AreaModel areaModel = new AreaModel();
			areaModel.setAreaCode(area.getAreaCode());
			areaModel.setAreaId(area.getAreaId());
			areaModel.setAreaLevel(area.getAreaLevel().getAreaLevelName());
			areaModel.setAreaName(area.getAreaName());
			areaModel.setParentAreaId(area.getParentAreaId());
			areaModelList.add(areaModel);
		}
		// making levelName as a key
		for (AreaModel areaModel : areaModelList) {
			if (areaMap.containsKey(areaModel.getAreaLevel())) {
				areaMap.get(areaModel.getAreaLevel()).add(areaModel);
			} else {
				areaModelList = new ArrayList<>();
				areaModelList.add(areaModel);
				areaMap.put(areaModel.getAreaLevel(), areaModelList);
			}
		}
		return areaMap;
	}
	
	@Override
	public ResponseEntity<List<TimeperiodModel>> getAllTimePeriod(Boolean isAggregate){
		
		List<TimePeriod> timePeriods = null;
		timePeriods = timePeriodRepository.findAllByOrderByStartDateDesc();
		
//		in case of aggregation show legacy tps only
		if(isAggregate!=null) {
			timePeriods.remove(0);
			timePeriods.remove(0);
		}
				
//		timePeriods.remove(0);//uncomment post UAT
		
		List<TimeperiodModel> models = new ArrayList<>();
		
		try {
			for (TimePeriod tp : timePeriods) {
				
				TimeperiodModel model= new TimeperiodModel();
				model.setTpId(tp.getTimePeriodId());
				model.setTpName(tp.getTimePeriod());
				model.setFinancialYear(tp.getFinancialYear());
				model.setPeriodicity(tp.getPeriodicity());
				model.setYear(tp.getYear());
				models.add(model);
			}
			
			return new ResponseEntity<>(models, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(models, HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@Override
	public String aggregateLegacyData(Integer tp, String tpName, String periodicity) {
	
		AggregateLegacyDataStatus aggregateLegacyDataStatus = new AggregateLegacyDataStatus();
		aggregateLegacyDataStatus.setStartTime(new Date());
		aggregateLegacyDataStatus.setTimePeriod(tpName);
		aggregateLegacyDataStatus.setPeriodicity(periodicity);
		aggregateLegacyDataStatus.setTpId(tp);
		aggregateLegacyDataStatus.setStatus("PENDING");
		aggregateLegacyDataStatus.setLegacy(true);
		AggregateLegacyDataStatus saved =aggregateLegacyDataStatusRepository.save(aggregateLegacyDataStatus);
	    
		collectionQueryChannel.aggregateOutChannel().send(MessageBuilder.withPayload(saved.getId()).build());
		
		return "done";
	}
	
	@StreamListener(value = CollectionQueryChannel.RMNCHAAGGREGATE_INPUTCHANNEL)
	public void saveLegacyData(String id) {

		System.out.println("stopThread-->>"+ stopThread);
		AggregateLegacyDataStatus aggregateLegacyDataStatus = aggregateLegacyDataStatusRepository.findById(id);
		try {
			if (!stopThread) {
				ExecutorService legacyExecutor = Executors.newSingleThreadExecutor();

				legacyExecutor.execute(new Runnable() {
					@Override
					public void run() {
						try {

							stopThread = true;
							System.out.println("in thread");
							final String uri = configurableEnvironment.getProperty("rmnchaaggregate.url")
									+ "mongoAggregate?tp=" + aggregateLegacyDataStatus.getTpId() + "&periodicity="
									+ aggregateLegacyDataStatus.getPeriodicity();

							aggregateLegacyDataStatus.setStatus("INPROGRESS");
							aggregateLegacyDataStatusRepository.save(aggregateLegacyDataStatus);

							RestTemplate restTemplate = new RestTemplate();

							String result = restTemplate.getForObject(uri, String.class);

//						    	if success

							aggregateLegacyDataStatus.setStatus("COMPLETED");
							aggregateLegacyDataStatus.setEndTime(new Date());
							aggregateLegacyDataStatusRepository.save(aggregateLegacyDataStatus);
							stopThread = false;
							System.out.println("closed Thread-->>"+ stopThread);
						} catch (Exception e) {
//							aggregateLegacyDataStatus.setStatus("FAILED");
//							aggregateLegacyDataStatusRepository.save(aggregateLegacyDataStatus);
							stopThread = false;
							throw new RuntimeException("RETRYING...");
						}
					}
				});
				legacyExecutor.shutdown();
			}else {
				throw new RuntimeException("RETRYING...");
			}

		} catch (Exception e) {
//			aggregateLegacyDataStatus.setStatus("FAILED");
//			aggregateLegacyDataStatusRepository.save(aggregateLegacyDataStatus);
			e.printStackTrace();
			throw new RuntimeException("RETRYING...");
		}
	}
	
	@Override
	public List<Map<String, String>> getLegacyData() {

		List<Map<String, String>> legacyDataStatusModels = new ArrayList<>();
		
		aggregateLegacyDataStatusRepository.findAllByLegacyTrueOrderByStartTimeDesc().stream().forEach(data -> {
			Map<String, String> statusModel = new HashMap<>();
			statusModel.put("Time Period", data.getTimePeriod());
			statusModel.put("Start Time", ymdDateTimeFormat.format(data.getStartTime()));
			statusModel.put("End Time", data.getEndTime()!=null ? ymdDateTimeFormat.format(data.getEndTime()) : "");
			statusModel.put("Status", data.getStatus());
			legacyDataStatusModels.add(statusModel);
		});
		
		return legacyDataStatusModels;
	}
	
	/*@PreDestroy
	public void beandestroy() {
		this.stopThread = true;
		if (legacyExecutor != null) {
			try {
				// wait 1 second for closing all threads
				legacyExecutor.awaitTermination(1, TimeUnit.SECONDS);
				
//				legacyExecutor = Executors.newSingleThreadExecutor();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}*/
}
