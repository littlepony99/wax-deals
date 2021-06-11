DROP TABLE confirmation_links;

create table confirmation_tokens
(
    id          SERIAL    PRIMARY KEY,
    user_id     INTEGER   NOT NULL UNIQUE,
    token       UUID      NOT NULL UNIQUE,
    created_at  TIMESTAMP without time zone NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);