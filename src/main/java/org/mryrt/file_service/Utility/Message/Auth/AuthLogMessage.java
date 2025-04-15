package org.mryrt.file_service.Utility.Message.Auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mryrt.file_service.Utility.Message.LogMessage;

@Getter
@AllArgsConstructor
public enum AuthLogMessage implements LogMessage {

    GENERATING_TOKEN("Generating authentication token for user '%s'."),
    TOKEN_GENERATED_SUCCESSFULLY("Authentication token successfully generated for user '%s'."),
    TOKEN_EXPIRED("The token for user '%s' has expired and needs to be refreshed."),
    TOKEN_VALIDATION_FAILED("Token validation failed for user '%s': %s."),

    FILE_IS_NOT_READABLE("File '%s' is not readable and has been removed from the disk."),
    FILE_IS_DIRECTORY("The path '%s' points to a directory instead of a file and has been removed from the disk."),
    FILE_NOT_FOUND_ON_DISK("File '%s' was not found on the disk and has been removed from the database."),
    NOT_USER_FILE("File '%s' does not belong to the user and has been removed from the disk."),
    FILE_SKIPPED("File '%s' was skipped due to: %s."),
    FILE_EXTENSIONS_MISMATCH("Security warning for file '%s': The MIME type extension does not match the file name extension."),
    FILE_DELETED_FROM_DISK("File '%s' has been successfully deleted from the disk."),
    FILE_DELETED_FROM_DATABASE("File '%s' has been successfully deleted from the database."),

    USER_LOGGED_IN("User '%s' has successfully logged in."),
    USER_LOGGED_OUT("User '%s' has successfully logged out."),
    INVALID_LOGIN_ATTEMPT("Invalid login attempt for user '%s': %s."),
    USER_ACCOUNT_LOCKED("User account '%s' has been locked due to multiple failed login attempts."),
    SECURITY_CONTEXT_INITIALIZED("Security context initialized for user '%s'.");

    private final String messageTemplate;

    @Override
    public String getFormattedMessage(Object... args) {
        return String.format(messageTemplate, args);
    }

}
