package org.mryrt.file_service.Auth.Model;

/**
 * Перечисление, представляющее роли пользователей в системе.
 * Данное перечисление определяет два типа ролей:
 * <ul>
 *     <li><strong>USER</strong> - стандартная роль пользователя с ограниченными правами.</li>
 *     <li><strong>ADMIN</strong> - роль администратора с расширенными правами доступа.</li>
 * </ul>
 */
public enum UserRole {
    USER,
    ADMIN
}
