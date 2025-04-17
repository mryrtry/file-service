package org.mryrt.file_service.Auth.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.mryrt.file_service.Auth.Annotation.ValidSignUpRequest;
import org.mryrt.file_service.Auth.Model.SignUpRequest;
import org.mryrt.file_service.Auth.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class SignUpRequestValidator implements ConstraintValidator<ValidSignUpRequest, SignUpRequest> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(SignUpRequest signUpRequest, ConstraintValidatorContext context) {
        return (RequestValidator.validateUsername(userRepository, signUpRequest.getUsername(), false)
                & RequestValidator.validatePassword(signUpRequest.getPassword()));
    }

}