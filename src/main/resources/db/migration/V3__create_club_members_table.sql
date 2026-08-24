CREATE SEQUENCE club_members_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE club_members (
    id BIGINT PRIMARY KEY,
    club_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    club_score INTEGER NOT NULL DEFAULT 0,
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_club_members_club
        FOREIGN KEY (club_id)
        REFERENCES clubs (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_club_members_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT uk_club_members_club_user UNIQUE (club_id, user_id),
    CONSTRAINT ck_club_members_role CHECK (role IN ('ADMIN', 'MEMBER')),
    CONSTRAINT ck_club_members_score_non_negative CHECK (club_score >= 0)
);

CREATE INDEX idx_club_members_club_id ON club_members (club_id);
CREATE INDEX idx_club_members_user_id ON club_members (user_id);