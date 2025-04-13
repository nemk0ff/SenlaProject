--liquibase formatted sql

--changeset admin:insert-test-data
INSERT INTO users (mail, role, password, firstname, lastname, birthdate, gender, about_me, registered_at)
VALUES ('ivanov_arkadiy@senla.ru', 'USER', '$2a$10$xJwL5v5Jz5U5Z5U5Z5U5Zu', 'Arkadiy', 'Ivanov', '1995-05-15', 'MALE',
        'MSU EF student / trader', '2025-04-09 10:15:00+03'),
       ('petrova_anna@senla.ru', 'USER', '$2a$10$xJwL5v5Jz5U5Z5U5Z5U5Zu', 'Anna', 'Petrova', '1998-08-22', 'FEMALE',
        'Python developer, Yandex', '2025-04-09 11:30:00+03'),
       ('sidorov_dmitry@senla.ru', 'USER', '$2a$10$xJwL5v5Jz5U5Z5U5Z5U5Zu', 'Dmitry', 'Sidorov', '1992-11-30', 'MALE',
        'Will be a Top G', '2025-04-09 12:45:00+03'),
       ('smirnova_elena@senla.ru', 'USER', '$2a$10$xJwL5v5Jz5U5Z5U5Z5U5Zu', 'Elena', 'Smirnova', '1990-03-10', 'FEMALE',
        'Акционер технологических компаний', '2025-04-09 14:00:00+03'),
       ('kozlov_alexey@senla.ru', 'USER', '$2a$10$xJwL5v5Jz5U5Z5U5Z5U5Zu', 'Alexey', 'Kozlov', '2000-01-01', 'MALE',
        'Профессиональный рыбак, участник турниров', '2025-04-09 15:15:00+03'),
       ('nikolaeva_olga@senla.ru', 'USER', '$2a$10$xJwL5v5Jz5U5Z5U5Z5U5Zu', 'Olga', 'Nikolaeva', '1997-07-18', 'FEMALE',
        'Спортсменка, мастер спорта по плаванию', '2025-04-09 16:30:00+03'),
       ('fedorov_maxim@senla.ru', 'USER', '$2a$10$xJwL5v5Jz5U5Z5U5Z5U5Zu', 'Maxim', 'Fedorov', '2002-09-25', 'MALE',
        'Студент MBA, будущий предприниматель', '2025-04-09 17:45:00+03'),
       ('morozova_ekaterina@senla.ru', 'USER', '$2a$10$xJwL5v5Jz5U5Z5U5Z5U5Zu', 'Ekaterina', 'Morozova', '1993-04-05',
        'FEMALE', 'UX/UI дизайнер в крупной IT компании', '2025-04-09 19:00:00+03'),
       ('volkov_andrey@senla.ru', 'USER', '$2a$10$xJwL5v5Jz5U5Z5U5Z5U5Zu', 'Andrey', 'Volkov', '2005-12-12', 'MALE',
        'Увлекаюсь робототехникой', '2025-04-09 20:15:00+03'),
       ('orlova_maria@senla.ru', 'USER', '$2a$10$xJwL5v5Jz5U5Z5U5Z5U5Zu', 'Maria', 'Orlova', '2007-06-20', 'FEMALE',
        'Будущий врач, учусь в медицинском колледже', '2025-04-09 21:30:00+03');

--rollback DELETE FROM users;
