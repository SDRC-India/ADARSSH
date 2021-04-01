package org.sdrc.rmnchadashboard.utils;

import org.sdrc.rmnchadashboard.web.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 24-Jul-2019 3:19:08 PM
 */
@Component
@Slf4j
public class OAuth2Utility {

	@Value(value = "${oauth2.authserver.url}")
	private String authServerURI;

	@Autowired
	private RestTemplate restTemplate;

	public UserModel getUserModel() {

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Authorization", attr.getRequest().getHeader("Authorization"));

		log.debug("get user By Using Access Token URL: {}", authServerURI + "/me");
		
		ResponseEntity<UserModel> restExchange = restTemplate.exchange(authServerURI + "/me", HttpMethod.GET,
				new HttpEntity<Void>(requestHeaders), new ParameterizedTypeReference<UserModel>() {});

		return restExchange.getBody();

	}


}