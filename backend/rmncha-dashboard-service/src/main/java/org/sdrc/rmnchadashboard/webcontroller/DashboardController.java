package org.sdrc.rmnchadashboard.webcontroller;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sdrc.rmnchadashboard.web.model.AreaLevelModel;
import org.sdrc.rmnchadashboard.web.model.AreaModel;
import org.sdrc.rmnchadashboard.web.model.FormSectorModel;
import org.sdrc.rmnchadashboard.web.model.ParamModel;
import org.sdrc.rmnchadashboard.web.model.SVGModel;
import org.sdrc.rmnchadashboard.web.model.SectorModel;
import org.sdrc.rmnchadashboard.web.model.TimeperiodModel;
import org.sdrc.rmnchadashboard.web.model.TypeDetailModel;
import org.sdrc.rmnchadashboard.web.service.DashboardService;
import org.sdrc.rmnchadashboard.web.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 25-Jul-2019 3:02:45 PM
 */
@RestController
@PreAuthorize("hasAnyAuthority('Data Entry & Visualization','Visualization')")
public class DashboardController {
	
	@Autowired
	public DashboardService dashboardService;
	
	@Autowired
	private ExportService exportService;
	
//	private final SimpMessagingTemplate template;
//	
//	 @Autowired
//	 DashboardController(SimpMessagingTemplate template){
//	        this.template = template;
//	    }
	
	@GetMapping(value="/pushIndicatorGroupData")
	public String pushIndicatorGroupData() {
	return	dashboardService.pushIndicatorGroupData();
	}
	
	@GetMapping(value="/isCoveargeDataAvailable")
	public boolean isCoveargeDataAvailable() {
		return dashboardService.isDataAvailable();
	}
	
	@GetMapping(value="/getCoverageData")
	public List<SectorModel> getDashboardData(@RequestParam("areaLevel") Integer areaLevel, 
			@RequestParam(value="areaId", required=true) Integer areaId,
			@RequestParam(value="sectorId", required=false) Integer sectorId, 
			@RequestParam(value="tpId", required=false) Integer tpId,
			@RequestParam(value="formId", required=false) Integer formId,
			@RequestParam(value="dashboardType", required=true)  String dashboardType,
			@RequestParam(value="typeId", required=false) Integer typeId,
			@RequestParam(value="levelId", required=false) Integer levelId) {
		
		return dashboardService.getDashboardData(areaLevel,areaId, sectorId, tpId, formId, dashboardType, typeId, levelId);
	}
	
	@GetMapping("/aggregateCoverageData")
	public String aggregateCoverageData() {
		return dashboardService.aggregate();
	}
	
	@GetMapping("/getAllChecklistSectors")
	public Map<String, List<FormSectorModel>> getAllChecklistSectors() {

		return dashboardService.getAllChecklistSectors();
	}
	
	@PostMapping(value = "/downloadChartDataPDF")
	public ResponseEntity<InputStreamResource> downloadChartDataPDF(@RequestBody List<SVGModel> listOfSvgs,
			@RequestParam(value = "districtName", required = false) String districtName,
			@RequestParam(value = "blockName", required = false) String blockName, HttpServletResponse response,
			HttpServletRequest request, @RequestParam(value = "stateName", required = false) String stateName,
			@RequestParam("areaLevel") String areaLevel, @RequestParam("dashboardType") String dashboardType,
			@RequestParam(value = "checkListName", required = false) String checkListName,
			@RequestParam(value = "timePeriod", required = false) String timePeriod,
			@RequestParam(value = "typeId", required = false) Integer typeId,
			@RequestParam(value = "levelId", required = false) Integer levelId) {

		String filePath = "";
		try {
			filePath = exportService.downloadChartDataPDF(listOfSvgs, districtName, blockName, request,stateName, areaLevel,dashboardType, checkListName, timePeriod, typeId, levelId);
			File file = new File(filePath);

			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders.add("Content-Disposition", "attachment; filename=" + file.getName());
			InputStreamResource isr = new InputStreamResource(new FileInputStream(file));

			file.delete();
			return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PostMapping(value = "/downloadChartDataExcel")
	public ResponseEntity<InputStreamResource> downloadChartDataExcel(@RequestBody ParamModel paramModel,
			HttpServletResponse response, HttpServletRequest request) {

		String filePath = "";
		try {
			filePath = exportService.downloadChartDataExcel(paramModel.getListOfSvgs(), paramModel, request);
			File file = new File(filePath);

			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders.add("Content-Disposition", "attachment; filename=" + file.getName());
			InputStreamResource isr = new InputStreamResource(new FileInputStream(file));

			file.delete();
			return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	@GetMapping("/bypass/getTypeDetails")
	public ResponseEntity<List<TypeDetailModel>> getTypeDetailsByType(@RequestParam("typeName") String typeName){
		
		return dashboardService.getTypeDetailsByType(typeName);
	}
	
	@GetMapping("/bypass/getAreaLevels")
	public ResponseEntity<List<AreaLevelModel>> getAreaLevels(){
		
		return dashboardService.getAreaLevels();
	}
	@RequestMapping(value = "/bypass/getAllArea")
	public Map<String, List<AreaModel>> getArea() {
		return dashboardService.getAllAreaList();

	}
	
	@RequestMapping(value = "/getTimePeriod")
	public ResponseEntity<List<TimeperiodModel>> getAllTimePeriod(@RequestParam(value="isAggregate", required = false) Boolean isAggregate) {
		return dashboardService.getAllTimePeriod(isAggregate);

	}
	
	@GetMapping("/getTypeDetailsByForm")
	public ResponseEntity<List<TypeDetailModel>> getTypeDetailsByFormAndType(@RequestParam("formId") Integer formId, @RequestParam("typeName") String typeName){
		
		return dashboardService.getTypeDetailsByFormAndType(formId, typeName);
	}
	
	@GetMapping("/legacyAggregate")
	public String getMongoAggregatedData(@RequestParam("tp") Integer tp,@RequestParam("tpName")  String tpName, @RequestParam("periodicity") String periodicity) {
		return dashboardService.aggregateLegacyData(tp, tpName, periodicity);
	}
	
	/*@MessageMapping("/send/legacyAggregate")
    public void getMongoAggregatedData( Integer tp, String tpName, String periodicity) {
		dashboardService.aggregateLegacyData(tp, tpName, periodicity);
        this.template.convertAndSend("/getData",  dashboardService.getLegacyData());
    }*/

//	@MessageMapping("/bypass/legacyAggregate")
//	@SendTo("/bypass/topic/getData")
//    public Integer getMongoAggregatedData(String message) {
//		System.out.println(message);
////        this.template.convertAndSend("/getData",  dashboardService.getLegacyData());
//		
//		return dashboardService.getLegacyData();
//    }
	
	@GetMapping("/getAllLegacyRecordStatus")
	public List<Map<String, String>> getAllLegacy(){
		return dashboardService.getLegacyData();
	}


}
