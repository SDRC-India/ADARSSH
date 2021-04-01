package org.sdrc.rmnchadashboard.utils;

import org.sdrc.rmnchadashboard.jpadomain.Feedback;
import org.sdrc.rmnchadashboard.model.ResponseModel;
import org.springframework.http.ResponseEntity;


public interface MailService {

	
	String sendMail(Mail mail);
	
	ResponseEntity<ResponseModel> sendSimpleMessage(Feedback feedback);
}
