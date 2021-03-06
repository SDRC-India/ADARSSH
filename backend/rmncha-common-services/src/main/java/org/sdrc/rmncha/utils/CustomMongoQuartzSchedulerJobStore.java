package org.sdrc.rmncha.utils;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import com.novemberain.quartz.mongodb.MongoDBJobStore;

/**
 * @author Sarita
 * 
 *         <p>
 *         We extend the {@link MongoDBJobStore} because we need to set the
 *         custom mongo db parameters. Some of the configuration comes from
 *         application.properties files we have for each environment.
 *         </p>
 * 
 *         < These are set as part of initialization. This class is initialized
 *         by {@link StdSchedulerFactory} and defined in the quartz.properties
 *         file.
 * 
 *         </p>
 *
 */
public class CustomMongoQuartzSchedulerJobStore extends MongoDBJobStore {

	private static String mongoAddresses;
	private static String userName;
	private static String password;
	private static String dbName;

	public CustomMongoQuartzSchedulerJobStore() {
		super();
		initializeMongo();
		setMongoUri("mongodb://" + mongoAddresses);
		setDbName(dbName);
	}

	/**
	 * <p>
	 * This method will initialize the mongo instance required by the Quartz
	 * scheduler.
	 * 
	 * The use case here is that we have two profiles;
	 * </p>
	 * 
	 * <ul>
	 * <li>Development</li>
	 * <li>Production</li>
	 * </ul>
	 * 
	 * <p>
	 * So when constructing the mongo instance to be used for the Quartz scheduler,
	 * we need to read the various properties set within the system to determine
	 * which would be appropriate depending on which spring profile is active.
	 * </p>
	 * 
	 */
	private static void initializeMongo() {
		/**
		 * The use case here is that when we run our application, the property
		 * spring.profiles.active is set as a system property during production. But it
		 * will not be set in a local environment.
		 */
		

		String env = System.getenv(SystemProperties.ENVIRONMENT);
		env = StringUtils.isNotBlank(env) ? env : "default";
		/**
		 * The mongo DB user name and password are only password as command line
		 * parameters in the production environment and for the development environment
		 * it will be null which is why we use StringUtils#trimToEmpty so we can pass
		 * empty strings for the user name and password in the development environment
		 * since we do not have authentication on the development environment.s
		 */
		
		PropertiesFactoryBean environmentSpecificProperties = new PropertiesFactoryBean();
		
		switch (env) {
		case "test":
			environmentSpecificProperties.setLocation(new ClassPathResource("/application-test.properties"));
			/**
			 * By default, in the production mongo instance, SSL is enabled and SSL invalid
			 * host name allowed property is set.
			 */
			try {
				environmentSpecificProperties.afterPropertiesSet();
				
				dbName = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_DB_NAME);
				
				String userNameKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_USERNAME);
				userNameKey= userNameKey.substring(2, userNameKey.length()-1);
				userName = System.getenv(userNameKey);
				
				String pwKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_PASSWORD);
				pwKey= pwKey.substring(2, pwKey.length()-1);
				password = System.getenv(pwKey).replaceAll("@", "%40"); //#replace the @ in password field with %40; sdrc@mongo6356 -> sdrc%40mongo6356
				
				String hostKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_HOST);
				hostKey= hostKey.substring(2, hostKey.length()-1);
				
				String portKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_PORT);
				portKey= portKey.substring(2, portKey.length()-1);
				
				mongoAddresses = userName+":"+  password +"@"+ System.getenv(hostKey)+ ":" +  System.getenv(portKey);
				
//				#org.quartz.jobStore.mongoUri=mongodb://admin:sdrc%40mongo635@localhost:27017
					
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "dev":
			/**
			 * For the development profile, we just read the mongo URI that is set.
			 */
			environmentSpecificProperties.setLocation(new ClassPathResource("/application-dev.properties"));
			try {
				environmentSpecificProperties.afterPropertiesSet();
				
				dbName = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_DB_NAME);
				
				String userNameKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_USERNAME);
				userNameKey= userNameKey.substring(2, userNameKey.length()-1);
				userName = System.getenv(userNameKey);
				
				String pwKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_PASSWORD);
				pwKey= pwKey.substring(2, pwKey.length()-1);
				password = System.getenv(pwKey).replaceAll("@", "%40"); //#replace the @ in password field with %40; sdrc@mongo6356 -> sdrc%40mongo6356
				
				String hostKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_HOST);
				hostKey= hostKey.substring(2, hostKey.length()-1);
				
				String portKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_PORT);
				portKey= portKey.substring(2, portKey.length()-1);
				
				mongoAddresses = userName+":"+  password +"@"+ System.getenv(hostKey)+ ":" +  System.getenv(portKey);
				
//				#org.quartz.jobStore.mongoUri=mongodb://admin:sdrc%40mongo635@localhost:27017
					
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case "local":
			/**
			 * For the local profile, we just read the mongo URI that is set.
			 */
			environmentSpecificProperties.setLocation(new ClassPathResource("/application-local.properties"));
			try {
				environmentSpecificProperties.afterPropertiesSet();
				
				dbName = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_DB_NAME);
				
				String userNameKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_USERNAME);
				userNameKey= userNameKey.substring(2, userNameKey.length()-1);
				userName = System.getenv(userNameKey);
				
				String pwKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_PASSWORD);
				pwKey= pwKey.substring(2, pwKey.length()-1);
				password = System.getenv(pwKey).replaceAll("@", "%40"); //#replace the @ in password field with %40; sdrc@mongo6356 -> sdrc%40mongo6356
				
				String hostKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_HOST);
				hostKey= hostKey.substring(2, hostKey.length()-1);
				
				String portKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_PORT);
				portKey= portKey.substring(2, portKey.length()-1);
				
				mongoAddresses = userName+":"+  password +"@"+ System.getenv(hostKey)+ ":" +  System.getenv(portKey);
				
//				#org.quartz.jobStore.mongoUri=mongodb://admin:sdrc%40mongo635@localhost:27017
					
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case "uat":
			/**
			 * By deafult, in the UAT mongo instance, SSL is enabled and SSL invalid
			 * host name allowed property is set.
			 */
			environmentSpecificProperties.setLocation(new ClassPathResource("/application-uat.properties"));
			try {
				environmentSpecificProperties.afterPropertiesSet();
				
				dbName = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_DB_NAME);
				
				String userNameKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_USERNAME);
				userNameKey= userNameKey.substring(2, userNameKey.length()-1);
				userName = System.getenv(userNameKey);
				
				String pwKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_PASSWORD);
				pwKey= pwKey.substring(2, pwKey.length()-1);
				password = System.getenv(pwKey).replaceAll("@", "%40"); //#replace the @ in password field with %40; sdrc@mongo6356 -> sdrc%40mongo6356
				
				String hostKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_HOST);
				hostKey= hostKey.substring(2, hostKey.length()-1);
				
				String portKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_PORT);
				portKey= portKey.substring(2, portKey.length()-1);
				
				mongoAddresses = userName+":"+  password +"@"+ System.getenv(hostKey)+ ":" +  System.getenv(portKey);
				
//				#org.quartz.jobStore.mongoUri=mongodb://admin:sdrc%40mongo635@localhost:27017
					
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "prod":
			/**
			 * By deafult, in the UAT mongo instance, SSL is enabled and SSL invalid
			 * host name allowed property is set.
			 */
			environmentSpecificProperties.setLocation(new ClassPathResource("/application-prod.properties"));
			try {
				environmentSpecificProperties.afterPropertiesSet();
				
				String userNameKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_USERNAME);
				userNameKey= userNameKey.substring(2, userNameKey.length()-1);
				userName = System.getenv(userNameKey);
				
				String pwKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_PASSWORD);
				pwKey= pwKey.substring(2, pwKey.length()-1);
				password = System.getenv(pwKey).replaceAll("@", "%40"); //#replace the @ in password field with %40; sdrc@mongo6356 -> sdrc%40mongo6356
				
				String hostKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_HOST);
				hostKey= hostKey.substring(2, hostKey.length()-1);
				
				String portKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_PORT);
				portKey= portKey.substring(2, portKey.length()-1);
				
				mongoAddresses = userName+":"+  password +"@"+ System.getenv(hostKey)+ ":" +  System.getenv(portKey);
				
//				#org.quartz.jobStore.mongoUri=mongodb://admin:sdrc%40mongo635@localhost:27017
					
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
			
			default: //production
				environmentSpecificProperties.setLocation(new ClassPathResource("/application.properties"));
				try {
					environmentSpecificProperties.afterPropertiesSet();
					
					String userNameKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_USERNAME);
					userNameKey= userNameKey.substring(2, userNameKey.length()-1);
					userName = System.getenv(userNameKey);
					
					String pwKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_PASSWORD);
					pwKey= pwKey.substring(2, pwKey.length()-1);
					password = System.getenv(pwKey).replaceAll("@", "%40"); //#replace the @ in password field with %40; sdrc@mongo6356 -> sdrc%40mongo6356
					
					String hostKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_HOST);
					hostKey= hostKey.substring(2, hostKey.length()-1);
					
					String portKey = environmentSpecificProperties.getObject().getProperty(SystemProperties.MONGO_PORT);
					portKey= portKey.substring(2, portKey.length()-1);
					
					mongoAddresses = userName+":"+  password +"@"+ System.getenv(hostKey)+ ":" +  System.getenv(portKey);
					
//					#org.quartz.jobStore.mongoUri=mongodb://admin:sdrc%40mongo635@localhost:27017
						
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
		}
	}
}
