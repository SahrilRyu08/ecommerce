package microservice.composite.product;

import microservice.api.composite.product.ProductAggregate;
import microservice.api.composite.product.RecommendationSummary;
import microservice.api.composite.product.ReviewSummary;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static reactor.core.publisher.Mono.just;


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
    void createCompositeProduct1() {
        ProductAggregate productAggregate = new ProductAggregate(1,"name",1,null,null,null);
        postAndVerifyProduct(productAggregate, OK);
    }

    @Test
    void createCompositeProduct2() {
        getAndVerifyProduct(PRODUCT_ID_OK, OK);
        ProductAggregate productAggregate = new ProductAggregate(1,"name",1,
                singletonList(new RecommendationSummary(1,"author 1", 1, "content 1")),
                singletonList(new ReviewSummary(1,"author 1", "subject 1", "content 1")),
                null);
        postAndVerifyProduct(productAggregate, OK);
    }

    private void getAndVerifyProduct(int productId, HttpStatus httpStatus) {
        webTestClient.get().uri("/product-composite/" + productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(httpStatus)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();

    }

    @Test
    void deleteCompositeProduct() {
        ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1,
                singletonList(new RecommendationSummary(1, "a", 1, "c")),
                singletonList(new ReviewSummary(1, "a", "s", "c")), null);

        postAndVerifyProduct(compositeProduct, OK);

        deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
        deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
    }

    private void deleteAndVerifyProduct(int productId, HttpStatus httpStatus) {
        webTestClient.delete().uri("/product-composite/" + productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(httpStatus)
                .expectBody();
    }

    private void postAndVerifyProduct(ProductAggregate productAggregate, HttpStatus httpStatus) {
        webTestClient.post().uri("/product-composite")
                .body(just(productAggregate), ProductAggregate.class)
                .exchange()
                .expectStatus().isEqualTo(httpStatus);
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