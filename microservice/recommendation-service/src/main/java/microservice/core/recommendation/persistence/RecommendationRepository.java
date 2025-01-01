package microservice.core.recommendation.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Dictionary;
import java.util.List;
import java.util.Optional;


public interface RecommendationRepository extends CrudRepository<RecommendationEntity, Long> {
    List<RecommendationEntity> findByProductId(int productId);

    RecommendationEntity findByRecommendationId(int recommendationId);


    Optional<RecommendationEntity> getRecommendationEntityById(String id);
}
