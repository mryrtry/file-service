package org.mryrt.file_service.Auth.Exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage;

@Slf4j
@Getter
public class InvalidCredentialsException extends RuntimeException {

    private final String field;

    private final String exCause;

    public InvalidCredentialsException(AuthErrorMessage authErrorMessage, Object ... args) {
        this.field = authErrorMessage.getField();
        this.exCause = authErrorMessage.getFormattedMessage(args);
        log.warn("Invalid credentials for {}: {}.", field, exCause);
    }

}
