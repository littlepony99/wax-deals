package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.exception.NotFoundException;
import com.vinylteam.vinyl.service.*;
import com.vinylteam.vinyl.util.impl.OneVinylOfferMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultOneVinylOffersService implements OneVinylOffersService {

    private final UniqueVinylService uniqueVinylService;
    private final OfferService offerService;
    private final ShopService shopService;
    private final DiscogsService discogsService;
    private final OneVinylOfferMapper offerMapper;

    public UniqueVinyl getUniqueVinyl(String id) throws NotFoundException {
        return uniqueVinylService.findById(id);
    }

    @Override
    public HashMap<String, List> getSortedInStockOffersAndShops(String identifier) throws NotFoundException {
        String uniqueVinylId = identifier;
        // find vinyl
        UniqueVinyl uniqueVinyl = uniqueVinylService.findById(uniqueVinylId);
        // find offers for uniqyeVinyl
        List<Offer> offers = offerService.findByUniqueVinylId(uniqueVinyl.getId());
        // 1 prepare offers -- validate and parse
        List<Offer> sortedInStockOffers = getInStockOffersByPrice(offers);
        // find shopIds for offers
        List<Integer> shopIds = offerService.findShopIds(sortedInStockOffers);
        // find shops by their ids
        List<Shop> shopsFromOffers = shopService.findShopsByListOfIds(shopIds);
        // if there is no offers
        checkIsVinylInStock(uniqueVinyl, sortedInStockOffers);

        HashMap<String, List> offersAndShopsMap = new HashMap<>();
        offersAndShopsMap.put("offers", sortedInStockOffers);
        offersAndShopsMap.put("shops", shopsFromOffers);

        return offersAndShopsMap;
    }

    @Override
    public List<UniqueVinyl> addAuthorVinyls(UniqueVinyl uniqueVinyl) {
        // 2 add artists vinyls to searchable vinyl
        return prepareVinylsSection(uniqueVinyl);
    }

    @Override
    public String getDiscogsLink(UniqueVinyl uniqueVinyl) {
        try {
            return discogsService.getDiscogsLink(uniqueVinyl.getArtist(),
                    uniqueVinyl.getRelease(), uniqueVinyl.getFullName());
        } catch (ParseException e) {
            log.error("Error while getting discogs link for unique vinyl, ParseException thrown {'uniqueVinyl':{}}", uniqueVinyl, e);
            return "";
        }
    }

    void checkIsVinylInStock(UniqueVinyl uniqueVinyl, List<Offer> inStockOffers) {
        if (inStockOffers.isEmpty()) {
            uniqueVinyl.setHasOffers(false);
            uniqueVinylService.updateOneUniqueVinyl(uniqueVinyl);
        }
    }

    List<Offer> getInStockOffersByPrice(List<Offer> dbOffers) {
        return dbOffers.stream()
                .map(offerService::actualizeOffer)
                .filter(Offer::isInStock)
                .sorted((offer1, offer2) -> (int) (offer1.getPrice() - offer2.getPrice()))
                .collect(Collectors.toList());
    }

    public Shop findOfferShop(List<Shop> shopsList, Offer offer) {
        return shopsList
                .stream()
                .filter(store -> store.getId() == offer.getShopId())
                .findFirst()
                .get();
    }

    List<UniqueVinyl> prepareVinylsSection(UniqueVinyl uniqueVinyl) {
        List<UniqueVinyl> preparedListById = new ArrayList<>();

        uniqueVinylService.findByArtist(uniqueVinyl.getArtist())
                .stream()
                .filter(v -> v.getId() != uniqueVinyl.getId())
                .forEach(preparedListById::add);

        return preparedListById;
    }

}