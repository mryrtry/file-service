package org.mryrt.file_service.Auth.Exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage;

import static org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage.*;

public enum JwtException {
    MALFORMED_JWT(INVALID_TOKEN, MalformedJwtException.class),
    EXPIRED_JWT(EXPIRED_TOKEN, ExpiredJwtException.class),
    INVALID_SIGNATURE(TOKEN_SIGNATURE_MISMATCH, SignatureException.class),
    INVALID_KEY(TOKEN_ALGORITHM_MISMATCH, InvalidKeyException.class),
    MISSING_CLAIM(MISSING_TOKEN_CLAIM, MissingClaimException.class),
    INCORRECT_CLAIM(INVALID_TOKEN_CLAIM, IncorrectClaimException.class);

    @Getter
    private final AuthErrorMessage errorMessage;

    private final Class<? extends Exception> exceptionClass;

    JwtException(AuthErrorMessage errorMessage, Class<? extends Exception> exceptionClass) {
        this.errorMessage = errorMessage;
        this.exceptionClass = exceptionClass;
    }

    public static JwtValidationException fromException(Exception ex) {
        for (JwtException error : values()) {
            if (error.exceptionClass.isInstance(ex)) {
                return new JwtValidationException(error.errorMessage);
            }
        }
        return new JwtValidationException(UNKNOWN_AUTH_ERROR);
    }
}
