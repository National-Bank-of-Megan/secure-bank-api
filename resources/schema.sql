use nbm;

DROP TABLE IF EXISTS  devices;
DROP TABLE IF EXISTS  account_balance;
DROP TABLE IF EXISTS  favorite_receivers;
DROP TABLE IF EXISTS  transfers;
DROP TABLE IF EXISTS  account_details;
DROP TABLE IF EXISTS  account;


CREATE TABLE account (
     social_security_number int not null unique,
     account_number int not null,
     password varchar(255) not null,
     primary key (social_security_number)
);


CREATE TABLE account_details(
    id int auto_increment,
    account_id int,
    firstname varchar(50) not null,
    lastname varchar(70) not null,
    email varchar(255) not null unique,
    phone_number int not null,
    birthday date not null,
    country varchar(30) not null,no jedna robimy
        primary key (id),
    foreign key (account_id) references account(social_security_number)
);

CREATE TABLE account_balance (
     id int auto_increment,
     account_id int,
     amount double not null,
     currency enum('euro','złoty','dollar'),
     primary key(id),
     foreign key (account_id) references account(social_security_number)

);

CREATE TABLE devices (
     id int not null auto_increment,
     user_id int not null,
     added datetime not null,
     ip char(19),
     primary key(id),
     foreign key (user_id) references account(social_security_number)
);

CREATE TABLE favorites (
   account_id int not null,
   receiver_id int not null,
   primary key (account_id,receiver_id),
   foreign key (account_id) references account(social_security_number),
   foreign key (receiver_id) references account(social_security_number)
);


CREATE TABLE transfer(
                         id int auto_increment,
                         sender_id int not null,
                         receiver_id int not null,
                         order_date datetime not null,
                         amount double not null default 5.00,
                         currency enum('euro','złoty','dollar'),
                         type enum('CLASSIC','MOBILE'),
                         primary key(id),
                         foreign key (sender_id) references  account(social_security_number),
                         foreign key (receiver_id) references  account(social_security_number)
);


CREATE TABLE currency_exchange(
                                  id int auto_increment,
                                  account_id int not null,
                                  ordered datetime not null,
                                  from enum('euro','złoty','dollar'),
                                  to enum('euro','złoty','dollar'),
                                  amount double not null,
                                  primary key (id),
                                  foreign key (account_id) references account(social_security_number)
);