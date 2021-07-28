package com.bhuvancom.ecom.exception.model;

import lombok.*;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ErrorResponse {
    private HttpStatus status;
    private int httpCode;
    private String message;
}
