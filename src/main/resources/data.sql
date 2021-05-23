INSERT INTO USERS (EMAIL, FIRST_NAME, LAST_NAME, PASSWORD)
VALUES ('user@mail.com', 'User_First', 'User_Last', '{noop}user'),
       ('user2@mail.com', 'UserTo_First', 'UserTo_Last', '{noop}user'),
       ('admin@mail.com', 'Admin_First', 'Admin_Last', '{noop}admin');

INSERT INTO USER_ROLE (ROLE, USER_ID)
VALUES ('USER', 1),
       ('USER', 2),
       ('ADMIN', 3),
       ('USER', 3);

INSERT INTO restaurants (NAME)
VALUES ('Tomato'),
       ('Varvara'),
       ('Kroshka Kartoshka');


INSERT INTO MENU (DATES, DESCRIPTION, PRICE, RESTAURANT_ID)
VALUES (NOW(), 'RICE', '10000', '1'),
       (NOW(), 'BEANS', '5000', '2'),
       (NOW(), 'WATER', '5000', '1'),
       (NOW(), 'COLA', '5000', '3'),
       (NOW(), 'TEA', '5000', '2'),
       (NOW(), 'CAKE', '5000', '2'),
       (NOW(), 'SOUP', '15000', '3'),
       (NOW()-1, 'SOUP', '15000', '3'),
       (NOW()-2, 'SOUP', '15000', '3');

INSERT INTO VOTE (DATES, RESTAURANT_ID, USER_ID)
VALUES (NOW(), '3', '2'),
       (NOW(), '3', '3'),
       (NOW()-1, '3', '3');