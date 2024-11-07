package org.mryrt.file_service.Auth.Service;

// Custom UserInfo, UserInfoRepository
import org.mryrt.file_service.Auth.Model.User;
import org.mryrt.file_service.Auth.Model.UserDTO;
import org.mryrt.file_service.Auth.Model.UserDetails;
import org.mryrt.file_service.Auth.Model.UserRole;
import org.mryrt.file_service.Auth.Repository.UserRepository;

// Spring annotations
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Spring security
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

// Java time
import java.time.Instant;

// Java util
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

// Lombok annotation for logging
import lombok.extern.slf4j.Slf4j;

/**
 * Реализация сервиса пользователей.
 *
 * <p>
 * Данный класс отвечает за управление пользователями, включая добавление, получение,
 * удаление пользователей и работу с ролями пользователей. Также реализует интерфейс
 * {@link UserDetailsService} для интеграции с системой аутентификации Spring.
 * </p>
 */
@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService, UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Метод внутренней реализации для поиска пользователя по уникальному идентификатору.
     *
     * @param id Id пользователя.
     * @return {@link User} - найденный пользователь.
     * @throws UsernameNotFoundException если пользователь с таким Id не существует.
     */
    private User getUserById(int id) throws UsernameNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> {
            log.warn("User id: {}, not found", id);
            return new UsernameNotFoundException("User not found: " + id);
        });
    }

    /**
     * Метод внутренней реализации для поиска пользователя по имени.
     *
     * @param username Имя пользователя.
     * @return {@link User} - найденный пользователь.
     * @throws UsernameNotFoundException если пользователь с таким именем не существует.
     */
    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("User username: {}, not found", username);
            return new UsernameNotFoundException("User not found: " + username);
        });
    }

    /**
     * Метод внутренней реализации для поиска всех пользователей в репозитории.
     *
     * @param predicate предикат для фильтрации списка пользователей.
     * @return {@link List<User>} - список всех пользователей из репозитория.
     */
    private List<User> inGetAllUsers(Predicate<User> predicate) {
        Stream<User> users = userRepository.findAll().stream();
        if (predicate == null) return users.toList();
        return users.filter(predicate).toList();
    }

    /**
     * Метод внутренней реализации для удаления пользователя по имени.
     *
     * @param id Id пользователя.
     * @return {@link User} - удаленный пользователь.
     * @throws UsernameNotFoundException если пользователь с таким именем не существует.
     */
    private User deleteUserById(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("User id: {}, not found", id);
            return new UsernameNotFoundException("User not found: " + id);
        });
        userRepository.deleteById(id);
        return user;
    }

    /**
     * Метод внутренней реализации для удаления пользователя по имени.
     *
     * @param username Имя пользователя.
     * @return {@link User} - удаленный пользователь.
     * @throws UsernameNotFoundException если пользователь с таким именем не существует.
     */
    private User deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("User username: {}, not found", username);
            return new UsernameNotFoundException("User not found: " + username);
        });
        userRepository.deleteById(user.getId());
        return user;
    }

    /**
     * Удаляет всех пользователей, которые не являются администраторами, и,
     * при необходимости, также фильтрует пользователей по заданному предикату.
     *
     * @param predicate условие фильтрации пользователей. Если значение
     *                 равно {@code null}, фильтрация не применяется.
     * @return поток пользователей, которые были удалены. Обратите внимание,
     *         что возвращаемый поток может быть не инициализирован, если
     *         не было пользователей для удаления.
     */
    private List<User> inDeleteAllUsers(Predicate<User> predicate) {
        Stream<User> users = userRepository.findAll()
                .stream()
                .filter(user -> !user.getUserRoles().contains(UserRole.ADMIN));
        if (predicate != null) users = users.filter(predicate);
        List<User> userToDelete = users.toList();
        userRepository.deleteAll(userToDelete);
        return userToDelete;
    }

    /**
     * Обрабатывает пользователя, устанавливая закодированный пароль, роль по умолчанию и временные метки.
     *
     * @param user объект {@link User}, который будет обработан.
     */
    private void processUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserRoles(new HashSet<>(Collections.singletonList(UserRole.USER)));
        user.setCreateAt(Instant.now());
        user.setUpdateAt(Instant.now());
    }

    /**
     * Добавляет пользователю выбранные роли и обновляет временные метки.
     *
     * @param user объект {@link User}, роли которого будут обновлены.
     * @param roles переменное количество ролей {@link UserRole} для назначения пользователю.
     */
    private void inAddUserRoles(User user, UserRole... roles) {
        user.getUserRoles().addAll(List.of(roles));
        user.setUpdateAt(Instant.now());
        userRepository.save(user);
    }

    /**
     * Удаляет у пользователя выбранные роли и обновляет временные метки.
     *
     * @param user объект {@link User}, роли которого будут обновлены.
     * @param roles переменное количество ролей {@link UserRole} необходимых для удаления у пользователя.
     */
    private void inRemoveUserRoles(User user, UserRole... roles) {
        List.of(roles).forEach(user.getUserRoles()::remove);
        user.setUpdateAt(Instant.now());
        userRepository.save(user);
    }

    /**
     * Загружает пользователя по имени пользователя.
     *
     * @param username имя пользователя, по которому будет осуществлен поиск.
     * @return UserDetails - объект, содержащий информацию о пользователе.
     * @throws UsernameNotFoundException если пользователь с указанным именем не найден.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user with username: {}", username);
        return new UserDetails(getUserByUsername(username));
    }

    @Override
    public UserDTO uploadUser(User user) {
        log.info("Adding new user: {}", user.getUsername());
        processUser(user);
        userRepository.save(user);
        log.info("User {} successfully added", user.getUsername());
        return new UserDTO(user);
    }

    @Override
    public UserDTO getUser(int id) throws UsernameNotFoundException {
        log.info("Getting user with id: {}", id);
        User user = getUserById(id);
        log.info("User {} successfully get", user.getUsername());
        return new UserDTO(user);
    }

    @Override
    public UserDTO getUser(String username) throws UsernameNotFoundException {
        log.info("Getting user by username with username: {}", username);
        User user = getUserByUsername(username);
        log.info("User {} successfully get", username);
        return new UserDTO(user);
    }

    @Override
    public UserDTO getAuthUser() throws UsernameNotFoundException {
        log.debug("Getting authentication from security context");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Getting user from security context");
        if (authentication != null && authentication.isAuthenticated())
            return getUser(authentication.getName());
        log.warn("Authentication not found or user isn't authenticated");
        throw new UsernameNotFoundException("User not found");
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Retrieving list of all users");
        List<UserDTO> users = inGetAllUsers(null).stream().map(UserDTO::new).toList();
        log.info("Number of users: {}", users.size());
        return users;
    }

    @Override
    public List<UserDTO> getAllUsers(Predicate<User> predicate) {
        log.info("Searching for all users by criteria: {}", predicate);
        List<UserDTO> users = inGetAllUsers(predicate).stream().map(UserDTO::new).toList();
        log.info("Number of users by criteria {}: {}", predicate, users.size());
        return users;
    }

    @Override
    public UserDTO deleteUser(String username) throws UsernameNotFoundException {
        log.info("Deleting user with username: {}", username);
        User user = deleteUserByUsername(username);
        log.info("User with username {} successfully deleted", username);
        return new UserDTO(user);
    }

    @Override
    public UserDTO deleteUser(int id) throws UsernameNotFoundException {
        log.info("Deleting user with ID: {}", id);
        User user = deleteUserById(id);
        log.info("User  with ID {} successfully deleted", id);
        return new UserDTO(user);
    }

    @Override
    public List<UserDTO> deleteAllUsers() {
        log.info("Deleting all users");
        List<UserDTO> users = inDeleteAllUsers(null).stream().map(UserDTO::new).toList();
        log.info("All non admin users successfully deleted, number of deleted: {}", users.size());
        return users;
    }

    @Override
    public List<UserDTO> deleteAllUsers(Predicate<User> predicate) {
        log.info("Delete of users with predicate: {}", predicate);
        List<UserDTO> users = inDeleteAllUsers(predicate).stream().map(UserDTO::new).toList();
        log.info("All non admin users with predicate: {}, number of deleted: {}", predicate, users.size());
        return users;
    }

    @Override
    public UserDTO addUserRoles(int id, UserRole... roles) {
        log.info("Getting user with id: {}", id);
        User user = getUserById(id);
        log.info("User {} successfully get", user.getUsername());
        log.info("Adding user {} roles: {}", user.getUsername(), roles);
        inAddUserRoles(user, roles);
        return new UserDTO(user);
    }

    @Override
    public UserDTO addUserRoles(String username, UserRole... roles) {
        log.info("Getting user by username with username: {}", username);
        User user = getUserByUsername(username);
        log.info("User {} successfully get", username);
        log.info("Adding user {} roles: {}", user.getUsername(), roles);
        inAddUserRoles(user, roles);
        return new UserDTO(user);
    }

    @Override
    public UserDTO removeUserRoles(int id, UserRole... roles) {
        log.info("Getting user with id: {}", id);
        User user = getUserById(id);
        log.info("User {} successfully get", user.getUsername());
        log.info("Removing user {} roles: {}", user.getUsername(), roles);
        inRemoveUserRoles(user, roles);
        return new UserDTO(user);
    }

    @Override
    public UserDTO removeUserRoles(String username, UserRole... roles) {
        log.info("Getting user by username with username: {}", username);
        User user = getUserByUsername(username);
        log.info("User {} successfully get", username);
        log.info("Removing user {} roles: {}", user.getUsername(), roles);
        inRemoveUserRoles(user, roles);
        return new UserDTO(user);
    }
}