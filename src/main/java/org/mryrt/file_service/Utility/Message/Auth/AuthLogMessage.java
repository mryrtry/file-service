package org.mryrt.file_service.Utility.Message.Auth;

import io.micrometer.common.util.internal.logging.InternalLogLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Utility.Message.LogMessage;
import org.slf4j.Logger;

@Getter
@AllArgsConstructor
@Slf4j
public enum AuthLogMessage implements LogMessage {

    GENERATING_TOKEN(InternalLogLevel.INFO, "Generating authentication token for user '%s'."),
    //    TOKEN_GENERATED_SUCCESSFULLY(LogLevel.INFO, "Authentication token successfully generated for user '%s'."),
//    TOKEN_EXPIRED(LogLevel.DEBUG, "The token for user '%s' has expired and needs to be refreshed."),
//    TOKEN_VALIDATION_FAILED(LogLevel.DEBUG, "Token validation failed for user '%s': %s."),
//
//    FILE_IS_NOT_READABLE(LogLevel.DEBUG, "File '%s' is not readable and has been removed from the disk."),
//    FILE_IS_DIRECTORY(LogLevel.DEBUG, "The path '%s' points to a directory instead of a file and has been removed from the disk."),
//    FILE_NOT_FOUND_ON_DISK(LogLevel.DEBUG, "File '%s' was not found on the disk and has been removed from the database."),
//    NOT_USER_FILE(LogLevel.DEBUG, "File '%s' does not belong to the user and has been removed from the disk."),
//    FILE_SKIPPED(LogLevel.DEBUG, "File '%s' was skipped due to: %s."),
//    FILE_EXTENSIONS_MISMATCH(LogLevel.WARN, "Security warning for file '%s': The MIME type extension does not match the file name extension."),
//    FILE_DELETED_FROM_DISK(LogLevel.DEBUG, "File '%s' has been successfully deleted from the disk."),
//    FILE_DELETED_FROM_DATABASE(LogLevel.DEBUG, "File '%s' has been successfully deleted from the database."),
//
    USER_LOGGED_IN(InternalLogLevel.INFO, "User '%s' has successfully logged in."),
//    INVALID_LOGIN_ATTEMPT(LogLevel.DEBUG, "Invalid login attempt for user '%s': %s."),
//    USER_ACCOUNT_LOCKED(LogLevel.WARN, "User account '%s' has been locked due to multiple failed login attempts."),
//    SECURITY_CONTEXT_INITIALIZED(LogLevel.DEBUG, "Security context initialized for user '%s'."),

    AUTH_EXCEPTION_OCCURRED(InternalLogLevel.DEBUG, "Auth service exception with cause: '%s'."),
    JWT_EXCEPTION_OCCURRED(InternalLogLevel.DEBUG, "Jwt service exception occurred with cause '%s'.");

    private final InternalLogLevel logLevel;

    private final String messageTemplate;

    @Override
    public Logger getLogger() {
        return log;
    }

}
