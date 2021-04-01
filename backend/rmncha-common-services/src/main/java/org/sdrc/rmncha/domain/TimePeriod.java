package org.sdrc.rmncha.domain;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class TimePeriod implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2226822562688452799L;

	@Id
	private String id;

	private Integer timePeriodId;

	private String timePeriod;

	private Date startDate;

	private Date endDate;

	private String periodicity;

	private Date createdDate;

	private String financialYear;

	private Integer year;
}
