package org.mryrt.file_service.FileService.Exceptions;

import lombok.Getter;
import org.mryrt.file_service.Utility.Message.ErrorMessage;

import static org.mryrt.file_service.Utility.Message.Files.FilesLogMessage.FILE_EXCEPTION_OCCURRED;
import static org.mryrt.file_service.Utility.Message.Files.FilesLogMessage.FILE_INTERNAL_EXCEPTION_OCCURRED;

@Getter
public class FileProcessException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public FileProcessException(ErrorMessage errorMessage, Object... args) {
        super(errorMessage.getFormattedMessage(args));
        this.errorMessage = errorMessage;
        FILE_EXCEPTION_OCCURRED.log(errorMessage.getFormattedMessage(args));
    }

    public FileProcessException(ErrorMessage errorMessage, Exception exception, Object... args) {
        super(errorMessage.getFormattedMessage(args));
        this.errorMessage = errorMessage;
        FILE_INTERNAL_EXCEPTION_OCCURRED.log(exception.getMessage(), errorMessage.getFormattedMessage(args));
    }

}
