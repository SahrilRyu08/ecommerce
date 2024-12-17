package microservice.core.recommendation.service;

import microservice.api.core.recomendation.RecomendationService;
import microservice.api.core.recomendation.Recommendation;
import microservice.api.exceptions.InvalidInputException;
import microservice.core.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RecommendationServiceImpl implements RecomendationService {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public RecommendationServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Recommendation> getRecomendations(int productid) {
        logger.info("getRecommendations" + productid);
        if (productid < 0) {
            throw new InvalidInputException("invalid productId " +  productid);
        }
        if (productid == 113) {
            return new ArrayList<>();
        }

        List<Recommendation> recomendationList = new ArrayList<>();
        recomendationList.add(new Recommendation(productid,1,"author1",1,"content 1", serviceUtil.getServiceAddress()));
        recomendationList.add(new Recommendation(productid,2,"author2",2,"content 2", serviceUtil.getServiceAddress()));
        recomendationList.add(new Recommendation(productid,3,"author3",3,"content 3", serviceUtil.getServiceAddress()));
        logger.info("getRecommendations" + recomendationList);
        return recomendationList;
    }
}
