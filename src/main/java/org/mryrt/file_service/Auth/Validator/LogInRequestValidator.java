package org.mryrt.file_service.Auth.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.mryrt.file_service.Auth.Annotation.ValidLogInRequest;
import org.mryrt.file_service.Auth.Model.LogInRequest;
import org.mryrt.file_service.Auth.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class LogInRequestValidator implements ConstraintValidator<ValidLogInRequest, LogInRequest> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(LogInRequest logInRequest, ConstraintValidatorContext context) {
        return (RequestValidator.validateUsername(userRepository, context, logInRequest.getUsername(), true)
                & RequestValidator.validatePassword(context, logInRequest.getPassword()));
    }

}