CREATE SEQUENCE events_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE events (
    id BIGINT PRIMARY KEY,
    club_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    organizer_id BIGINT NOT NULL,
    session_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    cinema_name VARCHAR(150) NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_events_club
        FOREIGN KEY (club_id)
        REFERENCES clubs (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_events_movie
        FOREIGN KEY (movie_id)
        REFERENCES movies (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_events_organizer
        FOREIGN KEY (organizer_id)
        REFERENCES users (id)
        ON DELETE RESTRICT,

    CONSTRAINT ck_events_total_amount_positive CHECK (total_amount > 0),
    CONSTRAINT ck_events_status CHECK (status IN ('OPEN', 'SETTLED', 'CANCELLED'))
);

CREATE INDEX idx_events_club_id ON events (club_id);
CREATE INDEX idx_events_movie_id ON events (movie_id);
CREATE INDEX idx_events_organizer_id ON events (organizer_id);
CREATE INDEX idx_events_session_datetime ON events (session_datetime);