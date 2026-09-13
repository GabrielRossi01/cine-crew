ALTER TABLE users ADD COLUMN username VARCHAR(30);

UPDATE users
SET username = 'user_' || id
WHERE username IS NULL;

ALTER TABLE users
    ALTER COLUMN username SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT uk_users_username UNIQUE (username);