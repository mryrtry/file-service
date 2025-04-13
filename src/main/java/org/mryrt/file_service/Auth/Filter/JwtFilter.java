package org.mryrt.file_service.Auth.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Auth.Exception.JwtValidationException;
import org.mryrt.file_service.Auth.Model.CustomUserDetails;
import org.mryrt.file_service.Auth.Model.User;
import org.mryrt.file_service.Auth.Repository.UserRepository;
import org.mryrt.file_service.Auth.Service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage.*;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private static final List<String> WHITE_LIST = List.of("/api/auth/log-in", "/api/auth/sign-up");

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private boolean _skipFilter(HttpServletRequest request) {
        return request.getMethod().equals(HttpMethod.OPTIONS.name()) || WHITE_LIST.contains(request.getRequestURI());
    }

    private String _extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            throw new JwtValidationException(MISSING_AUTH_HEADER);
        if (!authHeader.startsWith("Bearer "))
            throw new JwtValidationException(INVALID_AUTH_HEADER_FORMAT);
        return authHeader.substring(7);
    }

    private String _extractUsername(String token) {
        if (token.isEmpty() || token.isBlank())
            throw new JwtValidationException(EMPTY_TOKEN);
        String username = jwtService.extractUsername(token);
        if (username == null)
            throw new JwtValidationException(TOKEN_EXTRACTION_ERROR);
        if (SecurityContextHolder.getContext().getAuthentication() != null)
            throw new JwtValidationException(SECURITY_CONTEXT_ALREADY_SET);
        return username;
    }

    private User _getUser(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new JwtValidationException(USERNAME_NOT_FOUND, username));
    }

    private void _validateToken(String username, String token) {
        if (!jwtService.checkIssuedAtCorrect(token))
            throw new JwtValidationException(FUTURE_ISSUED_AT_TOKEN);
        if (!jwtService.validateToken(token, username))
            throw new JwtValidationException(USERNAME_MISMATCH);
    }

    private void _setAuthorization(HttpServletRequest request, String username) {
        UserDetails userDetails = new CustomUserDetails(_getUser(username));
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.info("User {} authenticated successfully", username);
    }

    // todo: Вложенный try catch фу
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            try {
                if (_skipFilter(request)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                String token = _extractToken(request);
                String username = _extractUsername(token);
                _validateToken(username, token);
                _setAuthorization(request, username);
                filterChain.doFilter(request, response);
            } catch (MalformedJwtException exception) {
                throw new JwtValidationException(INVALID_TOKEN, exception);
            } catch (ExpiredJwtException exception) {
                throw new JwtValidationException(EXPIRED_TOKEN, exception);
            } catch (SignatureException exception) {
                throw new JwtValidationException(TOKEN_SIGNATURE_MISMATCH, exception);
            } catch (InvalidKeyException exception) {
                throw new JwtValidationException(TOKEN_ALGORITHM_MISMATCH, exception);
            } catch (MissingClaimException exception) {
                throw new JwtValidationException(MISSING_TOKEN_CLAIM, exception);
            } catch (IncorrectClaimException exception) {
                throw new JwtValidationException(INVALID_TOKEN_CLAIM, exception);
            }
        } catch (JwtValidationException exception) {
            handleException(response, exception);
        }
    }

    private void handleException(HttpServletResponse response, Exception ex) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, Object> body = Map.of(
                "error", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }

}