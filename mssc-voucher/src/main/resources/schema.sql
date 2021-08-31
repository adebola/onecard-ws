create table batch (
    id varchar(64),
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    denomination decimal(8,2) NOT NULL,
    count int NOT NULL,
    activated boolean DEFAULT FALSE,
    activation_date timestamp,
    expiry_date timestamp NOT NULL,
    PRIMARY KEY (id)
);

CREATE TRIGGER `batch_before_insert`
    BEFORE INSERT ON `batch` FOR EACH ROW
BEGIN
    IF new.id IS NULL THEN
        SET new.id = uuid();
    END IF;

    IF new.expiry_date is NULL THEN
        SET NEW.expiry_date =  DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL 30 DAY);
    END IF;
END;

create table voucher (
    id int(11) AUTO_INCREMENT,
    code varchar(64) NOT NULL,
    denomination decimal(8,2) NOT NULL,
    batch_id int(11) NOT NULL,
    activated boolean DEFAULT FALSE,
    tx_code varchar(32) NULL,
    expiry_date timestamp NOT NULL,
    activation_date timestamp,
    deleted boolean DEFAULT FALSE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (batch_id) REFERENCES batch(id)
);

CREATE TRIGGER dateinsert BEFORE INSERT ON voucher
FOR EACH ROW
SET NEW.expiry_date =  DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL 30 DAY);


create table changes (
        id int(11) NOT NULL,
        name varchar(16) NOT NULL,
        PRIMARY KEY (id),
        UNIQUE KEY _idx_change(name)
);

create table ammendments (
    id int(11) AUTO_INCREMENT,
    voucher_id int(11) NOT NULL,
    change_id int (11) NOT NULL,
    from_narrative varchar(64) NOT NULL,
    to_narrative varchar(64) NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (voucher_id) REFERENCES voucher(id),
    FOREIGN KEY (change_id) REFERENCES changes(id)
);
