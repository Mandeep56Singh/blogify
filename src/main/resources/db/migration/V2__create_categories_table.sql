CREATE TABLE categories
(
    id               UUID         NOT NULL,
    title            VARCHAR(120) NOT NULL,
    description      TEXT         NOT NULL DEFAULT '',
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at       TIMESTAMPTZ  NOT NULL,
    last_modified_at TIMESTAMPTZ,
    version          BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT chk_categories_status CHECK (status IN ('ACTIVE', 'ARCHIVED'))
);