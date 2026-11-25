package com.digitalwallet.common.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public final class EventPayloads {
    private EventPayloads() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Party(
            String walletId,
            String userId,
            String displayName) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TransferCompleted(
            String transferId,
            long amount,
            String currency,
            String note,
            Party from,
            Party to) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DepositSettled(
            String depositId,
            String walletId,
            String userId,
            long amount,
            String currency,
            String failureReason) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WithdrawalRequested(
            String withdrawalId,
            String walletId,
            String userId,
            long amount,
            String currency) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WithdrawalSettled(
            String withdrawalId,
            String walletId,
            String userId,
            long amount,
            String currency,
            String failureReason) {
    }
}
