package org.mryrt.file_service.Utility.Message.Auth;

import lombok.Getter;

@Getter
public enum AuthErrorMessage {

    ID_NOT_FOUND("id", "Id %d wasn't found"),

    USERNAME_REQUIRED("username", "Username is required"),
    USERNAME_LENGTH("username", "Username must be between 2 and 30 characters"),
    USERNAME_INVALID_CHARS("username", "Username can only contain letters, numbers, and underscores"),
    USERNAME_ALREADY_EXISTS("username", "Username %s is already in use"),
    USERNAME_NOT_FOUND("username", "User %s wasn't found"),

    PASSWORD_REQUIRED("password", "Password is required"),
    PASSWORD_TOO_SHORT("password", "Password must be longer than 5 characters"),
    WRONG_PASSWORD("password", "Wrong password"),

    MISSING_AUTH_HEADER("auth", "Missing authorization header"),
    INVALID_AUTH_HEADER_FORMAT("auth", "Authorization token doesn't start with 'Bearer '"),
    EMPTY_TOKEN("auth", "Token is empty"),
    TOKEN_EXTRACTION_ERROR("auth", "Can't extract username from token"),
    SECURITY_CONTEXT_ALREADY_SET("auth", "Security context already set"),
    INVALID_TOKEN("auth", "Error while processing token"),
    EXPIRED_TOKEN("auth", "Expired token"),
    TOKEN_SIGNATURE_MISMATCH("auth", "Token signature mismatch"),
    TOKEN_ALGORITHM_MISMATCH("auth", "Token algorithm mismatch"),
    MISSING_TOKEN_CLAIM("auth", "Token claim missing"),
    INVALID_TOKEN_CLAIM("auth", "Token claim mismatch"),
    FUTURE_ISSUED_AT_TOKEN("auth", "Token with future issued at"),
    USERNAME_MISMATCH("auth", "Extracted username doesn't matches"),
    UNKNOWN_AUTH_ERROR("auth", "Unknown error"),

    INVALID_JSON("params", "Invalid JSON format");

    private final String field;

    private final String messageTemplate;

    AuthErrorMessage(String field, String messageTemplate) {
        this.field = field;
        this.messageTemplate = messageTemplate;
    }

    public String getFormattedMessage(Object... args) {
        return String.format(messageTemplate, args);
    }

}