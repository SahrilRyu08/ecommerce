package microservice.api.core.recomendation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RecomendationService {

    @GetMapping(
            value = "/recommendation",
            produces = "application/json"
    )
    List<Recommendation> getRecomendations(@RequestParam(value = "productId", required = true) int productid);
}
