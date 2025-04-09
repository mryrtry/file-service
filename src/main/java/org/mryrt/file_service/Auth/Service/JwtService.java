package org.mryrt.file_service.Auth.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration}")
    private int EXPIRATION;

    private String _createToken(String username) {
        log.debug("Creating token with for user: {}", username);
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(_getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private  <T> T _extractClaim(String token, Function<Claims, T> claimsResolver) throws MalformedJwtException {
        log.debug("Extracting claims from token");
        final Claims claims = _extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims _extractAllClaims(String token) throws MalformedJwtException {
        log.debug("Extracting all claims from token");
        return Jwts.parserBuilder()
                .setSigningKey(_getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date extractExpiration(String token) throws MalformedJwtException {
        log.debug("Extracting expiration date from token");
        return _extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) throws MalformedJwtException {
        log.debug("Extracting username from token");
        return _extractClaim(token, Claims::getSubject);
    }

    private Key _getSignKey() {
        log.debug("Getting signing key");
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean _isTokenExpired(String token) {
        log.debug("Checking if token is expired");
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        log.debug("Generating token for user: {}", username);
        return _createToken(username);
    }

    public Boolean validateToken(String token, String username) throws MalformedJwtException {
        log.debug("Validating token for user: {}", username);
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !_isTokenExpired(token));
    }

}
