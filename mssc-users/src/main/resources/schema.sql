create table users (
    id VARCHAR(64) NOT NULL,
    user_name VARCHAR(64) NOT NULL,
    first_name VARCHAR(64),
    last_name VARCHAR(64),
    email VARCHAR(64) NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    created_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    wallet_id VARCHAR(64),
    UNIQUE idx_user_name(user_name),
    PRIMARY KEY (id)
);

create table beneficiary (
    id int AUTO_INCREMENT NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    first_name varchar(64),
    last_name varchar(64),
    email varchar(64),
    telephone varchar(64) NOT NULL,
    created_on timestamp DEFAULT CURRENT_TIMESTAMP,
    UNIQUE idx_user_id_telephone(user_id, telephone),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

create table beneficiary_group (
    id int AUTO_INCREMENT NOT NULL,
    group_name VARCHAR(64) NOT NULL,
    group_owner VARCHAR(64) NOT NULL,
    created_on timestamp DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_owner) REFERENCES users (id),
    PRIMARY KEY (id)
);

create table beneficiary_group_users (
    beneficiary_id int NOT NULL,
    beneficiary_group_id int NOT NULL,
    FOREIGN KEY (beneficiary_id) REFERENCES beneficiary (id),
    FOREIGN KEY (beneficiary_group_id) REFERENCES beneficiary_group (id),
    UNIQUE idx_id_group_id(beneficiary_id, beneficiary_group_id)
);
