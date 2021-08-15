CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(30)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    expired    BOOLEAN      NOT NULL DEFAULT false,
    created_at TIMESTAMP    NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX users_username_idx ON users ((lower (username)) );

CREATE TABLE contacts
(
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    FOREIGN KEY (user1_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX contacts_user1_id_user2_id_idx ON contacts (user1_id, user2_id);

CREATE TABLE chat_messages
(
    id         BIGSERIAL PRIMARY KEY,
    chat_id    VARCHAR(255) NOT NULL,
    author_id  BIGINT       NOT NULL,
    text       TEXT         NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX chat_messages_chat_id_idx ON chat_messages (chat_id);
CREATE INDEX chat_messages_created_at_idx ON chat_messages (created_at DESC);
