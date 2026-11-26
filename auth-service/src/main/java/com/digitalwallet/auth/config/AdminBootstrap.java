package com.digitalwallet.auth.config;

import java.util.List;
import java.util.UUID;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.digitalwallet.auth.model.User;
import com.digitalwallet.auth.repository.UserRepository;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminBootstrap(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username}") String adminUsername,
            @Value("${app.admin.password}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User admin = new User(
                    UUID.randomUUID().toString(),
                    adminUsername,
                    passwordEncoder.encode(adminPassword),
                    "Administrator",
                    List.of("USER", "ADMIN"),
                    Instant.now());
            userRepository.insert(admin);
        }
    }
}
