CREATE SEQUENCE event_participants_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE event_participants (
    id BIGINT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount_owed NUMERIC(10, 2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_event_participants_event
        FOREIGN KEY (event_id)
        REFERENCES events (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_event_participants_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE RESTRICT,

    CONSTRAINT uk_event_participants_event_user UNIQUE (event_id, user_id),
    CONSTRAINT ck_event_participants_amount_owed_non_negative CHECK (amount_owed >= 0),
    CONSTRAINT ck_event_participants_payment_status
        CHECK (payment_status IN ('PENDING', 'PAID', 'CANCELLED')),
    CONSTRAINT ck_event_participants_paid_at
        CHECK (
            (payment_status = 'PAID' AND paid_at IS NOT NULL)
            OR (payment_status IN ('PENDING', 'CANCELLED') AND paid_at IS NULL)
        )
    );

CREATE INDEX idx_event_participants_event_id ON event_participants (event_id);
CREATE INDEX idx_event_participants_user_id ON event_participants (user_id);
CREATE INDEX idx_event_participants_payment_status
    ON event_participants (event_id, payment_status);