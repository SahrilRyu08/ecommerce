package microservice.core.review.service;

import microservice.api.core.review.Review;
import microservice.api.core.review.ReviewService;
import microservice.api.exceptions.InvalidInputException;
import microservice.core.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class ReviewServiceImpl implements ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public ReviewServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Review> getReviews(int productId) {

        if (productId < 1) {
            throw new InvalidInputException("productId must be greater than zero");
        }

        if (productId == 213) {
            logger.info("ReviewServiceImpl getReviews: productId=213");
            return new ArrayList<>();
        }

        List<Review> reviewList = new ArrayList<>();

        reviewList.add(new Review(productId,1,"author 1", "content 1", "subject 1",serviceUtil.getServiceAddress()));
        reviewList.add(new Review(productId,2,"author 2", "content 2","subject 2", serviceUtil.getServiceAddress()));
        reviewList.add(new Review(productId,3,"author 3", "content 3", "subject 3", serviceUtil.getServiceAddress()));
        logger.info("ReviewServiceImpl getReviews: productId={}, reviewList={}", productId, reviewList);
        return reviewList;
    }

    @Override
    public Review createReview(Review review) {
        return null;
    }

    @Override
    public void deleteReview(int productId) {

    }
}
