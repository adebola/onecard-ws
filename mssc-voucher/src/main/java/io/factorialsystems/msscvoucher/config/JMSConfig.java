package io.factorialsystems.msscvoucher.config;

public class JMSConfig {
    public static final String NEW_USER_QUEUE = "UserQueue";
    public static final String UPDATE_USER_WALLET_QUEUE = "update-user-wallet";
    public static final String NEW_USER_WALLET_QUEUE = "new-user-wallet";
    public static final String AUDIT_MESSAGE_QUEUE = "audit-message-queue";
    public static final String BATCH_SUSPEND_QUEUE = "batch-suspend-command";
    public static final String BATCH_UNSUSPEND_QUEUE = "batch-unsuspend-queue";
}
