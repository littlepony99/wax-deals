ALTER TABLE public.shops
    ADD COLUMN shop_order bigint;

UPDATE public.shops
   SET shop_order = 10
 WHERE name = 'BandCamp'
   AND shop_order is null;

COMMIT;