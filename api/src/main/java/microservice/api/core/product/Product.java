package microservice.api.core.product;

public class Product {
    private final int productId;
    private final String name;
    private final int weight;
    private String serviceAddress;

    public Product(int productId, String name, int weight, String serviceAddress) {
        this.productId = productId;
        this.name = name;
        this.weight = weight;
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

    public String getServiceAddress() {
        return serviceAddress;
    }


    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

}
