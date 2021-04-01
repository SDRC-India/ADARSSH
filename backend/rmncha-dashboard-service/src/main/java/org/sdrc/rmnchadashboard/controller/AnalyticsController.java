/**
 * 
 */
package org.sdrc.rmnchadashboard.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.sdrc.rmnchadashboard.model.AnalyticsColModel;
import org.sdrc.rmnchadashboard.model.ResponseModel;
import org.sdrc.rmnchadashboard.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

	@Autowired
	private AnalyticsService analyticsService;

	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@PostMapping("getCols")
	ResponseEntity<List<AnalyticsColModel>> getCols(@RequestBody String fileName) {

		try {
			return  ResponseEntity.status(HttpStatus.OK).body(analyticsService.getColumns(fileName));
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
					null);
		}
	}

	@PostMapping("/saveFile")
	public ResponseEntity<String> handleFileUpload(
			@RequestParam("file") MultipartFile file) {
		String message = "";
		try {
			String fileName = analyticsService.store(file);

			message = fileName;
			return ResponseEntity.status(HttpStatus.OK).body(message);

		} catch (Exception e) {
			message = "FAIL to upload " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
					message);
		}
	}

	@PostMapping("singleDiscriptiveStatics")
	@SuppressWarnings("unchecked")
	public ResponseEntity<ResponseModel> singleDiscriptiveStatics(
			@RequestBody Map<String, Object> selectionData) {

		try {
			ResponseModel responseModel = null;
			
			List<Integer> indicatorIds=(List<Integer>)selectionData.get("indicatorValue");
			if (indicatorIds.size()==1)
			{
				selectionData.put("indicatorValue", indicatorIds.get(0));
				responseModel = analyticsService
						.singleDiscriptiveStatics(selectionData);
			}
			else
				responseModel = analyticsService
						.multipleDiscriptiveStatics(selectionData);

			return ResponseEntity.status(HttpStatus.OK).body(responseModel);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
					null);
		}
	}

	@PostMapping("outlierDataMatrix")
	public ResponseEntity<ResponseModel> outlierDataMatrix(
			@RequestBody Map<String, Object> selectionData) {

		try {
			ResponseModel responseModel = analyticsService
					.outlierDataMatrix(selectionData);
			return ResponseEntity.status(HttpStatus.OK).body(responseModel);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
					null);
		}
	}

	@PostMapping("downloadFile")
	public ResponseEntity<InputStreamResource> download(
			@RequestBody String fileName) throws FileNotFoundException {
		
		try {
		File file = new File(fileName);
//		boolean fileExist = analyticsService.onFileAccess(file.toPath());
	
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.add("Content-Disposition", "attachment; filename="
				+ fileName);
		InputStreamResource isr = new InputStreamResource(new FileInputStream(
				file));

		return new ResponseEntity<InputStreamResource>(isr, respHeaders,
				HttpStatus.OK);
		}
		catch (Exception e) {
			log.error(e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
					null);
		}
	}
	
	
	
	@PostMapping("independentLinerRegration")
	public ResponseEntity<ResponseModel> independentLinerRegration(
			@RequestBody Map<String, Object> selectionData) {

		try {
			ResponseModel responseModel = analyticsService
					.independentLinerRegration(selectionData);
			return ResponseEntity.status(HttpStatus.OK).body(responseModel);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
					null);
		}
	}
	
	@PostMapping("treatMissingValues")	
	public ResponseEntity<ResponseModel> treatMissingValues(
			@RequestBody Map<String, Object> selectionData) {

		try {
			ResponseModel responseModel = analyticsService
					.treatMissingValues(selectionData);
			return ResponseEntity.status(HttpStatus.OK).body(responseModel);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
					null);
		}
	}

}
