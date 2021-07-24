package com.bhuvancom.ecom.controller;

import com.bhuvancom.ecom.exception.EcomError;
import com.bhuvancom.ecom.exception.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created Using IntelliJ Idea
 *
 * @author Bhuvaneshvar
 * Date    7/25/2021
 * Time    4:53 AM
 * Project ecomNew
 */
@RestController
@RequestMapping("/error")
public class ErrorPageCondtoller implements ErrorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorPageCondtoller.class.getName());

    @GetMapping("/999")
    public String error() {
        LOGGER.info("Starting error 999");
        throw new EcomError(new ErrorResponse(HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.value(),
                "Something went wrong"));
    }

    @RequestMapping("/404")
    public void toPage404() {
        throw new EcomError(new ErrorResponse(HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.value(),
                "This end point is not found"));
    }

    @RequestMapping("/500")
    public void toPage500() {
        throw new EcomError(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Server Error"));
    }
}
