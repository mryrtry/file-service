package org.mryrt.file_service.Auth.Model;

import lombok.Data;
import org.mryrt.file_service.Auth.Annotation.ValidSignUpRequest;

@Data
@ValidSignUpRequest
public class SignUpRequest {

    private String username;

    private String password;

}
