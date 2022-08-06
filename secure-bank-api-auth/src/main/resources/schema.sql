DROP TABLE IF EXISTS DEVICE;
DROP TABLE IF EXISTS TRANSFER;
DROP TABLE IF EXISTS favorite_receiver;
DROP TABLE IF EXISTS ACCOUNT_DETAILS;
DROP TABLE IF EXISTS SUB_ACCOUNT;
DROP TABLE IF EXISTS CURRENCY_EXCHANGE;
DROP TABLE IF EXISTS ACCOUNT;
DROP TABLE IF EXISTS ACCOUNT_HASH;


CREATE TABLE account
(
    client_id          VARCHAR(16) PRIMARY KEY,
    secret             VARCHAR(255),
    should_be_verified BOOLEAN DEFAULT false,
    current_hash_id    INT UNIQUE,
    account_number     VARCHAR(255) NOT NULL UNIQUE,
    password           VARCHAR(255) NOT NULL
);

CREATE TABLE account_details
(
    client_id  VARCHAR(16) PRIMARY KEY,
    first_name VARCHAR(50),
    last_name  VARCHAR(70),
    email      VARCHAR(255) NOT NULL unique,
    phone      VARCHAR(25),
    FOREIGN KEY (client_id) REFERENCES account (client_id)
);

CREATE TABLE sub_account
(
    client_id VARCHAR(16)                              NOT NULL,
    currency  ENUM ('EUR', 'USD', 'PLN', 'CHF', 'GBP') NOT NULL,
    balance   DECIMAL(15, 2) DEFAULT 0.00,
    FOREIGN KEY (client_id) REFERENCES account (client_id),
    PRIMARY KEY (client_id, currency)
);

-- CREATE TABLE ACCOUNT_SUB_ACCOUNT(
--     client_id VARCHAR(16) NOT NULL,
--     sub_account_id INT NOT NULL,
--     FOREIGN KEY (client_id) REFERENCES account (client_id),
--     FOREIGN KEY (sub_account_id) REFERENCES sub_account (id),
--     primary key (client_id,sub_account_id)
--
-- );

CREATE TABLE account_hash
(
    id                                INT PRIMARY KEY AUTO_INCREMENT,
    client_id                         VARCHAR(16)  NOT NULL,
    password_part                     VARCHAR(255) NOT NULL,
    password_part_characters_position VARCHAR(70)  NOT NULL
);

ALTER TABLE account
    ADD FOREIGN KEY (current_hash_id) REFERENCES account_hash (id);
-- ALTER TABLE account_hash
--     ADD FOREIGN KEY (client_id) REFERENCES account (client_id);

CREATE TABLE favorite_receiver
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    client_id      VARCHAR(16)  NOT NULL,
    name           VARCHAR(50)  NOT NULL,
    account_number VARCHAR(255) NOT NULL,
    UNIQUE (client_id, account_number),
    FOREIGN KEY (client_id) REFERENCES account (client_id)
);

CREATE TABLE device
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    client_id VARCHAR(16) NOT NULL,
    name      VARCHAR(200),
    ip        VARCHAR(50) NOT NULL,
    FOREIGN KEY (client_id) REFERENCES account (client_id)
);

CREATE TABLE transfer
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    title        NVARCHAR(60) NOT NULL,
    sender_id    VARCHAR(16) NOT NULL,
    receiver_id  VARCHAR(16) NOT NULL,
    request_date DATETIME    NOT NULL,
    done_date    DATETIME,
    amount       DECIMAL(15, 2) NOT NULL,
    currency     ENUM ('EUR', 'USD', 'PLN', 'CHF', 'GBP') NOT NULL,
    type         ENUM ('CLASSIC', 'MOBILE'),
    status       ENUM ('PENDING', 'DONE') NOT NULL,
    FOREIGN KEY (sender_id)
        REFERENCES account (client_id),
    FOREIGN KEY (receiver_id)
        REFERENCES account (client_id)
);

CREATE TABLE currency_exchange
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    client_id       VARCHAR(16),
    ordered_on      DATETIME,
    currency_bought ENUM ('EUR', 'USD', 'PLN', 'CHF', 'GBP'),
    currency_sold   ENUM ('EUR', 'USD', 'PLN', 'CHF', 'GBP'),
    amount_bought   DECIMAL(15, 2),
    amount_sold     DECIMAL(15, 2),
    FOREIGN KEY (client_id) REFERENCES account (client_id)

);

