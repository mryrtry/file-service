package org.mryrt.file_service.Auth.Exception;

import lombok.Getter;
import org.mryrt.file_service.Utility.Message.Auth.AuthLogMessage;

import java.util.Map;

@Getter
public class RequestValidationException extends RuntimeException {

    private final Map<String, String> validationErrors;

    public RequestValidationException(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
        AuthLogMessage.VALIDATION_EXCEPTION_OCCURRED.log(validationErrors.toString());
    }

}
