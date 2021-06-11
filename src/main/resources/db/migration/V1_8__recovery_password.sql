create table recovery_password_tokens
(
    id          SERIAL    PRIMARY KEY,
    user_id     INTEGER   NOT NULL UNIQUE,
    token       UUID      NOT NULL UNIQUE,
    created_at  TIMESTAMP without time zone NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

UPDATE shops
   SET link_to_main_page = 'https://www.triplevision.nl/'
 WHERE id = 10;
