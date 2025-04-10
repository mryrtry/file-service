package org.mryrt.file_service.Auth.Validator;

import jakarta.validation.ConstraintValidatorContext;
import org.mryrt.file_service.Auth.Repository.UserRepository;

public class RequestValidator {

    public static boolean validateUsername(UserRepository userRepository, ConstraintValidatorContext context, String username, boolean invert) {
        if (username == null || username.isBlank()) {
            _buildConstraintViolation(context, "username", "Username is required");
            return false;
        } else if (username.length() < 2 || username.length() > 30) {
            _buildConstraintViolation(context, "username", "Username must be between 2 and 30 characters");
            return false;
        } else if (!username.matches("^[a-zA-Z0-9_\\s]+$") || !username.strip().equals(username)) {
            _buildConstraintViolation(context, "username", "Username can only contain letters, numbers, and underscores");
            return false;
        }

        if (userRepository.existsByUsername(username) != invert) {
            _buildConstraintViolation(context, "username", invert
                    ? "User %s wasn't found".formatted(username)
                    : "Username %s is already in use".formatted(username));
            return false;
        }

        return true;
    }

    public static boolean validatePassword(ConstraintValidatorContext context, String password) {
        if (password == null || password.isBlank()) {
            _buildConstraintViolation(context, "password", "Password is required");
            return false;
        } else if (password.length() < 5) {
            _buildConstraintViolation(context, "password", "Password must be longer than 5 characters");
            return false;
        }

        return true;
    }

    private static void _buildConstraintViolation(ConstraintValidatorContext context, String field, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }

}
