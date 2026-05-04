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
            String correctHash = passwordEncoder.encode("Admin@123");
            admin.setPassword(correctHash);
            userRepository.save(admin);
            log.info("Admin password initialised");
        });
    }
}
