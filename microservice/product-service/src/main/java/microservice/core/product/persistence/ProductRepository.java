package microservice.core.product.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends PagingAndSortingRepository<ProductEntity, Long> {
    Optional<ProductEntity> findAllByProductId(int productId);

    void deleteAll();

    ProductEntity save(ProductEntity productRepository);

    Optional<ProductEntity> findById(String id);

    int count();

    void delete(ProductEntity saveEntity);

    boolean existsById(String id);

    Optional<ProductEntity> findByProductId(int productId);
}
