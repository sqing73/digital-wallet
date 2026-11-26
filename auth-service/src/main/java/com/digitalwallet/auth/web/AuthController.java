package com.digitalwallet.auth.web;

import com.digitalwallet.auth.model.User;
import com.digitalwallet.auth.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public record RegisterRequest(
            @NotBlank @Pattern(regexp = "^[a-z0-9_]{3,20}$", message = "Must be 3-20 lowercase letters, digits, or underscore") String username,
            @NotBlank @Size(min = 8, max = 72, message = "Must be between 8 and 72 characters") String password,
            @NotBlank @Size(min = 1, max = 50, message = "Must be between 1 and 50 characters") String displayName) {
    }

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password) {
    }

    public record UserResponse(
            String id,
            String username,
            String displayName,
            List<String> roles) {
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request.username(), request.password(), request.displayName());
        return new UserResponse(user.id(), user.username(), user.displayName(), user.roles());
    }

    @PostMapping("/login")
    public AuthService.LoginResult login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.username(), request.password());
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return new UserResponse(
                jwt.getSubject(),
                jwt.getClaimAsString("username"),
                jwt.getClaimAsString("name"),
                jwt.getClaimAsStringList("roles"));
    }
}
