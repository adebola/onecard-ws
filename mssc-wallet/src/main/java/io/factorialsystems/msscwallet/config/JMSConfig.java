package io.factorialsystems.msscwallet.config;

public class JMSConfig {
    public static final String NEW_USER_QUEUE = "UserQueue";
    public static final String UPDATE_USER_WALLET_QUEUE = "update-user-wallet";
    public static final String NEW_USER_WALLET_QUEUE = "new-user-wallet";
    public static final String AUDIT_MESSAGE_QUEUE = "audit-message-queue";
    public static final String NEW_TRANSACTION_QUEUE = "new-transaction-queue";
    public static final String NEW_RECHARGE_PROVIDER_WALLET_QUEUE = "new-recharge-provider-wallet";
    public static final String DELETE_ACCOUNT_QUEUE = "delete-account";

    public static final String ADD_ORGANIZATION_ACCOUNT_QUEUE = "add-organization";
    public static final String REMOVE_ORGANIZATION_ACCOUNT_QUEUE = "remove-organization";
}
