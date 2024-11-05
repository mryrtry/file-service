package org.mryrt.file_service.Auth.Model;

// Lombok
import lombok.Data;

// Java time
import java.time.Instant;
import java.util.HashSet;

/**
 * Представляет объект передачи данных (DTO) для пользователя в системе аутентификации.
 * Этот класс используется для передачи данных, связанных с пользователем, между различными уровнями приложения.
 */
@Data
public class UserDTO {

    /**
     * Конструктор, создающий DTO сущность на основе сущности {@link User}.
     *
     * @param user сущность класса {@link User} для создания DTO объекта.
     */
    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.createAt = user.getCreateAt();
        this.updateAt = user.getUpdateAt();
        this.userRoles = user.getUserRoles();
    }

    /**
     * Уникальный идентификатор пользователя.
     */
    private int id;

    /**
     * Имя пользователя.
     * Обычно используется для аутентификации и отображения.
     */
    private String username;

    /**
     * Временная метка, указывающая, когда пользователь был создан.
     * Представляется как {@link Instant}, чтобы зафиксировать точное время создания.
     */
    private Instant createAt;

    /**
     * Временная метка, указывающая, когда пользователь был обновлен.
     * Представляется как {@link Instant}, чтобы зафиксировать точное время обновление.
     */
    private Instant updateAt;

    /**
     * Список ролей пользователя.
     * Этот список содержит роли, которые назначены пользователю в системе.
     */
    private HashSet<UserRole> userRoles;
}
