create table payment
(
    id                varchar(64)                         NOT NULL,
    amount            DECIMAL(10, 2)                      NOT NULL,
    authorization_url varchar(64),
    redirect_url      varchar(64),
    access_code       varchar(64),
    reference         varchar(64),
    message           varchar(64),
    status            varchar(64),
    verified          boolean   DEFAULT FALSE,
    payment_created   timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    payment_verified  timestamp,
    payment_mode      varchar (32)                        NOT NULL,
    balance           decimal(10, 2),
    INDEX access_index_idx (access_code),
    PRIMARY KEY (id)
);

create table refund (
    id                       varchar(64) NOT NULL ,
    payment_id               varchar(64) NOT NULL,
    amount                   DECIMAL(10,2) NOT NULL,
    refunded_on              timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    refunded_by              varchar(64) NOT NULL,
    fund_wallet_request_id   varchar(64) NOT NULL,
    FOREIGN KEY (payment_id) REFERENCES payment(id),
    PRIMARY KEY (id)
);
