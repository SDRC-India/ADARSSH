package org.sdrc.rmnchadashboard.web.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class Attachment {

	@Id
	private String attachmentId;

	private String columnName;

	private String filePath;

	private Integer typeDetailId; // typedetails

	private Boolean isDeleted;

	private String originalName;

	private Long fileSize;

}
