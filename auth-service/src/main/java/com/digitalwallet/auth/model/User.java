package com.digitalwallet.auth.model;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public record User(
    String id,
    String username,
    String passwordHash,
    String displayName,
    List<String> roles,
    Instant createdAt
) {
    public String rolesAsString() {
        return String.join(",", roles);
    }

    public static List<String> parseRoles(String rolesCsv) {
        if (rolesCsv == null || rolesCsv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(rolesCsv.split(",")).map(String::trim).toList();
    }
}
