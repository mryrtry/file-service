package org.mryrt.file_service.Utility.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mryrt.file_service.Utility.Message.Global.GlobalErrorMessage.INVALID_JSON;
import static org.mryrt.file_service.Utility.Message.Global.GlobalErrorMessage.INVALID_REQUEST_TYPE;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        errors.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ignored) {
        HashMap<String, String> errors = new HashMap<>();
        errors.put(INVALID_JSON.getErrorField(), INVALID_JSON.getFormattedMessage());
        errors.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // todo: ну хз, будто надо не так это чекать
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<HashMap<String, String>> handleMultipartException(MultipartException ignored) {
        HashMap<String, String> errors = new HashMap<>();
        errors.put(INVALID_REQUEST_TYPE.getErrorField(), INVALID_REQUEST_TYPE.getFormattedMessage());
        errors.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(RateLimitedException.class)
    public ResponseEntity<HashMap<String, String>> handleRateLimitedException(RateLimitedException ex) {
        HashMap<String, String> errors = new HashMap<>();
        errors.put(ex.getExceptionField(), ex.getExceptionCause());
        errors.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errors);
    }

}
