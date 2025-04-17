package org.mryrt.file_service.Utility.Exceptions;

import lombok.Getter;
import org.mryrt.file_service.Utility.Message.ErrorMessage;
import org.mryrt.file_service.Utility.Message.Global.GlobalLogMessage;

@Getter
public class RateLimitedException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public RateLimitedException(ErrorMessage errorMessage, Object... args) {
        super(errorMessage.getFormattedMessage(args));
        this.errorMessage = errorMessage;
        GlobalLogMessage.GLOBAL_ERROR_OCCURRED.log(errorMessage.getFormattedMessage(args));
    }

}