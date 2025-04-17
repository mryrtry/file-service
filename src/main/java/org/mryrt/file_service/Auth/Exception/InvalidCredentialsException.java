package org.mryrt.file_service.Auth.Exception;

import lombok.Getter;
import org.mryrt.file_service.Utility.Message.Auth.AuthLogMessage;
import org.mryrt.file_service.Utility.Message.ErrorMessage;

@Getter
public class InvalidCredentialsException extends RuntimeException {

    private final String exceptionField;

    private final String exceptionCause;

    public InvalidCredentialsException(ErrorMessage errorMessage, Object... args) {
        this.exceptionField = errorMessage.getErrorField();
        this.exceptionCause = errorMessage.getFormattedMessage(args);
        AuthLogMessage.AUTH_EXCEPTION_OCCURRED.log(exceptionCause);
    }

}
