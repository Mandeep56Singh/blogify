CREATE TABLE users
(
    id               UUID         NOT NULL,
    email            VARCHAR(255) NOT NULL,
    user_name        VARCHAR(100) NOT NULL,
    password         VARCHAR(255) NOT NULL,
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    role             VARCHAR(20)  NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL,
    last_modified_at TIMESTAMPTZ,
    version          BIGINT       NOT NULL DEFAULT 0,


    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_user_name UNIQUE (user_name),
    CONSTRAINT chk_users_role CHECK (role IN ('USER', 'ADMIN')),
    CONSTRAINT user_name_length_check CHECK ( CHAR_LENGTH(user_name) >= 2 AND CHAR_LENGTH(user_name) <= 120)
);