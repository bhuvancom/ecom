package com.bhuvancom.ecom.exception;

import com.bhuvancom.ecom.exception.model.ErrorResponse;
import lombok.Getter;

@Getter
public class EcomError extends RuntimeException {
    private ErrorResponse errorResponse;

    public EcomError(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }
}
