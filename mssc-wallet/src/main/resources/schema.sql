DELIMITER $$
CREATE PROCEDURE sp_GetActiveAccountByUserIdForUpdate(IN userId varchar(64))
BEGIN
    DECLARE chargeAccount varchar(64);

    IF EXISTS(SELECT 1 from accounts where user_id = userId and deleted = FALSE)  THEN
        select charge_account from accounts where user_id = userId and deleted = FALSE into chargeAccount;
        IF (chargeAccount IS NULL) THEN
            SELECT id, name, balance, user_id, account_type, activated, createdAt, createdBy, charge_account, web_hook, kyc_verified, daily_limit
            FROM accounts
            WHERE user_id = userId
              AND deleted = FALSE
                FOR UPDATE;
        ELSE
            SELECT id, name, balance, user_id, account_type, activated, createdAt, createdBy, charge_account, web_hook, kyc_verified, daily_limit
            FROM accounts
            WHERE id = chargeAccount
                FOR UPDATE;
        END IF;
    ELSE
        SELECT 1 from dual WHERE false;
    END IF;
END$$
DELIMITER ;

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
    kyc_verified BOOLEAN NOT NULL DEFAULT FALSE,
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

alter table accounts add column kyc_verified BOOLEAN NOT NULL DEFAULT FALSE;
alter table accounts add column telephone varchar(32);
alter table accounts add column daily_limit decimal(10,2) DEFAULT 50000 NOT NULL;

create table sms_verification (
                                  id varchar(36) NOT NULL,
                                  code varchar(16) NOT NULL,
                                  account_id varchar(64) NOT NULL,
                                  expiry timestamp NOT NULL,
                                  msisdn varchar(32) NOT NULL,
                                  verified bool NOT NULL default FALSE,
                                  verified_on TIMESTAMP,
                                  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                  FOREIGN KEY (account_id) REFERENCES accounts(id),
                                  PRIMARY KEY (id)
);

create table bvn_verification(
                                 id varchar(36) NOT NULL,
                                 bvn varchar(64) NOT NULL,
                                 account_id varchar(64) NOT NULL,
                                 first_name varchar(64),
                                 last_name varchar(64),
                                 middle_name varchar(64),
                                 date_of_birth varchar(64),
                                 phone_number varchar(32),
                                 verified bool NOT NULL default FALSE,
                                 verified_on TIMESTAMP,
                                 created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                 FOREIGN KEY (account_id) REFERENCES accounts(id),
                                 PRIMARY KEY (id)
);

create table account_verification_log (
                                          id int AUTO_INCREMENT NOT NULL,
                                          account_id varchar(64) NOT NULL,
                                          sms_verification_id varchar(36)NOT NULL,
                                          bvn_verification_id varchar(36) NOT NULL,
                                          verified_by varchar(64) NOT NULL,
                                          verified_on timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                          FOREIGN KEY (sms_verification_id) REFERENCES sms_verification(id),
                                          FOREIGN KEY (bvn_verification_id) REFERENCES bvn_verification(id),
                                          FOREIGN KEY (account_id) REFERENCES accounts(id),
                                          PRIMARY KEY (id)
);


create table account_settings (
                                  id int NOT NULL,
                                  short_name varchar(16) NOT NULL,
                                  name varchar(64) NOT NULL,
                                  value varchar(64) NOT NULL,
                                  PRIMARY KEY (id)
);

insert into account_settings (id, short_name, name, value)
values (1, 'userLimit', 'Daily Un-Verified User Limit', '50000'),
       (2, 'corporateLimit', 'Daily Un-Verified Organization Limit', '100000'),
       (3, 'enabled', 'Daily Limits Enabled', '0'),
       (4, 'firstName', 'First Name in BVN Search', '1'),
       (5, 'lastName', 'Last Name in BVN Search', '1'),
       (6, 'telephone', 'Telephone Number in BVN Search', '0');

create table account_ledger (
                                id varchar(64) NOT NULL,
                                account_id varchar(64) NOT NULL,
                                amount DECIMAL(10, 2) NOT NULL,
                                created_on timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                operation int NOT NULL,
                                description varchar(128),
                                FOREIGN KEY (account_id) REFERENCES accounts(id),
                                PRIMARY KEY (id)
);

