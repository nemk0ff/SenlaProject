--liquibase formatted sql

--changeset admin:create-tables-user-&-friendRequests
CREATE TABLE IF NOT EXISTS Users (
                                     id SERIAL PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(32) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(32) NOT NULL,
    lastname VARCHAR(32) NOT NULL,
    birthdate DATE,
    gender VARCHAR(32) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    about_me VARCHAR(1000),
    registered_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                                );

CREATE TABLE IF NOT EXISTS friendRequests
(
    id           SERIAL PRIMARY KEY,
    sender_id    INTEGER     NOT NULL REFERENCES Users (id),
    recipient_id INTEGER     NOT NULL REFERENCES Users (id),
    status       VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                               );

--rollback DROP TABLE FriendRequests;
--rollback DROP TABLE Users;