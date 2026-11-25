package com.digitalwallet.common.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WalletEventEnvelope<T>(
    String eventId,
    String type,
    int version,
    Instant occurredAt,
    String aggregateId,
    T payload
) {
    
}
