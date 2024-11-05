package org.mryrt.file_service.Auth.Controller;

// Custom AuthRequest, User (Dto), JwtService, UserInfoService
import org.mryrt.file_service.Auth.Model.AuthRequest;
import org.mryrt.file_service.Auth.Model.User;
import org.mryrt.file_service.Auth.Model.UserDTO;
import org.mryrt.file_service.Auth.Model.UserRole;
import org.mryrt.file_service.Auth.Service.JwtService;

// Spring annotations & other
import org.mryrt.file_service.Auth.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Spring security
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// Java util
import java.util.List;

/**
 * Контроллер для управления аутентификацией и пользователями.
 * Этот класс предоставляет REST API для добавления новых пользователей,
 * получения информации о пользователе и генерации JWT токенов для аутентификации.
 */
@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    /**
     * Регистрирует нового пользователя.
     *
     * @param user объект {@link User} с данными для регистрации.
     * @return {@link ResponseEntity} с объектом {@link UserDTO} зарегистрированного пользователя.
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {
        UserDTO userDto = userService.addUser(user);
        return ResponseEntity.ok().body(userDto);
    }

    /**
     * Аутентифицирует пользователя и возвращает JWT токен.
     *
     * @param authRequest объект {@link AuthRequest} с именем пользователя и паролем.
     * @return {@link ResponseEntity} с JWT токеном или статус 404, если пользователь не найден.
     */
    @PostMapping("/getToken")
    public ResponseEntity getToken(@RequestBody AuthRequest authRequest) {
        try {
            String token = jwtService.authenticate(authRequest);
            return ResponseEntity.ok().body(token);
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return ResponseEntity с UserDTO в качестве тела ответа.
     */
    @GetMapping("admin/get/id/{id}")
    public ResponseEntity<UserDTO> getId(@PathVariable("id") int id) {
        try {
            UserDTO userDTO = userService.getUser(id);
            return ResponseEntity.ok().body(userDTO);
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Получает пользователя по имени пользователя.
     *
     * @param username имя пользователя.
     * @return ResponseEntity с UserDTO в качестве тела ответа.
     */
    @GetMapping("admin/get/username/{username}")
    public ResponseEntity<UserDTO> getUsername(@PathVariable("username") String username) {
        try {
            UserDTO userDTO = userService.getUser(username);
            return ResponseEntity.ok().body(userDTO);
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Получает список всех пользователей.
     *
     * @return ResponseEntity со списком UserDTO в качестве тела ответа.
     */
    @GetMapping("admin/get/all")
    public ResponseEntity<List<UserDTO>> getAll() {
        List<UserDTO> userDTOList = userService.getAllUsers();
        return ResponseEntity.ok().body(userDTOList);
    }

    /**
     * Получает всех пользователей по указанной роли.
     *
     * @param role роль пользователей.
     * @return ResponseEntity со списком UserDTO в качестве тела ответа.
     */
    @GetMapping("admin/get/role")
    public ResponseEntity<List<UserDTO>> getAllRole(@RequestParam String role) {
        UserRole userRole = UserRole.valueOf(role);
        List<UserDTO> userDTOList = userService.getAllUsers(user -> user.getUserRoles().contains(userRole));
        return ResponseEntity.ok().body(userDTOList);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return ResponseEntity с UserDTO в качестве тела ответа.
     */
    @DeleteMapping("admin/delete/id/{id}")
    public ResponseEntity<UserDTO> deleteUserId(@PathVariable int id) {
        try {
            UserDTO userDTO = userService.deleteUser(id);
            return ResponseEntity.ok().body(userDTO);
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param username имя пользователя.
     * @return ResponseEntity с UserDTO в качестве тела ответа.
     */
    @DeleteMapping("admin/delete/username/{username}")
    public ResponseEntity<UserDTO> deleteUserUsername(@PathVariable String username) {
        try {
            UserDTO userDTO = userService.deleteUser(username);
            return ResponseEntity.ok().body(userDTO);
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Удаляет всех пользователей.
     *
     * @return ResponseEntity со списком UserDTO в качестве тела ответа.
     */
    @DeleteMapping("admin/delete/all")
    public ResponseEntity<List<UserDTO>> deleteAll() {
        List<UserDTO> userDTOList = userService.deleteAllUsers();
        return ResponseEntity.ok().body(userDTOList);
    }

    /**
     * Предоставление прав администратора пользователю по его идентификатору.
     *
     * @param id Идентификатор пользователя, которому нужно предоставить права администратора.
     * @return UserDTO, представляющий обновленного пользователя с правами администратора.
     */
    @GetMapping("admin/assign/id/{id}")
    public ResponseEntity<UserDTO> assignId(@PathVariable int id) {
        try {
            UserDTO userDTO = userService.addUserRoles(id, UserRole.ADMIN);
            return ResponseEntity.ok().body(userDTO);
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Предоставление прав администратора пользователю по его идентификатору.
     *
     * @param username Имя пользователя, которому нужно предоставить права администратора.
     * @return UserDTO, представляющий обновленного пользователя с правами администратора.
     */
    @GetMapping("assign/username/{username}")
    public ResponseEntity<UserDTO> assignUsername(@PathVariable String username) {
        try {
            UserDTO userDTO = userService.addUserRoles(username, UserRole.ADMIN);
            return ResponseEntity.ok().body(userDTO);
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Отзыв прав администратора пользователю по его идентификатору.
     *
     * @param id Идентификатор пользователя, у которого нужно отозвать права администратора.
     * @return UserDTO, представляющий обновленного пользователя с правами администратора.
     */
    @GetMapping("admin/revoke/id/{id}")
    public ResponseEntity<UserDTO> revokeId(@PathVariable int id) {
        try {
            UserDTO userDTO = userService.removeUserRoles(id, UserRole.ADMIN);
            return ResponseEntity.ok().body(userDTO);
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Отзыв прав администратора пользователю по его идентификатору.
     *
     * @param username Имя пользователя, у которого нужно отозвать права администратора.
     * @return UserDTO, представляющий обновленного пользователя с правами администратора.
     */
    @GetMapping("admin/revoke/username/{username}")
    public ResponseEntity<UserDTO> revokeUsername(@PathVariable String username) {
        try {
            UserDTO userDTO = userService.removeUserRoles(username, UserRole.ADMIN);
            return ResponseEntity.ok().body(userDTO);
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }
}