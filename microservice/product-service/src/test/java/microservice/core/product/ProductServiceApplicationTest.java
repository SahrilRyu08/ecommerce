package microservice.core.product;

import microservice.api.core.product.Product;
import microservice.core.product.persistence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTest extends MongoDbTest{

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void getProductById() {
        int productId = 1;
        postAndVerifyProduct(productId);
        webTestClient.get().uri("/product/" + productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody().jsonPath("$.productId").isEqualTo(productId);

    }

    @Test
    void duplicateError() {
        int productId = 1;
        postAndVerifyProduct(productId);
        assertTrue(productRepository.findByProductId(productId).isPresent());
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
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
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

    private void postAndVerifyProduct(int productId) {
        Product product = new Product(productId, "name " + productId, productId, "SA");
        webTestClient.post()
                .uri("/product")
                .body(Mono.just(product), Product.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    private void getAndVerifyProduct(int productId) {
        webTestClient.get()
                .uri("/product/" + productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().contentType("application/json")
                .expectBody();
    }

    private void deleteAndVerifyProduct(int productId) {
        webTestClient.delete()
                .uri("/product/" + productId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
                .expectBody();
    }
}