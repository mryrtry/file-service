package org.mryrt.file_service.Auth.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Auth.Exception.JwtException;
import org.mryrt.file_service.Auth.Exception.JwtValidationException;
import org.mryrt.file_service.Auth.Service.JwtService;
import org.mryrt.file_service.Auth.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage.*;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private static final Set<String> WHITE_LIST = Set.of("/api/auth/log-in", "/api/auth/sign-up");

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    private boolean _skipFilter(HttpServletRequest request) {
        return request.getMethod().equals(HttpMethod.OPTIONS.name()) || WHITE_LIST.contains(request.getRequestURI());
    }

    private String _extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            throw new JwtValidationException(MISSING_AUTH_HEADER);
        if (!authHeader.startsWith("Bearer "))
            throw new JwtValidationException(INVALID_AUTH_HEADER_FORMAT);
        String token = authHeader.substring(7);
        if (token.isEmpty() || token.isBlank())
            throw new JwtValidationException(EMPTY_TOKEN);
        return token;
    }

    private String _extractUsername(String token) {
        String username = jwtService.extractUsername(token);
        if (username == null)
            throw new JwtValidationException(TOKEN_EXTRACTION_ERROR);
        if (SecurityContextHolder.getContext().getAuthentication() != null)
            throw new JwtValidationException(SECURITY_CONTEXT_ALREADY_SET);
        return username;
    }

    private void _validateToken(String token) {
        if (!jwtService.isIssuedAtValid(token))
            throw new JwtValidationException(FUTURE_ISSUED_AT_TOKEN);
    }

    private void _setAuthorization(HttpServletRequest request, String username) {
        try {
            UserDetails userDetails = userService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.debug("User {} authenticated successfully", username);
        } catch (UsernameNotFoundException exception) {
            throw new JwtValidationException(USERNAME_NOT_FOUND, username);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            if (_skipFilter(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = _extractToken(request);
            String username = _extractUsername(token);
            _validateToken(token);
            _setAuthorization(request, username);
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            if (exception instanceof JwtValidationException)
                handleException(response, exception);
            else handleException(response, JwtException.fromException(exception));
        }
    }

    private void handleException(HttpServletResponse response, Exception exception) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, Object> body = Map.of(
                "error", exception.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }

}