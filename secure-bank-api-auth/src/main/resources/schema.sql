CREATE TABLE account (
    client_id INT NOT NULL UNIQUE AUTO_INCREMENT,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (client_id)
);

CREATE TABLE account_hashes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    client_id INT NOT NULL UNIQUE AUTO_INCREMENT,
    password_part VARCHAR(255) NOT NULL,
    password_part_characters_positions CHAR(6) NOT NULL,
    FOREIGN KEY (client_id) REFERENCES account (client_id)
);

CREATE TABLE current_account_hash (
	account_hash_id INT PRIMARY KEY,
    FOREIGN KEY (account_hash_id) REFERENCES account_hashes (id)
);

CREATE TABLE otp (
    client_id INT NOT NULL UNIQUE,
    otp VARCHAR(64) NOT NULL,
    otp_requested_time DATETIME NOT NULL
);

