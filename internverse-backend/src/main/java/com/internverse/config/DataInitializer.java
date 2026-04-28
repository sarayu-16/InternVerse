package com.internverse.config;

import com.internverse.model.Role;
import com.internverse.model.User;
import com.internverse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds a default admin account when the database is empty (local development).
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedAdmin() {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }
            User admin = User.builder()
                    .name("System Admin")
                    .email("admin@internverse.local")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .college("InternVerse")
                    .skillsJson("[]")
                    .build();
            userRepository.save(admin);
            log.info("Seeded default admin: admin@internverse.local / Admin@123");
        };
    }
}
