package org.sdrc.rmnchaapi;

import org.sdrc.rmnchaapi.filters.AuthHeaderFilter;
import org.sdrc.rmnchaapi.filters.ErrorFilter;
import org.sdrc.rmnchaapi.filters.PostFilter;
import org.sdrc.rmnchaapi.filters.RouteFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

	//Register the filter bean 
	@Bean
	AuthHeaderFilter authHeaderFilter() {
		return new AuthHeaderFilter();
	}

	@Bean
	public PostFilter postFilter() {
		return new PostFilter();
	}

	@Bean
	public ErrorFilter errorFilter() {
		return new ErrorFilter();
	}

	@Bean
	public RouteFilter routeFilter() {
		return new RouteFilter();
	}

}