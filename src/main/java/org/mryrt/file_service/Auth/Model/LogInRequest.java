package org.mryrt.file_service.Auth.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.mryrt.file_service.Auth.Annotation.Login;

@Data
public class LogInRequest {

    @Login
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be longer than 5 characters")
    private String password;

}
