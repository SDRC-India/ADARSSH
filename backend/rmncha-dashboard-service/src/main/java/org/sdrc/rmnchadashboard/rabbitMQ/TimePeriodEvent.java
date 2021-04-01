/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 25-Jul-2019 2:27:43 PM
 */
package org.sdrc.rmnchadashboard.rabbitMQ;

import java.util.Date;

import lombok.Data;

/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 25-Jul-2019 2:27:43 PM
 */
@Data
public class TimePeriodEvent {
	
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
