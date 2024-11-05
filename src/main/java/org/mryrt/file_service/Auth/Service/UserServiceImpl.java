package org.mryrt.file_service.Auth.Service;

// Custom UserInfo, UserInfoRepository
import lombok.extern.slf4j.Slf4j;
import org.mryrt.file_service.Auth.Model.User;
import org.mryrt.file_service.Auth.Model.UserDetails;
import org.mryrt.file_service.Auth.Model.UserRole;
import org.mryrt.file_service.Auth.Repository.UserRepository;

// Spring annotations
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Spring security
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

/**
 * Сервис для работы с информацией о пользователях.
 * Реализует интерфейс UserDetailsService для интеграции с механизмом аутентификации Spring Security.
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    /**
     * Загружает пользователя по имени пользователя.
     *
     * @param username имя пользователя, по которому будет осуществлен поиск.
     * @return UserDetails - объект, содержащий информацию о пользователе.
     * @throws UsernameNotFoundException если пользователь с указанным именем не найден.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByName(username).orElse(null);
        assert user != null;
        return new UserDetails(repository.findByName(username).orElseThrow());
    }

    /**
     * Добавляет нового пользователя в систему.
     * Пароль пользователя шифруется перед сохранением.
     *
     * @param user объект UserInfo, содержащий информацию о новом пользователе.
     * @return UserInfo - сохраненный объект пользователя, или null, если пользователь не найден после сохранения.
     */
    public User addUser (User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        ArrayList<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.USER);
        user.setUserRoles(roles);
        repository.save(user);
        return user;
    }

    public User getUser (String username) {
        return repository.findByName(username).orElseThrow();
    }
}