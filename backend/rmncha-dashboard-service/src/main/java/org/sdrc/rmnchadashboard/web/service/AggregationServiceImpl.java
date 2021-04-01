package org.sdrc.rmnchadashboard.web.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sdrc.rmncha.domain.Area;
import org.sdrc.rmncha.mongodomain.AllChecklistFormData;
import org.sdrc.rmncha.mongodomain.DataValue;
import org.sdrc.rmncha.mongodomain.Indicator;
import org.sdrc.rmncha.mongorepository.DataDomainRepository;
import org.sdrc.rmncha.mongorepository.IndicatorRepository;
import org.sdrc.rmncha.repositories.AreaRepository;
import org.sdrc.rmnchadashboard.model.ChecklistSubmissionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class AggregationServiceImpl implements AggregationService {

	@Autowired
	private DataDomainRepository dataDomainRepository;
	
	@Autowired
	private IndicatorRepository mongoIndicatorRepository;
	
	@Autowired
	private AreaRepository mongoAreaRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public String getAggregatedDataInExcel(String formId, Integer tpId) {
		
//		List<Indicator> indicators = indicatorRepository.findAll();
		List<Indicator> indicators = mongoIndicatorRepository.getIndicatorByFormId(formId);
		Map<Integer, Indicator> indIdMap = new HashMap<>();
		for (Indicator indicator : indicators) {
			
			if(indicator.getIndicatorDataMap().get("indicatorNid").toString().equals("")) {
				continue;
			}
			indIdMap.put(Integer.parseInt(indicator.getIndicatorDataMap().get("indicatorNid").toString()), indicator);
		}
		
		List<Area> areas = mongoAreaRepository.findAll();
		
		Map<Integer, Area> areaIdMap = new HashMap<>();
		for (Area area : areas) {
			areaIdMap.put(area.getAreaId(), area);
		}
		List<DataValue> dataValues = dataDomainRepository.findByTp(tpId);
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet xssfSheet = workbook.createSheet("Data Value");
		
		int column = 0;
		
		XSSFRow row = xssfSheet.createRow(0);
		Cell cell = row.createCell(column);
		cell.setCellValue("Sl No.");
		
		cell = row.createCell(++column);
		cell.setCellValue("State");
		
		cell = row.createCell(++column);
		cell.setCellValue("Area Code");
		
		cell = row.createCell(++column);
		cell.setCellValue("Area");
		
		cell = row.createCell(++column);
		cell.setCellValue("Facility type");
		
		cell = row.createCell(++column);
		cell.setCellValue("Facility level");
		
		cell = row.createCell(++column);
		cell.setCellValue("Indicator");
		
		cell = row.createCell(++column);
		cell.setCellValue("Unit");
		
		cell = row.createCell(++column);
		cell.setCellValue("Data Value");

		
		
		int rowIndex = 1;
		for (DataValue dataValue : dataValues) {
			
			if(dataValue.getAreaId()!=-1) {
				
				if(indIdMap.containsKey(dataValue.getInid())) {
					
					column = 0;
					
					row = xssfSheet.createRow(rowIndex);
					cell = row.createCell(column);
					cell.setCellValue(rowIndex);
					
					cell = row.createCell(++column);
					
					cell.setCellValue(areaIdMap.get(dataValue.getAreaId())== null ? "": areaIdMap.get(dataValue.getAreaId()).getStateId() != null
							? areaIdMap.get(areaIdMap.get(dataValue.getAreaId()).getStateId()).getAreaName()
							: "");
					
					cell = row.createCell(++column);
					cell.setCellValue(areaIdMap.get(dataValue.getAreaId()).getAreaCode());
					
					cell = row.createCell(++column);
					cell.setCellValue(areaIdMap.get(dataValue.getAreaId()).getAreaName());
					
					cell = row.createCell(++column);
					if(dataValue.getF1FacilityType()!=null)
						cell.setCellValue(dataValue.getF1FacilityType());
					
					cell = row.createCell(++column);
					if(dataValue.getF1FacilityLevel()!=null)
						cell.setCellValue(dataValue.getF1FacilityLevel());
					
//					cell = row.createCell(++column);
//					cell.setCellValue(areaIdMap.get(dataValue.getAreaId()).getAreaName());
					
					cell = row.createCell(++column);
					cell.setCellValue(indIdMap.get(dataValue.getInid()).getIndicatorDataMap().get("indicatorName").toString());
					
					cell = row.createCell(++column);
					cell.setCellValue(indIdMap.get(dataValue.getInid()).getIndicatorDataMap().get("unit").toString());
					
					cell = row.createCell(++column);
					if(dataValue.getDataValue()!=null)
						cell.setCellValue(dataValue.getDataValue());
					
					rowIndex++;
				}
		
			}
			
		}
		
		try {
			FileOutputStream fileOut = new FileOutputStream("C:\\rmncha datavalue.xlsx");
			workbook.write(fileOut);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "done";
		
	}
	
	@Override
	public String updateSubmissionStatusToApprove() {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("checklistSubmissionStatus").in(Arrays.asList(ChecklistSubmissionStatus.PENDING, ChecklistSubmissionStatus.DEFAULT)));
		Update update = new Update();
		update.set("checklistSubmissionStatus",ChecklistSubmissionStatus.APPROVED);
		update.set("isValid", true);
		mongoTemplate.updateMulti(query, update, AllChecklistFormData.class);
		
		return "updated";
	}
	
}
