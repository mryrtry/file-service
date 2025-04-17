package org.mryrt.file_service.FileService.Exceptions;

import lombok.Getter;
import org.mryrt.file_service.Utility.Message.ErrorMessage;

import static org.mryrt.file_service.Utility.Message.Files.FilesLogMessage.FILE_EXCEPTION_OCCURRED;
import static org.mryrt.file_service.Utility.Message.Files.FilesLogMessage.FILE_INTERNAL_EXCEPTION_OCCURRED;

@Getter
public class FileProcessException extends RuntimeException {

    private final String exceptionField;

    private final String exceptionCause;

    public FileProcessException(ErrorMessage errorMessage, Object... args) {
        this.exceptionField = errorMessage.getErrorField();
        this.exceptionCause = errorMessage.getFormattedMessage(args);
        FILE_EXCEPTION_OCCURRED.log(exceptionCause);
    }

    public FileProcessException(ErrorMessage errorMessage, Exception exception, Object... args) {
        this.exceptionField = errorMessage.getErrorField();
        this.exceptionCause = errorMessage.getFormattedMessage(args);
        FILE_INTERNAL_EXCEPTION_OCCURRED.log(exception.getMessage(), exceptionCause);
    }

}
