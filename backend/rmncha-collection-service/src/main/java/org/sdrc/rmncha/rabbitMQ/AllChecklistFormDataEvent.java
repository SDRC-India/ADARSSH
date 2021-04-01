package org.sdrc.rmncha.rabbitMQ;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sdrc.rmncha.model.ChecklistSubmissionStatus;
import org.sdrc.rmncha.model.SubmissionCompleteStatus;
import org.springframework.data.annotation.Id;

import in.co.sdrc.sdrcdatacollector.models.FormAttachmentsModel;
import lombok.Data;

@Data
public class AllChecklistFormDataEvent implements Serializable{

	/**
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 24-Jul-2019 1:28:01 PM
	 */
	private static final long serialVersionUID = -5779429680428784638L;

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

//	private Integer timePeriodId;
	
//	private String areaCode;
	
	private AreaModelEvent area;
	
	private TimePeriodEvent timePeriod;

	private Boolean isAggregated;

	private Boolean isValid;

	private Integer attachmentCount = 0;

	Map<String, List<FormAttachmentsModel>> attachments;

	private SubmissionCompleteStatus submissionCompleteStatus = SubmissionCompleteStatus.PC;
	
	private ChecklistSubmissionStatus checklistSubmissionStatus = ChecklistSubmissionStatus.PENDING;

	private Boolean latest;
	
	private Date rejectedApprovedDate;
	
//	@DBRef
//	private Account actionBy;
	private String actionByUserName;

	private String actionByFullName;
}
