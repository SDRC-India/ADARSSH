package org.sdrc.rmnchadashboard.web.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.sdrc.rmnchadashboard.web.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution

public class CoverageJob extends QuartzJobBean {
	private static Logger log = LoggerFactory.getLogger(CoverageJob.class);
	private ApplicationContext applicationContext;

	private DashboardService dashboardService;

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
		log.info("coverage aggregate job started", applicationContext.getBean(Environment.class));
		
		dashboardService = applicationContext.getBean(DashboardService.class);
		
		dashboardService.aggregate();
	}

}
