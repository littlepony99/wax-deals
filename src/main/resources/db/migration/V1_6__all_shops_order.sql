UPDATE public.shops
   SET shop_order = 1
 WHERE name = 'JunoCoUk'
   AND shop_order is null;

UPDATE public.shops
   SET shop_order = 2
 WHERE name = 'deejay.de'
   AND shop_order is null;

UPDATE public.shops
   SET shop_order = 3
 WHERE name = 'Clone.NL'
   AND shop_order is null;

UPDATE public.shops
   SET shop_order = 4
 WHERE name = 'Red Eye Records'
   AND shop_order is null;

UPDATE public.shops
   SET shop_order = 5
 WHERE name = 'decks.de'
   AND shop_order is null;

UPDATE public.shops
   SET shop_order = 6
 WHERE name = 'Triple vision'
   AND shop_order is null;

UPDATE public.shops
   SET shop_order = 7
 WHERE name = 'Hard Wax'
   AND shop_order is null;

UPDATE public.shops
   SET shop_order = 8
 WHERE name = 'Diskultura'
   AND shop_order is null;

UPDATE public.shops
   SET shop_order = 9
 WHERE name = 'VinylUa'
   AND shop_order is null;

COMMIT;