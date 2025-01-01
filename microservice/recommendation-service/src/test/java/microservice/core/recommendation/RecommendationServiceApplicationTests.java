package microservice.core.recommendation;

import microservice.api.core.recomendation.Recommendation;
import microservice.core.recommendation.persistence.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import static reactor.core.publisher.Mono.just;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecommendationServiceApplicationTests extends MongoDBTest {

	private final static int PRODUCT_ID = 1;
	private final static int RECOMMENDATION_ID = 2;
	private final static int RECOMMENDATION_ID_2 = 3;

	@Autowired
	private WebTestClient webClient;

	@Autowired
	private RecommendationRepository recommendationRepository;

	@BeforeEach
	void setUp() {
		recommendationRepository.deleteAll();
	}

	@Test
	void getRecommndationByProduct() {
		int productId = 1;

		postAndVerifyRecommendation(productId, 1, OK);
		postAndVerifyRecommendation(productId, 2, OK);
		postAndVerifyRecommendation(productId, 3, OK);
		assertEquals(3, recommendationRepository.findByProductId(productId).size());

		webClient.get().uri("/recommendation?productId=" + productId)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType("application/json")
				.expectBody().jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[2].productId").isEqualTo(productId)
				.jsonPath("$[2].productId").isEqualTo(productId)
				.jsonPath("$[2].recommendationId").isEqualTo(3);
	}

	@Test
	void duplicateError() {
		int productId = 1;
		int recommendationId = 1;

		postAndVerifyRecommendation(productId, recommendationId, OK)
				.jsonPath("$.productId").isEqualTo(productId)
				.jsonPath("$.recommendationId").isEqualTo(recommendationId);
		assertEquals(1, recommendationRepository.count());

		postAndVerifyRecommendation(productId, recommendationId, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Duplicate recommendation");
		assertEquals(1, recommendationRepository.count());

	}

	private WebTestClient.BodyContentSpec postAndVerifyRecommendation(int productId, int recommendationId, HttpStatus httpStatus) {
		Recommendation recommendation = new Recommendation(productId, recommendationId,"Author " + recommendationId, recommendationId,"content " + recommendationId, "SA");
		return webClient.post().uri("/recommendation")
				.contentType(MediaType.APPLICATION_JSON)
				.body(just(recommendation), Recommendation.class)
				.exchange()
				.expectStatus().isEqualTo(httpStatus)
				.expectHeader().contentType("application/json")
				.expectBody();
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
