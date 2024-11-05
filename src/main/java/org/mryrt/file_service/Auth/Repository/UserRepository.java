package org.mryrt.file_service.Auth.Repository;

// Entity class User
import org.mryrt.file_service.Auth.Model.User;

// Spring annotation
import org.springframework.stereotype.Repository;

// Jpa Repository
import org.springframework.data.jpa.repository.JpaRepository;

// Optional
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с сущностью {@link User}.
 * Этот интерфейс наследует от {@link JpaRepository}, предоставляя стандартные методы
 * для выполнения операций CRUD (создание, чтение, обновление, удаление) с пользователями.
 * Также включает метод для поиска пользователя по имени.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Находит пользователя по его уникальному имени.
     *
     * @param username уникальное имя пользователя, по которому будет выполнен поиск пользователя.
     * @return объект {@link Optional<User>}, который содержит найденного пользователя,
     *         если он существует, или пустой объект, если пользователь не найден.
     */
    Optional<User> findByUsername(String username);
}