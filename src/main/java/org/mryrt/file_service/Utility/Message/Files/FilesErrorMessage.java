package org.mryrt.file_service.Utility.Message.Files;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mryrt.file_service.Utility.Message.ErrorMessage;

@Getter
@AllArgsConstructor
public enum FilesErrorMessage implements ErrorMessage {

    INVALID_FILE_UUID("uuid", "Invalid UUID format. The UUID must follow the pattern: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX."),
    UUID_NOT_EXIST("uuid", "The UUID '%s' does not exist in the directory of user with ID '%d'."),
    INVALID_FILE("file", "The provided file is not readable or corrupted."),
    FILE_SIZE_TOO_LARGE("file", "The file size exceeds the allowed limit."),
    FILE_INVALID_MIME_TYPE("file", "The MIME type of the file could not be detected or is unsupported."),
    FILE_IS_EMPTY("file", "The uploaded file is empty. Please provide a valid non-empty file."),
    FILES_LIMIT_EXCEEDED("file", "The maximum number of files has been reached. Only one file is permitted per operation."),
    FILE_COPY_ERROR("file", "An error occurred while copying the file. Please try again later."),
    NOT_ENOUGH_SPACE("file", "Insufficient space available in the directory of user with ID '%d'."),
    NOT_ENOUGH_SPACE_ON_DISK("file", "There is not enough free space on the disk to store the file."),
    USER_FILE_NOT_READABLE("file", "The file '%s' for user with ID '%d' is not readable. Ensure the file exists and has proper permissions."),
    USER_FILE_NOT_EXIST("file", "The file '%s' for user with ID '%d' was not found on the disk."),
    USER_DIRECTORY_NOT_READABLE("directory", "The directory for user with ID '%d' is not readable. Check permissions or contact support."),
    USER_DIRECTORY_ACCESS_ERROR("directory", "Access to the directory for user with ID '%d' is denied. Verify permissions or try again later.");

    private final String errorField;

    private final String messageTemplate;

    public String getFormattedMessage(Object... args) {
        return String.format(messageTemplate, args);
    }

}