package microservice.core.product.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<ProductEntity, Long> {
    Optional<ProductEntity> findAllByProductId(int productId);

    void deleteAll();

    ProductEntity save(ProductEntity productRepository);

    Optional<ProductEntity> findById(String id);

    long count();

    void delete(ProductEntity saveEntity);

    boolean existsById(String id);

    Optional<ProductEntity> findByProductId(int productId);
}
