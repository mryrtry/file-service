package org.mryrt.file_service.Utility.Message.Auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mryrt.file_service.Utility.Message.ErrorMessage;

@Getter
@AllArgsConstructor
public enum AuthErrorMessage implements ErrorMessage {

    ID_NOT_FOUND("id", "The requested ID %d was not found. Please verify the provided value."),

    USERNAME_REQUIRED("username", "Username is required. Please provide a valid username."),
    USERNAME_LENGTH("username", "Username must be between 2 and 30 characters."),
    USERNAME_INVALID_CHARS("username", "Username can only contain letters, numbers, and underscores."),
    USERNAME_ALREADY_EXISTS("username", "The username '%s' is already taken. Please choose a different one."),
    USERNAME_NOT_FOUND("username", "User with the username '%s' was not found. Please check your input."),

    PASSWORD_REQUIRED("password", "Password is required. Please provide a valid password."),
    PASSWORD_TOO_SHORT("password", "Password must be longer than 5 characters."),
    WRONG_PASSWORD("password", "Incorrect password. Please try again."),

    MISSING_AUTH_HEADER("auth", "Missing authorization header. Please include it in your request."),
    INVALID_AUTH_HEADER_FORMAT("auth", "Authorization header must start with 'Bearer '."),
    EMPTY_TOKEN("auth", "Token is empty. Please provide a valid token."),
    TOKEN_EXTRACTION_ERROR("auth", "Failed to extract username from the token."),
    SECURITY_CONTEXT_ALREADY_SET("auth", "Security context is already set. Cannot overwrite existing context."),
    INVALID_TOKEN("auth", "The provided token is invalid or malformed."),
    EXPIRED_TOKEN("auth", "The token has expired. Please generate a new one."),
    TOKEN_SIGNATURE_MISMATCH("auth", "Token signature does not match. Please verify the token."),
    TOKEN_ALGORITHM_MISMATCH("auth", "The token algorithm is incorrect. Please use the supported algorithm."),
    MISSING_TOKEN_CLAIM("auth", "A required claim is missing from the token."),
    INVALID_TOKEN_CLAIM("auth", "The token claim is invalid or mismatched."),
    FUTURE_ISSUED_AT_TOKEN("auth", "The token has a future 'issued at' timestamp. Please check the token validity."),
    USERNAME_MISMATCH("auth", "The extracted username does not match the expected value."),
    UNKNOWN_AUTH_ERROR("auth", "An unknown authentication error occurred."),
    USER_NOT_AUTHENTICATED("auth", "User authentication was not found. Please log in and try again.");

    private final String errorField;

    private final String messageTemplate;

    public String getFormattedMessage(Object... args) {
        return String.format(messageTemplate, args);
    }

}