package org.sdrc.rmncha.job;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import org.sdrc.rmncha.domain.TimePeriod;
import org.sdrc.rmncha.rabbitMQ.CollectionChannel;
import org.sdrc.rmncha.rabbitMQ.TimePeriodEvent;
import org.sdrc.rmncha.repositories.TimePeriodRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sarita Panigrahi
 * Creates monthly timeperiod
 *
 */
@Slf4j
@Component
public class JobService {

	private SimpleDateFormat simpleDateformater = new SimpleDateFormat("yyyy-MM-dd");
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	@Autowired
	private TimePeriodRepository timePeriodRepository;

	@Autowired
	private CollectionChannel collectionChannel;
	
//	@Scheduled(cron = "0 0 0 1 1/1 ?") //implement quartz scheduler
	@Transactional
	public void createMonthlyTimePeriod() throws ParseException {

		LocalDateTime time = LocalDateTime.now();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM");
		int month = cal.get(Calendar.MONTH);
//		cal.set(Calendar.DAY_OF_MONTH, 1);

		TimePeriod timePeriod = new TimePeriod();

//		Date startDate = cal.getTime();

		//start date is last to last month's end date. as mongo keeps UTC time period
		Calendar startDateCalendar1 = Calendar.getInstance();
		startDateCalendar1.add(Calendar.MONTH, -1);
		startDateCalendar1.set(Calendar.DATE, startDateCalendar1.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date startDate1 = (Date) formatter.parse(simpleDateformater.format(startDateCalendar1.getTime()) + " 23:59:59.000");
		
		timePeriod.setStartDate(startDate1);

//		String sDate = sdf.format(startDate1);
//		cal.add(Calendar.MONTH, 0);
//		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

		Calendar endDateCalendar = Calendar.getInstance();
		endDateCalendar.add(Calendar.MONTH, 0);
//		endDateCalendar.set(Calendar.DATE, 1);
		endDateCalendar.set(Calendar.DATE, endDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		
//		Date endDate = cal.getTime();
		Date endDate = endDateCalendar.getTime();
		String endDateStr = simpleDateformater.format(endDate);
		Date endDateFormatted = (Date) formatter.parse(endDateStr + " 23:59:59.000");
		
		String eDate = sdf.format(endDateFormatted);

//		List<TimePeriod> tps = timePeriodRepository.findTop1ByPeriodicityOrderByTimePeriodIdDesc();
		
		timePeriod.setEndDate(endDateFormatted);

		timePeriod.setPeriodicity("1"); // for periodicity
//		timePeriod.setTimePeriod(sDate.equals(eDate) ? sDate : sDate + "-" + eDate);
		timePeriod.setTimePeriod(eDate);
		timePeriod.setYear(time.getYear()); // for year
		timePeriod.setTimePeriodId(timePeriodRepository.findTop1ByPeriodicityOrderByTimePeriodIdDesc("1").getTimePeriodId() + 1);
		timePeriod.setCreatedDate(new Date());
		int preYear = 0, nextYear = 0;

		if (month > 2) {
			preYear = cal.get(Calendar.YEAR);
			cal.add(Calendar.YEAR, 1);
			nextYear = cal.get(Calendar.YEAR);
		} else {
			cal.add(Calendar.YEAR, -1);
			preYear = cal.get(Calendar.YEAR);
			cal.add(Calendar.YEAR, 1);
			nextYear = cal.get(Calendar.YEAR);
		}
		timePeriod.setFinancialYear(preYear + "-" + nextYear);

		timePeriodRepository.save(timePeriod);
		log.info("timeperiod created");
		
		TimePeriodEvent target = new TimePeriodEvent();
		BeanUtils.copyProperties(timePeriod, target);
		
		collectionChannel.timeperiodChannel().send(MessageBuilder.withPayload(target).build());
	}
}
