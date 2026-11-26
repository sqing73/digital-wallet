package com.digitalwallet.auth.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.digitalwallet.auth.model.User;
import com.digitalwallet.auth.repository.UserRepository;
import com.digitalwallet.auth.security.JwtService;
import com.digitalwallet.auth.security.LoginRateLimiter;
import com.digitalwallet.common.error.ApiException;
import com.digitalwallet.common.error.FieldErrorDetail;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginRateLimiter rateLimiter;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            LoginRateLimiter rateLimiter) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.rateLimiter = rateLimiter;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String password, String displayName) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "VALIDATION_FAILED",
                    "Validation failed",
                    "Password must be at most 72 bytes in UTF-8",
                    List.of(new FieldErrorDetail("password", "Must be at most 72 bytes in UTF-8")),
                    null);
        }

        User user = new User(
                UUID.randomUUID().toString(),
                username,
                passwordEncoder.encode(password),
                displayName,
                List.of("USER"),
                Instant.now());

        try {
            userRepository.insert(user);
        } catch (DuplicateKeyException ex) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "USERNAME_TAKEN",
                    "Username taken",
                    "The username '" + username + "' is already registered.");
        }
        return user;
    }

    public LoginResult login(String username, String password) {
        rateLimiter.checkAndRecord(username);

        User user = userRepository.findByUsername(username).orElseThrow(AuthService::invalidCredentials);
        if (!passwordEncoder.matches(password, user.passwordHash())) {
            throw invalidCredentials();
        }

        String token = jwtService.issueAccessToken(user.id(), user.username(), user.displayName(), user.roles());

        return new LoginResult(token, "Bearer", jwtService.expirationSeconds());
    }

    private static ApiException invalidCredentials() {
        return new ApiException(HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "Invalid credentials",
                "Invalid username or password.");
    }

    public record LoginResult(String accessToken, String tokenType, long expiresIn) {
    }
}
