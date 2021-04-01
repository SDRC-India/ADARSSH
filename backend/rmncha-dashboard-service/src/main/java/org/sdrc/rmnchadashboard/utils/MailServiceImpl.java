package org.sdrc.rmnchadashboard.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.WordUtils;
import org.sdrc.rmnchadashboard.jpadomain.Feedback;
import org.sdrc.rmnchadashboard.jparepository.FeedbackJpaRepository;
import org.sdrc.rmnchadashboard.model.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

//	@Autowired
//	private MessageSource messageSource;

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Autowired
	public JavaMailSender emailSender;
	
	
	@Autowired
	private FeedbackJpaRepository feedbackJpaRepository;

	@Override
	public String sendMail(Mail mail) {

		if (!configurableEnvironment.containsProperty("authentication.userid")
				|| !configurableEnvironment
						.containsProperty("authentication.password")) {
			throw new IllegalArgumentException(
					"Properties 'authentication.userid' or authentication.password are not found in application.properties files.");
		}

		try {

			Properties props = new Properties();

			props.put(configurableEnvironment.getProperty(
					Constants.SMTP_HOST_KEY, null, null),
					configurableEnvironment.getProperty(Constants.SMTP_HOST,
							null, null));

			props.put(configurableEnvironment.getProperty(
					Constants.SOCKETFACTORY_PORT_KEY, null, null),
					configurableEnvironment.getProperty(
							Constants.SOCKETFACTORY_PORT, null, null));

			props.put(configurableEnvironment.getProperty(
					Constants.SOCKETFACTORY_CLASS_KEY, null, null),
					configurableEnvironment.getProperty(
							Constants.SOCKETFACTORY_CLASS, null, null));

			props.put(configurableEnvironment.getProperty(
					Constants.SMTP_AUTH_KEY, null, null),
					configurableEnvironment.getProperty(Constants.SMTP_AUTH,
							null, null));

			props.put(configurableEnvironment.getProperty(
					Constants.SMTP_PORT_KEY, null, null),
					configurableEnvironment.getProperty(Constants.SMTP_PORT,
							null, null));

			javax.mail.Session session = javax.mail.Session.getDefaultInstance(
					props, new javax.mail.Authenticator() {

						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(
									configurableEnvironment.getProperty(
											Constants.AUTHENTICATION_USERID,
											null, null),
									configurableEnvironment.getProperty(
											Constants.AUTHENTICATION_PASSWORD,
											null, null));
						}
					});

			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(configurableEnvironment
					.getProperty(Constants.AUTHENTICATION_USERID, null, null)));

			// adding "to"
			List<String> toList = mail.getToEmailIds();
			String toAddress = "";

			for (String to : toList) {

				toAddress += to;

				if (toList.size() > 1) {
					toAddress += ",";
				}
			}

			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(toAddress));

			// adding "cc"
			List<String> ccList = mail.getCcEmailIds();

			if (null != ccList && ccList.size() > 0) {

				String ccAddress = "";

				for (String cc : ccList) {
					ccAddress += cc;
					if (ccList.size() > 1) {
						ccAddress += ",";
					}
				}

				message.setRecipients(Message.RecipientType.CC,
						InternetAddress.parse(ccAddress));
			}

			message.setSubject(mail.getSubject());

			// set mail message
			String mailMessageBody = null != mail.getMessage() ? mail
					.getMessage() : "";

			String msg = (String) ("<html>" + "<body><b>Dear "
					+ WordUtils.capitalize(mail.getToUserName())
					+ ",</b><br><br>"

					// + "NOTIFICATION DETAILS:" + "\n" + "Message : " +
					// mail.getMsg()

					+ mailMessageBody + "<br><br><b>" + "Regards," + "<br>"
					+ mail.getFromUserName() + "</b>" + "	</body>" + "</html>");

			// for attaching files and send it through email
			if (mail.getAttachments() == null) {
				message.setContent(msg, "text/html");
			}

			else if (mail.getAttachments().size() > 0) {

				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(msg, "text/html");
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);

				Iterator<Entry<String, String>> it = mail.getAttachments()
						.entrySet().iterator();

				while (it.hasNext()) {

					Map.Entry<String, String> pair = (Map.Entry<String, String>) it
							.next();

					String path = (String) pair.getValue();
					String name = (String) pair.getKey();

					messageBodyPart = new MimeBodyPart();
					String filename = path + name;
					DataSource source = new FileDataSource(filename);
					messageBodyPart.setDataHandler(new DataHandler(source));
					messageBodyPart.setFileName(name);
					multipart.addBodyPart(messageBodyPart);

				}

				message.setContent(multipart);

			}

			Transport.send(message);
			return "Done";

		} catch (Exception e) {

			throw new RuntimeException(e);
		}

	}

	@Override
	public ResponseEntity<ResponseModel>  sendSimpleMessage(Feedback mail) {
		ResponseModel responseModel=new ResponseModel();
		try
		{
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);	
//		SimpleMailMessage message = new SimpleMailMessage();
		String feedbackMailsTo=configurableEnvironment.getProperty("email.reciver.to",null,null);
		String feedbackMailsCC=configurableEnvironment.getProperty("email.reciver.cc",null,null);
		String feedbackMailsBcc=configurableEnvironment.getProperty("email.reciver.bcc",null,null);
		
		helper.setTo(feedbackMailsTo.split(","));
		
		if(!feedbackMailsCC.trim().equals(""))
		helper.setCc(feedbackMailsCC.split(","));
		
		if(!feedbackMailsBcc.trim().equals(""))
		helper.setBcc(feedbackMailsBcc.split(","));
		helper.setReplyTo(mail.getEmail());
		helper.setSubject("Feedback on e-MITRA application");
		helper.setText(formatMail(mail),true);
		
		feedbackJpaRepository.save(mail);
		emailSender.send(message);
		
		MimeMessage senderMessage = emailSender.createMimeMessage();
		MimeMessageHelper senderHelper = new MimeMessageHelper(senderMessage);	

		
		senderHelper.setTo(mail.getEmail());
		
		senderHelper.setReplyTo(configurableEnvironment.getProperty("sdrc.tech.support"));
		senderHelper.setSubject("Feedback on e-MITRA application");
		senderHelper.setText(formatReciverMail(mail),true);
		
		emailSender.send(senderMessage);
		
		return ResponseEntity.status(HttpStatus.OK).body(responseModel);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		return	ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
					responseModel);
		}
	}
	
	private String formatMail(Feedback mail)
	{
		String message = "<html> <body> Dear Sir/Madam,<br> <br> "
				+ "There is a feedback about the application. Please find details below: <br> "
				+ "<br>Name:"+ mail.getName()
				+ "<br>Email: " + mail.getEmail()
				+ "<br>Feedback: " + mail.getFeedback()
				+ "<br><br>" + "Regards,<br> e-Mitra Team" + "<br>"
				+ " </body> </html>";
		return message;
	}
	
	private String formatReciverMail(Feedback mail)
	{
		String message = "<html> <body> Dear "+mail.getName()+",<br> <br> "
				+ "Thank you for your valuable feedback. "
				+ "For any further query, please contact the following: <br> "
				+ "Email: " + configurableEnvironment.getProperty("sdrc.tech.support",null,null)
				+ "<br><br>" + "Regards,<br> e-Mitra Team" + "<br>"
				+ " </body> </html>";
		return message;
	}
}
