CREATE TABLE account
(
    client_id       INT          NOT NULL UNIQUE,
    current_hash_id INT UNIQUE,
    account_number  VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    PRIMARY KEY(client_id)
);


CREATE TABLE account_hash (
    id INT PRIMARY KEY AUTO_INCREMENT,
    client_id INT NOT NULL,
    password_part VARCHAR(255) NOT NULL,
    password_part_characters_position VARCHAR(70) NOT NULL
);

ALTER TABLE account
  ADD FOREIGN KEY (current_hash_id) REFERENCES account_hash(id);
ALTER TABLE account_hash
  ADD FOREIGN KEY (client_id) REFERENCES account (client_id);

CREATE TABLE otp
(
    client_id          INT         NOT NULL UNIQUE,
    otp                VARCHAR(64) NOT NULL,
    otp_requested_time datetime    not null,
    primary key (client_id)
);

CREATE TABLE device(
	id INT PRIMARY KEY AUTO_INCREMENT,
    account_id INT NOT NULL,
    name VARCHAR(200),
    ip VARCHAR(50) NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account(client_id)
);

