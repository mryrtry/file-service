package org.mryrt.file_service.FileService.Exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Utility.Message.ErrorMessage;

@Slf4j
@Getter
public class FileProcessException extends RuntimeException {

    private final String exceptionField;

    private final String exceptionCause;

    public FileProcessException(ErrorMessage errorMessage, Object... args) {
        this.exceptionField = errorMessage.getErrorField();
        this.exceptionCause = errorMessage.getFormattedMessage(args);
        log.warn("File exception: {}", exceptionCause);
    }

    public FileProcessException(ErrorMessage errorMessage, Exception exception, Object... args) {
        this.exceptionField = errorMessage.getErrorField();
        this.exceptionCause = errorMessage.getFormattedMessage(args);
        log.warn("File exception: {} Message: {}", exception.getMessage(), exceptionCause);
    }

}
