package com.bhuvancom.ecom.exception;

import com.bhuvancom.ecom.exception.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@RestControllerAdvice
public class MyApiExceptionHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(MyApiExceptionHandler.class.getName());

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handle(ConstraintViolationException e) {
        LOGGER.error("Constraint violation {} ", e.getMessage());
        e.getConstraintViolations().forEach(v -> {
            LOGGER.error("Constraint violation v {}, {} ", v.getMessageTemplate(), v.getMessage());
        });
        ErrorResponse errorResponse = new ErrorResponse();
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        constraintViolations.forEach(violation ->
                errorResponse.setMessage(violation.getMessage() + " code " + violation.getMessageTemplate()));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EcomError.class)
    public ResponseEntity<ErrorResponse> handle(EcomError e) {
        LOGGER.error("Custom error {} ", e.getErrorResponse());
        return new ResponseEntity<>(e.getErrorResponse(), e.getErrorResponse().getStatus());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handle(NoHandlerFoundException e) {
        LOGGER.error("No Handler error {} ", e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        e.getRequestURL() + "Not Found"), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        LOGGER.error("Generic error {}, stack ", e.getMessage(), e.fillInStackTrace());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                String.valueOf(e.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
