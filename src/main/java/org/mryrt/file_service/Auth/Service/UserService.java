package org.mryrt.file_service.Auth.Service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Auth.Exception.InvalidCredentialsException;
import org.mryrt.file_service.Auth.Model.*;
import org.mryrt.file_service.Auth.Repository.UserRepository;
import org.mryrt.file_service.Utility.Annotation.TrackExecutionTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@TrackExecutionTime
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private void processUser(User user, SignUpRequest signUpRequest) {
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRoles(Set.of(UserRole.USER));
    }

    private User getUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException(USERNAME_NOT_FOUND, username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new CustomUserDetails(userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND.getFormattedMessage(username))));
    }

    @Transactional
    public UserDTO userSignUp(@Valid SignUpRequest signUpRequest) {
        User user = new User();
        processUser(user, signUpRequest);
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }

    public String userLogIn(@Valid LogInRequest logInRequest) {
        User user = getUserByUsername(logInRequest.getUsername());
        if (!passwordEncoder.matches(logInRequest.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException(WRONG_PASSWORD);
        return jwtService.generateToken(user.getUsername());
    }

    public long getAuthUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated())
            return getUserByUsername(authentication.getName()).getId();
        throw new InvalidCredentialsException(USER_NOT_AUTHENTICATED);
    }

    public boolean checkUserExists(String filename) {
        try {
            long userId = Long.parseLong(filename);
            return userRepository.existsById(userId);
        } catch (NumberFormatException exception) {
            return false;
        }
    }

}
