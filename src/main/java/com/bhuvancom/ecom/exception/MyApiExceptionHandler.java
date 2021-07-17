package com.bhuvancom.ecom.exception;

import com.bhuvancom.ecom.exception.model.ErrorResponse;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@RestControllerAdvice
public class MyApiExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handle(ConstraintViolationException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        constraintViolations.forEach(violation ->
                errorResponse.setMessage(violation.getMessage() + " code " + violation.getMessageTemplate()));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EcomError.class)
    public ResponseEntity<ErrorResponse> handle(EcomError e) {
        return new ResponseEntity<>(e.getErrorResponse(), e.getErrorResponse().getStatus());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> hanlde() {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "Not found"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
