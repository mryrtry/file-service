package org.mryrt.file_service.Auth.Service;

import org.mryrt.file_service.Auth.Model.User;
import org.mryrt.file_service.Auth.Model.UserDTO;
import org.mryrt.file_service.Auth.Model.UserRole;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.function.Predicate;

/**
 * Интерфейс для управления пользователями в системе.
 * Предоставляет методы для добавления, получения, удаления пользователей и работы с ролями пользователей.
 */
public interface UserService {

    // TODO: Изъятие прав администратора не обнуляет токен с правами администратора.

    /**
     * Добавляет нового пользователя в репозиторий.
     * <p>
     * Пароль пользователя шифруется перед сохранением, и пользователю назначается роль {@link UserRole#USER}.
     * </p>
     *
     * @param user объект {@link User}, содержащий информацию о новом пользователе.
     * @return UserDTO - сохраненный объект пользователя.
     */
    UserDTO addUser(User user);

    /**
     * Получает пользователя по уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя.
     * @return UserDTO - объект с информацией о пользователе.
     * @throws UsernameNotFoundException если пользователь не найден.
     */
    UserDTO getUser(int id) throws UsernameNotFoundException;

    /**
     * Получает пользователя по уникальному идентификатору.
     *
     * @param username имя пользователя.
     * @return UserDTO - объект с информацией о пользователе.
     * @throws UsernameNotFoundException если пользователь не найден.
     */
    UserDTO getUser(String username) throws UsernameNotFoundException;

    /**
     * Получает список всех пользователей.
     *
     * @return List<UserDTO> - список объектов с информацией о всех пользователях.
     */
    List<UserDTO> getAllUsers();

    /**
     * Получает список всех пользователей, отфильтрованных по заданному предикату.
     *
     * @param predicate предикат {@link Predicate}, который определяет критерии фильтрации пользователей.
     * @return список {@link List} объектов {@link UserDTO}, которые соответствуют критериям фильтрации,
     *         определенным предикатом. Если ни один пользователь не соответствует критериям, возвращается пустой список.
     */
    List<UserDTO> getAllUsers(Predicate<User> predicate);

    /**
     * Удаляет пользователя по ID.
     *
     * @param id ID пользователя, которого нужно удалить.
     * @return UserDTO - объект {@link UserDTO}, представляющий удаленного пользователя.
     * @throws UsernameNotFoundException если пользователь с указанным ID не найден.
     */
    UserDTO deleteUser(int id) throws UsernameNotFoundException;

    /**
     * Удаляет пользователя по ID.
     *
     * @param username Имя пользователя, которого нужно удалить.
     * @return UserDTO - объект {@link UserDTO}, представляющий удаленного пользователя.
     * @throws UsernameNotFoundException если пользователь с указанным ID не найден.
     */
    UserDTO deleteUser(String username) throws UsernameNotFoundException;

    /**
     * Удаляет всех пользователей из репозитория.
     *
     * @return список объектов {@link UserDTO}, представляющих всех удаленных пользователей.
     */
    List<UserDTO> deleteAllUsers();

    /**
     * Удаляет пользователей из репозитория, соответствующих заданному предикату.
     *
     * @param predicate условие фильтрации пользователей. Если значение
     *                 равно {@code null}, будет удалено всех пользователей.
     * @return список объектов {@link UserDTO}, представляющих всех удаленных пользователей,
     *         которые соответствовали предикату.
     */
    List<UserDTO> deleteAllUsers(Predicate<User> predicate);

    /**
     * Добавляет роли пользователю по его идентификатору.
     *
     * @param id    Идентификатор пользователя.
     * @param roles Роли, которые необходимо добавить пользователю.
     * @return Объект UserDTO, представляющий обновленного пользователя с новыми ролями.
     */
    UserDTO addUserRoles(int id, UserRole... roles);

    /**
     * Добавляет роли пользователю по его имени пользователя.
     *
     * @param username Имя пользователя.
     * @param roles    Роли, которые необходимо добавить пользователю.
     * @return Объект UserDTO, представляющий обновленного пользователя с новыми ролями.
     */
    UserDTO addUserRoles(String username, UserRole... roles);

    /**
     * Удаляет роли у пользователя по его идентификатору.
     *
     * @param id    Идентификатор пользователя.
     * @param roles Роли, которые необходимо удалить у пользователя.
     * @return Объект UserDTO, представляющий обновленного пользователя без удаленных ролей.
     */
    UserDTO removeUserRoles(int id, UserRole... roles);

    /**
     * Удаляет роли у пользователя по его имени пользователя.
     *
     * @param username Имя пользователя.
     * @param roles    Роли, которые необходимо удалить у пользователя.
     * @return Объект UserDTO, представляющий обновленного пользователя без удаленных ролей.
     */
    UserDTO removeUserRoles(String username, UserRole... roles);
}
