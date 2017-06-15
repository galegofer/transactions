package com.backbase.transactions.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Class that catches all the possible exceptions due to validations and
 * re-convert them in a more user friendly way.
 * 
 * @author Damian
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
    private ResponseEntity<Object> handleValidationException(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Error while handling parameters, problem is: " + ex.getMessage();

        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = { RestClientException.class })
    protected ResponseEntity<Object> handleCommunicationException(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Error while communicating with server, problem is: " + ex.getMessage();

        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
