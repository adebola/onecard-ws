create table cluster(
    id varchar(64) NOT NULL,
    name varchar(64) NOT NULL,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    amount decimal(12,2) NOT NULL,
    balance decimal(12,2) NOT NULL,
    activated boolean DEFAULT FALSE,
    activation_date timestamp,
    activatedBy varchar(64),
    description varchar(255),
    suspended boolean DEFAULT FALSE,
    PRIMARY KEY (id)
);

create trigger `cluster_before_insert` before insert on `cluster` for each row
    begin
        if new.id is null then
            set new.id = uuid();
        end if;
    end;

create table batch (
    id varchar(64),
    cluster_id varchar(64) NOT NULL,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    denomination decimal(8,2) NOT NULL,
    voucher_count int NOT NULL,
    activated boolean DEFAULT FALSE,
    activation_date timestamp,
    activatedBy varchar(64),
    expiry_date timestamp NOT NULL,
    suspended boolean DEFAULT FALSE,
    FOREIGN KEY (cluster_id) REFERENCES cluster(id),
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
    activatedBy varchar(64),
    suspended boolean DEFAULT FALSE NOT NULL,
    PRIMARY KEY (id),
    UNIQUE idx_serial_number (serial_number),
    FOREIGN KEY (batch_id) REFERENCES batch(id)
);

CREATE TRIGGER `dateinsert` BEFORE INSERT ON `voucher`
FOR EACH ROW
SET NEW.expiry_date =  DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL 30 DAY);

CREATE PROCEDURE `sp_suspend_cluster` (IN clusterId varchar(64) )
BEGIN
    DECLARE batchId varchar(64);
    DECLARE done INT DEFAULT FALSE;
    DECLARE batch_cursor CURSOR FOR SELECT id from batch where cluster_id = clusterId;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    update cluster set suspended = true where id = clusterId;
    update batch set suspended = true where cluster_id = clusterId;

    OPEN batch_cursor;

    read_loop: LOOP
        FETCH batch_cursor INTO batchId;
        IF done THEN
            LEAVE read_loop;
        END IF;
        update voucher set suspended = true where batch_id = batchId;
    END LOOP;

    CLOSE batch_cursor;
END;

CREATE PROCEDURE `sp_unsuspend_cluster` (IN clusterId varchar(64) )
BEGIN
    DECLARE batchId varchar(64);
    DECLARE done INT DEFAULT FALSE;
    DECLARE batch_cursor CURSOR FOR SELECT id from batch where cluster_id = clusterId;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    update cluster set suspended = false where id = clusterId;
    update batch set suspended = false where cluster_id = clusterId;

    OPEN batch_cursor;

    read_loop: LOOP
        FETCH batch_cursor INTO batchId;
        IF done THEN
            LEAVE read_loop;
        END IF;
        update voucher set suspended = false where batch_id = batchId;
    END LOOP;

    CLOSE batch_cursor;
END;



