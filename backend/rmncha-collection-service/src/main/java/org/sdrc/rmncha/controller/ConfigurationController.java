package org.sdrc.rmncha.controller;

import java.text.ParseException;

import org.sdrc.rmncha.job.JobService;
import org.sdrc.rmncha.service.CollectionService;
import org.sdrc.rmncha.service.ConfigurationService;
//import org.sdrc.ted.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.co.sdrc.sdrcdatacollector.engine.UploadFormConfigurationService;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 */

@RestController
@RequestMapping("/api")
public class ConfigurationController {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private UploadFormConfigurationService uploadFormConfigurationService;

	@Autowired
	CollectionService collectionService;
	
	@Autowired
	JobService jobService;

	
	@GetMapping("/mongoClient")
	public String createMongoOauth2Client() {

		return configurationService.createMongoOauth2Client();

	}

	@GetMapping("/importQuestions")
	public ResponseEntity<String> importQuestions() {
		return uploadFormConfigurationService.importQuestionData();
//		return configurationService.importFilterExpInTypeDetail();
	}

	@GetMapping("/area")
	public ResponseEntity<String> area() {
		return configurationService.importAreas();
	}

	@GetMapping("/config")
	public ResponseEntity<String> config() {
		return configurationService.config();
	}

	@GetMapping("/configureRoleFormMappingOfEngine")
	public ResponseEntity<String> configureRoleFormMappingOfEngine() {
//		uploadFormConfigurationService.configureRoleFormMapping();
		return new ResponseEntity<String>("success", HttpStatus.OK);
	}

	@GetMapping("/formsValue")
	public ResponseEntity<String> formsValue() {
		return configurationService.formsValue();
	}

	@RequestMapping("/importOrganization")
	public String importOrganization() throws Exception {
		return collectionService.saveDate();

	}

	@RequestMapping("/importMonthlyTimeperiod")
	public String importTimeperiod() {
		return collectionService.saveTimePeriod();

	}

	@RequestMapping("/importDevelopmentPartners")
	public String importDevelopmentPartners() throws Exception {
		return collectionService.saveDevelopmentPartners();

	}
	
	@GetMapping("/createTimePeriod")
	public String createTimePeriod() throws ParseException {
		 jobService.createMonthlyTimePeriod();
		 return "Success";
	}
	

}
