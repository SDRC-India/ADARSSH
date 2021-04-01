package org.sdrc.rmncha.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class NotificationDetail {

	@Id
	private String id;
	private Integer formId;
	private Integer deadlineDays;
	private Integer notifyByDays;
}
