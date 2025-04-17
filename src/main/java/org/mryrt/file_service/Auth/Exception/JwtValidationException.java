package org.mryrt.file_service.Auth.Exception;

import org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage;
import org.mryrt.file_service.Utility.Message.Auth.AuthLogMessage;

public class JwtValidationException extends RuntimeException {

    public JwtValidationException(AuthErrorMessage authErrorMessage, Object ... args) {
        super(authErrorMessage.getFormattedMessage(args));
        AuthLogMessage.JWT_EXCEPTION_OCCURRED.log(authErrorMessage.getFormattedMessage(args));
    }

}
