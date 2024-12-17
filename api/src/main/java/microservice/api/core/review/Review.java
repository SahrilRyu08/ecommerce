package microservice.api.core.review;

public class Review {
    private final int productId;
    private final int reviewId;
    private final String author;
    private final String content;
    private final String subject;
    private final String serviceAddress;

    public Review(int productId, int reviewId, String author, String content, String subject, String serviceAddress) {
        this.productId = productId;
        this.reviewId = reviewId;
        this.author = author;
        this.content = content;
        this.subject = subject;
        this.serviceAddress = serviceAddress;
    }

    public int getProductId() {
        return productId;
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getSubject() {
        return subject;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }
}
