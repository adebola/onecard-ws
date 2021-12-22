drop procedure sp_factory;
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
    service_cost decimal(8,2),
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
    id int AUTO_INCREMENT,
    service_id int NOT NULL,
    service_cost decimal(10,2) NOT NULL,
    recipient varchar(64) NOT NULL,
    telephone varchar(64),
    product_id varchar(64) NOT NULL,
    payment_id varchar(64),
    authorization_url varchar(64),
    closed boolean NOT NULL DEFAULT FALSE,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (service_id) REFERENCES provider_services(id),
    PRIMARY KEY (id)
);

create table ringo_data_plans (
    product_id varchar(16) NOT NULL,
    network varchar(32) NOT NULL,
    category varchar(32) NOT NULL,
    price decimal(8,2) NOT NULL,
    allowance varchar(32) NOT NULL,
    validity varchar(32) NOT NULL,
    PRIMARY KEY (product_id)
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
    order by psrp.weight desc;

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
       (2, 'AIR-AIRTIME', 'AIRTEL AIRTIME', NULL, 1, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (3, 'GLO-AIRTIME', 'GLO AIRTIME', NULL, 1, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (4, '9-AIRTIME', '9MOBILE AIRTIME', NULL, 1, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (1, 'MTN-DATA', 'MTN DATA', NULL, 1, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (2, 'AIR-DATA', 'AIRTEL DATA', NULL, 1, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (3, 'GLO-DATA', 'GLO DATA', NULL, 1, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (4, '9-DATA', '9MOBILE DATA', NULL, 1, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (5, 'EKEDP', 'EKO Disco Recharge', NULL, 1, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (6, 'JED', 'Jos Disco Recharge', NULL, 1, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya');

insert into recharge_providers(name, wallet_id, code, createdBy, activated, activation_date, activatedBy)
values
       ('Ringo', NULL, 'Ringo','Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       ('Energize', NULL, 'Energize','Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       ('Crown', NULL, 'Crown','Adebola Omoboya', true, NOW(), 'Adebola Omoboya');


-- 7, 8 and 9 used due to insert faiulures on recharge_providers
insert into provider_services_recharge_providers(provider_service_id, recharge_provider_id, weight)
values (1, 7, 1),
       (2, 7, 1),
       (3, 7, 1),
       (4, 7, 1),
       (5, 7, 1),
       (6, 7, 1),
       (7, 7, 1),
       (8, 7, 1),
       (9, 7, 2),
       (10, 7, 2),
       (9, 9, 1),
       (10, 8, 1);


-- Modifications to Existing Tables
alter table provider_services_recharge_providers add UNIQUE KEY idx_provider_recharge_service (provider_service_id, recharge_provider_id);


