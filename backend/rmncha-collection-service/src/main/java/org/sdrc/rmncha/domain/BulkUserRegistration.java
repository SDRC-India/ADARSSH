package org.sdrc.rmncha.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class BulkUserRegistration {
	
	@Id
	private String id;
	
	private String filePath;
	
	private String createdDate;

}
