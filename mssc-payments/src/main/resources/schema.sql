create table payment
(
    id                varchar(64)                         NOT NULL,
    amount            DECIMAL(10, 2)                      NOT NULL,
    status            boolean                             NOT NULL,
    message           varchar(255)                        NOT NULL,
    authorization_url varchar(64)                         NOT NULL,
    redirect_url      varchar(64),
    access_code       varchar(64)                         NOT NULL,
    reference         varchar(64)                         NOT NULL,
    verified          boolean   DEFAULT FALSE,
    payment_created   timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    payment_verified  timestamp,
    INDEX access_index_idx (access_code),
    PRIMARY KEY (id)
);
