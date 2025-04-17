package org.mryrt.file_service.Utility.Exceptions;

import lombok.Getter;
import org.mryrt.file_service.Utility.Message.ErrorMessage;
import org.mryrt.file_service.Utility.Message.Global.GlobalLogMessage;

@Getter
public class RateLimitedException extends RuntimeException {

    private final String exceptionField;

    private final String exceptionCause;

    public RateLimitedException(ErrorMessage errorMessage, Object... args) {
        this.exceptionField = errorMessage.getErrorField();
        this.exceptionCause = errorMessage.getFormattedMessage(args);
        GlobalLogMessage.GLOBAL_ERROR_OCCURRED.log(exceptionCause);
    }

}