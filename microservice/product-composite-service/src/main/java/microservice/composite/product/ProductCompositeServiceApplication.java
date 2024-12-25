package microservice.composite.product;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("microservice")
public class ProductCompositeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductCompositeServiceApplication.class, args);
	}


	@Value("${api.common.version}") String apiVersion;
	@Value("${api.common.title}") String apiTitle;
	@Value("${api.common.description}") String apiDescription;
	@Value("${api.common.termsOfService}") String apiTermsOfService;
	@Value("${api.common.license}") String apiLicense;
	@Value("${api.common.licenseUrl}") String apiLicenseUrl;
	@Value("${api.common.externalDocDesc}") String apiExternalDocDesc;
	@Value("${api.common.externalDocUrl}") String apiExternalDocUrl;
	@Value("${api.common.contact.name}") String contactName;
	@Value("${api.common.contact.url}") String contactUrl;
	@Value("${api.common.contact.email}") String contactEmail;

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info().
				title(apiTitle)
						.description(apiDescription)
				.version(apiVersion)
				.contact(new Contact()
						.name(contactName)
						.url(contactUrl)
						.email(contactEmail))
				.termsOfService(apiTermsOfService)
				.license(new License()
						.name(apiLicense)
						.url(apiLicenseUrl)))
				.externalDocs(new ExternalDocumentation()
						.description(apiExternalDocDesc)
						.url(apiExternalDocUrl));
	}
}


