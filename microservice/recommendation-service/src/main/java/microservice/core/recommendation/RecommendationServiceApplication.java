package microservice.core.recommendation;

import microservice.api.core.recomendation.Recommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

@SpringBootApplication
@ComponentScan("microservice")
public class RecommendationServiceApplication {

	@Autowired
	MongoOperations mongotemplate;

	public static void main(String[] args) {
		SpringApplication.run(RecommendationServiceApplication.class, args);
	}


	@EventListener(ContextRefreshedEvent.class)
	public void init() {
		MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongotemplate.getConverter().getMappingContext();
		MongoPersistentEntityIndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);
		IndexOperations indexOperations = mongotemplate.indexOps(Recommendation.class);
		resolver.resolveIndexFor(Recommendation.class).forEach(indexOperations::ensureIndex);
	}


}
