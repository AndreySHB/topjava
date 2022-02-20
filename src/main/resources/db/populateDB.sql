DELETE
FROM user_roles;
DELETE
FROM meals;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, date, description, calories)
VALUES (100000, '2022-05-17 10:04:10', 'макароны с сыром', 1200),
       (100000, '2022-05-17 15:04:10', 'печенка с ананасом', 795),
       (100000, '2022-05-17 19:04:10', 'жаренный круассан', 500),
       (100001, '2022-05-17 10:06:10', 'вишенка', 5),
       (100001, '2022-05-17 15:06:10', 'омлет', 1200),
       (100001, '2022-05-17 19:06:10', 'гибискус в шоколаде', 794),
       (100002, '2022-05-17 00:00:00', 'ничего не предложили', 0);
