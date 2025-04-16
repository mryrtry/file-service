package org.mryrt.file_service.Auth.Validator;

import jakarta.validation.ConstraintValidatorContext;
import org.mryrt.file_service.Auth.Repository.UserRepository;
import org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage;

import static org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage.*;

public class RequestValidator {

    public static boolean validateUsername(UserRepository userRepository, ConstraintValidatorContext context, String username, boolean invert) {
        if (username == null || username.isBlank()) {
            _buildConstraintViolation(context, USERNAME_REQUIRED);
            return false;
        } else if (username.length() < 2 || username.length() > 30) {
            _buildConstraintViolation(context, USERNAME_LENGTH);
            return false;
        } else if (!username.matches("^[a-zA-Z0-9_\\s]+$") || !username.strip().equals(username)) {
            _buildConstraintViolation(context, USERNAME_INVALID_CHARS);
            return false;
        }

        if (userRepository.existsByUsername(username) != invert) {
            _buildConstraintViolation(context, invert
                    ? USERNAME_NOT_FOUND
                    : USERNAME_ALREADY_EXISTS, username);
            return false;
        }

        return true;
    }

    public static boolean validatePassword(ConstraintValidatorContext context, String password) {
        if (password == null || password.isBlank()) {
            _buildConstraintViolation(context, PASSWORD_REQUIRED);
            return false;
        } else if (password.length() < 5) {
            _buildConstraintViolation(context, PASSWORD_TOO_SHORT);
            return false;
        }

        return true;
    }

    private static void _buildConstraintViolation(ConstraintValidatorContext context, AuthErrorMessage authErrorMessage, Object ... args) {
        context.buildConstraintViolationWithTemplate(authErrorMessage.getFormattedMessage(args))
                .addPropertyNode(authErrorMessage.getErrorField())
                .addConstraintViolation();
    }

}
