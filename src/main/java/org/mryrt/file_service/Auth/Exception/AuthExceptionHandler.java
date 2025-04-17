package org.mryrt.file_service.Auth.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidCredentialsException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put(ex.getErrorMessage().getErrorField(), ex.getMessage());
        errors.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(ex.getErrorMessage().getHttpStatus()).body(errors);
    }

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(RequestValidationException ex) {
        Map<String, String> errors = ex.getValidationErrors();
        errors.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

}