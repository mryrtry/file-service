package org.mryrt.file_service.FileService.Exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Utility.Message.Files.FilesErrorMessage;

@Slf4j
@Getter
public class FileProcessException extends RuntimeException {

    private final String exceptionField;

    private final String exceptionCause;

    public FileProcessException(FilesErrorMessage filesErrorMessage, Object... args) {
        this.exceptionField = filesErrorMessage.getErrorField();
        this.exceptionCause = filesErrorMessage.getFormattedMessage(args);
        log.warn("File exception: {}", exceptionCause);
    }

    public FileProcessException(FilesErrorMessage filesErrorMessage, Exception exception, Object... args) {
        this.exceptionField = filesErrorMessage.getErrorField();
        this.exceptionCause = filesErrorMessage.getFormattedMessage(args);
        log.warn("File exception: {} Message: {}", exception.getMessage(), exceptionCause);
    }

}
