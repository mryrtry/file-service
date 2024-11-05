package org.mryrt.file_service.Auth.Filter;

import org.mryrt.file_service.Auth.Service.JwtService;
import org.mryrt.file_service.Auth.Service.UserServiceImpl;

// Jakarta Servlet
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Spring annotations
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

// Spring security
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

// Spring Web
import org.springframework.web.filter.OncePerRequestFilter;

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
     * @param request   объект HttpServletRequest, представляющий входящий запрос.
     * @param response  объект HttpServletResponse, представляющий исходящий ответ.
     * @param filterChain цепочка фильтров для продолжения обработки запроса.
     * @throws ServletException если возникает ошибка при обработке запроса.
     * @throws IOException      если возникает ошибка ввода-вывода.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorized");
                    return;
                }
            } catch (UsernameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorized");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}