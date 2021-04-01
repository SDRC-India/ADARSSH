package org.sdrc.rmncha.job;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * 
 * This will configure the job to run within quartz.
 * 
 * @author Sarita
 *
 */
@Configuration
public class JobConfiguration {

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@PostConstruct
	private void initialize() throws Exception {
		schedulerFactoryBean.getScheduler().addJob(tpJobDetail(), true, true);
//		schedulerFactoryBean.getScheduler().addJob(defaultJobDetail(), true, true);
//		schedulerFactoryBean.getScheduler().addJob(defaultNotifyJobDetail(), true, true);

		if (!schedulerFactoryBean.getScheduler()
				.checkExists(new TriggerKey(SchedulerConstants.TP_JOB_TRIGGER_KEY, SchedulerConstants.TP_JOB_GROUP))) {
			schedulerFactoryBean.getScheduler().scheduleJob(tpJobTrigger());

		}
		
		/*if (!schedulerFactoryBean.getScheduler().checkExists(
				new TriggerKey(SchedulerConstants.DEFAULT_EMAIL_JOB_TRIGGER_KEY, SchedulerConstants.DEFAULT_JOB_GROUP))) {
			schedulerFactoryBean.getScheduler().scheduleJob(defaultNotifyJobTrigger());

		}
		
		if (!schedulerFactoryBean.getScheduler().checkExists(
				new TriggerKey(SchedulerConstants.DEFAULT_JOB_TRIGGER_KEY, SchedulerConstants.DEFAULT_JOB_GROUP))) {
			schedulerFactoryBean.getScheduler().scheduleJob(defaultJobTrigger());

		}*/
	}

	/**
	 * <p>
	 * The job is configured here where we provide the job class to be run on each
	 * invocation. We give the job a name and a value so that we can provide the
	 * trigger to it on our method {@link #tpJobTrigger()}
	 *  {@link #defaultJobTrigger()}
	 *  {@link #defaultNotifyJobTrigger()}
	 * </p>
	 * 
	 * @return an instance of {@link JobDetail}
	 */
	private static JobDetail tpJobDetail() {
		JobDetailImpl jobDetail = new JobDetailImpl();
		jobDetail.setKey(new JobKey(SchedulerConstants.TP_JOB_KEY, SchedulerConstants.TP_JOB_GROUP));
		jobDetail.setJobClass(QuartzTimePeriodJob.class);
		jobDetail.setDurability(true);
		jobDetail.setRequestsRecovery(true);
		return jobDetail;
	}

	/*private static JobDetail defaultJobDetail() {
		JobDetailImpl jobDetail = new JobDetailImpl();
		jobDetail.setKey(new JobKey(SchedulerConstants.DEFAULT_JOB_KEY, SchedulerConstants.DEFAULT_JOB_GROUP));
		jobDetail.setJobClass(DefaultJob.class);
		jobDetail.setDurability(true);
		jobDetail.setRequestsRecovery(true);
		return jobDetail;
	}

	private static JobDetail defaultNotifyJobDetail() {
		JobDetailImpl jobDetail = new JobDetailImpl();
		jobDetail.setKey(new JobKey(SchedulerConstants.DEFAULT_EMAIL_JOB_KEY, SchedulerConstants.DEFAULT_JOB_GROUP));
		jobDetail.setJobClass(DefaultNotifyJob.class);
		jobDetail.setDurability(true);
		jobDetail.setRequestsRecovery(true);
		return jobDetail;
	}*/

	/**
	 * <p>
	 * This method will define the frequency with which we will be running the
	 * scheduled job which in this instance is monthly.
	 * </p>
	 * 
	 * @return an instance of {@link Trigger}
	 */
	private static Trigger tpJobTrigger() {
		return TriggerBuilder.newTrigger().forJob(tpJobDetail())
				.withIdentity(SchedulerConstants.TP_JOB_TRIGGER_KEY, SchedulerConstants.TP_JOB_GROUP).withPriority(50)
				.withSchedule(CronScheduleBuilder.cronSchedule("0 30 0 1 1/1 ?")).build();
	}

	/**
	 * @return
	 * This job notifies all state admins to approve the submissions
	 */
	/*private static Trigger defaultNotifyJobTrigger() {
		return TriggerBuilder.newTrigger().forJob(defaultJobDetail())
				.withIdentity(SchedulerConstants.DEFAULT_EMAIL_JOB_TRIGGER_KEY, SchedulerConstants.DEFAULT_JOB_GROUP)
				.withPriority(50).withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 18/7 * ?")).build();
	}

	*//**
	 * @return This job runs every month on 26th. it updates all PENDING submission
	 *         status to DEFAULT.
	 *//*
	private static Trigger defaultJobTrigger() {
		return TriggerBuilder.newTrigger().forJob(defaultNotifyJobDetail())
				.withIdentity(SchedulerConstants.DEFAULT_JOB_TRIGGER_KEY, SchedulerConstants.DEFAULT_JOB_GROUP)
				.withPriority(50).withSchedule(CronScheduleBuilder.cronSchedule("0 5 0 26 * ?")).build();
	}*/
}
