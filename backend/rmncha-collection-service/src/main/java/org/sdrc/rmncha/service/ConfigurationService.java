package org.sdrc.rmncha.service;

import org.springframework.http.ResponseEntity;

public interface ConfigurationService {

	ResponseEntity<String> importAreas();

	ResponseEntity<String> formsValue();

	String createMongoOauth2Client();

	ResponseEntity<String> config();

	ResponseEntity<String> importFilterExpInTypeDetail();

}
