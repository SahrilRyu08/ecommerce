package microservice.api.exceptions;

import org.springframework.stereotype.Component;

public class InternalServerExcpetion extends Throwable {
    InternalServerExcpetion() {}

    InternalServerExcpetion(String message) {
        super(message);
    }
    InternalServerExcpetion(String message, Throwable cause) {
        super(message, cause);
    }
    InternalServerExcpetion(Throwable cause) {
        super(cause);
    }
}
