package org.sdrc.rmnchadashboard.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.sdrc.rmnchadashboard.repository.IndicatorDAL;
import org.sdrc.rmnchadashboard.service.ConfigurationService;
import org.sdrc.rmnchadashboard.service.SqlConfigurationService;
import org.sdrc.rmnchadashboard.service.SynchronizationService;
import org.sdrc.rmnchadashboard.utils.Mail;
import org.sdrc.rmnchadashboard.utils.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ConfigurationController {

	@Autowired
	IndicatorDAL indicatorDAL;

	@Autowired
	ConfigurationService configurationService;

	@Autowired
	MailService mailService;

	@Autowired
	SynchronizationService synchronizationService;

	@Autowired
	SqlConfigurationService sqlConfigurationService;
	
	@GetMapping("/configureSQLDb")
	public Boolean configureSQLDb() {
		try {
//			sqlConfigurationService.configureSQLDb();
			System.out.println("Calculating Rank");
			sqlConfigurationService.calculateRankForSQLDb();
			System.out.println("Rank Calculation Completed");
			sqlConfigurationService.calculateTrendForSQLDb();
			System.out.println("Trend Calculation Completed");
			sqlConfigurationService.calculateTopButtomForSQLDb();
			System.out.println("calculateTopButtomForSQLDb Completed");

			Mail mail = new Mail();

			List<String> emailId = new ArrayList<String>();
			emailId.add("harsh@sdrc.co.in");

			mail.setToEmailIds(emailId);
			mail.setToUserName("harsh@sdrc.co.in");
			mail.setSubject("Importing Data To SQLDB Completed Successfully");

			mail.setMessage("Congrats");
			mail.setFromUserName("Administrator");

		 mailService.sendMail(mail);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Mail mail = new Mail();

			List<String> emailId = new ArrayList<String>();
			emailId.add("harsh@sdrc.co.in");

			mail.setToEmailIds(emailId);
			mail.setToUserName("harsh@sdrc.co.in");
			mail.setSubject("Exception Occured While Import Data To SQLDB");

			mail.setMessage("<pre>" + ExceptionUtils.getFullStackTrace(e) + "</pre>");
			mail.setFromUserName("Administrator");

			 mailService.sendMail(mail);


		}
		return false;
	}

	@GetMapping("syncElasticSearchWithRDBMS$$THIS_IS_THE_SERET_KEY_$$_USE_IT_IN_YOUR_RISK$$12345$!SECRET")
	public boolean sync() {
		try {
			synchronizationService.startSyncProcess();
			Mail mail = new Mail();

			List<String> emailId = new ArrayList<String>();
			emailId.add("harsh@sdrc.co.in");

			mail.setToEmailIds(emailId);
			mail.setToUserName("harsh@sdrc.co.in");
			mail.setSubject("Synchronizing Data To ElasticSearch From SQL DB Completed Successfully.");

			mail.setSubject("Sync Process Completed Successfully");

			mail.setMessage("Congrats");

			 mailService.sendMail(mail);
		} catch (Exception e) {
			e.printStackTrace();

			Mail mail = new Mail();

			List<String> emailId = new ArrayList<String>();
			emailId.add("harsh@sdrc.co.in");

			mail.setToEmailIds(emailId);
			mail.setToUserName("harsh@sdrc.co.in");
			mail.setSubject("Exception Occured While Synchronizing Data To ElasticSearch");

			mail.setMessage("<pre>" + ExceptionUtils.getFullStackTrace(e) + "</pre>");
			mail.setFromUserName("Administrator");

			 mailService.sendMail(mail);

		}
		return false;
	}

}
