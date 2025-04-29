CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(32) NOT NULL,
    birthdate DATE,
    gender VARCHAR(32) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    about_me VARCHAR(1000),
    registered_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS friend_requests (
    id SERIAL PRIMARY KEY,
    sender_id INTEGER     NOT NULL REFERENCES users (id),
    recipient_id INTEGER     NOT NULL REFERENCES users (id),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS chats (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    is_group BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE
    );

CREATE TABLE IF NOT EXISTS group_members (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    chat_id INTEGER REFERENCES chats(id),
    community_id INTEGER,
    muted_until TIMESTAMP WITH TIME ZONE,
    is_banned BOOLEAN,
    banned_reason VARCHAR(255),
    join_date TIMESTAMP WITH TIME ZONE NOT NULL,
    role VARCHAR(32) NOT NULL,
    gm_type VARCHAR(32)
    );

CREATE TABLE IF NOT EXISTS content_fragments (
    id SERIAL PRIMARY KEY,
    author_id INTEGER NOT NULL REFERENCES users(id),
    chat_id INTEGER REFERENCES chats(id),
    community_id INTEGER,
    body VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    cf_type VARCHAR(32),
    likes_count INTEGER,
    dislikes_count INTEGER,
    is_pinned BOOLEAN,
    reply_to_id INTEGER REFERENCES content_fragments(id)
    );

CREATE TABLE IF NOT EXISTS posts (
    id SERIAL PRIMARY KEY,
    author_id INTEGER NOT NULL REFERENCES users(id),
    community_id INTEGER,
    wall_owner_id INTEGER REFERENCES users(id),
    tags VARCHAR(255),
    is_pinned BOOLEAN,
    mood VARCHAR(32),
    location VARCHAR(255),
    body VARCHAR(2000),
    created_at TIMESTAMP WITH TIME ZONE,
    post_type VARCHAR(32)
    );

CREATE TABLE IF NOT EXISTS communities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    owner_id INTEGER REFERENCES users(id),
    description VARCHAR(1000),
    type VARCHAR(32),
    created_at TIMESTAMP WITH TIME ZONE
    );

--DROP TABLE users CASCADE;
--DROP TABLE content_fragments CASCADE;
--DROP TABLE posts CASCADE;
--DROP TABLE group_members CASCADE;
--DROP TABLE chats CASCADE;
--DROP TABLE communities;
--DROP TABLE friend_requests;
