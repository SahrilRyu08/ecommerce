package microservice.core.util;


import microservice.api.exceptions.InvalidInputException;
import microservice.api.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;


@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public @ResponseBody HttpErrorInfo handleNotFoundException(ServerHttpRequest request, Exception exception) {
        return createHttpErrorInfo(NOT_FOUND, request, exception);
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException.class)
    public @ResponseBody HttpErrorInfo handleUnprocessableEntity(ServerHttpRequest request, Exception exception) {
        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, exception);
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(HttpClientErrorException.class)
    public @ResponseBody HttpErrorInfo handleHttpClientException(ServerHttpRequest request, HttpClientErrorException exception) {
        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, exception);
    }


    private HttpErrorInfo createHttpErrorInfo(HttpStatus notFound, ServerHttpRequest request, Exception exception) {
        final String path = request.getPath().pathWithinApplication().value();
        final String mesage = exception.getMessage();

        LOGGER.info("return Http Status ; {} for path : {} with message : {}", notFound, path, mesage);
        return new HttpErrorInfo(path, notFound, mesage);
    }
}
