package com.clara.ops.challenge.document_management_service_challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handle404(NoResourceFoundException ex) {
        log.error("Route not found: {}", ex.getMessage());  
        return new ResponseEntity<>("Route not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<String> handle500(InternalServerError ex) {
        log.error("Internal server error: {}", ex.getMessage());
        return new ResponseEntity<>("Internal server error:", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handle400(IllegalArgumentException ex) {
        log.error("Bad request: {}", ex.getMessage());
        return new ResponseEntity<>("Bad request: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
