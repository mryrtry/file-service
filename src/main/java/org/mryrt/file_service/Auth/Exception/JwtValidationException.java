package org.mryrt.file_service.Auth.Exception;

import lombok.Getter;
import org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage;
import org.mryrt.file_service.Utility.Message.Auth.AuthLogMessage;
import org.mryrt.file_service.Utility.Message.ErrorMessage;

@Getter
public class JwtValidationException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public JwtValidationException(AuthErrorMessage authErrorMessage, Object... args) {
        super(authErrorMessage.getFormattedMessage(args));
        this.errorMessage = authErrorMessage;
        AuthLogMessage.JWT_EXCEPTION_OCCURRED.log(authErrorMessage.getFormattedMessage(args));
    }

}
