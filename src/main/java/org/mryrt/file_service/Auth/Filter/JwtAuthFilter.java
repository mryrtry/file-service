package org.mryrt.file_service.Auth.Filter;

// Custom JwtService, UserServiceImpl
import org.mryrt.file_service.Auth.Service.JwtService;
import org.mryrt.file_service.Auth.Service.UserServiceImpl;

// Jakarta Servlet
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Spring annotations
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

// Spring security
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

// Spring Web
import org.springframework.web.filter.OncePerRequestFilter;

// Lombok logger
import lombok.extern.slf4j.Slf4j;

// Exceptions
import java.io.IOException;

/**
 * Фильтр для обработки JWT аутентификации.
 * Проверяет наличие JWT в заголовке запроса и устанавливает контекст безопасности для аутентифицированного пользователя.
 */
@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserServiceImpl userDetailsService;

    /**
     * Обрабатывает входящие HTTP-запросы для проверки JWT и настройки аутентификации.
     *
     * @param request     объект HttpServletRequest, представляющий входящий запрос.
     * @param response    объект HttpServletResponse, представляющий исходящий ответ.
     * @param filterChain цепочка фильтров для продолжения обработки запроса.
     * @throws ServletException если возникает ошибка при обработке запроса.
     * @throws IOException      если возникает ошибка ввода-вывода.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContext context = SecurityContextHolder.getContext();
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        try {
            // Проверка на наличие заголовка Authorization и его корректность
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtService.extractUsername(token);
                log.info("Extracted username: {}", username);
            } else {
                log.warn("Authorization header is missing or invalid");
            }

            // Проверка аутентификации
            if (username != null && context.getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    log.info("User {} authenticated successfully", username);
                } else {
                    // Возвращаем ответ с ошибкой, если токен недействителен
                    log.warn("Invalid JWT token for user: {}", username);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                    return; // Прерываем цепочку фильтров
                }
            }
        } catch (Exception e) {
            // Обработка исключений, например, если произошла ошибка извлечения пользователя
            log.warn("Authentication error: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return; // Прерываем цепочку фильтров
        }

        // Продолжение фильтрации
        filterChain.doFilter(request, response);
    }
}