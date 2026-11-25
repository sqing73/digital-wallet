package com.digitalwallet.common.pagination;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.digitalwallet.common.error.ApiException;
import com.digitalwallet.common.error.FieldErrorDetail;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class CursorCodec {

    public static final int DEFAULT_LIMIT = 20;
    public static final int MAX_LIMIT = 100;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private CursorCodec() {
    }

    public static int resolveLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "VALIDATION_FAILED",
                    "Validation failed",
                    "limit must be between 1 and " + MAX_LIMIT + ".",
                    List.of(new FieldErrorDetail("limit", "Must be between 1 and " + MAX_LIMIT)),
                    null);
        }
        return limit;
    }

    public static String encode(Instant createdAt, String id) {
        try {
            String json = MAPPER.writeValueAsString(Map.of("t", createdAt.toString(), "i", id));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to encode cursor", ex);
        }
    }

    public static Cursor decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cursor);
            JsonNode node = MAPPER.readTree(decoded);
            JsonNode tNode = node.get("t");
            JsonNode iNode = node.get("i");
            if (tNode == null || iNode == null || !tNode.isTextual() || !iNode.isTextual()) {
                throw invalidCursor();
            }
            Instant createdAt = Instant.parse(tNode.asText());
            String id = iNode.asText();
            if (id.isBlank()) {
                throw invalidCursor();
            }
            return new Cursor(createdAt, id);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw invalidCursor();
        }
    }

    private static ApiException invalidCursor() {
        return new ApiException(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_FAILED",
                "Validation Failed",
                "The cursor parameter is invalid.",
                List.of(new FieldErrorDetail("cursor", "Invalid cursor")),
                null);
    }
}
