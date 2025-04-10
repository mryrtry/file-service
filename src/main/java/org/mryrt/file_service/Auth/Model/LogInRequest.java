package org.mryrt.file_service.Auth.Model;

import lombok.Data;
import org.mryrt.file_service.Auth.Annotation.ValidLogInRequest;

@Data
@ValidLogInRequest
public class LogInRequest {

    private String username;

    private String password;

}
