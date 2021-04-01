package org.sdrc.rmncha;

import org.sdrc.rmncha.rabbitMQ.CollectionChannel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.client.RestTemplate;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 * @author Sarita
 */
@SpringBootApplication
//@EnableDiscoveryClient
//@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableMongoRepositories(basePackages = { "in.co.sdrc.sdrcdatacollector.mongorepositories",
		"org.sdrc.rmncha.repositories" })
@ComponentScan(basePackages = { "org.sdrc.rmncha", "in.co.sdrc.sdrcdatacollector", "org.sdrc.usermgmt.core" })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@EnableCaching
@PropertySource(value = { "classpath:messages.properties","classpath:rmncha_notification.properties" })
@EnableBinding(CollectionChannel.class)
@EnableScheduling
public class StartUpClass extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(StartUpClass.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(StartUpClass.class);
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:springsecuritymessages");
		messageSource.setCacheSeconds(10); // reload messages every 10 seconds
		return messageSource; // return messageSource;
	}
	
	@LoadBalanced
	@Bean
	public RestTemplate testTemplate() {
		return new RestTemplate();
	}

}
