package org.mryrt.file_service.Auth.Model;

// Java annotations
import org.jetbrains.annotations.NotNull;

// Spring security
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

// Java Util
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * UserDetails - класс, реализующий интерфейс UserDetails из Spring Security.
 * Этот класс используется для хранения информации о пользователе,
 * включая его имя пользователя, пароль и права доступа (authorities).
 * Он преобразует объект User в формат, необходимый для аутентификации в Spring Security.
 */
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {

    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    /**
     * Конструктор, создающий объект UserDetails на основе объекта User.
     *
     * @param user объект User, содержащий информацию о пользователе.
     */
    public UserDetails(@NotNull User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        user.getUserRoles().forEach(a -> grantedAuthorities.add(new SimpleGrantedAuthority(a.toString())));
        this.authorities = grantedAuthorities;
    }

    /**
     * Возвращает права доступа (authorities) пользователя.
     *
     * @return коллекция прав доступа, связанных с пользователем.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return пароль в виде строки.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя в виде строки.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Проверяет, истек ли срок действия учетной записи пользователя.
     *
     * @return true, если учетная запись не истекла; false в противном случае.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Проверяет, заблокирована ли учетная запись пользователя.
     *
     * @return true, если учетная запись не заблокирована; false в противном случае.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Проверяет, истек ли срок действия учетных данных пользователя.
     *
     * @return true, если учетные данные не истекли; false в противном случае.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Проверяет, активна ли учетная запись пользователя.
     *
     * @return true, если учетная запись активна; false в противном случае.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}