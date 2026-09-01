CREATE SEQUENCE watchlist_items_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE watchlist_items (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uk_watchlist_user_movie UNIQUE (user_id, movie_id)
);

CREATE INDEX idx_watchlist_items_user_id ON watchlist_items(user_id);