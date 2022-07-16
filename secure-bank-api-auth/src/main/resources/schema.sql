DROP TABLE IF EXISTS DEVICE;
DROP TABLE IF EXISTS TRANSFER;
DROP TABLE IF EXISTS CURRENCY_EXCHANGE;
DROP TABLE IF EXISTS ACCOUNT_DETAILS;
DROP TABLE IF EXISTS SUB_ACCOUNT;
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

CREATE TABLE sub_account(
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(8),
    currency VARCHAR(3),
    balance DOUBLE default 0.00,
    FOREIGN KEY (client_id) REFERENCES account (client_id)
);

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

CREATE TABLE device
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    client_id VARCHAR(16) NOT NULL,
    name      VARCHAR(200),
    ip        VARCHAR(50) NOT NULL,
    FOREIGN KEY (client_id) REFERENCES account (client_id)
);

