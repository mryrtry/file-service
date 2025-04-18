package org.mryrt.file_service.Auth.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserDTO {

    private long id;
    private String username;
    private Set<UserRole> roles;
    private LocalDateTime createdAt;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.roles = user.getRoles();
        this.createdAt = user.getCreatedAt();
    }

}
