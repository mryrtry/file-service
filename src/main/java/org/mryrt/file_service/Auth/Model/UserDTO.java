package org.mryrt.file_service.Auth.Model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDTO {

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.roles = user.getRoles();
        this.createdAt = user.getCreatedAt();
    }

    private long id;

    private String username;

    private Set<UserRole> roles;

    private LocalDateTime createdAt;

}
