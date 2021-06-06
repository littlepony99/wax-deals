create table recovery_password
(
    id                serial       not null
        constraint recovery_password_pkey
            primary key,
    user_id           integer      not null
        constraint user_id_fk
            references users
            unique,
    token varchar(500) not null unique
);

UPDATE public.shops
   SET link_to_main_page = 'https://www.triplevision.nl/'
 WHERE id = 10;