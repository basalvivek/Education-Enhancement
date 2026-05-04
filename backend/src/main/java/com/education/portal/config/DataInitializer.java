package com.education.portal.config;

import com.education.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        userRepository.findByEmail("admin@education.com").ifPresent(admin -> {
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            userRepository.save(admin);
            log.info("admin@education.com password initialised");
        });

        userRepository.findByEmail("basalvivek@gmail.com").ifPresent(user -> {
            user.setRole(com.education.portal.model.User.Role.ADMIN);
            user.setPassword(passwordEncoder.encode("basal123$"));
            userRepository.save(user);
            log.info("basalvivek@gmail.com promoted to ADMIN with updated password");
        });
    }
}
