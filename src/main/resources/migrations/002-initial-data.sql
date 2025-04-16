--liquibase formatted sql

--changeset admin:insert-user-raws
INSERT INTO users (email, role, password, name, surname, birthdate, gender, about_me, registered_at)
VALUES ('ivanov_arkadiy@senla.ru', 'USER', '$2a$10$nGSytfuznHGN4lLgtYxI8.fR6wECXaYX8WaQwXZYOVV/F75s3dUji', 'Arkadiy', 'Ivanov', '1995-05-15', 'MALE',
        'MSU EF student / trader', '2025-04-09 10:15:00+03'),
       ('petrova_anna@senla.ru', 'USER', '$2a$10$nGSytfuznHGN4lLgtYxI8.fR6wECXaYX8WaQwXZYOVV/F75s3dUji', 'Anna', 'Petrova', '1998-08-22', 'FEMALE',
        'Python developer, Yandex', '2025-04-09 11:30:00+03'),
       ('sidorov_dmitry@senla.ru', 'USER', '$2a$10$nGSytfuznHGN4lLgtYxI8.fR6wECXaYX8WaQwXZYOVV/F75s3dUji', 'Dmitry', 'Sidorov', '1992-11-30', 'MALE',
        'Will be a Top G', '2025-04-09 12:45:00+03'),
       ('smirnova_elena@senla.ru', 'USER', '$2a$10$nGSytfuznHGN4lLgtYxI8.fR6wECXaYX8WaQwXZYOVV/F75s3dUji', 'Elena', 'Smirnova', '1990-03-10', 'FEMALE',
        'Акционер технологических компаний', '2025-04-09 14:00:00+03'),
       ('kozlov_alexey@senla.ru', 'USER', '$2a$10$nGSytfuznHGN4lLgtYxI8.fR6wECXaYX8WaQwXZYOVV/F75s3dUji', 'Alexey', 'Kozlov', '2000-01-01', 'MALE',
        'Профессиональный рыбак, участник турниров', '2025-04-09 15:15:00+03'),
       ('nikolaeva_olga@senla.ru', 'USER', '$2a$10$nGSytfuznHGN4lLgtYxI8.fR6wECXaYX8WaQwXZYOVV/F75s3dUji', 'Olga', 'Nikolaeva', '1997-07-18', 'FEMALE',
        'Спортсменка, мастер спорта по плаванию', '2025-04-09 16:30:00+03'),
       ('fedorov_maxim@senla.ru', 'USER', '$2a$10$nGSytfuznHGN4lLgtYxI8.fR6wECXaYX8WaQwXZYOVV/F75s3dUji', 'Maxim', 'Fedorov', '2002-09-25', 'MALE',
        'Студент MBA, будущий предприниматель', '2025-04-09 17:45:00+03'),
       ('morozova_ekaterina@senla.ru', 'USER', '$2a$10$nGSytfuznHGN4lLgtYxI8.fR6wECXaYX8WaQwXZYOVV/F75s3dUji', 'Ekaterina', 'Morozova', '1993-04-05',
        'FEMALE', 'UX/UI дизайнер в крупной IT компании', '2025-04-09 19:00:00+03'),
       ('volkov_andrey@senla.ru', 'USER', '$2a$10$nGSytfuznHGN4lLgtYxI8.fR6wECXaYX8WaQwXZYOVV/F75s3dUji', 'Andrey', 'Volkov', '2005-12-12', 'MALE',
        'Увлекаюсь робототехникой', '2025-04-09 20:15:00+03'),
       ('orlova_maria@senla.ru', 'USER', '$2a$10$nGSytfuznHGN4lLgtYxI8.fR6wECXaYX8WaQwXZYOVV/F75s3dUji', 'Maria', 'Orlova', '2007-06-20', 'FEMALE',
        'Будущий врач, учусь в медицинском колледже', '2025-04-09 21:30:00+03');

--changeset admin:insert-30-unique-requests
INSERT INTO friendRequests (sender_id, recipient_id, status, created_at)
VALUES
-- Первые 10 заявок (каждый отправляет по 1 заявке)
(1, 5, 'PENDING', '2025-04-10 09:00:00+03'),
(2, 3, 'ACCEPTED', '2025-04-10 10:15:00+03'),
(3, 4, 'REJECTED', '2025-04-10 11:30:00+03'),
(4, 5, 'PENDING', '2025-04-10 12:45:00+03'),
(5, 6, 'ACCEPTED', '2025-04-10 14:00:00+03'),
(6, 7, 'REJECTED', '2025-04-11 09:15:00+03'),
(7, 8, 'PENDING', '2025-04-11 10:30:00+03'),
(8, 9, 'ACCEPTED', '2025-04-11 11:45:00+03'),
(9, 10, 'REJECTED', '2025-04-11 13:00:00+03'),
(10, 1, 'PENDING', '2025-04-11 14:15:00+03'),

-- Следующие 10 заявок (продолжаем уникальные пары)
(1, 3, 'ACCEPTED', '2025-04-12 08:30:00+03'),
(2, 4, 'REJECTED', '2025-04-12 09:45:00+03'),
(3, 5, 'PENDING', '2025-04-12 11:00:00+03'),
(4, 6, 'ACCEPTED', '2025-04-12 12:15:00+03'),
(5, 7, 'REJECTED', '2025-04-12 13:30:00+03'),
(6, 8, 'PENDING', '2025-04-13 10:00:00+03'),
(7, 9, 'ACCEPTED', '2025-04-13 11:15:00+03'),
(8, 10, 'REJECTED', '2025-04-13 12:30:00+03'),
(9, 1, 'PENDING', '2025-04-13 13:45:00+03'),
(10, 2, 'ACCEPTED', '2025-04-13 15:00:00+03'),

-- Последние 10 заявок (оставшиеся уникальные пары)
(1, 4, 'REJECTED', '2025-04-14 09:30:00+03'),
(2, 5, 'PENDING', '2025-04-14 10:45:00+03'),
(3, 6, 'ACCEPTED', '2025-04-14 12:00:00+03'),
(4, 7, 'REJECTED', '2025-04-14 13:15:00+03'),
(5, 8, 'PENDING', '2025-04-15 08:45:00+03'),
(6, 9, 'ACCEPTED', '2025-04-15 10:00:00+03'),
(7, 10, 'REJECTED', '2025-04-15 11:15:00+03'),
(8, 1, 'PENDING', '2025-04-15 12:30:00+03'),
(9, 2, 'ACCEPTED', '2025-04-15 13:45:00+03'),
(10, 3, 'REJECTED', '2025-04-16 09:00:00+03');

--rollback DELETE FROM friendRequests;
--rollback DELETE FROM users;
