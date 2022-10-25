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
    async BOOLEAN default true,
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
    recharge_provider_id INT,
    recipient varchar(64) NOT NULL,
    telephone varchar(64),
    product_id varchar(64),
    payment_id varchar(64),
    authorization_url varchar(64),
    redirect_url varchar(64),
    message varchar(64),
    status int,
    account_type varchar(64),
    name varchar(64),
    closed boolean NOT NULL DEFAULT FALSE,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    payment_mode varchar(32) NOT NULL,
    async_request BOOLEAN default true,
    scheduled_request_id varchar(64),
    auto_request_id varchar(64),
    bulk_request_id varchar(64),
    failed boolean default FALSE NOT NULL,
    failed_message varchar(256),
    successful_retry_id int,
    refund_id varchar(64),
    results varchar(128),
    FOREIGN KEY (successful_retry_id) REFERENCES  single_recharge_request_retries (id),
    FOREIGN KEY (scheduled_request_id) REFERENCES scheduled_recharge(id),
    FOREIGN KEY (auto_request_id) REFERENCES auto_recharge(id),
    FOREIGN KEY (bulk_request_id) REFERENCES bulk_recharge_requests(id),
    FOREIGN KEY (service_id) REFERENCES provider_services(id),
    FOREIGN KEY (recharge_provider_id) REFERENCES recharge_providers(id),
    PRIMARY KEY (id)
);

create table single_recharge_request_retries (
    id varchar(64),
    recharge_request_id varchar(64) NOT NULL,
    retried_on timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    retried_by VARCHAR(64) NOT NULL,
    recipient VARCHAR(64) NOT NULL,
    successful boolean NOT NULL,
    status_message varchar(256),
    FOREIGN KEY (recharge_request_id) REFERENCES recharge_requests(id),
    PRIMARY KEY (id)
);

create table single_recharge_request_resolve (
    id varchar(64) NOT NULL,
    recharge_request_id varchar(64) NOT NULL,
    resolved_by varchar(64) NOT NULL,
    resolved_on timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    resolution_message varchar(256) NOT NULL,
    FOREIGN KEY (recharge_request_id) REFERENCES recharge_requests(id),
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
    INDEX idx_network (network),
    PRIMARY KEY (product_id)
);

# create table bulk_recharge_requests (
#     id varchar(64),
#     user_id varchar(64),
#     service_id int NOT NULL,
#     service_cost decimal(10,2) NOT NULL,
#     total_service_cost decimal(10,2) NOT NULL,
#     group_id int,
#     product_id varchar(64),
#     payment_id varchar(64),
#     payment_mode varchar(32) NOT NULL,
#     authorization_url varchar(64),
#     redirect_url varchar(64),
#     closed boolean NOT NULL DEFAULT FALSE,
#     createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
#     scheduled_request_id varchar(64),
#     auto_request_id varchar(64),
#     FOREIGN KEY (scheduled_request_id) REFERENCES scheduled_recharge(id),
#     FOREIGN KEY (auto_request_id) REFERENCES auto_recharge(id),
#     FOREIGN KEY (service_id) REFERENCES provider_services(id),
#     PRIMARY KEY (id)
# );

create table recharge_request_recipients (
    bulk_recharge_request_id varchar(64),
    scheduled_recharge_request_id varchar(64),
    auto_recharge_request_id varchar(64),
    msisdn varchar(64) NOT NULL,
    INDEX idx_bulk_recharge_request_id (bulk_recharge_request_id),
    INDEX idx_scheduled_recharge_request_id(scheduled_recharge_request_id),
    INDEX idx_auto_recharge_request_id(auto_recharge_request_id),
    FOREIGN KEY (auto_recharge_request_id) REFERENCES new_auto_recharge_requests(id),
    FOREIGN KEY (scheduled_recharge_request_id) REFERENCES new_scheduled_recharge_requests (id),
    FOREIGN KEY (bulk_recharge_request_id) REFERENCES new_bulk_recharge_requests (id)
);

create table new_auto_recharge_requests (
    id varchar (64) NOT NULL,
    user_id varchar(64),
    title varchar(128),
    start_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    end_date timestamp,
    recurring_type int NOT NULL,
    separation_count int default 0,
    payment_mode varchar(64) NOT NULL,
    deleted boolean default FALSE,
    deleted_date timestamp,
    INDEX idx_user_id(user_id),
    INDEX idx_recurring_type(recurring_type),
    PRIMARY KEY (id)
);

create table auto_recurring_events (
    id int AUTO_INCREMENT,
    auto_request_id varchar(64) NOT NULL,
    day_of_period int NOT NULL,
    disabled boolean default  FALSE,
    INDEX idx_auto_request(auto_request_id),
    INDEX idx_day_of_period(day_of_period),
    INDEX idx_disabled(disabled),
    FOREIGN KEY (auto_request_id) REFERENCES new_auto_recharge_requests(id),
    PRIMARY KEY (id)
);

create table auto_events_ran(
    id  int AUTO_INCREMENT,
    auto_request_id varchar(64) NOT NULL,
    recurring_event_id int NOT NULL,
    period_id int NOT NULL,
    ran_on_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    INDEX idx_period_id(period_id),
    FOREIGN KEY (recurring_event_id) REFERENCES auto_recurring_events(id),
    FOREIGN KEY (auto_request_id) REFERENCES new_auto_recharge_requests(id),
    PRIMARY KEY (id)
);

create table new_bulk_recharge_requests (
    id varchar(64),
    user_id varchar(64),
    total_service_cost decimal(10,2) NOT NULL,
    payment_id varchar(64),
    payment_mode varchar(32) NOT NULL,
    authorization_url varchar(64),
    redirect_url varchar(64),
    closed boolean NOT NULL DEFAULT FALSE,
    createdAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    scheduled_request_id varchar(64),
    auto_request_id varchar(64),
    FOREIGN KEY (scheduled_request_id) REFERENCES new_scheduled_recharge_requests(id),
    FOREIGN KEY (auto_request_id) REFERENCES auto_recharge(id),
    PRIMARY KEY (id)
);

create table auto_individual_requests (
    id int AUTO_INCREMENT,
    auto_request_id varchar(64) NOT NULL,
    service_id int NOT NULL,
    service_cost decimal(10,2) NOT NULL,
    product_id varchar(64),
    telephone varchar(64),
    recipient varchar(64) NOT NULL,
    FOREIGN KEY (auto_request_id) REFERENCES new_auto_recharge_requests(id),
    PRIMARY KEY (id)
);

create table bulk_individual_requests (
    id int AUTO_INCREMENT,
    bulk_request_id varchar(64),
    scheduled_request_id varchar(64),
    auto_request_id varchar(64),
    external_request_id varchar(64) NOT NULL,
    recharge_provider_id INT,
    service_id int NOT NULL,
    service_cost decimal(10,2) NOT NULL,
    product_id varchar(64),
    telephone varchar(64),
    recipient varchar(64) NOT NULL,
    failed BOOLEAN default false,
    failed_message varchar(256),
    refund_id varchar(64),
    successful_retry_id varchar(64),
    resolve_id  varchar(64),
    FOREIGN KEY (resolve_id) REFERENCES bulk_individual_resolve_request(id),
    FOREIGN KEY (scheduled_request_id) REFERENCES new_scheduled_recharge_requests(id),
    FOREIGN KEY (bulk_request_id) REFERENCES new_bulk_recharge_requests(id),
    FOREIGN KEY (auto_request_id) REFERENCES new_auto_recharge_requests(id),
    FOREIGN KEY (recharge_provider_id) REFERENCES recharge_providers(id),
    PRIMARY KEY (id)
);

create table bulk_individual_request_retries(
    id varchar(64),
    bulk_individual_request_id int,
    retried_on timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    retried_by VARCHAR(64) NOT NULL,
    recipient VARCHAR(64) NOT NULL,
    successful boolean default FALSE NOT NULL,
    status_message varchar(256),
    FOREIGN KEY (bulk_individual_request_id) REFERENCES bulk_individual_requests(id),
    PRIMARY KEY (id)
);

create table bulk_individual_resolve_request(
        id varchar(64) NOT NULL,
        bulk_request_id varchar(64) NOT NULL,
        resolved_on timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
        resolved_by VARCHAR(64) NOT NULL,
        resolution_message varchar(256) NOT NULL,
        FOREIGN KEY (bulk_request_id) REFERENCES new_bulk_recharge_requests(id),
        PRIMARY KEY (id)
);

create table new_scheduled_recharge_requests (
    id varchar(64),
    user_id varchar(64),
    request_type int DEFAULT  1 NOT NULL,
    request_scheduled_date timestamp NOT NULL,
    total_service_cost decimal (10,2),
    payment_mode varchar(16),
    authorization_url varchar(64),
    redirect_url varchar(64),
    payment_id varchar(64),
    message varchar(64),
    status int,
    request_created_on timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    request_ran_on timestamp,
    closed boolean NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);

# create table scheduled_recharge (
#     id varchar(64),
#     user_id varchar(64),
#     request_id int,
#     request_type int DEFAULT 1 NOT NULL,
#     request_scheduled_date timestamp NOT NULL,
#     service_id int NOT NULL,
#     service_cost decimal(10, 2),
#     total_service_cost decimal (10,2),
#     group_id int,
#     recipient varchar(32),
#     product_id varchar(32),
#     telephone varchar (32),
#     redirect_url varchar(64),
#     authorization_url varchar(64),
#     payment_mode varchar(16),
#     payment_id varchar(64),
#     message varchar(64),
#     status int,
#     request_created_on timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
#     request_ran_on timestamp,
#     closed boolean NOT NULL DEFAULT FALSE,
#     FOREIGN KEY (service_id) REFERENCES provider_services(id),
#     PRIMARY KEY (id)
# );


create procedure sp_disable_and_load_events (IN auto_recharge_id VARCHAR(64))
    BEGIN
        update auto_recurring_events set disabled = true where auto_request_id = auto_recharge_id;
        select id, auto_request_id, day_of_period, disabled
        from auto_recurring_events
        where auto_request_id = auto_recharge_id;
    END;

create procedure sp_factory ( IN provider_service_id INT)
BEGIN
    DECLARE provider_code VARCHAR(64);
    DECLARE service_action VARCHAR(64);
    DECLARE recharge_provider_code VARCHAR(64);
    DECLARE recharge_provider_id INT;
    DECLARE async BOOLEAN;
    DECLARE has_results BOOLEAN;

    select p.code, sa.action, ps.async, ps.has_results into provider_code, service_action, async, has_results
    from providers p, provider_services ps, service_actions sa
    where ps.id = provider_service_id
    and  ps.action = sa.id
    and ps.provider_id = p.id;

    select rp.id, rp.code into recharge_provider_id, recharge_provider_code from recharge_providers rp, provider_services_recharge_providers psrp
    where psrp.provider_service_id = provider_service_id
    and psrp.recharge_provider_id = rp.id
    order by psrp.weight desc
    limit 1;

    select provider_code, recharge_provider_id, recharge_provider_code, service_action, async, has_results;
END;

create procedure sp_addRechargeProvider(IN provider_id INT, IN service_id INT, IN weight_value INT)
BEGIN
   IF NOT EXISTS(select 1 from provider_services_recharge_providers
                 where recharge_provider_id = provider_id
                 and provider_service_id = service_id) THEN

          insert into  provider_services_recharge_providers(provider_service_id, recharge_provider_id, weight)
          values(service_id, provider_id, weight_value);
       END IF;
END;

create procedure sp_amendRechargeProvider(IN provider_id INT, IN service_id INT, IN weight_value INT)
BEGIN
    IF EXISTS(select 1 from provider_services_recharge_providers
                  where recharge_provider_id = provider_id
                  and provider_service_id = service_id) THEN

        update provider_services_recharge_providers set weight = weight_value
        where recharge_provider_id = provider_id
        and provider_service_id = service_id;
    END IF;
END;

insert into service_actions (action)
values('AIRTIME'),
       ('DATA'),
       ('ELECTRICITY'),
       -- Additions
       ('SPECTRANET'),
       ('SMILE'),
       ('DSTV'),
       ('GOTV'),
       ('STARTIMES');

insert into provider_categories(category_name, createdBy)
values('Mobile', 'Adebola Omoboya'),
       ('Disco', 'Adebola Omoboya');

insert into providers(category_id, name, createdBy, activated, activation_date, activatedBy, code)
values(1, 'MTN', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'MTN'),
       (1, 'Airtel', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'AIRT'),
       (1, 'Globacom', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'GLO'),
       (1, '9Mobile', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', '9MOB'),
       (2, 'Eko Distribution', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'EKEDP'),
       (2, 'Jos Distribution', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'JED'),
       -- Additions
       (1, 'SPECTRANET', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'SPECTRANET'),
       (1, 'SMILE', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'SMILE'),
       (1, 'DSTV', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'DSTV'),
       (1, 'GOTV', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'GOTV'),
       (1, 'STARTIMES', 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya', 'STARTIMES');

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
       (6, 'JED', 'Jos Disco Recharge', NULL, 3, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       -- Additions
       (7, 'SPECTRANET-DATA', 'SPECTRANET DATA', NULL, 4, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (8, 'SMILE-DATA', 'SMILE DATA', NULL, 5, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (9, 'DSTV', 'DSTV', NULL, 6, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (10, 'GOTV', 'GOTV', NULL, 7, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
       (11, 'STARTIMES', 'STARTIMES', NULL, 8, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya');

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
       (10, 2, 2),
       -- Additions
       (11, 1, 1),
       (12, 1, 1),
       (13, 1, 1),
       (14, 1, 1),
       (15, 1, 1);

insert into ringo_data_plans (product_id, network, category, price, code, validity)
values
       ('SP1', 'SPECTRANET', 'Pin', 500, 'SPECTRANET-DATA', 'No Validity'),
       ('SP2', 'SPECTRANET', 'Pin', 1000, 'SPECTRANET-DATA','No Validity'),
       ('SP3', 'SPECTRANET', 'Pin', 2000, 'SPECTRANET-DATA','No Validity'),
       ('SP4', 'SPECTRANET', 'Pin', 5000, 'SPECTRANET-DATA','No Validity'),
       ('SP5', 'SPECTRANET', 'Pin', 7000, 'SPECTRANET-DATA','No Validity'),
       ('SP6', 'SPECTRANET', 'Pin', 10000, 'SPECTRANET-DATA','No Validity');

update ringo_data_plans set product_id = 'SP6' where product_id = 'spec-10000';


insert into ringo_data_plans (product_id, network, category, price, code, validity, allowance)
values
    ('508', 'SMILE', 'Daily', 200, 'SMILE-DATA', '3', 'International SmileVoice ONLY 23'),
    ('624', 'SMILE', 'Daily', 300, 'SMILE-DATA', '1', '1GB FlexiDaily'),
    ('625', 'SMILE', 'Daily', 500, 'SMILE-DATA', '2', '2.5GB FlexiDaily'),
    ('626', 'SMILE', 'Weekly', 500, 'SMILE-DATA', '7', '1GB FlexiWeekly'),
    ('509', 'SMILE', 'Weekly', 500, 'SMILE-DATA', '7', 'International SmileVoice ONLY 60'),
    ('516', 'SMILE', 'Monthly', 510, 'SMILE-DATA', '30', 'SmileVoice ONLY 65'),
    ('606', 'SMILE', 'Monthly', 1000, 'SMILE-DATA', '30', '1.5GB Bigga'),
    ('627', 'SMILE', 'Weekly', 1000, 'SMILE-DATA', '7', '2GB FlexiWeekly'),
    ('510', 'SMILE', 'Monthly', 1000, 'SMILE-DATA', '7', 'International SmileVoice ONLY 125'),
    ('517', 'SMILE', 'Monthly', 1020, 'SMILE-DATA', '7', 'SmileVoice ONLY 135'),
    ('413', 'SMILE', 'Weekly', 1020, 'SMILE-DATA', '7', '2GB MidNite'),
    ('607', 'SMILE', 'Monthly', 1200, 'SMILE-DATA', '30', '2GB Bigga'),
    ('608', 'SMILE', 'Monthly', 1500, 'SMILE-DATA', '30', '3GB Bigga'),
    ('628', 'SMILE', 'Weekly', 1500, 'SMILE-DATA', '7', '6GB FlexiWeekly'),
    ('698', 'SMILE', 'Monthly', 1500, 'SMILE-DATA', '60', 'SmileVoice ONLY 150'),
    ('414', 'SMILE', 'Weekly', 1530, 'SMILE-DATA', '7', '3GB MidNite'),
    ('415', 'SMILE', 'Daily', 1530, 'SMILE-DATA', '3', '3GB Weekend ONLY'),
    ('620', 'SMILE', 'Monthly', 2000, 'SMILE-DATA', '30', '5GB Bigga'),
    ('700', 'SMILE', 'Monthly', 2000, 'SMILE-DATA', '90', 'SmileVoice ONLY 175'),
    ('609', 'SMILE', 'Monthly', 2500, 'SMILE-DATA', '30', '6.5GB Bigga'),
    ('610', 'SMILE', 'Monthly', 3000, 'SMILE-DATA', '30', '8GB Bigga'),
    ('518', 'SMILE', 'Monthly', 3070, 'SMILE-DATA', '30', 'SmileVoice ONLY 430'),
    ('611', 'SMILE', 'Monthly', 3500, 'SMILE-DATA', '30', '10GB Bigga'),
    ('612', 'SMILE', 'Monthly', 4000, 'SMILE-DATA', '30', '12GB Bigga'),
    ('699', 'SMILE', 'Monthly', 3500, 'SMILE-DATA', '60', 'SmileVoice ONLY 450'),
    ('613', 'SMILE', 'Monthly', 5000, 'SMILE-DATA', '30', '15GB Bigga'),
    ('701', 'SMILE', 'Monthly', 5000, 'SMILE-DATA', '90', 'SmileVoice ONLY 500'),
    ('614', 'SMILE', 'Monthly', 6000, 'SMILE-DATA', '30', '20GB Bigga'),
    ('615', 'SMILE', 'Monthly', 8000, 'SMILE-DATA', '30', '30GB Bigga'),
    ('687', 'SMILE', 'Yearly', 9000, 'SMILE-DATA', '365', '15GB 365'),
    ('616', 'SMILE', 'Monthly', 10000, 'SMILE-DATA', '30', '40GB Bigga'),
    ('629', 'SMILE', 'Monthly', 10000, 'SMILE-DATA', '30', 'UnlimitedLite'),
    ('617', 'SMILE', 'Monthly', 13500, 'SMILE-DATA', '30', '60GB Bigga'),
    ('618', 'SMILE', 'Monthly', 15000, 'SMILE-DATA', '30', '75GB Bigga'),
    ('630', 'SMILE', 'Monthly', 15000, 'SMILE-DATA', '30', 'UnlimitedEssential'),
    ('619', 'SMILE', 'Monthly', 18000, 'SMILE-DATA', '30', '100GB Bigga'),
    ('688', 'SMILE', 'Yearly', 19000, 'SMILE-DATA', '365', '35GB 365'),
    ('668', 'SMILE', 'Monthly', 19800, 'SMILE-DATA', '30', '130GB Bigga'),
    ('665', 'SMILE', 'Monthly', 20000, 'SMILE-DATA', '60', '90GB Jumbo'),
    ('669', 'SMILE', 'Monthly', 20000, 'SMILE-DATA', '30', 'Freedom 3Mbps'),
    ('670', 'SMILE', 'Monthly', 24000, 'SMILE-DATA', '30', 'Freedom 6Mbps'),
    ('689', 'SMILE', 'Yearly', 32000, 'SMILE-DATA', '365', '70GB 365'),
    ('666', 'SMILE', 'Monthly', 34000, 'SMILE-DATA', '90', '160GB Jumbo'),
    ('671', 'SMILE', 'Monthly', 36000, 'SMILE-DATA', '30', 'BestEffort Freedom'),
    ('667', 'SMILE', 'Monthly', 34000, 'SMILE-DATA', '120', '200GB Jumbo'),
    ('664', 'SMILE', 'Yearly', 50000, 'SMILE-DATA', '365', '125GB 365'),
    ('604', 'SMILE', 'Yearly', 70000, 'SMILE-DATA', '365', '200GB 365'),
    ('673', 'SMILE', 'Yearly', 100000, 'SMILE-DATA', '365', '500GB 365'),
    ('674', 'SMILE', 'Yearly', 120000, 'SMILE-DATA', '365', '1TB 365');


(8, 'SPECTRANET-DATA', 'SPECTRANET DATA', NULL, 4, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya'),
(9, 'SMILE-DATA', 'SMILE DATA', NULL, 5, 'Adebola Omoboya', true, NOW(), 'Adebola Omoboya');

update ringo_data_plans set validity = trim(substring(validity,1,3)) where network = 'SMILE';