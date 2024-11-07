package org.mryrt.file_service.Auth.Service;

// JWT
import io.jsonwebtoken.MalformedJwtException;

// Custom auth request
import org.mryrt.file_service.Auth.Model.AuthRequest;

// Spring security
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// Java time
import java.util.Date;

/**
 * Интерфейс для работы с JWT (JSON Web Tokens).
 * Предоставляет методы для аутентификации пользователей,
 * валидации токенов и извлечения данных из токенов.
 */
public interface JwtService {
    /**
     * Аутентифицирует пользователя и генерирует JWT токен.
     *
     * @param authRequest объект {@link AuthRequest}, содержащий имя пользователя и пароль.
     * @return сгенерированный JWT токен.
     * @throws UsernameNotFoundException если аутентификация не удалась.
     */
    String authenticate(AuthRequest authRequest) throws UsernameNotFoundException;

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
     * @throws UsernameNotFoundException если пользователь не найден.
     */
    Boolean validateToken(String token, UserDetails userDetails) throws UsernameNotFoundException;

    /**
     * Извлекает имя пользователя из токена.
     *
     * @param token JWT токен.
     * @return имя пользователя, извлеченное из токена.
     * @throws MalformedJwtException если токен имеет неверный формат.
     */
    String extractUsername(String token) throws MalformedJwtException;

    /**
     * Извлекает дату истечения токена.
     *
     * @param token JWT токен.
     * @return дата истечения токена.
     * @throws MalformedJwtException если токен имеет неверный формат.
     */
    Date extractExpiration(String token) throws MalformedJwtException;
}