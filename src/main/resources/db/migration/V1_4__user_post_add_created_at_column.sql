ALTER TABLE public.user_posts
    ADD COLUMN created_at timestamp not null;

alter table public.user_posts drop column user_id;