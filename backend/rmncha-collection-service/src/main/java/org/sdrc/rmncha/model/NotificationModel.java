package org.sdrc.rmncha.model;

import lombok.Data;

@Data
public class NotificationModel {

	private Integer formId;
	private Integer deadlineDays;
	private Integer notifyByDays;
}
