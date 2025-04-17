package org.mryrt.file_service.Utility.Confiruration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Configuration
@EnableJpaAuditing
public class JPAConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Validator passwordValidator() {
        return new Validator() {
            @Override
            public boolean supports(@NonNull Class<?> clazz) {
                return String.class.isAssignableFrom(clazz);
            }

            @Override
            public void validate(@NonNull Object target, @NonNull Errors errors) {
                String password = (String) target;
                if (password.length() < 5) {
                    errors.rejectValue("password", "Password too short");
                }
            }
        };
    }


}