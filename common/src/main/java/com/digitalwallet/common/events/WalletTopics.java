package com.digitalwallet.common.events;

public class WalletTopics {
    private WalletTopics() {}

    public static final String WALLET_EVENTS = "wallet.events";
    public static final String WALLET_EVENTS_DLT = "wallet.events.dlt";

    public static final int CURRENT_VERSION = 1;

    public static final String TRANSFER_COMPLETED = "transfer.completed";
    public static final String DEPOSIT_SUCCEEDED = "deposit.succeeded";
    public static final String DEPOSIT_FAILED = "deposit.failed";
    public static final String WITHDRAWAL_REQUESTED = "withdrawal.requested";
    public static final String WITHDRAWAL_SUCCEEDED = "withdrawal.succeeded";
    public static final String WITHDRAWAL_FAILED = "withdrawal.failed";
}
