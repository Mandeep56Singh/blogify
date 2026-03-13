CREATE TABLE posts
(
    id               UUID         NOT NULL,
    title            VARCHAR(150) NOT NULL,
    slug             VARCHAR(150) NOT NULL,
    content          TEXT         NOT NULL,
    author_id        UUID         NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    published_at     TIMESTAMPTZ,
    created_at       TIMESTAMPTZ  NOT NULL,
    last_modified_at TIMESTAMPTZ,
    version          BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_posts PRIMARY KEY (id),
    CONSTRAINT uq_posts_slug UNIQUE (slug),
    CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT chk_posts_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED')),
    CONSTRAINT title_length_check CHECK (CHAR_LENGTH(title) >= 5 AND CHAR_LENGTH(title) <= 150),
    CONSTRAINT content_min_length_check CHECK (CHAR_LENGTH(content) >= 100)
);

CREATE INDEX idx_posts_status ON posts (status);
CREATE INDEX idx_posts_author_id ON posts (author_id);
CREATE INDEX idx_posts_published_at ON posts (published_at DESC);