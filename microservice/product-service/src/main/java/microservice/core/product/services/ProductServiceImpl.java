package microservice.core.product.services;

import com.mongodb.DuplicateKeyException;
import microservice.api.core.product.Product;
import microservice.api.core.product.ProductService;
import microservice.api.exceptions.InvalidInputException;
import microservice.api.exceptions.NotFoundException;
import microservice.core.product.persistence.ProductEntity;
import microservice.core.product.persistence.ProductRepository;
import microservice.core.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;


    @Autowired
    public ProductServiceImpl(ServiceUtil serviceUtil, ProductMapper productMapper, ProductRepository productRepository) {
        this.serviceUtil = serviceUtil;
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    @Override
    public Product getProduct(int productId) {

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        ProductEntity entity = productRepository.findAllByProductId(productId)
                .orElseThrow(() -> new NotFoundException("No product found for productId: " + productId));
        Product product = productMapper.entityToApi(entity);
        product.setServiceAddress(serviceUtil.getServiceAddress());

        logger.debug("/product return the found product for productId={}", productId);
        return  product;
    }

    @Override
    public Product createProduct(Product product) {
        try {
            ProductEntity entity = productMapper.apiToEntity(product);
            ProductEntity entity1 = productRepository.save(entity);
            logger.info("createProduct: entity created for product Id ={}", entity1.getProductId());
            return productMapper.entityToApi(entity1);
        } catch (DuplicateKeyException e) {
            throw new InvalidInputException("Invalid product: " + product.getProductId());

        }
    }

    @Override
    public void deleteProduct(int productId) {
        if (logger.isDebugEnabled()) logger.debug("/product return the found product for productId={}", productId);
        productRepository.findByProductId(productId).ifPresent(productRepository::delete);
    }

}
