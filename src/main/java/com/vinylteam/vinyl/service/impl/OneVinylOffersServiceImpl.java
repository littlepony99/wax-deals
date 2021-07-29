package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.*;
import com.vinylteam.vinyl.util.impl.OneVinylOfferMapper;
import com.vinylteam.vinyl.web.dto.OneVinylOfferDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OneVinylOffersServiceImpl implements OneVinylOffersService {

    private final UniqueVinylService uniqueVinylService;
    private final OfferService offerService;
    private final ShopService shopService;
    private final DiscogsService discogsService;
    private final OneVinylOfferMapper offerMapper;

    public UniqueVinyl getUniqueVinyl(String id) {
        return uniqueVinylService.findById(id);
    }

    @Override
    public List<OneVinylOfferDto> getOffers(String identifier) {
        String uniqueVinylId = identifier;
        // find vinyl
        UniqueVinyl uniqueVinyl = uniqueVinylService.findById(uniqueVinylId);

        // find offers for uniqyeVinyl
        List<Offer> offers = offerService.findByUniqueVinylId(uniqueVinyl.getId());
        // find shopIds for offers
        List<Integer> shopIds = offerService.findShopIds(offers);
        // find shops by their ids
        List<Shop> shopsFromOffers = shopService.findShopsByListOfIds(shopIds);

        // 1 prepare offers -- validate and parse
        List<OneVinylOfferDto> offersList = prepareOffersSection(offers, shopsFromOffers);
        // if there is no offers
        checkIsVinylInStock(uniqueVinyl, offersList);

        return offersList;
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

    void checkIsVinylInStock(UniqueVinyl uniqueVinyl, List<OneVinylOfferDto> offersResponseList) {
        if (offersResponseList.isEmpty()) {
            uniqueVinyl.setHasOffers(false);
            uniqueVinylService.updateOneUniqueVinyl(uniqueVinyl);
        }
    }

    List<OneVinylOfferDto> prepareOffersSection(List<Offer> dbOffers, List<Shop> shopsList) {
        return dbOffers.stream()
                .map(offerService::actualizeOffer)
                .filter(Offer::isInStock)
                .map(offer -> offerMapper.offerAndShopToVinylOfferDto(offer, findOfferShop(shopsList, offer)))
                .sorted((offer1, offer2) -> (int) (offer1.getPrice() - offer2.getPrice()))
                .collect(Collectors.toList());
    }

    Shop findOfferShop(List<Shop> shopsList, Offer offer) {
        return shopsList
                .stream()
                .filter(store -> Objects.equals(store.getId(), offer.getShopId()))
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