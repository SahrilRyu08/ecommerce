package microservice.composite.product;

import microservice.api.core.product.Product;
import microservice.api.core.recomendation.Recommendation;
import microservice.api.core.review.Review;
import microservice.api.exceptions.InvalidInputException;
import microservice.api.exceptions.NotFoundException;
import microservice.composite.product.serivices.ProductCompositeIntegration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.codec.ByteArrayDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductCompositeServiceApplicationTest {

    private final static int PRODUCT_ID_OK = 1;
    private final static int PRODUCT_ID_NOT_FOUND = 213;
    private final static int PRODUCT_ID_INVALID = 3;

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private ProductCompositeIntegration productCompositeIntegration;


    @BeforeEach
    void setUp() {
        when(productCompositeIntegration.getProduct(PRODUCT_ID_OK))
                .thenReturn(new Product(PRODUCT_ID_OK, "name", 1,"mock-address"));
        ArrayList<Recommendation> recommendations = new ArrayList<>();
        Recommendation recommendation = new Recommendation(PRODUCT_ID_OK,1,"author 1", 1,"content 1", "mock-address");
        recommendations.add(recommendation);
        when(productCompositeIntegration.getRecommendations(PRODUCT_ID_OK))
                .thenReturn(recommendations);
        ArrayList<Review> reviews = new ArrayList<>();
        Review review = new Review(PRODUCT_ID_OK,1,"author 1", "content 1", "subject 1", "mock-address");
        reviews.add(review);
        when(productCompositeIntegration.getReviews(PRODUCT_ID_OK))
                .thenReturn(reviews);
        when(productCompositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND))
                .thenThrow(new NotFoundException("NOT FOUND:" + PRODUCT_ID_NOT_FOUND));
        when(productCompositeIntegration.getProduct(PRODUCT_ID_INVALID))
                .thenThrow(new InvalidInputException("INVALID : " + PRODUCT_ID_INVALID));
    }

    @Test
    void getProductId() {
        webTestClient.get().uri("/product-composite/" + PRODUCT_ID_OK)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK)
                .jsonPath("$.recommendationSummaryList.length()").isEqualTo(1)
                .jsonPath("$.reviewSummaryList.length()").isEqualTo(1);
    }

    @Test
    void getProductNotFound() {
        webTestClient.get().uri("/product-composite/" + PRODUCT_ID_NOT_FOUND)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
                .jsonPath("$.message").isEqualTo("NOT FOUND:" + PRODUCT_ID_NOT_FOUND);
    }

    @Test
    void getProductInvalidInput() {
        webTestClient.get().uri("/product-composite/" + PRODUCT_ID_INVALID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
                .jsonPath("$.message").isEqualTo("INVALID : " + PRODUCT_ID_INVALID);
    }

    @Test
    void getProductWithBadRequest() {

        webTestClient.get().uri("/product-composite/non-integer")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.path").isEqualTo("/product-composite/non-integer")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }
}