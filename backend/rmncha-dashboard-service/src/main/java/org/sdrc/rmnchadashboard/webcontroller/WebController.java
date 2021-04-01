/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 27-Jul-2019 12:55:15 PM
 */
package org.sdrc.rmnchadashboard.webcontroller;

import java.util.Map;

import org.sdrc.rmnchadashboard.web.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 27-Jul-2019 12:55:15 PM
 */

@RestController
public class WebController {

	@Autowired(required = false)
	private TokenStore tokenStore;
	
	/**
	 * It extracts the user details from jwt access-token.
	 * 
	 * @param auth
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public Map<String, Object> getExtraInfo(OAuth2Authentication auth) {
		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(details.getTokenValue());
		return accessToken.getAdditionalInformation();
	}

	/*
	 * this is for oauth2
	 */
	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(value = "/userDetails", method = RequestMethod.GET)
	public UserModel getExtraInfo() {

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Authorization", attr.getRequest().getHeader("Authorization"));

		ResponseEntity<UserModel> restExchange = restTemplate.exchange("http://localhost:8080/si-rmncha-dashboard/user",
				HttpMethod.GET, new HttpEntity<Void>(requestHeaders), new ParameterizedTypeReference<UserModel>() {
				});

		return restExchange.getBody();

	}

}
