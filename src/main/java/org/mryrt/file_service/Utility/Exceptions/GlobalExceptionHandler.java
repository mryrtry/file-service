package org.mryrt.file_service.Utility.Exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mryrt.file_service.Utility.Message.Global.GlobalErrorMessage.INVALID_JSON;
import static org.mryrt.file_service.Utility.Message.Global.GlobalErrorMessage.INVALID_REQUEST_TYPE;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ignored) {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put(INVALID_JSON.getErrorField(), INVALID_JSON.getFormattedMessage());
        errors.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(INVALID_JSON.getHttpStatus()).body(errors);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, String>> handleMultipartException(MultipartException ignored) {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put(INVALID_REQUEST_TYPE.getErrorField(), INVALID_REQUEST_TYPE.getFormattedMessage());
        errors.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(INVALID_REQUEST_TYPE.getHttpStatus()).body(errors);
    }

    @ExceptionHandler(RateLimitedException.class)
    public ResponseEntity<Map<String, String>> handleRateLimitedException(RateLimitedException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put(ex.getErrorMessage().getErrorField(), ex.getMessage());
        errors.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(ex.getErrorMessage().getHttpStatus()).body(errors);
    }

}
