package microservice.api.composite.product;


import java.util.List;

public class ProductAggregate {
    private final int productId;
    private final String name;
    private final int weight;
    private final List<RecommendationSummary> recommendationSummaryList;
    private final List<ReviewSummary> reviewSummaryList;
    private final ServiceAddress serviceAddress;

    public ProductAggregate(int productId, String name, int weight, List<RecommendationSummary> recommendationSummaryList, List<ReviewSummary> reviewSummaryList, ServiceAddress serviceAddress) {
        this.productId = productId;
        this.name = name;
        this.weight = weight;
        this.recommendationSummaryList = recommendationSummaryList;
        this.reviewSummaryList = reviewSummaryList;
        this.serviceAddress = serviceAddress;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public List<RecommendationSummary> getRecommendationSummaryList() {
        return recommendationSummaryList;
    }

    public List<ReviewSummary> getReviewSummaryList() {
        return reviewSummaryList;
    }

    public ServiceAddress getServiceAddress() {
        return serviceAddress;
    }
}
