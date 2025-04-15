package org.mryrt.file_service.Utility.Message.Files;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mryrt.file_service.Utility.Message.LogMessage;

@Getter
@AllArgsConstructor
public enum FilesLogMessage implements LogMessage {

    FILE_CONTAINS_MACROS("Security warning: The file '%s' contains macros, which may pose a security risk."),
    FILE_IS_NOT_READABLE("The file '%s' is not readable and has been deleted from the disk."),
    FILE_IS_DIRECTORY("The file '%s' is a directory and has been deleted from the disk."),
    FILE_NOT_FOUND_ON_DISK("The file '%s' was not found on the disk and has been removed from the database."),
    NOT_USER_FILE("The file '%s' does not belong to the user and has been deleted from the disk."),
    FILE_SKIPPED("The file '%s' was skipped due to the following reason: %s."),
    FILE_REMOVED("The file '%s' has been successfully removed from the base directory."),
    FILE_EXTENSIONS_MISMATCH("Security warning: The file '%s' has a mismatch between its MIME type extension and the extension in its filename.");

    private final String messageTemplate;

    @Override
    public String getFormattedMessage(Object... args) {
        return String.format(messageTemplate, args);
    }

}