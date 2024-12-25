package microservice.api.exceptions;

import org.springframework.stereotype.Component;

public class BadRequestException extends RuntimeException {

    BadRequestException() {}

    BadRequestException(String message) {
        super(message);
    }

    BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    BadRequestException(Throwable cause) {
        super(cause);
    }

}
