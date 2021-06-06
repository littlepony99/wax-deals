ALTER TABLE public.recovery_password
    ADD COLUMN created_at timestamp not null,
    ADD COLUMN token_lifetime timestamp not null;