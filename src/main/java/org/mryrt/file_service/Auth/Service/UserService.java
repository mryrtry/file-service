package org.mryrt.file_service.Auth.Service;

// Custom UserInfo, UserInfoRepository
import org.mryrt.file_service.Auth.Model.User;
import org.mryrt.file_service.Auth.Repository.UserRepository;

// Spring annotations
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Spring security
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Сервис для работы с информацией о пользователях.
 * Реализует интерфейс UserDetailsService для интеграции с механизмом аутентификации Spring Security.
 */
@Service
public class UserInfoService implements UserDetailsService {

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
        return new UserInfoDetails(repository.findByName(username).orElseThrow());
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
        user.setRoles("ROLE_USER");
        repository.save(user);
        return repository.findByName(user.getName()).orElse(null);
    }
}