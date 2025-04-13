package org.mryrt.file_service.Utility.Message.Files;

import lombok.Getter;

@Getter
public enum FilesLogMessage {

    FILE_CONTAINS_MACROS("File '%s' secure warning: File contains macros"),
    FILE_IS_NOT_READABLE("File %s is not readable, so was deleted from disk"),
    FILE_IS_DIRECTORY("File %s is directory, so was deleted from disk"),
    FILE_NOT_FOUND_ON_DISK("File %s wasn't found on disk, so was deleted from database"),
    NOT_USER_FILE("File %s is not user's file, so was deleted from disk"),
    FILE_SKIPPED("Skipped file %s: %s"),
    FILE_EXTENSIONS_MISMATCH("File '%s' secure warning: Mime type extension & Extension from filename mismatch");

    private final String messageTemplate;

    FilesLogMessage(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getFormattedMessage(Object... args) {
        return String.format(messageTemplate, args);
    }

}
