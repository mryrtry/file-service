package org.mryrt.file_service.Auth.Exception;

import lombok.Getter;
import org.mryrt.file_service.Utility.Message.Auth.AuthLogMessage;
import org.mryrt.file_service.Utility.Message.ErrorMessage;

@Getter
public class InvalidCredentialsException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public InvalidCredentialsException(ErrorMessage errorMessage, Object... args) {
        super(errorMessage.getFormattedMessage(args));
        this.errorMessage = errorMessage;
        AuthLogMessage.AUTH_EXCEPTION_OCCURRED.log(errorMessage.getFormattedMessage(args));
    }

}
