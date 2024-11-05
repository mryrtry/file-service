package org.mryrt.file_service.Auth.Model;

// Jakarta persistence
import jakarta.persistence.*;

// Lombok
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// Java util
import java.time.Instant;
import java.util.HashSet;

/**
 * Класс модели для представления информации о пользователе.
 * Содержит поля для хранения имени пользователя, пароля и ролей.
 */
@Entity
@Table(name = "file_service_user")
@Data
@NoArgsConstructor
@ToString
public class User {
    /**
     * Уникальный идентификатор пользователя.
     * Автоматически генерируется при добавлении нового пользователя в базу данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Уникальное имя пользователя.
     */
    private String username;

    /**
     * Пароль пользователя.
     */
    private String password;

    /**
     * Роли пользователя.
     */
    private HashSet<UserRole> userRoles;

    /**
     * Дата создания аккаунта пользователя.
     */
    private Instant createAt;

    /**
     * Дата обновления аккаунта пользователя.
     */
    private Instant updateAt;
}