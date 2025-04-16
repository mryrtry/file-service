package org.mryrt.file_service.Utility.Exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Utility.Message.ErrorMessage;

@Getter
@Slf4j
public class RateLimitedException extends RuntimeException {

    private final String errorField;

    private final String errorCause;

    public RateLimitedException(ErrorMessage errorMessage, Object... args) {
        this.errorField = errorMessage.getErrorField();
        this.errorCause = errorMessage.getFormattedMessage(args);
        log.info("Rate limited exception exception: {}", errorCause);
    }

}