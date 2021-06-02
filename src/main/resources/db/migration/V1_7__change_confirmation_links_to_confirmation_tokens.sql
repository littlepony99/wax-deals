ALTER TABLE IF EXISTS confirmation_links
RENAME TO confirmation_tokens;

ALTER TABLE confirmation_tokens
    ADD COLUMN token uuid NOT NULL
        CONSTRAINT unique_token UNIQUE,
    ADD COLUMN created_at TIMESTAMP without time zone NOT NULL,
    ADD CONSTRAINT unique_user_id UNIQUE (user_id),
    DROP COLUMN IF EXISTS confirmation_link,
    DROP COLUMN IF EXISTS date_and_time;
