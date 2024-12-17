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
    public ProductAggregate getProductAggregate(Integer productId) {
        Product product = productCompositeIntegration.getProduct(productId);
        List<Recommendation> recommendations = productCompositeIntegration.getRecomendations(productId);
        List<Review> reviews = productCompositeIntegration.getReviews(productId);

        return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    private ProductAggregate createProductAggregate(Product product, List<Recommendation> recommendations, List<Review> reviews, String serviceAddress) {
        logger.info("Creating product aggregate for product id {}", product.getProductId());

        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        List<RecomendationSummary> recomendationSummaries = (recommendations == null) ? null : recommendations.stream().map(
                r -> new RecomendationSummary(r.getRecommendationId(),r.getAuthor(),r.getRate())
        ).collect(Collectors.toList());

        List<ReviewSummary> reviewSummaries = (reviews == null) ? null : reviews.stream().map(
                r -> new ReviewSummary(r.getReviewId(),r.getAuthor(),r.getSubject())
        ).collect(Collectors.toList());
        String productServiceAddress = product.getServiceAddress();
        String reviewAddress = (reviews != null && !reviews.isEmpty()) ? reviews.get(0).getServiceAddress() : "";
        String recommendationAddress = (recommendations != null && !recommendations.isEmpty()) ? recommendations.get(0).getServiceAddress() : "";
        ServiceAddress serviceAddress1 = new ServiceAddress(serviceAddress,productServiceAddress,reviewAddress,recommendationAddress);

        return new ProductAggregate(productId,name,weight,
                recomendationSummaries,
                reviewSummaries,
                serviceAddress1);
    }
}
