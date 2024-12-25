package microservice.api.core.recomendation;

import microservice.api.core.product.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface RecommendationService {

    @GetMapping(
            value = "/recommendation",
            produces = "application/json"
    )
    List<Recommendation> getRecommendations(@RequestParam(value = "productId", required = true) int productid);

    @PostMapping(
            value = "/recommendation",
            consumes = "application/json",
            produces = "application/json"
    )
    Recommendation createRecommendation(@RequestBody Recommendation recommendation);

    @DeleteMapping(
            value = "/recommendation"
    )
    void deleteRecommendation(@RequestParam(value = "productId", required = true) int productId);
}
