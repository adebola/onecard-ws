create table users (
            id VARCHAR(64) NOT NULL,
            organization_name varchar(64),
            user_name VARCHAR(64) NOT NULL,
            first_name VARCHAR(64),
            last_name VARCHAR(64),
            email VARCHAR(64) NOT NULL,
            created_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
            wallet_id VARCHAR(64),
            UNIQUE idx_user_name(user_name),
            PRIMARY KEY (id)
);