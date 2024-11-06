package org.mryrt.file_service.Auth.Service;

// Custom Auth Request

import io.jsonwebtoken.MalformedJwtException;
import org.mryrt.file_service.Auth.Model.AuthRequest;

// JWT
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

// Spring annotations
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Spring security
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// Java security
import java.security.Key;

// Java util classes
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

/**
 * JwtService - сервис для работы с JSON Web Token (JWT).
 * Этот класс предоставляет методы для генерации, валидации и извлечения информации из JWT.
 * JWT используется для аутентификации и авторизации пользователей в приложении.
 */
@Component
@Slf4j
public class JwtService {
    /**
     * Секретный ключ для подписи JWT, извлекаемый из конфигурации.
     */
    @Value("${jwt.secret}")
    private String SECRET;

    /**
     * Время жизни токена в миллисекундах, извлекаемое из конфигурации.
     */
    @Value("${jwt.expiration}")
    private int EXPIRATION;

    /**
     * Мэнеджер аутентификации пользователя.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Аутентифицирует пользователя и генерирует JWT токен.
     *
     * @param authRequest объект {@link AuthRequest}, содержащий имя пользователя и пароль.
     * @return сгенерированный JWT токен.
     * @throws UsernameNotFoundException если аутентификация не удалась.
     */
    public String authenticate(AuthRequest authRequest) {
        log.info("Authenticating user: {}", authRequest.username());
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password());
        Authentication authentication = authenticationManager.authenticate(authToken);
        if (authentication.isAuthenticated()) {
            String token = generateToken(authRequest.username());
            log.info("User {} authenticated successfully. Token generated.", authRequest.username());
            return token;
        }
        log.warn("Authentication failed for user: {}", authRequest.username());
        throw new UsernameNotFoundException("Invalid username or password");
    }

    /**
     * Генерирует JWT для указанного пользователя.
     *
     * @param username имя пользователя, для которого генерируется токен.
     * @return сгенерированный JWT в виде строки.
     */
    private String generateToken(String username) {
        log.info("Generating token for user: {}", username);
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Создает JWT на основе клеймов и имени пользователя.
     *
     * @param claims   мапа клеймов для включения в токен.
     * @param username имя пользователя, для которого создается токен.
     * @return сгенерированный JWT в виде строки.
     */
    private String createToken(Map<String, Object> claims, String username) {
        log.debug("Creating token with for user: {}", username);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // Токен действителен в течение указанного времени
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Получает секретный ключ для подписи JWT.
     *
     * @return объект Key, представляющий секретный ключ.
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Извлекает имя пользователя из токена.
     *
     * @param token JWT токен.
     * @return имя пользователя, извлеченное из токена.
     */
    public String extractUsername(String token) throws MalformedJwtException {
        log.debug("Extracting username from token.");
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Извлекает дату истечения токена.
     *
     * @param token JWT токен.
     * @return дата истечения токена.
     */
    public Date extractExpiration(String token) throws MalformedJwtException {
        log.debug("Extracting expiration date from token.");
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает указанный клейм из токена.
     *
     * @param token          JWT токен.
     * @param claimsResolver функция для извлечения клейма.
     * @param <T>            тип клейма.
     * @return извлеченное значение к
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws MalformedJwtException {
        log.debug("Extracting claims from token.");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Извлекает все клеймы из токена.
     *
     * @param token JWT токен.
     * @return объект Claims, содержащий все клеймы.
     */
    private Claims extractAllClaims(String token) throws MalformedJwtException {
        log.debug("Extracting all claims from token.");
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Проверяет, истек ли токен.
     *
     * @param token JWT токен.
     * @return true, если токен истек, иначе false.
     */
    private Boolean isTokenExpired(String token) {
        log.debug("Checking if token is expired.");
        return extractExpiration(token).before(new Date());
    }

    /**
     * Проверяет валидность токена по сравнению с данными пользователя.
     * Этот метод извлекает имя пользователя из токена и сравнивает его с именем пользователя,
     * предоставленным в объекте UserDetails. Также проверяется, не истек ли токен.
     *
     * @param token       JWT токен, который необходимо проверить.
     * @param userDetails объект UserDetails, содержащий информацию о пользователе,
     *                    с которой будет производиться сравнение.
     * @return true, если токен валиден (имя пользователя совпадает и токен не истек),
     * иначе false.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        log.debug("Validating token for user: {}", userDetails.getUsername());
        final String username = extractUsername(token);
        boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        log.info("Token validation result for user {}: {}", username, isValid);
        return isValid;
    }
}
