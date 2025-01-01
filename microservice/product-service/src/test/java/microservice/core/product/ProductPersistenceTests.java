package microservice.core.product;

import microservice.core.product.persistence.ProductEntity;
import microservice.core.product.persistence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class ProductPersistenceTests extends MongoDbTest{


    @Autowired
    private ProductRepository productRepository;
    private ProductEntity saveEntity;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        ProductEntity entity = new ProductEntity(1,"n",1);
        saveEntity = productRepository.save(entity);
        assertEqualsProduct(saveEntity, entity);
    }

    @Test
    void create() {
        ProductEntity entity = new ProductEntity(2, "n", 2);
        productRepository.save(entity);

        ProductEntity entity1 = productRepository.findById(entity.getId()).get();

        assertEqualsProduct(entity, entity1);
        assertEquals(2, productRepository.count());
    }


    @Test
    void update() {
        saveEntity.setName("n2");
        productRepository.save(saveEntity);

        ProductEntity entity = productRepository.findById(saveEntity.getId()).get();
        assertEquals(1, entity.getVersion());
        assertEquals("n2", entity.getName());
    }

    @Test
    void delete() {
        productRepository.delete(saveEntity);
        assertFalse(productRepository.existsById(saveEntity.getId()));
    }

    @Test
    void getByProductId() {
        Optional<ProductEntity> entity = productRepository.findByProductId(saveEntity.getProductId());

        assertTrue(entity.isPresent());
        assertEqualsProduct(entity.get(), saveEntity);
    }

    @Test
    void duplicateError() {
        assertThrows(DuplicateKeyException.class, () -> {
            ProductEntity entity = new ProductEntity(saveEntity.getProductId(), "n", 1);
            productRepository.save(entity);
        });
    }

    @Test
    void optimisticLockInError() {
        ProductEntity entity = productRepository.findById(saveEntity.getId()).get();
        ProductEntity entity2 = productRepository.findById(saveEntity.getId()).get();

        entity.setName("n2");
        productRepository.save(entity);


        assertThrows(OptimisticLockingFailureException.class, () -> {
            entity2.setName("n2");
            productRepository.save(entity2);
        });

        ProductEntity entity3 = productRepository.findById(saveEntity.getId()).get();

        assertEquals(1, entity3.getVersion());
        assertEquals("n2", entity3.getName());
    }


    @Test
    void paging() {
        productRepository.deleteAll();
        List<ProductEntity> entities = IntStream.rangeClosed(1001, 1010)
                .mapToObj(id -> new ProductEntity(id, "name " + id, id))
                .toList();
        entities.forEach(entity -> productRepository.save(entity));

        Pageable pageable = PageRequest.of(0, 4, Sort.Direction.ASC, "productId");
        pageable = nextPage(pageable, "[1001, 1002, 1003, 1004]", true);
        pageable = nextPage(pageable, "[1005, 1006, 1007, 1008]", true);
        pageable = nextPage(pageable, "[1009, 1010]", false);
    }

    private Pageable nextPage(Pageable pageable, String s, boolean b) {
        Page<ProductEntity> productEntityPage = productRepository.findAll(pageable);
        assertEquals(s, productEntityPage.getContent()
                .stream()
                .map(ProductEntity::getProductId)
                .toList().toString());

        assertEquals(b, productEntityPage.hasNext());

        return productEntityPage.nextPageable();
    }

    private void assertEqualsProduct(ProductEntity productEntity, ProductEntity entity) {
        assertEquals(productEntity.getId(), entity.getId());
        assertEquals(productEntity.getName(), entity.getName());
        assertEquals(productEntity.getProductId(), entity.getProductId());
        assertEquals(productEntity.getVersion(), entity.getVersion());
        assertEquals(productEntity.getWeight(), entity.getWeight());
    }
}
