package org.mryrt.file_service.Auth.Validator;

import org.mryrt.file_service.Auth.Exception.InvalidCredentialsException;
import org.mryrt.file_service.Auth.Repository.UserRepository;

import static org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage.*;

public class RequestValidator {

    public static boolean validateUsername(UserRepository userRepository, String username, boolean invert) {
        if (username == null || username.isBlank()) throw new InvalidCredentialsException(USERNAME_REQUIRED);
        if (username.length() < 2 || username.length() > 30) throw new InvalidCredentialsException(USERNAME_LENGTH);
        if (!username.matches("^[a-zA-Z0-9_\\s]+$") || !username.strip().equals(username))
            throw new InvalidCredentialsException(USERNAME_INVALID_CHARS);
        if (userRepository.existsByUsername(username) != invert)
            throw new InvalidCredentialsException(invert ? USERNAME_NOT_FOUND : USERNAME_ALREADY_EXISTS, username);

        return true;
    }

    public static boolean validatePassword(String password) {
        if (password == null || password.isBlank()) throw new InvalidCredentialsException(PASSWORD_REQUIRED);
        if (password.length() < 5) throw new InvalidCredentialsException(PASSWORD_TOO_SHORT);

        return true;
    }

}
