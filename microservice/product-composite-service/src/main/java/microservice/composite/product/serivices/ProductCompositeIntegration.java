package microservice.composite.product.serivices;


import com.fasterxml.jackson.databind.ObjectMapper;
import microservice.api.core.product.Product;
import microservice.api.core.product.ProductService;
import microservice.api.core.recomendation.Recommendation;
import microservice.api.core.recomendation.RecommendationService;
import microservice.api.core.review.Review;
import microservice.api.core.review.ReviewService;
import microservice.api.exceptions.InvalidInputException;
import microservice.api.exceptions.NotFoundException;
import microservice.core.util.GlobalControllerExceptionHandler;
import microservice.core.util.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.web.exchanges.reactive.HttpExchangesWebFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;
    private final GlobalControllerExceptionHandler globalControllerExceptionHandler;

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
            String reviewServicePort, GlobalControllerExceptionHandler globalControllerExceptionHandler) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        this.recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation";
        this.reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review";
        this.globalControllerExceptionHandler = globalControllerExceptionHandler;
    }

    @Override
    public Product getProduct(int productId) {
        try {
            String url = productServiceUrl + productId;
            logger.info("get url: {}", url);
            Product product = restTemplate.getForObject(url, Product.class);
            assert product != null;
            logger.info("found a product: {}", product.getProductId());
            return product;
        } catch (HttpClientErrorException ex) {
            switch (Objects.requireNonNull(HttpStatus.resolve(ex.getStatusCode().value()))) {
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

    @Override
    public Product createProduct(Product product) {
        try {
            return restTemplate.postForObject(productServiceUrl, product, Product.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);

        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {
            restTemplate.delete(productServiceUrl + "/" + productId);
        } catch (HttpClientErrorException ex) {
            throw new InvalidInputException(getErrorMessage(ex));
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
    public List<Recommendation> getRecommendations(int productid) {
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
    public Recommendation createRecommendation(Recommendation recommendation) {
        try {
            String url = recommendationServiceUrl;
            logger.debug("get url: {}", url);
            Recommendation recommendation1 = restTemplate.postForObject(url, recommendation, Recommendation.class);
            logger.debug("create recommendation: {}", recommendation1.getProductId());
            return recommendation1;
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }



    @Override
    public void deleteRecommendation(int productId) {
        try {
            restTemplate.delete(recommendationServiceUrl + "?productId=" + productId);
        } catch (HttpClientErrorException ex) {
            throw new InvalidInputException(getErrorMessage(ex));
        }
    }

    @Override
    public List<Review> getReviews(int productId) {
        try {
            String url = reviewServiceUrl + productId;
            logger.info("get url: {}", url);
            List<Review> body = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {
            }).getBody();

            logger.info("found a review: {}, productIf: {}", body.size(), productId);
            return body;
        } catch (Exception ex) {
            logger.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Review createReview(Review review) {
        try {
            return restTemplate.postForObject(reviewServiceUrl, review, Review.class);
        } catch (HttpClientErrorException ex) {
            throw new InvalidInputException(getErrorMessage(ex));
        }
    }

    @Override
    public void deleteReview(int productId) {
        try {
            restTemplate.delete(reviewServiceUrl + "?productId=" + productId);
        } catch (HttpClientErrorException ex) {
            throw new InvalidInputException(getErrorMessage(ex));
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (Objects.requireNonNull(HttpStatus.resolve(ex.getStatusCode().value()))) {
            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));
            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(getErrorMessage(ex));
            default:
                logger.warn("Got an unexpected HTTP error : {}, will rethrow it", ex.getStatusCode());
                logger.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }

}
