package org.sdrc.rmncha.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class RmnchaEurekaServerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(RmnchaEurekaServerApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RmnchaEurekaServerApplication.class);
	}

}

