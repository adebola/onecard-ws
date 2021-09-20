create table batch (
    id varchar(64),
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    denomination decimal(8,2) NOT NULL,
    voucher_count int NOT NULL,
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
    serial_number varchar (32) NOT NULL,
    code varchar(64) NOT NULL,
    denomination decimal(8,2) NOT NULL,
    batch_id varchar(64) NOT NULL,
    activated boolean DEFAULT FALSE,
    tx_code varchar(32) NULL,
    expiry_date timestamp NOT NULL,
    activation_date timestamp,
    deleted boolean DEFAULT FALSE NOT NULL,
    PRIMARY KEY (id),
    UNIQUE idx_serial_number (serial_number),
    FOREIGN KEY (batch_id) REFERENCES batch(id)
);

CREATE TRIGGER dateinsert BEFORE INSERT ON voucher
FOR EACH ROW
SET NEW.expiry_date =  DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL 30 DAY);



