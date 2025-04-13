package org.mryrt.file_service.Auth.Exception;

import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage;

@Slf4j
public class JwtValidationException extends RuntimeException {

    public JwtValidationException(AuthErrorMessage authErrorMessage, Exception exception, Object ... args) {
        super(authErrorMessage.getFormattedMessage(args));
        log.warn("JWT exception: {} Message: {}", exception.getMessage(), authErrorMessage.getFormattedMessage(args));
    }

    public JwtValidationException(AuthErrorMessage authErrorMessage, Object ... args) {
        super(authErrorMessage.getFormattedMessage(args));
        log.warn("JWT exception: {}", authErrorMessage.getFormattedMessage(args));
    }

}
