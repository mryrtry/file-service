package org.mryrt.file_service.Auth.Validator;

import lombok.AllArgsConstructor;
import org.mryrt.file_service.Auth.Exception.RequestValidationException;
import org.mryrt.file_service.Auth.Repository.UserRepository;
import org.mryrt.file_service.Utility.Message.ErrorMessage;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage.*;

@Component
@AllArgsConstructor
public class RequestValidator {

    private final UserRepository userRepository;

    public void validate(String username, String password, boolean shouldExist) {
        Map<String, String> errors = new LinkedHashMap<>();
        validateUsername(errors, username, shouldExist);
        validatePassword(errors, password);
        if (!errors.isEmpty())
            throw new RequestValidationException(errors);
    }

    public void validateUsername(Map<String, String> errors, String username, boolean shouldExist) {

        if (username == null || username.isBlank()) {
            errors.put(USERNAME_REQUIRED.getErrorField(), USERNAME_REQUIRED.getFormattedMessage());
            return;
        }

        if (username.length() < 2 || username.length() > 30) {
            errors.put(USERNAME_LENGTH.getErrorField(), USERNAME_LENGTH.getFormattedMessage());
            return;
        }

        if (!username.matches("^[a-zA-Z0-9_\\s]+$") || !username.strip().equals(username)) {
            errors.put(USERNAME_INVALID_CHARS.getErrorField(), USERNAME_INVALID_CHARS.getFormattedMessage());
            return;
        }

        if (userRepository.existsByUsername(username) != shouldExist) {
            ErrorMessage errorMessage = shouldExist ? USERNAME_NOT_FOUND : USERNAME_ALREADY_EXISTS;
            errors.put(errorMessage.getErrorField(), errorMessage.getFormattedMessage(username));
        }

    }

    public void validatePassword(Map<String, String> errors, String password) {

        if (password == null || password.isBlank()) {
            errors.put(PASSWORD_REQUIRED.getErrorField(), PASSWORD_REQUIRED.getFormattedMessage());
            return;
        }

        if (password.length() < 5)
            errors.put(PASSWORD_TOO_SHORT.getErrorField(), PASSWORD_TOO_SHORT.getFormattedMessage());

    }

}
