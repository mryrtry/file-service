package org.mryrt.file_service.Auth.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mryrt.file_service.Auth.Annotation.ValidSignUpRequest;

@Data
@ValidSignUpRequest
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    private String username;

    private String password;

}
