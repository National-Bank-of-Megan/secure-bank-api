use
    nbm;

DROP TABLE IF EXISTS devices;
DROP TABLE IF EXISTS account_balance;
DROP TABLE IF EXISTS favorite_receivers;
DROP TABLE IF EXISTS transfers;
DROP TABLE IF EXISTS account_details;
DROP TABLE IF EXISTS account;


CREATE TABLE account
(
    social_security_number INT          NOT NULL UNIQUE,
    account_number         INT          NOT NULL,
    password               VARCHAR(255) NOT NULL,
    PRIMARY KEY (social_security_number)
);


CREATE TABLE account_details
(
    id           int auto_increment,
    account_id   int,
    firstname    varchar(50)  not null,
    lastname     varchar(70)  not null,
    email        varchar(255) not null unique,
    phone_number int          not null,
    birthday     date         not null,
    country      varchar(30)  not null,
    primary key (id),
    foreign key (account_id) references account (social_security_number)
);

CREATE TABLE account_balance
(
    id         INT AUTO_INCREMENT,
    account_id INT,
    amount     DOUBLE NOT NULL,
    currency   ENUM ('euro', 'złoty', 'dollar'),
    PRIMARY KEY (id),
    FOREIGN KEY (account_id)
        REFERENCES account (social_security_number)
);

CREATE TABLE devices
(
    id      INT      NOT NULL AUTO_INCREMENT,
    user_id INT      NOT NULL,
    added   DATETIME NOT NULL,
    ip      CHAR(19),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES account (social_security_number)
);

CREATE TABLE favorites
(
    account_id  INT NOT NULL,
    receiver_id INT NOT NULL,
    PRIMARY KEY (account_id, receiver_id),
    FOREIGN KEY (account_id)
        REFERENCES account (social_security_number),
    FOREIGN KEY (receiver_id)
        REFERENCES account (social_security_number)
);


CREATE TABLE transfer
(
    id          INT AUTO_INCREMENT,
    sender_id   INT      NOT NULL,
    receiver_id INT      NOT NULL,
    order_date  DATETIME NOT NULL,
    amount      DOUBLE   NOT NULL DEFAULT 5.00,
    currency    ENUM ('euro', 'złoty', 'dollar'),
    type        ENUM ('CLASSIC', 'MOBILE'),
    PRIMARY KEY (id),
    FOREIGN KEY (sender_id)
        REFERENCES account (social_security_number),
    FOREIGN KEY (receiver_id)
        REFERENCES account (social_security_number)
);


CREATE TABLE currency_exchange
(
    id            INT AUTO_INCREMENT,
    account_id    INT      NOT NULL,
    ordered       DATETIME NOT NULL,
    currency_from ENUM ('euro', 'złoty', 'dollar'),
    currency_to   ENUM ('euro', 'złoty', 'dollar'),
    amount        DOUBLE   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id)
        REFERENCES account (social_security_number)
);