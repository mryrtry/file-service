package org.mryrt.file_service.Auth.Service;

import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Auth.Exception.InvalidCredentialsException;
import org.mryrt.file_service.Auth.Model.*;
import org.mryrt.file_service.Auth.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private void _processUser(User user, SignUpRequest signUpRequest) {
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRoles(new HashSet<>(Collections.singletonList(UserRole.USER)));
    }

    private User _getUserId(long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new InvalidCredentialsException("id", "id %d does not exist".formatted(id)));
    }

    private User _getUserUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("username", "username %s does not exist".formatted(username)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new CustomUserDetails(userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username %s does not exist".formatted(username))));
    }

    public UserDTO userSignUp(SignUpRequest signUpRequest) {
        User user = new User();
        _processUser(user, signUpRequest);
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }

    public String userLogIn(LogInRequest logInRequest) {
        User user = _getUserUsername(logInRequest.getUsername());
        if (!passwordEncoder.matches(logInRequest.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException("password", "Wrong password");
        return jwtService.generateToken(user.getUsername());
    }

    public UserDTO getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated())
            return new UserDTO(_getUserUsername(authentication.getName()));
        throw new InvalidCredentialsException("username", "username %s does not exist".formatted(authentication.getName()));
    }

}
