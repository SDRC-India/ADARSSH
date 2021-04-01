package org.sdrc.rmncha.util;

import org.springframework.http.ResponseEntity;

public interface MailService {

	 ResponseEntity<String>  sendSimpleMessage(Mail mail) ;
	
	
}
