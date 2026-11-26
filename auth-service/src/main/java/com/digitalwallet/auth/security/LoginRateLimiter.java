package com.digitalwallet.auth.security;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.digitalwallet.common.error.ApiException;

@Component
public class LoginRateLimiter {

    private final int maxAttempts;
    private final long windowSeconds;
    private final ConcurrentHashMap<String, Deque<Instant>> attemptsByUsername = new ConcurrentHashMap<>();

    public LoginRateLimiter(
            @Value("${app.login-rate-limit.max-attempts:5}") int maxAttempts,
            @Value("${app.login-rate-limit.window-seconds:60}") long windowSeconds) {
        this.maxAttempts = maxAttempts;
        this.windowSeconds = windowSeconds;
    }

    public void checkAndRecord(String username) {
        String key = username == null ? "" : username.toLowerCase();
        Instant now = Instant.now();
        Instant cutoff = now.minusSeconds(windowSeconds);

        Deque<Instant> timestamps = attemptsByUsername.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && !timestamps.peekFirst().isAfter(cutoff)) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= maxAttempts) {
                Instant oldest = timestamps.peekFirst();
                long retryAfter = Math.max(1, Duration.between(now, oldest.plusSeconds(windowSeconds)).toSeconds() + 1);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfter));
                throw new ApiException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "RATE_LIMITED",
                        "Too many login attempts",
                        "Too many login attempts for this username. Please try again later",
                        null,
                        headers);
            }
            timestamps.addLast(now);
        }
    }
}
