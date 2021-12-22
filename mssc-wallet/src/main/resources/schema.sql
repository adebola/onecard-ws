create table accounts (
    id VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    balance DECIMAL(8,2) DEFAULT 0,
    user_id VARCHAR(64) NOT NULL,
    account_type int NOT NULL DEFAULT 1,
    activated BOOLEAN DEFAULT FALSE,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    threshold_level decimal(8, 2) NOT NULL DEFAULT 0,
    UNIQUE idx_user_id(user_id),
    PRIMARY KEY (id)
);

create table transactions (
    id int AUTO_INCREMENT NOT NULL,
    account_id VARCHAR(64) NOT NULL,
    counterparty_id VARCHAR(64) NOT NULL,
    service_id VARCHAR(32) NOT NULL,
    tx_datetime timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    tx_amount DECIMAL(8,2) NOT NULL ,
    tx_narrative VARCHAR(256),
    tx_status VARCHAR(16) NOT NULL,
    threshold_level DECIMAL(8,2),
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (counterparty_id) REFERENCES accounts(id),
    PRIMARY KEY (id)
);

create table alert_users (
    user_id VARCHAR(64) NOT NULL,
    threshold_level DECIMAL(8,2) NOT NULL
);
