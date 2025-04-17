package org.mryrt.file_service.Utility.Message.Files;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mryrt.file_service.Utility.Message.ErrorMessage;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FilesErrorMessage implements ErrorMessage {

    INVALID_FILE_UUID(HttpStatus.BAD_REQUEST, "uuid", "Invalid UUID format. The UUID must follow the pattern: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX."),
    FILES_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "file", "The maximum number of files has been reached. Only one file is permitted per operation."),
    FILE_SIZE_TOO_LARGE(HttpStatus.BAD_REQUEST, "file", "The file size exceeds the allowed limit."),
    FILE_IS_EMPTY(HttpStatus.BAD_REQUEST, "file", "The uploaded file is empty. Please provide a valid non-empty file."),
    UUID_NOT_EXIST(HttpStatus.NOT_FOUND, "uuid", "The UUID '%s' does not exist in the directory of user with ID '%d'."),
    USER_FILE_NOT_EXIST(HttpStatus.NOT_FOUND, "file", "The file '%s' for user with ID '%d' was not found on the disk."),
    FILE_COPY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "file", "An error occurred while copying the file. Please try again later."),
    NOT_ENOUGH_SPACE(HttpStatus.INTERNAL_SERVER_ERROR, "file", "Insufficient space available in the directory of user with ID '%d'."),
    USER_FILE_NOT_READABLE(HttpStatus.INTERNAL_SERVER_ERROR, "file", "The file '%s' for user with ID '%d' is not readable. Ensure the file exists and has proper permissions."),
    USER_DIRECTORY_NOT_READABLE(HttpStatus.INTERNAL_SERVER_ERROR, "directory", "The directory for user with ID '%d' is not readable. Check permissions or contact support."),
    USER_DIRECTORY_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "directory", "Access to the directory for user with ID '%d' is denied. Verify permissions or try again later.");

    private final HttpStatus httpStatus;

    private final String errorField;

    private final String messageTemplate;

    public String getFormattedMessage(Object... args) {
        return String.format(messageTemplate, args);
    }

}