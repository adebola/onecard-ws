drop procedure sp_factory;
drop procedure sp_addRechargeProvider;
drop table provider_services_recharge_providers;
drop table recharge_requests;
drop table provider_services;
drop table providers;
drop table provider_categories;
drop table recharge_providers;
drop table service_actions;


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
    name varchar(32) NOT NULL,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    activated boolean DEFAULT FALSE,
    activation_date timestamp,
    activatedBy varchar(64),
    code varchar(16) NOT NULL,
    suspended boolean DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY idx_code(code),
    FOREIGN KEY (category_id) REFERENCES provider_categories(id)
);

create table service_actions (
    id int AUTO_INCREMENT,
    action varchar(16) NOT NULL,
    fixed_price boolean DEFAULT FALSE,
    UNIQUE KEY idx_action (action),
    PRIMARY KEY (id)
);

create table provider_services (
    id int AUTO_INCREMENT,
    provider_id int NOT NULL,
    service_code varchar(32) NOT NULL,
    service_name varchar(64) NOT NULL,
    service_cost decimal(10,2),
    action int NOT NULL,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    activated boolean DEFAULT FALSE,
    activation_date timestamp,
    activatedBy varchar(64),
    suspended boolean DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY idx_service_code(service_code),
    FOREIGN KEY (action) REFERENCES service_actions(id),
    FOREIGN KEY (provider_id) REFERENCES providers(id)
);

create table recharge_providers (
    id int AUTO_INCREMENT,
    name varchar(64) NOT NULL,
    wallet_id varchar(64),
    code varchar(16) NOT NULL,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createdBy varchar(64) NOT NULL,
    activated boolean DEFAULT FALSE,
    activation_date timestamp,
    activatedBy varchar(64),
    suspended boolean DEFAULT FALSE,
    UNIQUE KEY idx_code(code),
    PRIMARY KEY (id)
);

create table provider_services_recharge_providers (
    provider_service_id int NOT NULL,
    recharge_provider_id int NOT NULL,
    weight int NOT NULL DEFAULT 1,
    FOREIGN KEY (provider_service_id) REFERENCES provider_services(id),
    FOREIGN KEY (recharge_provider_id) REFERENCES recharge_providers(id),
    UNIQUE KEY idx_provider_recharge_service (provider_service_id, recharge_provider_id)
);

create table recharge_requests (
    id varchar(64),
    user_id varchar(64),
    service_id int NOT NULL,
    service_cost decimal(10,2) NOT NULL,
    recipient varchar(64) NOT NULL,
    telephone varchar(64),
    product_id varchar(64),
    payment_id varchar(64),
    authorization_url varchar(64),
    redirect_url varchar(64),
    message varchar(64),
    status int,
    account_type varchar(64),
    closed boolean NOT NULL DEFAULT FALSE,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    payment_mode varchar(32) NOT NULL,
    scheduled_request_id varchar(64),
    auto_request_id varchar(64),
    FOREIGN KEY (scheduled_request_id) REFERENCES scheduled_recharge(id),
    FOREIGN KEY (auto_request_id) REFERENCES auto_recharge(id),
    FOREIGN KEY (service_id) REFERENCES provider_services(id),
    PRIMARY KEY (id)
);

create table ringo_data_plans (
    product_id varchar(16) NOT NULL,
    network varchar(32) NOT NULL,
    category varchar(32) NOT NULL,
    price decimal(10,2) NOT NULL,
    code varchar(32) NOT NULL,
    validity varchar(32) NOT NULL,
    allowance varchar(64),
    PRIMARY KEY (product_id)
);

create table bulk_recharge_requests (
    id varchar(64),
    user_id varchar(64),
    service_id int NOT NULL,
    service_cost decimal(10,2) NOT NULL,
    total_service_cost decimal(10,2) NOT NULL,
    group_id int,
    product_id varchar(64),
    payment_id varchar(64),
    payment_mode varchar(32) NOT NULL,
    authorization_url varchar(64),
    redirect_url varchar(64),
    closed boolean NOT NULL DEFAULT FALSE,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    scheduled_request_id varchar(64),
    auto_request_id varchar(64),
    FOREIGN KEY (scheduled_request_id) REFERENCES scheduled_recharge(id),
    FOREIGN KEY (auto_request_id) REFERENCES auto_recharge(id),
    FOREIGN KEY (service_id) REFERENCES provider_services(id),
    PRIMARY KEY (id)
);

create table recharge_request_recipients (
    bulk_recharge_request_id varchar(64),
    scheduled_recharge_request_id varchar(64),
    auto_recharge_request_id varchar(64),
    msisdn varchar(64) NOT NULL,
    INDEX idx_bulk_recharge_request_id (bulk_recharge_request_id),
    INDEX idx_scheduled_recharge_request_id(scheduled_recharge_request_id),
    INDEX idx_auto_recharge_request_id(auto_recharge_request_id),
    FOREIGN KEY (auto_recharge_request_id) REFERENCES auto_recharge(id),
    FOREIGN KEY (scheduled_recharge_request_id) REFERENCES scheduled_recharge (id),
    FOREIGN KEY (bulk_recharge_request_id) REFERENCES bulk_recharge_requests (id)
);

create table scheduled_recharge (
    id varchar(64),
    user_id varchar(64),
    request_id int,
    request_type int DEFAULT 1 NOT NULL,
    request_scheduled_date timestamp NOT NULL,
    service_id int NOT NULL,
    service_cost decimal(10, 2),
    total_service_cost decimal (10,2),
    group_id int,
    recipient varchar(32),
    product_id varchar(32),
    telephone varchar (32),
    redirect_url varchar(64),
    authorization_url varchar(64),
    payment_mode varchar(16),
    payment_id varchar(64),
    message varchar(64),
    status int,
    request_created_on timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    request_ran_on timestamp,
    closed boolean NOT NULL DEFAULT FALSE,
    FOREIGN KEY (service_id) REFERENCES provider_services(id),
    PRIMARY KEY (id)
);

create table auto_recharge (
    id varchar(64),
    user_id varchar(64),
    request_type int DEFAULT 1 NOT NULL,
    request_start timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    request_end timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    request_created timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    request_start_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    request_end_date timestamp,
    service_id int NOT NULL,
    group_id int,
    recipient varchar(32),
    product_id varchar(32),
    telephone varchar (32),
    service_cost decimal(10, 2),
    redirect_url varchar(64),
    payment_mode varchar(16),
    closed boolean NOT NULL DEFAULT FALSE,
    FOREIGN KEY (service_id) REFERENCES provider_services(id),
    PRIMARY KEY (id)
);

create procedure sp_factory ( IN provider_service_id INT)
BEGIN
    DECLARE provider_code VARCHAR(64);
    DECLARE service_action VARCHAR(64);
    DECLARE recharge_provider_code VARCHAR(64);

    select p.code, sa.action into provider_code, service_action from providers p, provider_services ps, service_actions sa
    where ps.id = provider_service_id
    and  ps.action = sa.id
    and ps.provider_id = p.id;

    select rp.code into recharge_provider_code from recharge_providers rp, provider_services_recharge_providers psrp
    where psrp.provider_service_id = provider_service_id
    and psrp.recharge_provider_id = rp.id
    order by psrp.weight desc
    limit 1;

    select provider_code, recharge_provider_code, service_action;
END;

create procedure sp_addRechargeProvider(IN provider_id INT, IN service_id INT)
BEGIN
   IF NOT EXISTS(select * from provider_services_recharge_providers
                 where recharge_provider_id = provider_id
                 and provider_service_id = service_id) THEN

          insert into  provider_services_recharge_providers(provider_service_id, recharge_provider_id)
          values(service_id, provider_id);
       END IF;
END;

insert into service_actions (action)
values('AIRTIME'),
       ('DATA'),
       ('ELECTRICITY');

insert into provider_categories(category_name, createdBy)
values('Mobile', 'Adebola Omoboya'),
       ('Disco', 'Adebola Omoboya');

insert into providers(category_id, name, createdBy, activated, activation_date, activatedBy, code)
values(1, 'MTN', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'MTN'),
       (1, 'Airtel', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'AIRT'),
       (1, 'Globacom', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'GLO'),
       (1, '9Mobile', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', '9MOB'),
       (2, 'Eko Distribution', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'EKEDP'),
       (2, 'Jos Distribution', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'JED');

insert into provider_services(provider_id, service_code, service_name, service_cost, action, createdby, activated, activation_date, activatedby)
values (1, 'MTN-AIRTIME', 'MTN AIRTIME', NULL, 1, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (2, 'AIRTEL-AIRTIME', 'AIRTEL AIRTIME', NULL, 1, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (3, 'GLO-AIRTIME', 'GLO AIRTIME', NULL, 1, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (4, '9MOBILE-AIRTIME', '9MOBILE AIRTIME', NULL, 1, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (1, 'MTN-DATA', 'MTN DATA', NULL, 2, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (2, 'AIRTEL-DATA', 'AIRTEL DATA', NULL, 2, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (3, 'GLO-DATA', 'GLO DATA', NULL, 2, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (4, '9MOBILE-DATA', '9MOBILE DATA', NULL, 2, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (5, 'EKEDP', 'EKO Disco Recharge', NULL, 3, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (6, 'JED', 'Jos Disco Recharge', NULL, 3, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya');

insert into recharge_providers(name, wallet_id, code, createdBy, activated, activation_date, activatedBy)
values
       ('Ringo', NULL, 'Ringo','Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       ('Energize', NULL, 'Energize','Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       ('Crown', NULL, 'Crown','Adebola Omoboya', true, NOW(), 'Adebola Omoboya');


-- 7, 8 and 9 used due to insert failures on recharge_providers Not applicable
insert into provider_services_recharge_providers(provider_service_id, recharge_provider_id, weight)
values (1, 1, 1),
       (2, 1, 1),
       (3, 1, 1),
       (4, 1, 1),
       (5, 1, 1),
       (6, 1, 1),
       (7, 1, 1),
       (8, 1, 1),
       (9, 1, 1),
       (10, 1, 1),
       (9, 3, 2),
       (10, 2, 2);


