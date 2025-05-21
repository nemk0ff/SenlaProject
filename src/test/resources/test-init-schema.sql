CREATE TABLE IF NOT EXISTS users
(
    id            SERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    role          VARCHAR(10)  NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    name          VARCHAR(255) NOT NULL,
    surname       VARCHAR(32)  NOT NULL,
    birthdate     DATE,
    profile_type  VARCHAR(10)  NOT NULL CHECK (profile_type IN ('OPEN', 'CLOSED')),
    gender        VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    about_me      VARCHAR(1000),
    registered_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS friend_requests
(
    id           SERIAL PRIMARY KEY,
    sender_id    INTEGER     NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    recipient_id INTEGER     NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status       VARCHAR(10) NOT NULL CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED')),
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS communities
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255),
    description VARCHAR(1000),
    type        VARCHAR(32),
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chats
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255),
    is_group   BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS group_members
(
    id            SERIAL PRIMARY KEY,
    user_id       INTEGER                  NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    chat_id       INTEGER REFERENCES chats (id) ON DELETE CASCADE,
    community_id  INTEGER REFERENCES communities (id) ON DELETE CASCADE,
    muted_until   TIMESTAMP WITH TIME ZONE,
    is_banned     BOOLEAN,
    banned_reason VARCHAR(255),
    join_date     TIMESTAMP WITH TIME ZONE NOT NULL,
    leave_date    TIMESTAMP WITH TIME ZONE,
    role          VARCHAR(32)              NOT NULL,
    gm_type       VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS posts
(
    id            SERIAL PRIMARY KEY,
    author_id     INTEGER REFERENCES group_members (id) ON DELETE CASCADE,
    community_id  INTEGER REFERENCES communities (id) ON DELETE CASCADE,
    wall_owner_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    is_pinned     BOOLEAN,
    mood          VARCHAR(32),
    location      VARCHAR(255),
    body          VARCHAR(2000),
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    post_type     VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS content_fragments
(
    id          SERIAL PRIMARY KEY,
    author_id   INTEGER       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    chat_id     INTEGER REFERENCES chats (id) ON DELETE CASCADE,
    post_id     INTEGER REFERENCES posts (id) ON DELETE CASCADE,
    body        VARCHAR(2000) NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    cf_type     VARCHAR(32),
    is_pinned   BOOLEAN,
    reply_to_id INTEGER       REFERENCES content_fragments (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS reactions
(
    id         SERIAL PRIMARY KEY,
    type       VARCHAR(10) NOT NULL CHECK (type IN ('LIKE', 'DISLIKE')),
    user_id    INTEGER REFERENCES users (id) ON DELETE CASCADE,
    comment_id INTEGER REFERENCES content_fragments (id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

CREATE INDEX idx_friend_requests_sender_recipient_status ON friend_requests(sender_id, recipient_id, status);
CREATE INDEX idx_friend_requests_recipient_sender_status ON friend_requests(recipient_id, sender_id, status);

CREATE INDEX idx_chats_is_group ON chats(is_group);
CREATE INDEX idx_chats_created_at ON chats(created_at);

CREATE INDEX idx_group_members_user_id ON group_members(user_id);
CREATE INDEX idx_group_members_chat_id ON group_members(chat_id);
CREATE INDEX idx_group_members_community_id ON group_members(community_id);
CREATE INDEX idx_group_members_join_date_leave_date ON group_members(join_date, leave_date);
CREATE INDEX idx_group_members_role ON group_members(role);

CREATE INDEX idx_posts_author_id ON posts(author_id);
CREATE INDEX idx_posts_community_id ON posts(community_id);
CREATE INDEX idx_posts_wall_owner_id ON posts(wall_owner_id);
CREATE INDEX idx_posts_is_pinned ON posts(is_pinned);
CREATE INDEX idx_posts_created_at ON posts(created_at);

CREATE INDEX idx_content_fragments_author_id ON content_fragments(author_id);
CREATE INDEX idx_content_fragments_chat_id ON content_fragments(chat_id);
CREATE INDEX idx_content_fragments_post_id ON content_fragments(post_id);
CREATE INDEX idx_content_fragments_is_pinned ON content_fragments(is_pinned);
CREATE INDEX idx_content_fragments_reply_to_id ON content_fragments(reply_to_id);
CREATE INDEX idx_content_fragments_created_at ON content_fragments(created_at);

CREATE INDEX idx_reactions_user_id ON reactions(user_id);
CREATE INDEX idx_reactions_comment_id ON reactions(comment_id);
CREATE INDEX idx_reactions_type ON reactions(type);

CREATE INDEX idx_chat_member_active ON group_members(chat_id, user_id)
    WHERE leave_date IS NULL OR join_date > leave_date;

CREATE INDEX idx_friend_request_status ON friend_requests(status);

CREATE INDEX idx_community_member_active ON group_members(community_id, user_id)
    WHERE leave_date IS NULL OR join_date > leave_date;

CREATE INDEX idx_content_fragments_chat_id_created_at ON content_fragments(chat_id, created_at);

CREATE INDEX idx_posts_community_id_created_at ON posts(community_id, created_at);

-- DROP TABLE users CASCADE;
-- DROP TABLE friend_requests CASCADE;
-- DROP TABLE group_members CASCADE;
-- DROP TABLE content_fragments CASCADE;
-- DROP TABLE chats CASCADE;
-- DROP TABLE communities CASCADE;
-- DROP TABLE posts CASCADE;
-- DROP TABLE reactions;