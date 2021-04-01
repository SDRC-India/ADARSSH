package org.sdrc.rmnchadashboard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.sdrc.rmnchadashboard.rabbitMQ.CollectionQueryChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
//@EnableDiscoveryClient
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableElasticsearchRepositories(basePackages = "org.sdrc.rmnchadashboard.repository")
@EnableJpaRepositories(basePackages = { "org.sdrc.rmnchadashboard.jparepository" })
@EntityScan(basePackages = { "org.sdrc.rmnchadashboard.jpadomain" })
@EnableMongoRepositories(basePackages = { "in.co.sdrc.sdrcdatacollector.mongorepositories",
		"org.sdrc.rmncha.repositories", "org.sdrc.rmncha.mongorepository", "org.sdrc.usermgmt.mongodb.repository" })
@ComponentScan(basePackages = { "org.sdrc.rmnchadashboard", "org.sdrc.rmncha", "in.co.sdrc.sdrcdatacollector",
		"org.sdrc.usermgmt.core" })
@EnableResourceServer
@EnableBinding(CollectionQueryChannel.class)
@PropertySource(value = { "classpath:messages.properties" })
public class DashboardService extends SpringBootServletInitializer {

	Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private final Path uploadLocation = Paths.get("/rmncha-data");
	private final Path downloadLocation = Paths.get("/out");
	private final Path jsonDataLocation = Paths.get("/json-data");

	@Value("${output.path.pdf}")
	private String directoryPath;
	
	@Value("${cms.upload.path}")
	private String cmsFilePath;
	
//	@Autowired
//	private ConfigurableEnvironment configurableEnvironment;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DashboardService.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DashboardService.class);
	}

	@LoadBalanced
	@Bean
	RestTemplate testTemplate() {
		return new RestTemplate();
	}

//	@Bean
//	public ResourceServerTokenServices userInfoTokenServices() {
//
//		String authUrl = configurableEnvironment.getProperty("oauth2.authserver.url");
//		RemoteTokenServices tokenServices = new RemoteTokenServices();
//		tokenServices.setClientId("web");
//		tokenServices.setClientSecret("pass");
//		tokenServices.setCheckTokenEndpointUrl(authUrl + "/oauth/check_token");
//		return tokenServices;
//	}

	@PostConstruct
	public void init() {
		try {
			if (!Files.isDirectory(uploadLocation)) {
				Files.createDirectory(uploadLocation);
				Files.createDirectory(downloadLocation);
			}

			if (!Files.isDirectory(jsonDataLocation)) {
				Files.createDirectories(jsonDataLocation);
			}
			
			Path filePath = Paths.get(directoryPath+cmsFilePath);
			
			if (!Files.isDirectory(filePath)) {
				Files.createDirectories(filePath);
			}
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
	}

}
