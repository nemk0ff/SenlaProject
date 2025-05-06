INSERT INTO users (email, role, password, name, surname, birthdate, gender, about_me, registered_at)
VALUES ('ivanov_arkadiy@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Arkadiy', 'Ivanov', '1995-05-15', 'MALE',
        'MSU EF student / trader', '2025-04-09 10:15:00+03'),
       ('petrova_anna@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Anna', 'Petrova', '1998-08-22', 'FEMALE',
        'Python developer, Yandex', '2025-04-09 11:30:00+03'),
       ('sidorov_dmitry@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Dmitry', 'Sidorov', '1992-11-30', 'MALE',
        'Will be a Top G', '2025-04-09 12:45:00+03'),
       ('smirnova_elena@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Elena', 'Smirnova', '1990-03-10', 'FEMALE',
        'Акционер технологических компаний', '2025-04-09 14:00:00+03'),
       ('kozlov_alexey@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Alexey', 'Kozlov', '2000-01-01', 'MALE',
        'Профессиональный рыбак, участник турниров', '2025-04-09 15:15:00+03'),
       ('nikolaeva_olga@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Olga', 'Nikolaeva', '1997-07-18', 'FEMALE',
        'Спортсменка, мастер спорта по плаванию', '2025-04-09 16:30:00+03'),
       ('fedorov_maxim@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Maxim', 'Fedorov', '2002-09-25', 'MALE',
        'Студент MBA, будущий предприниматель', '2025-04-09 17:45:00+03'),
       ('morozova_ekaterina@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Ekaterina', 'Morozova', '1993-04-05',
        'FEMALE', 'UX/UI дизайнер в крупной IT компании', '2025-04-09 19:00:00+03'),
       ('volkov_andrey@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Andrey', 'Volkov', '2005-12-12', 'MALE',
        'Увлекаюсь робототехникой', '2025-04-09 20:15:00+03'),
       ('orlova_maria@senla.ru', 'USER', '$2a$10$Smy9JptsypUyVBHOTMidTeh3pEZ9UA1uoGv6H5yLhqXXdLMkQsTKK', 'Maria', 'Orlova', '2007-06-20', 'FEMALE',
        'Будущий врач, учусь в медицинском колледже', '2025-04-09 21:30:00+03'),
       ('admin@senla.ru', 'ADMIN', '$2a$10$zp4b4MEUJJPBESQka4E7tuHFYnGMAwYa2OqZxLo5LeNX66fLeKa4m', 'Admin', 'Senla','1995-04-20', 'MALE',
        'Java Web Developer, Founder of this application', '2025-04-09 09:00:00+03');



INSERT INTO communities (name, owner_id, description, created_at)
VALUES
    ('Трейдеры и инвесторы', 1, 'Сообщество для обсуждения рынков и инвестиций', '2025-04-10 09:00:00+03'),
    ('Java Developers', 11, 'Сообщество Java-разработчиков. Spring, Hibernate, Jakarta EE', '2025-04-11 10:00:00+03'),
    ('Спортивное плавание', 6, 'Все о плавании: тренировки, соревнования, экипировка', '2025-04-12 11:00:00+03');

INSERT INTO group_members (user_id, community_id, join_date, role, gm_type, is_banned, banned_reason)
VALUES
-- "Трейдеры и инвесторы"
(1, 1, '2025-04-10 09:05:00+03', 'ADMIN', 'COMMUNITY', false, NULL),
(3, 1, '2025-04-10 10:15:00+03', 'MEMBER', 'COMMUNITY', false, NULL),
(4, 1, '2025-04-10 11:30:00+03', 'MEMBER', 'COMMUNITY', true, 'Нарушение правил сообщества: реклама'),

-- "Java Developers"
(11, 2, '2025-04-11 10:05:00+03', 'ADMIN', 'COMMUNITY', false, NULL),
(2, 2, '2025-04-11 12:45:00+03', 'MODERATOR', 'COMMUNITY', false, NULL),
(5, 2, '2025-04-11 14:20:00+03', 'MEMBER', 'COMMUNITY', false, NULL),
(7, 2, '2025-04-11 15:30:00+03', 'MEMBER', 'COMMUNITY', true, 'Флуд и оффтоп'),

-- "Спортивное плавание"
(6, 3, '2025-04-12 11:05:00+03', 'ADMIN', 'COMMUNITY', false, NULL),
(8, 3, '2025-04-12 13:10:00+03', 'MEMBER', 'COMMUNITY', false, NULL),
(10, 3, '2025-04-12 15:40:00+03', 'MEMBER', 'COMMUNITY', true, 'Оскорбление участников');

INSERT INTO posts (author_id, community_id, body, created_at, post_type)
VALUES
    (1, 1, 'Сегодня сильное движение на нефтяном рынке, все в шорте?', '2025-04-10 09:30:00+03', 'COMMUNITY'),
    (3, 1, 'Кто что думает про акции Tesla после последнего отчета?', '2025-04-10 14:45:00+03', 'COMMUNITY'),

    (11, 2, 'Переходим на Spring Boot 3.2 - основные изменения', '2025-04-11 11:20:00+03', 'COMMUNITY'),
    (2, 2, 'Как правильно делать интеграционные тесты в Spring?', '2025-04-11 13:45:00+03', 'COMMUNITY'),
    (5, 2, 'Hibernate vs JPA - в чем разница на практике?', '2025-04-11 16:30:00+03', 'COMMUNITY'),
    (11, 2, 'Новый LTS релиз Java 21 - кто уже перешел?', '2025-04-11 17:50:00+03', 'COMMUNITY'),

    (6, 3, 'Подготовка к чемпионату Москвы - график тренировок', '2025-04-12 12:15:00+03', 'COMMUNITY'),
    (8, 3, 'Обзор новых гидрокостюмов 2025 года', '2025-04-12 17:50:00+03', 'COMMUNITY');