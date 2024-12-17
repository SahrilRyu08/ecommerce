package microservice.composite.product.serivices;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductCompositeService {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
