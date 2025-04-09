package org.mryrt.file_service.Auth.Exception;

public class JwtValidationException extends RuntimeException {

    public JwtValidationException(String message) {
        super(message);
    }

}
