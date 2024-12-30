package microservice.composite.product.serivices;

import microservice.api.composite.product.*;
import microservice.api.composite.product.ProductCompositeService;
import microservice.api.core.product.Product;
import microservice.api.core.recomendation.Recommendation;
import microservice.api.core.review.Review;
import microservice.core.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {

    private static final Logger logger = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductCompositeIntegration productCompositeIntegration;

    @Autowired
    public ProductCompositeServiceImpl(ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.productCompositeIntegration = integration;
    }

    @Override
    public void createProduct(ProductAggregate productAggregate) {
        try {
            logger.info("Creating product {}", productAggregate.getProductId());
            Product product = new Product(productAggregate.getProductId(), productAggregate.getName(), productAggregate.getWeight(), null);
            productCompositeIntegration.createProduct(product);
            if (productAggregate.getRecommendationSummaryList() != null) {
                productAggregate.getRecommendationSummaryList().forEach(
                        recommendationSummary -> {
                            Recommendation recommendation = new Recommendation(productAggregate.getProductId(),
                                    recommendationSummary.getRecommendationId(),
                                    recommendationSummary.getAuthor(),
                                    recommendationSummary.getRate(),
                                    recommendationSummary.getContent(), null);
                            productCompositeIntegration.createRecommendation(recommendation);
                        });
            }

            if (productAggregate.getReviewSummaryList() != null) {
                productAggregate.getReviewSummaryList().forEach(reviewSummary -> {
                    Review review = new Review(reviewSummary.getReviewId(), reviewSummary.getReviewId(), reviewSummary.getAuthor(), reviewSummary.getSubject(),reviewSummary.getContent(), null);
                    productCompositeIntegration.createReview(review);
                });
            }
        } catch (RuntimeException e) {
            logger.error("Error creating product", e);
            throw e;

        }
    }

    @Override
    public ProductAggregate getProduct(int productId) {
        Product product = productCompositeIntegration.getProduct(productId);
        List<Recommendation> recommendations = productCompositeIntegration.getRecommendations(productId);
        List<Review> reviews = productCompositeIntegration.getReviews(productId);

        return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    @Override
    public void deleteProduct(int productId) {
        productCompositeIntegration.deleteProduct(productId);
        productCompositeIntegration.deleteRecommendation(productId);
        productCompositeIntegration.deleteReview(productId);
    }


    private ProductAggregate createProductAggregate(Product product, List<Recommendation> recommendations, List<Review> reviews, String serviceAddress) {
            // 1. Setup product info
            int productId = product.getProductId();
            String name = product.getName();
            int weight = product.getWeight();

            // 2. Copy summary recommendation info, if available
            List<RecommendationSummary> recommendationSummaries =
                    (recommendations == null) ? null : recommendations.stream()
                            .map(r -> new RecommendationSummary(r.recommendationId(), r.author(), r.rate(), r.content()))
                            .collect(Collectors.toList());

            logger.info("recommendation summaries", Arrays.toString(recommendationSummaries.toArray()));
            // 3. Copy summary review info, if available
            List<ReviewSummary> reviewSummaries =
                    (reviews == null) ? null : reviews.stream()
                            .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                            .collect(Collectors.toList());

            logger.info("Review summaries", Arrays.toString(reviewSummaries.toArray()));

            // 4. Create info regarding the involved microservices addresses
            String productAddress = product.getServiceAddress();
            String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
            String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).serviceAddress() : "";
            ServiceAddress serviceAddresses = new ServiceAddress(serviceAddress, productAddress, reviewAddress, recommendationAddress);

            return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);

    }
}
