ALTER TABLE public.offers
    ADD COLUMN cat_number character varying(200),
    ADD COLUMN in_stock boolean NOT NULL;