package org.mryrt.file_service.FileService.Exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Utility.Message.Files.FilesErrorMessage;

@Slf4j
@Getter
public class FileProcessException extends RuntimeException {

    private final String field;

    private final String exCause;

    public FileProcessException(FilesErrorMessage filesErrorMessage, Object... args) {
        this.field = filesErrorMessage.getField();
        this.exCause = filesErrorMessage.getFormattedMessage(args);
        log.warn("File exception: {}", exCause);
    }

    public FileProcessException(FilesErrorMessage filesErrorMessage, Exception exception, Object... args) {
        this.field = filesErrorMessage.getField();
        this.exCause = filesErrorMessage.getFormattedMessage(args);
        log.warn("File exception: {} Message: {}", exception.getMessage(), exCause);
    }

}
