CREATE TABLE post_categories
(
    post_id     UUID NOT NULL,
    category_id UUID NOT NULL,

    CONSTRAINT pk_post_categories PRIMARY KEY (post_id, category_id),
    CONSTRAINT fk_post_categories_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_post_categories_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE INDEX idx_post_categories_category_id ON post_categories (category_id);