
-- AUTHORIZATION SERVER DATABASE
create table users (
    id int(11) AUTO_INCREMENT,
    username varchar(64) NOT NULL,
    email varchar(64) NOT NULL,
    password varchar(64) NOT NULL,
    firstname varchar(32) NOT NULL,
    lastname varchar(32) NOT NULL,
    telephone varchar(64),
    organization varchar(64),
    activated boolean NOT NULL DEFAULT FALSE,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP,
    lastModifiedAt timestamp DEFAULT NULL,
    PRIMARY KEY(id),
    UNIQUE KEY idx_username_tenant(username),
    UNIQUE KEY idx_email_tenant(email)
);


create table roles (
    id int(11) AUTO_INCREMENT,
    name varchar(32) NOT NULL,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP,
    lastModifiedAt timestamp DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY idx_name_tenant(name)
);


create table services (
    id varchar(64) NOT NULL,
    service_name varchar(64) NOT NULL,
    created timestamp DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

create table user_roles (
    user_id int(11) NOT NULL,
    role_id int(11) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    UNIQUE KEY _idx_user_role(user_id, role_id)
);
