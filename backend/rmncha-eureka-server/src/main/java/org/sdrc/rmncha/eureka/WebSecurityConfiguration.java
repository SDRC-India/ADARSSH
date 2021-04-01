package org.sdrc.rmncha.eureka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter{

	
	/**
	 * performing in-memory authentication here
	 */
	@Autowired      // here is configuration related to spring boot basic authentication
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		
        auth.inMemoryAuthentication()
        .passwordEncoder(NoOpPasswordEncoder.getInstance())                   // for inMemory Authentication
            .withUser("eurekaAdmin").password("eurekaAdmin123").authorities("ADMIN");       
        
    }
	
	
	/**
	 * configure basic auth and allow which url pattern to be blocked or released without authentication.
	 */
	@Override
	 public void configure(HttpSecurity http) throws Exception {
			http
			.httpBasic().and()//indicate basic authentication is requires
			.authorizeRequests()
				.anyRequest().authenticated()////indicate all request will be secure
				.and()
			.csrf().disable();

	 }
}
