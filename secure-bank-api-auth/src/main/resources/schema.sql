CREATE TABLE account
(
    client_id       INT NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    PRIMARY KEY (client_id)
);

CREATE TABLE otp
(
    client_id  INT NOT NULL UNIQUE,
    otp       VARCHAR(64) NOT NULL,
    otp_requested_time datetime not null
);

