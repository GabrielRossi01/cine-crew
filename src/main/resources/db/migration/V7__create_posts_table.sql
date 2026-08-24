CREATE SEQUENCE posts_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE posts (
    id BIGINT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    caption VARCHAR(1000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_posts_event
        FOREIGN KEY (event_id)
        REFERENCES events (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_posts_author
        FOREIGN KEY (author_id)
        REFERENCES users (id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_posts_event_id ON posts (event_id);
CREATE INDEX idx_posts_author_id ON posts (author_id);
CREATE INDEX idx_posts_created_at ON posts (created_at DESC);