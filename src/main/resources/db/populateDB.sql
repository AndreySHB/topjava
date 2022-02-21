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

INSERT INTO meals (user_id, datetime, description, calories)
VALUES (100000, '2022-05-16 10:04', 'макароны с сыром', 1200),
       (100000, '2022-05-17 15:04', 'печенка с ананасом', 795),
       (100000, '2022-05-17 19:04', 'жаренный круассан', 500),
       (100001, '2022-05-17 10:06', 'вишенка', 5),
       (100001, '2022-05-17 15:06', 'омлет', 1200),
       (100001, '2022-05-17 19:06', 'гибискус в шоколаде', 794),
       (100001, '2022-05-31 00:00', 'Еда на граничное значение', 444),
       (100002, '2022-05-17 00:00', 'ничего не предложили', 0);
