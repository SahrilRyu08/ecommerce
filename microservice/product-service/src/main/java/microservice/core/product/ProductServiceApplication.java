package microservice.core.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("microservice")
public class ProductServiceApplication {
	private static Logger logger = LoggerFactory.getLogger(ProductServiceApplication.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(ProductServiceApplication.class, args);

		String mongoHost = run.getEnvironment().getProperty("spring.data.mongodb.host");
		String mongoPort = run.getEnvironment().getProperty("spring.data.mongodb.port");

		logger.info("connect to mongodb host: " + mongoHost + ", and port :" + mongoPort);

	}

}
