create table accounts (
    id VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    balance DECIMAL(8,2) DEFAULT 0,
    user_id VARCHAR(64) NOT NULL,
    account_type int NOT NULL DEFAULT 1,
    activated BOOLEAN DEFAULT FALSE,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    anonymous BOOLEAN DEFAULT FALSE,
    threshold_level decimal(8, 2) NOT NULL DEFAULT 0,
    UNIQUE idx_user_id(user_id),
    PRIMARY KEY (id)
);

create table transactions (
    id int AUTO_INCREMENT NOT NULL,
    account_id VARCHAR(64),
    service_id INT NOT NULL,
    service_name VARCHAR(64) NOT NULL,
    tx_datetime timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    tx_amount DECIMAL(8,2) NOT NULL,
    recharge_request_id VARCHAR(64) NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    PRIMARY KEY (id)
);

create table alert_users (
    user_id VARCHAR(64) NOT NULL,
    threshold_level DECIMAL(8,2) NOT NULL
);
