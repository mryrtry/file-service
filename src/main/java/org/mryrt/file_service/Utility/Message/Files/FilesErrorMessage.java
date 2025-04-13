package org.mryrt.file_service.Utility.Message.Files;

import lombok.Getter;

@Getter
public enum FilesErrorMessage {

    INVALID_FILE("file", "File is not readable"),
    FILE_SIZE_TOO_LARGE("file", "File size to large"),
    FILE_INVALID_MIME_TYPE("file", "File mime type is undetectable"),
    FILE_IS_EMPTY("file", "File is empty"),
    FILE_COPY_ERROR("file", "Couldn't copy error"),
    NOT_ENOUGH_SPACE("file", "Not enough space in user '%s' folder"),
    NOT_ENOUGH_SPACE_ON_DISK("file", "Not enough space on disk"),
    USER_DIRECTORY_NOT_READABLE("directory", "User '%s' directory is not readable"),
    USER_DIRECTORY_ACCESS_ERROR("directory", "User '%s' directory is not accessible");

    private final String field;

    private final String messageTemplate;

    FilesErrorMessage(String field, String messageTemplate) {
        this.field = field;
        this.messageTemplate = messageTemplate;
    }

    public String getFormattedMessage(Object... args) {
        return String.format(messageTemplate, args);
    }

}
