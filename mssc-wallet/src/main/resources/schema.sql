create table accounts (
    id VARCHAR(64) NOT NULL,
    name VARCHAR(128) NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0,
    user_id VARCHAR(64) NOT NULL,
    account_type int NOT NULL DEFAULT 1,
    activated BOOLEAN DEFAULT FALSE,
    charge_account VARCHAR(64),
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    anonymous BOOLEAN DEFAULT FALSE,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_date timestamp,
    deleted_by varchar(64),
    threshold_level decimal(10, 2) NOT NULL DEFAULT 0,
    web_hook varchar(128),
    FOREIGN KEY (charge_account) REFERENCES accounts(id),
    UNIQUE idx_user_id(user_id),
    PRIMARY KEY (id)
);

create table transactions (
    id int AUTO_INCREMENT NOT NULL,
    account_id VARCHAR(64),
    service_id INT NOT NULL DEFAULT 0,
    service_name VARCHAR(64) NOT NULL,
    tx_datetime timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    tx_amount DECIMAL(10,2) NOT NULL,
    recharge_request_id VARCHAR(64) NOT NULL,
    recipient varchar(64),
    charge_account_id varchar(64),
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    PRIMARY KEY (id)
);

create table alert_users (
    user_id VARCHAR(64) NOT NULL,
    threshold_level DECIMAL(10,2) NOT NULL
);

create table fund_wallet_request (
    id varchar(64) NOT NULL,
    user_id varchar(64) NOT NULL,
    authorization_url varchar(64),
    redirect_url varchar(64),
    amount decimal(10,2) NOT NULL,
    status int,
    message varchar(64),
    payment_id varchar(64) NOT NULL,
    payment_verified BOOLEAN DEFAULT FALSE,
    created_on timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    closed BOOLEAN DEFAULT FALSE
);

create table adjustments(
    id varchar(64) NOT NULL,
    fund_wallet_request_id varchar(64) NOT NULL,
    adjusted_value DECIMAL(10, 2) NOT NULL,
    previous_value DECIMAL(10, 2) NOT NULL,
    adjusted_on timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    adjusted_by varchar(64) NOT NULL,
    account_id varchar(64) NOT NULL,
    narrative varchar(512),
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (fund_wallet_request_id) REFERENCES fund_wallet_request(id),
    PRIMARY KEY (id)
);

