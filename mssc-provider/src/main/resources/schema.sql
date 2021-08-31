create table provider_categories (
    id int AUTO_INCREMENT,
    category_name varchar(32) NOT NULL,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY idx_category_name(category_name)
);

create table providers (
    id int AUTO_INCREMENT,
    category_id int NOT NULL,
    status VARCHAR(16) DEFAULT 'UNAPPROVED',
    name varchar(32) NOT NULL,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    activated boolean DEFAULT FALSE,
    activation_date timestamp,
    code varchar(16) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY idx_code(code),
    FOREIGN KEY (category_id) REFERENCES provider_categories(id)
);

create table provider_services (
    id int AUTO_INCREMENT,
    provider_id int NOT NULL,
    service_name varchar(32) NOT NULL,
    service_cost decimal(8,2) DEFAULT 0,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    activated boolean DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY idx_service_name(service_name),
    FOREIGN KEY (provider_id) REFERENCES providers(id)
);
