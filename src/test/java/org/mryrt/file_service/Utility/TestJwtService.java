package org.mryrt.file_service.Utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating JWT (JSON Web Token) tokens with various configurations
 * for testing purposes. Supports creating valid tokens as well as tokens with specific
 * invalid attributes to test authentication failure scenarios.
 */
@Component
public class TestJwtService {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.issuer}")
    private String ISSUER;

    @Value("${jwt.expiration}")
    private int EXPIRATION;

    /**
     * Creates HMAC signing key from Base64-encoded secret.
     *
     * @param secret Base64-encoded secret key string
     * @return HMAC-SHA signing key
     * @throws IllegalArgumentException for invalid Base64 or insufficient key length
     */
    private Key getSignKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Creates a basic JWT token with default claims.
     *
     * @param username   Subject of the token (typically username)
     * @param issuedAt   Token issuance timestamp
     * @param expiration Token expiration timestamp
     * @param algorithm  Signing algorithm to use
     * @param secret     Secret key for signing
     * @return Generated JWT token as compact string
     */
    private String createToken(String username, Date issuedAt, Date expiration,
                               SignatureAlgorithm algorithm, String secret) {
        return Jwts.builder()
                .setClaims(Map.of("iss", ISSUER))
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSignKey(secret), algorithm)
                .compact();
    }

    /**
     * Creates a JWT token with custom claims.
     *
     * @param username   Subject of the token
     * @param issuedAt   Token issuance timestamp
     * @param expiration Token expiration timestamp
     * @param algorithm  Signing algorithm
     * @param secret     Secret key for signing
     * @param claims     Additional custom claims to include
     * @return Generated JWT token as compact string
     */
    private String createToken(String username, Date issuedAt, Date expiration,
                               SignatureAlgorithm algorithm, String secret, Map claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSignKey(secret), algorithm)
                .compact();
    }

    /**
     * Creates a standard valid JWT token with default expiration.
     *
     * @param username Subject to include in token
     * @return Valid JWT token
     */
    public String createToken(String username) {
        return createToken(username, new Date(),
                new Date(System.currentTimeMillis() + EXPIRATION),
                SignatureAlgorithm.HS256, SECRET);
    }

    /**
     * Creates an expired JWT token (expiration set to current time).
     *
     * @param username Subject to include in token
     * @return Expired JWT token
     */
    public String createExpiredToken(String username) {
        return createToken(username, new Date(), new Date(),
                SignatureAlgorithm.HS256, SECRET);
    }

    /**
     * Creates a JWT token signed with wrong secret key.
     *
     * @param username Subject to include in token
     * @return JWT token with invalid signature
     */
    public String createWrongSecretToken(String username) {
        return createToken(username, new Date(),
                new Date(System.currentTimeMillis() + EXPIRATION),
                SignatureAlgorithm.HS256,
                "3e974a5d089c78300833eec921abddf5e73a893b5797cfe8bad817d017a88da6");
    }

    /**
     * Creates a JWT token with future issued-at time.
     *
     * @param username Subject to include in token
     * @return JWT token that appears to be issued in the future
     */
    public String createTokenWithFutureIssuedAt(String username) {
        return createToken(username,
                new Date(System.currentTimeMillis() + 3600 * 1000L),
                new Date(System.currentTimeMillis() + EXPIRATION),
                SignatureAlgorithm.HS256, SECRET);
    }

    /**
     * Creates a JWT token with custom issuer claim.
     *
     * @param username Subject to include in token
     * @return JWT token with non-standard issuer
     */
    public String createTokenWithIssuer(String username) {
        return createToken(username, new Date(),
                new Date(System.currentTimeMillis() + EXPIRATION),
                SignatureAlgorithm.HS256, SECRET,
                Map.of("iss", "#issuer"));
    }

    /**
     * Creates a JWT token without subject claim.
     *
     * @param ignored Not actually used (method signature maintained for consistency)
     * @return JWT token missing required subject claim
     */
    public String createWithoutSubjectToken(String ignored) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSignKey(SECRET), SignatureAlgorithm.HS256)
                .compact();
    }
}
