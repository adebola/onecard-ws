create table organizations (
  id varchar(64),
  organization_name varchar(128),
  wallet_id VARCHAR(64),
  created_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  created_by varchar(64) NOT NULL,
  PRIMARY KEY (id)
);

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
    secret varchar(128),
    organization_id varchar(64),
    profile_picture varchar(1024),
    FOREIGN KEY (organization_id) REFERENCES organizations (id),
    UNIQUE idx_user_name(user_name),
    UNIQUE idx_email(email),
    PRIMARY KEY (id)
);

create table roles (
    id varchar(64) NOT NULL,
    role_name varchar(64) NOT NULL,
    PRIMARY KEY (id)
);

create table user_roles (
    role_id varchar(64) NOT NULL,
    user_id varchar(64) NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
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

insert into roles(id, role_name)
values ('e9ce2988-5a5a-4dd7-9a58-419397301f91', 'Onecard_Admin'),
       ('9a3e18db-41f3-478d-a6e9-484e89951022', 'Onecard_Operator'),
       ('55a710f0-d038-4e4e-9de5-d2ebe4644875', 'Onecard_Agent');
