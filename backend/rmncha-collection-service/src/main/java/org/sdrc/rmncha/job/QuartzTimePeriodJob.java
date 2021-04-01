package org.sdrc.rmncha.job;

import java.text.ParseException;
import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution

public class QuartzTimePeriodJob extends QuartzJobBean {
	private static Logger log = LoggerFactory.getLogger(QuartzTimePeriodJob.class);
	private ApplicationContext applicationContext;

	private JobService jobService;

	/**
	 * This method is called by Spring since we set the
	 * {@link SchedulerFactoryBean#setApplicationContextSchedulerContextKey(String)}
	 * in {@link QuartzConfiguration}
	 * 
	 * @param applicationContext
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * This is the method that will be executed each time the trigger is fired.
	 */
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("Timeperiod creation job started", applicationContext.getBean(Environment.class));
		
		jobService = applicationContext.getBean(JobService.class);
		

		try {
			jobService.createMonthlyTimePeriod();
		} catch (ParseException e) {
			log.error("Timeperiod creation failed!! Time: ", new Date().getTime());
		}
	}

}
