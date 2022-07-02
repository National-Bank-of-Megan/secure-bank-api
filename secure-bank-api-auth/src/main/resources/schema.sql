CREATE TABLE account
(
    client_id       INT NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    PRIMARY KEY (client_id)
);