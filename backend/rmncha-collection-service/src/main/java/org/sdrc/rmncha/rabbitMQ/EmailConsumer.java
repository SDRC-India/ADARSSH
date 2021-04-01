/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 25-Jul-2019 10:58:38 AM
 */
package org.sdrc.rmncha.rabbitMQ;

import org.sdrc.rmncha.service.ReportService;
import org.sdrc.rmncha.util.Mail;
import org.sdrc.rmncha.util.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 25-Jul-2019 10:58:38 AM
 */

@Service
public class EmailConsumer {
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private ReportService reportService;
	
	
	@StreamListener(value = CollectionChannel.RMNCHAEMAIL_INPUTCHANNEL)
	public void sendEmail(Mail mailModel) {

		mailService.sendSimpleMessage(mailModel);
	}
	
	@StreamListener(value = CollectionChannel.RMNCHASUBMITEMAIL_INPUTCHANNEL)
	public void sendSubmissionEmail(AllChecklistFormDataEvent targetEvent) {

		try {
			reportService.getNotification(targetEvent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
