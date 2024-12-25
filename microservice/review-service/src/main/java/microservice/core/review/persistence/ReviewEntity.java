package microservice.core.review.persistence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "review_product_idx", unique = true, columnList = "productId, reviewId")
})
@Data
public class ReviewEntity {
    @Id
    private Long id;
    @Column(name = "product_id")
    private int productId;
    @Column(name = "review_id")
    private int reviewId;
    private String author;
    private String content;
    private String subject;
}
