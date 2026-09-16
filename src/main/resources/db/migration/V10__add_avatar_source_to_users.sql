ALTER TABLE users
    ADD COLUMN avatar_source VARCHAR(20);

UPDATE users
SET avatar_source =
        CASE
            WHEN avatar_url IS NULL OR avatar_url = '' THEN 'NONE'
            ELSE 'GOOGLE'
            END
WHERE avatar_source IS NULL;

ALTER TABLE users
    ALTER COLUMN avatar_source SET DEFAULT 'NONE';

ALTER TABLE users
    ALTER COLUMN avatar_source SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT ck_users_avatar_source
        CHECK (avatar_source IN ('NONE', 'GOOGLE', 'UPLOAD'));