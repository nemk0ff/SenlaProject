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

--changeset admin:insert-friend-requests
INSERT INTO friendRequests (sender_id, recipient_id, status, created_at)
VALUES
    -- Pending requests
    (1, 2, 'PENDING', '2025-04-10 09:15:00+03'),
    (1, 3, 'PENDING', '2025-04-10 10:20:00+03'),
    (2, 4, 'PENDING', '2025-04-10 11:25:00+03'),
    (3, 5, 'PENDING', '2025-04-10 12:30:00+03'),
    (4, 6, 'PENDING', '2025-04-10 13:35:00+03'),

    -- Accepted requests
    (2, 1, 'ACCEPTED', '2025-04-09 14:40:00+03'),
    (3, 2, 'ACCEPTED', '2025-04-09 15:45:00+03'),
    (5, 3, 'ACCEPTED', '2025-04-09 16:50:00+03'),
    (6, 4, 'ACCEPTED', '2025-04-09 17:55:00+03'),
    (7, 5, 'ACCEPTED', '2025-04-09 18:00:00+03'),

    -- Rejected requests
    (8, 1, 'REJECTED', '2025-04-08 19:05:00+03'),
    (9, 2, 'REJECTED', '2025-04-08 20:10:00+03'),
    (10, 3, 'REJECTED', '2025-04-08 21:15:00+03'),
    (4, 7, 'REJECTED', '2025-04-08 22:20:00+03'),
    (5, 8, 'REJECTED', '2025-04-08 23:25:00+03');

--rollback DELETE FROM friendRequests;
--rollback DELETE FROM users;
