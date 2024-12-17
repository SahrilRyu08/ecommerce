package microservice.composite.product.serivices;


import com.fasterxml.jackson.databind.ObjectMapper;
import microservice.api.core.product.Product;
import microservice.api.core.product.ProductService;
import microservice.api.core.recomendation.Recommendation;
import microservice.api.core.recomendation.RecomendationService;
import microservice.api.core.review.Review;
import microservice.api.core.review.ReviewService;
import microservice.api.exceptions.InvalidInputException;
import microservice.api.exceptions.NotFoundException;
import microservice.core.util.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Component
public class ProductCompositeIntegration implements ProductService, RecomendationService, ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;


    @Autowired
    public ProductCompositeIntegration(
            RestTemplate restTemplate, ObjectMapper objectMapper,
            @Value("${app.product-service.host}")
            String productServiceHost,
            @Value("${app.product-service.port}")
            String productServicePort,

            @Value("${app.recommendation-service.host}")
            String recommendationServiceHost,
            @Value("${app.recommendation-service.port}")
            String recommendationServicePort,

            @Value("${app.review-service.host}")
            String reviewServiceHost,
            @Value("${app.review-service.port}")
            String reviewServicePort) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        this.recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
        this.reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
    }

    @Override
    public Product getProduct(int productId) {
        try {
            String url = productServiceUrl + productId;
            logger.info("get url: {}", url);
            Product product = restTemplate.getForObject(url, Product.class);
            logger.info("found a product: {}", product.getProductId());
            return product;
        } catch (HttpClientErrorException ex) {
            switch (HttpStatus.resolve(ex.getStatusCode().value())) {
                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(ex));
                case UNPROCESSABLE_ENTITY:
                    throw new InvalidInputException(getErrorMessage(ex));
                default:
                    logger.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    logger.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
            }
        }
    }

    private String getErrorMessage(HttpClientErrorException exception) {
        try {
            return objectMapper.readValue(exception.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ex) {
            return exception.getResponseBodyAsString();
        }
    }


    @Override
    public List<Recommendation> getRecomendations(int productid) {
        try {
            String url = recommendationServiceUrl  + productid;
            logger.info("get url: {}", url);
            List<Recommendation> body = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Recommendation>>() {
            }).getBody();
            logger.info("found a recommendation: {}, productIf: {}", body.size(), productid);
            return body;
        } catch (Exception ex) {
            logger.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Review> getReviews(int productId) {
        try {
            String url = reviewServiceUrl + productId;
            logger.info("get url: {}", url);
            List<Review> body = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {
            }).getBody();
            return body;
        } catch (Exception ex) {
            logger.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getMessage());
            return new ArrayList<>();
        }
    }
}
