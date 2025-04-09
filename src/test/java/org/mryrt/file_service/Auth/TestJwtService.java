package org.mryrt.file_service.Auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

@Component
public class TestJwtService {

    @Value("${jwt.secret}")
    private String DEFAULT_SECRET;

    @Value("${jwt.expiration}")
    private int EXPIRATION;

    private Key _getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(DEFAULT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key _getFakeSignKey() {
        String fakeSecret = "ca0a0bc26c967011d828df85dc38d3065be6f24bf59b506ef90a93655fa68d8f73902dea7b7c3c8536ecc70e64193129d8842840154b99037423d6a9f9e6245e";
        byte[] keyBytes = Decoders.BASE64.decode(fakeSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String username) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(_getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createToken(String username, int expirationTime) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(_getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createFakeToken(String username) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(_getFakeSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
