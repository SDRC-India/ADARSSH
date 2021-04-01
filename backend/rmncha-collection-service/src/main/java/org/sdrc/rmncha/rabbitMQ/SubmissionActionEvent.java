/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 24-Jul-2019 5:13:58 PM
 */
package org.sdrc.rmncha.rabbitMQ;

import java.util.Date;

import org.sdrc.rmncha.model.ChecklistSubmissionStatus;

import lombok.Data;

/**
 * @author Sarita Panigrahi email-sari.panigrahi@gmail.com 24-Jul-2019 5:13:58
 *         PM
 */
@Data
public class SubmissionActionEvent {

	private Integer formId;
	private String submissionId;
	private String actionByUserName;
	private String actionByFullName;
	private String rejectMessage;
	private Boolean rejected;
	private Date rejectedApprovedDate;
	private ChecklistSubmissionStatus checklistSubmissionStatus = ChecklistSubmissionStatus.PENDING;
	private Boolean isValid;
}
