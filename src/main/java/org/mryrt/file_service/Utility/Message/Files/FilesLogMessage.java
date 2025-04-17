package org.mryrt.file_service.Utility.Message.Files;

import io.micrometer.common.util.internal.logging.InternalLogLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Utility.Message.LogMessage;
import org.slf4j.Logger;

@AllArgsConstructor
@Getter
@Slf4j
public enum FilesLogMessage implements LogMessage {

    FILE_IS_DIRECTORY(InternalLogLevel.DEBUG, "The file '%s' is a directory and has been deleted from the user's with id '%s' directory."),
    FILE_NOT_FOUND_ON_DISK(InternalLogLevel.WARN, "The file '%s' was not found in users with id '%s' directory and has been removed from the database."),
    FILE_SKIPPED(InternalLogLevel.WARN, "The file '%s' was skipped due to the following reason: %s."),
    FILE_NOT_READABLE(InternalLogLevel.DEBUG, "The file '%s' is not readable and has been deleted from the user's with id '%s' directory."),

    NOT_USER_FILE(InternalLogLevel.DEBUG, "The file '%s' does not belong to the user with id '%s' and has been deleted from the disk."),

    FILE_INTERNAL_EXCEPTION_OCCURRED(InternalLogLevel.DEBUG, "File service exception occurred: '%s', cause: '%s'."),
    FILE_EXCEPTION_OCCURRED(InternalLogLevel.DEBUG, "File service exception with cause: '%s'."),

    BASE_DIRECTORY_INVALID_FILE_REMOVED(InternalLogLevel.DEBUG, "Invalid file '%s' was found in the base directory and has been removed from the disk."),
    NONEXISTENT_USER_DIRECTORY_REMOVED(InternalLogLevel.DEBUG, "User with id '%s' was not found in database and his directory has been removed from the disk.");

    private final InternalLogLevel logLevel;

    private final String messageTemplate;

    @Override
    public Logger getLogger() {
        return log;
    }

}