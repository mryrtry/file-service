package org.mryrt.file_service.FileService.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class FilesExceptionHandler {

    @ExceptionHandler(FileProcessException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(FileProcessException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getExceptionField(), ex.getExceptionCause());
        errors.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

}
