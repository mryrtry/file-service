package org.mryrt.file_service.Auth.Controller;

import org.mryrt.file_service.Auth.Model.LogInRequest;
import org.mryrt.file_service.Auth.Model.SignUpRequest;
import org.mryrt.file_service.Auth.Model.UserDTO;
import org.mryrt.file_service.Auth.Service.UserService;
import org.mryrt.file_service.Utility.Annotation.RateLimited;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @RateLimited
    @PostMapping("/sign-up")
    public ResponseEntity userSignUp(@RequestBody SignUpRequest signUpRequest) {
        UserDTO userDTO = userService.userSignUp(signUpRequest);
        return ResponseEntity.ok(userDTO);
    }

    @RateLimited
    @PostMapping("/log-in")
    public ResponseEntity<String> userLogIn(@RequestBody LogInRequest logInRequest) {
        String userToken  = userService.userLogIn(logInRequest);
        return ResponseEntity.ok(userToken);
    }
    
}
