package org.mryrt.file_service.Auth.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Utility.Annotation.TrackExecutionTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static org.mryrt.file_service.Utility.Message.Auth.AuthLogMessage.GENERATING_TOKEN;


@Slf4j
@Service
@TrackExecutionTime
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.issuer}")
    private String ISSUER;

    @Value("${jwt.expiration}")
    private Duration EXPIRATION;


    private String createToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(Map.of("iss", ISSUER))
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(EXPIRATION.toMillis())))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws MalformedJwtException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws MalformedJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .requireIssuer(ISSUER)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) throws MalformedJwtException {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isIssuedAtValid(String token) {
        Instant issuedAt = extractClaim(token, Claims::getIssuedAt).toInstant();
        return !issuedAt.isAfter(Instant.now());
    }

    public String generateToken(String username) {
        log.debug(GENERATING_TOKEN.getFormattedMessage(username));
        return createToken(username);
    }

}
