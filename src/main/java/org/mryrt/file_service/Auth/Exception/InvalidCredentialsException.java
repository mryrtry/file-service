package org.mryrt.file_service.Auth.Exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class InvalidCredentialsException extends RuntimeException {

    private final String field;

    private final String exCause;

    public InvalidCredentialsException(String field, String exCause) {
        log.info("Invalid credentials field: {} cause: {}", field, exCause);
        this.field = field;
        this.exCause = exCause;
        log.warn("Invalid credentials for {}: {}", field, exCause);
    }
}
