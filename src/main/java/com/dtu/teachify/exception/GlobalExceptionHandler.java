package com.dtu.teachify.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // User not authenticated / not found
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleAuthException(UsernameNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }

    // Custom Exception Class (statusCode not hardcoded)
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> handleApiException(ApiException ex){
        return ResponseEntity
                .status(ex.getStatus())
                .body(ex.getMessage());
    }

    // Database errors
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDBError(DataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Database error occurred");
    }

    // Fallback (any unknown error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Something went wrong ...");
    }
}