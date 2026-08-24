CREATE SEQUENCE movies_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE movies (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    tmdb_id BIGINT,
    poster_url VARCHAR(500),
    release_year SMALLINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_movies_tmdb_id UNIQUE (tmdb_id),
    CONSTRAINT ck_movies_release_year_valid
        CHECK (release_year IS NULL OR release_year BETWEEN 1888 AND 2100)
);