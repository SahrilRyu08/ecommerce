package microservice.api.composite.product;

public class ServiceAddress {
    private final String compositeAddress;
    private final String productAddress;
    private final String reviewAddress;
    private final String recommendAddress;

    public ServiceAddress(String compositeAddress, String productAddress, String reviewAddress, String recommendAddress) {
        this.compositeAddress = compositeAddress;
        this.productAddress = productAddress;
        this.reviewAddress = reviewAddress;
        this.recommendAddress = recommendAddress;
    }

    public String getCompositeAddress() {
        return compositeAddress;
    }

    public String getProductAddress() {
        return productAddress;
    }

    public String getReviewAddress() {
        return reviewAddress;
    }

    public String getRecommendAddress() {
        return recommendAddress;
    }
}
