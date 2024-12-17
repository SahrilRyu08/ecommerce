package microservice.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

@Component
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidInputException extends RuntimeException{
    public InvalidInputException() {}

    public InvalidInputException(String string) {
        super(string);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidInputException(Throwable cause) {
        super(cause);
    }
}
