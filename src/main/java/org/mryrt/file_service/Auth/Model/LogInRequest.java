package org.mryrt.file_service.Auth.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mryrt.file_service.Auth.Annotation.ValidLogInRequest;

@Data
@ValidLogInRequest
@AllArgsConstructor
@NoArgsConstructor
public class LogInRequest {

    private String username;

    private String password;

}
