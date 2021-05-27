create table users
(
    id         serial        not null
        constraint users_pkey
            primary key,
    email      varchar(500)  not null
        constraint unique_email
            unique,
    salt       varchar(500)  not null,
    iterations integer       not null,
    password   varchar(1000) not null
        constraint unique_password
            unique,
    status     boolean       not null,
    role       varchar(50)   not null
        constraint either_of_two_roles
            check (((role)::text = 'USER'::text) OR ((role)::text = 'ADMIN'::text)),
    discogs_user_name      varchar(500)
        constraint unique_discogs_user_name
            unique
);

create table unique_vinyls
(
    id            bigint        not null
        constraint unique_id
            unique,
    release       varchar(200)  not null,
    artist        varchar(200)  not null,
    full_name     varchar(400)  not null,
    link_to_image varchar(1000) not null,
    has_offers    boolean       not null
);

create table shops
(
    id                serial       not null
        constraint shops_pkey
            primary key,
    link_to_main_page varchar(500) not null,
    link_to_image     varchar(500) not null,
    name              varchar(500) not null
);

create table user_posts
(
    id      serial        not null
        constraint user_posts_pkey
            primary key,
    user_id integer       not null
        constraint user_id_fk
            references users,
    email   varchar(100)  not null,
    name    varchar(500)  not null,
    theme   varchar(500)  not null,
    message varchar(1000) not null
);

create table confirmation_links
(
    id                serial       not null
        constraint confirmation_links_pkey
            primary key,
    user_id           integer      not null
        constraint user_id_fk
            references users,
    confirmation_link varchar(500) not null,
    date_and_time timestamp without time zone not null
);

create table offers
(
    id              bigserial        not null
        constraint offers_pkey
            primary key,
    unique_vinyl_id integer       not null
        constraint unique_vinyl_id_fk
            references unique_vinyls (id),
    shop_id         integer       not null
        constraint shop_id_fk
            references shops
        constraint chk_shop_id
            check (shop_id > 0),
    price           double precision not null,
    currency        character varying(50) COLLATE pg_catalog."default" NOT NULL
        constraint either_of_four_currencies
            check (((currency)::text = 'UAH'::text) OR ((currency)::text = 'USD'::text) OR ((currency)::text = 'GBP'::text) OR ((currency)::text = 'EUR'::text)),
    genre           varchar(100),
    link_to_offer   varchar(1000) not null
);

create table unique_vinyls_browsing_history
(
    id              serial  not null
        constraint unique_vinyls_browsing_history_pkey
            primary key,
    user_id         integer not null
        constraint user_id_fk
            references users,
    unique_vinyl_id integer not null
        constraint unique_vinyls_id_fk
            references unique_vinyls (id)
);





