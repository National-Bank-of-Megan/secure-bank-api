INSERT INTO ACCOUNT_HASH (client_id, password_part, password_part_characters_position)
VALUES ('12345678', '$2a$05$CmsWPE/GTki0mHMmYMMY.uWq68FFnRPXzZiCgzeaD6u.IkDHfSHGW', '1 2 3 4 6 7'),
       ('12345678', '$2a$05$zD8oX8KrZQIg/J5kpP1/7u7bcqSZ5amLUuesDw9cCpPX3U3wao0Vi', '0 2 3 5 6 7'),
       ('12345678', '$2a$05$hGqVeHyO1HNzRT/YKMSH5u9b.RNP4TOyAvj03Sbs31VNeQeD9/.Su', '0 1 4 5 6 7'),
       ('12345678', '$2a$05$XNVF2ER7cYEIF7Pb7wboZu5olO7WJOzw5Yckg46w6XW8YDZh5PQkK', '0 1 3 4 6 7'),
       ('12345678', '$2a$05$jTId9ZjUGiFiyRJvY.V7geh8VBcc1q5cCMYSPp6UOQ/AmylD1H18i', '0 1 2 5 6 7'),
       ('12345679', '$2a$05$CmsWPE/GTki0mHMmYMMY.uWq68FFnRPXzZiCgzeaD6u.IkDHfSHGW', '1 2 3 4 6 7'),
       ('12345679', '$2a$05$zD8oX8KrZQIg/J5kpP1/7u7bcqSZ5amLUuesDw9cCpPX3U3wao0Vi', '0 2 3 5 6 7'),
       ('12345679', '$2a$05$hGqVeHyO1HNzRT/YKMSH5u9b.RNP4TOyAvj03Sbs31VNeQeD9/.Su', '0 1 4 5 6 7'),
       ('12345679', '$2a$05$XNVF2ER7cYEIF7Pb7wboZu5olO7WJOzw5Yckg46w6XW8YDZh5PQkK', '0 1 3 4 6 7'),
       ('12345679', '$2a$05$jTId9ZjUGiFiyRJvY.V7geh8VBcc1q5cCMYSPp6UOQ/AmylD1H18i', '0 1 2 5 6 7');

INSERT INTO ACCOUNT(client_id, password, secret, account_number, current_hash_id)
values ('12345678', '$2a$12$T1jPgI8pJScKKNN6RXdH7uoKtVVU0T8gvjdwykFTrBwsmgiGx1fC2', '2IHXSDQ3RJZ2XVJTKSS6U3YQWW5EKFCT',
        '31570775012720354348035486', 1), ('12345679', '$2a$12$T1jPgI8pJScKKNN6RXdH7uoKtVVU0T8gvjdwykFTrBwsmgiGx1fC2',
                                          '2IHXSDQ3RJZ2XVJTKSS6U3YQWW5EKFCT',
                                          '31570775012720354348035480', 6);

INSERT INTO ACCOUNT_DETAILS(client_id, first_name, last_name, email, phone)
VALUES ('12345678', 'Megan Thee', 'Stallion', 'quuen_megan@email.com', '911911911'),
       ('12345679', 'Doja', 'Cat', 'cow@email.com', '911911900');
       
INSERT INTO KLIK(client_id) VALUES('12345678'), ('12345679');

INSERT INTO SUB_ACCOUNT (client_id, currency, balance)
VALUES ('12345678', 'USD', 1025.25),
       ('12345678', 'PLN', 1025.25),
       ('12345678', 'CHF', 1025.25),
       ('12345678', 'EUR', 1025.25),
       ('12345678', 'GBP', 1025.25),
       ('12345679', 'USD', 1025.25),
       ('12345679', 'PLN', 1025.25),
       ('12345679', 'CHF', 1025.25),
       ('12345679', 'EUR', 1025.25),
       ('12345679', 'GBP', 1025.25);

INSERT INTO CURRENCY_EXCHANGE(client_id, ordered_on, currency_bought, currency_sold, amount_bought, amount_sold)
VALUES ('12345678', '2022-07-21', 'PLN', 'CHF', 400, 98.7889),
       ('12345678', '2021-06-30 13:24:01', 'PLN', 'CHF', 400, 98.7889),
       ('12345678', '2021-07-21', 'PLN', 'CHF', 400, 98.7889),
       ('12345678', '2022-08-21', 'PLN', 'CHF', 400, 98.7889),
       ('12345678', '2022-07-21 00:01:52', 'PLN', 'CHF', 400, 98.7889),
       ('12345678', '2021-07-21', 'PLN', 'CHF', 400, 98.7889);

INSERT INTO TRANSFER(sender_id,title, receiver_id, request_date, done_date, amount, currency, type, status)
VALUES ('12345679','Dla Megan','12345678','2022-10-21','2022-05-22',70.0,'USD','CLASSIC','DONE'),
       ('12345678','Spotify','12345679','2022-09-22','2022-05-22',500.87,'USD','CLASSIC','DONE'),
       ('12345678','Spotify 2','12345679','2022-05-21 13:53:21','2022-05-22',500.87,'USD','CLASSIC','DONE'),
       ('12345678','Spotify 3','12345679','2022-05-23','2022-05-22',500.87,'USD','CLASSIC','DONE'),
       ('12345678','Spotify 4','12345679','2022-05-17 00:00:01','2022-05-22',500.87,'USD','CLASSIC','DONE'),
       ('12345678','Spotify 5','12345679','2022-05-21','2022-05-22',500.87,'USD','CLASSIC','DONE'),
       ('12345678','Spotify 6','12345679','2022-05-21','2022-05-22',500.87,'USD','CLASSIC','DONE'),
       ('12345678','Spotify 6','12345679','2022-05-21','2022-05-22',500.87,'USD','CLASSIC','DONE');





