package microservice.core.recommendation.service;

import com.mongodb.DuplicateKeyException;
import microservice.api.core.recomendation.RecommendationService;
import microservice.api.core.recomendation.Recommendation;
import microservice.api.exceptions.InvalidInputException;
import microservice.core.recommendation.persistence.RecommendationEntity;
import microservice.core.recommendation.persistence.RecommendationRepository;
import microservice.core.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RecommendationServiceImpl implements RecommendationService {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final RecommendationMapper recommendationMapper;

    private final RecommendationRepository recommendationRepository;


    @Autowired
    public RecommendationServiceImpl(ServiceUtil serviceUtil, RecommendationMapper recommendationMapper, RecommendationRepository recommendationRepository) {
        this.serviceUtil = serviceUtil;
        this.recommendationMapper = recommendationMapper;
        this.recommendationRepository = recommendationRepository;
    }

    @Override
    public List<Recommendation> getRecommendations(int productid) {
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

    @Override
    public Recommendation createRecommendation(Recommendation recommendation) {
        try {
            RecommendationEntity recommendationEntity = recommendationMapper.apiToEntity(recommendation);
            RecommendationEntity recommendationEntity1 = recommendationRepository.save(recommendationEntity);
            logger.info("createRecommendation{}", recommendationEntity1);
            return recommendationMapper.entityToApi(recommendationEntity1);
        } catch (DuplicateKeyException e) {
            throw new InvalidInputException("invalid recommendation " + recommendation);

        }
    }

    @Override
    public void deleteRecommendation(int productId) {
        recommendationRepository.deleteAll(recommendationRepository.findByProductId(productId));
    }


}
