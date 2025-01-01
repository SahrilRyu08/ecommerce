package microservice.core.recommendation;

import microservice.core.recommendation.persistence.RecommendationEntity;
import microservice.core.recommendation.persistence.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class RecommendationPersistenceTest extends MongoDBTest{

    private static final Logger log = LoggerFactory.getLogger(RecommendationPersistenceTest.class);


    @Autowired
    private RecommendationRepository recommendationRepository;
    private RecommendationEntity entity;

    @BeforeEach
    void setUp() {
        recommendationRepository.deleteAll();
        RecommendationEntity recommendationEntity1 = new RecommendationEntity(1,1, "author 1",1, "content 1");
        entity = recommendationRepository.save(recommendationEntity1);
        assertEqualsRecommendation(entity, recommendationEntity1);
    }

    @Test
    void create() {
        RecommendationEntity recommendationEntity  = new RecommendationEntity(1,1, "author 1",1, "content 1");
        RecommendationEntity recommendationEntity1 = recommendationRepository.save(recommendationEntity);
        assertEqualsRecommendation(recommendationEntity, recommendationEntity1);
        assertEquals(2, recommendationRepository.count());

    }


    @Test
    void update() {
        entity.setAuthor("a2");
        recommendationRepository.save(entity);

        RecommendationEntity recommendationEntity = recommendationRepository.getRecommendationEntityById(entity.getId()).get();
        assertEquals(1, recommendationEntity.getVersion());
        assertEquals("a2", recommendationEntity.getAuthor());
    }

    @Test
    void delete() {
        recommendationRepository.delete(entity);
        assertFalse(recommendationRepository.getRecommendationEntityById(entity.getId()).isPresent());

    }

    @Test
    void duplicateError() {
        assertThrows(DuplicateKeyException.class, () -> {
            RecommendationEntity recommendationEntity = new RecommendationEntity(1,1, "author 1",1, "content 1");
            recommendationRepository.save(recommendationEntity);
        });
    }

    @Test
    void optimisticLockInError() {
        RecommendationEntity recommendationEntity = recommendationRepository.getRecommendationEntityById(entity.getId()).get();
        RecommendationEntity recommendationEntity1 = recommendationRepository.getRecommendationEntityById(entity.getId()).get();

        recommendationEntity.setAuthor("author 1");
        recommendationRepository.save(recommendationEntity);

        assertThrows(OptimisticLockingFailureException.class, () -> {
            recommendationEntity1.setAuthor("author 2");
            recommendationRepository.save(recommendationEntity1);
        });

        RecommendationEntity recommendationEntity2 = recommendationRepository.getRecommendationEntityById(entity.getId()).get();
        assertEquals(1,recommendationEntity2.getVersion());
        assertEquals("author 1", recommendationEntity2.getAuthor());
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
