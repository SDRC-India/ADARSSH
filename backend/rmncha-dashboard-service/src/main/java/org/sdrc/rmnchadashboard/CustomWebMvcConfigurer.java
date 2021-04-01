package org.sdrc.rmnchadashboard;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
public class CustomWebMvcConfigurer extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

//		registry.addResourceHandler("/**/*").addResourceLocations("classpath:/static/").resourceChain(true)
//				.addResolver(new PathResourceResolver() {
//					@Override
//					protected Resource getResource(String resourcePath, Resource location) throws IOException {
//						Resource requestedResource = location.createRelative(resourcePath);
//						return requestedResource.exists() && requestedResource.isReadable() ? requestedResource
//								: new ClassPathResource("/static/index.html");
//					}
//				});
	}
}
