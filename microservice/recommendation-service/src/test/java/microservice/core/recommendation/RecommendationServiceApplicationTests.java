package microservice.core.recommendation;

import microservice.api.core.recomendation.Recommendation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecommendationServiceApplicationTests {

	private final static int PRODUCT_ID = 1;
	private final static int RECOMMENDATION_ID = 2;
	private final static int RECOMMENDATION_ID_2 = 3;

	@Autowired
	private WebTestClient webClient;


	@Test
	void getRecommndationById() {
		int productId = 1;
		webClient.get().uri("/recommendation?productId=" + productId)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType("application/json")
				.expectBody().jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[0].productId").isEqualTo(productId);
	}

	@Test
	void getRecommendationWithMissingParameter() {
		webClient.get().uri("/recommendation")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectHeader().contentType("application/json")
				.expectBody().jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Required query parameter 'productId' is not present.");
	}

	@Test
	void getRecommendationInvalidParameterString() {
		int productId = -1;
		webClient.get().uri("/recommendation?productId=" + productId)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectHeader().contentType("application/json")
				.expectBody()
				.jsonPath("$.message")
				.isEqualTo("invalid productId " + productId);
	}

	
}
