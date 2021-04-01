package org.sdrc.rmncha.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

	@Autowired
	public JavaMailSender emailSender;

	@Override
	public ResponseEntity<String> sendSimpleMessage(Mail mail) {
		try {

			SimpleMailMessage message = new SimpleMailMessage();

			message.setTo(mail.getEmail());
			message.setSubject(mail.getSubject());
			message.setText(mail.getToUserName() + "\n" + mail.getMessage() + "\n" + mail.getFromUserName());

			emailSender.send(message);

			return new ResponseEntity<>("OTP verified", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Try Again", HttpStatus.OK);
		}
	}


}
