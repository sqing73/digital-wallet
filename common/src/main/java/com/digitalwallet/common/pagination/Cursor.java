package com.digitalwallet.common.pagination;

import java.time.Instant;

public record Cursor(Instant createdAt, String id) {
    
}
