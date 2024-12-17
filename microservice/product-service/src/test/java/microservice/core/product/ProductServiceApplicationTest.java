package microservice.core.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getProductById() {
        int productId = 1;
        webTestClient.get().uri("/product/" + productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody().jsonPath("$.productId").isEqualTo(productId);

    }

    @Test
    void getProductInvalidParameterString() {
        webTestClient.get().uri("/product/non-integer")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(BAD_REQUEST)
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.path").isEqualTo("/product/non-integer")
                .jsonPath("$.message").isEqualTo("Type mismatch.");

    }

    @Test
    void getProductNotFound() {
        int productId = 13;

        webTestClient.get().uri("/product/" + productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.path").isEqualTo("/product/" + productId)
                .jsonPath("$.message").isEqualTo("No product found for productId: " + productId);

    }

    @Test
    void getProductInvalidParameterNegativeValue() {
        int productId = -1;
        webTestClient.get().uri("/product/"+ productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.path").isEqualTo("/product/" + productId)
                .jsonPath("$.message").isEqualTo("Invalid productId: " + productId);
    }
}