package org.sdrc.rmncha.mongodomain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sdrc.rmncha.domain.Area;
import org.sdrc.rmnchadashboard.web.model.ChecklistSubmissionStatus;
import org.sdrc.rmnchadashboard.web.model.SubmissionCompleteStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import in.co.sdrc.sdrcdatacollector.models.FormAttachmentsModel;
import lombok.Data;

@Document
@Data
public class AllChecklistFormData {

	@Id
	private String id;

	private String userName;

	private String userId;
	
	private String submittedBy;

	private Date createdDate;

	private Date updatedDate;

	private Date syncDate;

	private Map<String, Object> data;

	private Integer formId;

	private String uniqueId;

	private boolean rejected = false;

	private String rejectMessage;

	private String uniqueName;

	private org.sdrc.rmncha.domain.TimePeriod timePeriod;
	
	private Area area;

	private Boolean isAggregated;

	private Boolean isValid = true;
	
	private Boolean duplicate  = false;

	private Integer attachmentCount = 0;

	Map<String, List<FormAttachmentsModel>> attachments;

	private SubmissionCompleteStatus submissionCompleteStatus = SubmissionCompleteStatus.PC;
	
	private ChecklistSubmissionStatus checklistSubmissionStatus = ChecklistSubmissionStatus.APPROVED;

	private Boolean latest;
	
	private Date rejectedApprovedDate;
	
//	@DBRef
//	private Account actionBy;
	private String actionByUserName;

	private String actionByFullName;
}

