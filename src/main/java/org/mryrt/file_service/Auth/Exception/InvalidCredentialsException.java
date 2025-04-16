package org.mryrt.file_service.Auth.Exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Utility.Message.ErrorMessage;

@Slf4j
@Getter
public class InvalidCredentialsException extends RuntimeException {

    private final String field;

    private final String exCause;

    public InvalidCredentialsException(ErrorMessage errorMessage, Object... args) {
        this.field = errorMessage.getErrorField();
        this.exCause = errorMessage.getFormattedMessage(args);
        log.warn("Invalid credentials for {}: {}.", field, exCause);
    }

}
