package microservice.core.recommendation;

import lombok.Data;
import microservice.core.recommendation.persistence.RecommendationEntity;
import microservice.core.recommendation.persistence.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
public class RecommendationPersistenceTest {

    @Autowired
    private RecommendationRepository recommendationRepository;
    private RecommendationEntity entity;

    @BeforeEach
    void setUp() {
        recommendationRepository.deleteAll();
        RecommendationEntity recommendationEntity1 = new RecommendationEntity("1",1L,1,1, "author 1",1, "content 1");
        entity = recommendationRepository.save(recommendationEntity1);
        assertEqualsRecommendation(entity, recommendationEntity1);
    }

    @Test
    void create() {
        RecommendationEntity recommendationEntity  = new RecommendationEntity("1",1L,1,1, "author 1",1, "content 1");
        RecommendationEntity recommendationEntity1 = recommendationRepository.save(recommendationEntity);
        assertEqualsRecommendation(recommendationEntity, recommendationEntity1);
        assertEquals(2, recommendationEntity1.getVersion());
        assertEquals("author 1", recommendationEntity1.getAuthor());

    }

    private void assertEqualsRecommendation(RecommendationEntity recommendationEntity, RecommendationEntity recommendationEntity1) {
        assertEquals(recommendationEntity.getId(), recommendationEntity1.getId());
        assertEquals(recommendationEntity.getVersion(), recommendationEntity1.getVersion());
        assertEquals(recommendationEntity.getRecommendationId(), recommendationEntity1.getRecommendationId());
        assertEquals(recommendationEntity.getContent(), recommendationEntity1.getContent());
        assertEquals(recommendationEntity.getAuthor(), recommendationEntity1.getAuthor());
        assertEquals(recommendationEntity.getRating(), recommendationEntity1.getRating());

    }
}
